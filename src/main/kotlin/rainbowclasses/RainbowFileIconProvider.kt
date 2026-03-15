package rainbowclasses

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IconUtil
import com.jetbrains.php.lang.psi.elements.PhpClass
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentHashMap
import javax.swing.Icon

/**
 * Overlays a small deterministic colored badge onto the file's existing icon.
 *
 * How the base icon is preserved
 * ───────────────────────────────
 * [FileIconProvider.getIcon] is called by IntelliJ whenever it needs a file
 * icon (tabs, project tree, recent files, …). A ThreadLocal re-entrancy guard
 * lets us call [IconUtil.getIcon] to obtain the real base icon without infinite
 * recursion — IntelliJ's full icon pipeline (PHP class/interface/trait icons,
 * Git overlays, …) runs and hands us back exactly what it would show without
 * this plugin.
 *
 * How compositing works
 * ─────────────────────
 * We return a [CompositeIcon] that paints base + badge onto a single
 * [BufferedImage.TYPE_INT_ARGB] buffer, giving full control over alpha.
 *
 * Caching
 * ───────
 * [RainbowMarker] is cached per (filePath, modificationStamp, palette).
 * Palette changes automatically cause a cache miss. [clearCache] is called
 * from [RainbowClassesConfigurable] on Apply to force an immediate repaint.
 */
class RainbowFileIconProvider : FileIconProvider {

    private val computing = ThreadLocal.withInitial { false }

    private val cache = ConcurrentHashMap<String, CacheEntry>()
    private data class CacheEntry(val stamp: Long, val palette: RainbowPalette, val marker: RainbowMarker?)

    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        if (computing.get()) return null

        if (file.extension?.lowercase() != "php") return null
        if (project == null || project.isDisposed) return null

        val settings = RainbowClassesSettings.getInstance()
        if (!settings.enabled) return null

        val marker = resolveMarker(file, project, settings) ?: return null

        computing.set(true)
        val base: Icon = try {
            IconUtil.getIcon(file, flags, project)
        } finally {
            computing.set(false)
        }

        return CompositeIcon(base, marker.color)
    }

    fun clearCache() = cache.clear()

    private fun resolveMarker(file: VirtualFile, project: Project, settings: RainbowClassesSettings): RainbowMarker? {
        val stamp = file.modificationStamp
        val palette = settings.palette
        cache[file.path]?.let { e -> if (e.stamp == stamp && e.palette == palette) return e.marker }

        if (DumbService.isDumb(project)) return cache[file.path]?.marker

        var result: RainbowMarker? = null
        ApplicationManager.getApplication().runReadAction {
            val psiFile = PsiManager.getInstance(project).findFile(file) ?: return@runReadAction
            val classes = PsiTreeUtil
                .findChildrenOfType(psiFile, PhpClass::class.java)
                .filter { !it.isAnonymousClass() }
            val phpClass = classes.firstOrNull { it.name == file.nameWithoutExtension }
                ?: classes.firstOrNull()
                ?: return@runReadAction
            result = RainbowClassesService.classMarker(phpClass.fqcnKey(), palette)
        }

        cache[file.path] = CacheEntry(stamp, palette, result)
        return result
    }
}

// ── Composite icon ────────────────────────────────────────────────────────────

private const val BADGE_INNER = 8   // colored fill side, px
private const val BADGE_OUTER = 10  // total badge size (1 px outline on each side)
private const val ARC         = 2   // corner radius

/**
 * Paints base icon + badge onto a single [BufferedImage.TYPE_INT_ARGB] buffer
 * so alpha compositing blends the badge directly against the base icon pixels.
 */
private class CompositeIcon(private val base: Icon, private val color: Color) : Icon {
    override fun getIconWidth()  = base.iconWidth
    override fun getIconHeight() = base.iconHeight

    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val w = base.iconWidth
        val h = base.iconHeight

        val img = BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB)
        val g2: Graphics2D = img.createGraphics()
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        // Layer 0: base icon
        base.paintIcon(c, g2, 0, 0)

        // Layer 1: badge at bottom-right
        val bx = (w - BADGE_OUTER).coerceAtLeast(0)
        val by = (h - BADGE_OUTER).coerceAtLeast(0)
        g2.color = Color(0, 0, 0, 110)
        g2.fillRoundRect(bx, by, BADGE_OUTER, BADGE_OUTER, ARC + 1, ARC + 1)
        g2.color = color
        g2.fillRoundRect(bx + 1, by + 1, BADGE_INNER, BADGE_INNER, ARC, ARC)

        g2.dispose()
        g.drawImage(img, x, y, null)
    }
}
