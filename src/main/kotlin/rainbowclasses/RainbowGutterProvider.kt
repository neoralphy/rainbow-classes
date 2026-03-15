package rainbowclasses

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.psi.PsiElement
import com.jetbrains.php.lang.psi.elements.PhpClass
import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import javax.swing.Icon

/**
 * Adds a single colored stripe in the gutter on the class/interface/trait/enum
 * declaration line. Nothing is rendered inside the class body.
 */
class RainbowGutterProvider : LineMarkerProvider {

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        val settings = RainbowClassesSettings.getInstance()
        if (!settings.enabled) return null

        // Only act on the name identifier leaf of a class-like element.
        if (element.firstChild != null) return null
        val parent = element.parent as? PhpClass ?: return null
        if (element != parent.nameIdentifier) return null
        if (parent.isAnonymousClass()) return null

        val fqcn = parent.fqcnKey()
        val marker = RainbowClassesService.classMarker(fqcn, settings.palette)

        return LineMarkerInfo(
            element,
            element.textRange,
            ClassSquareIcon(marker.color),
            { "Rainbow Classes\nClass: $fqcn" },
            null,
            GutterIconRenderer.Alignment.LEFT,
            { "Rainbow Classes\nClass: $fqcn" }
        )
    }
}

// ── Gutter icon ──────────────────────────────────────────────────────────────

private const val SIZE   = 9  // square side in pixels
private const val RADIUS = 2  // corner radius in pixels

/** Small filled rounded square rendered in the gutter on the declaration line. */
private class ClassSquareIcon(private val color: Color) : Icon {
    override fun getIconWidth()  = SIZE
    override fun getIconHeight() = SIZE

    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
        val g2 = g.create() as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        g2.color = color
        g2.fillRoundRect(x, y, SIZE, SIZE, RADIUS, RADIUS)
        g2.dispose()
    }
}
