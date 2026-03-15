package rainbowclasses

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.ide.FileIconProvider
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*

class RainbowClassesConfigurable : BoundConfigurable("Rainbow Classes") {

    override fun createPanel(): DialogPanel {
        val s = RainbowClassesSettings.getInstance()
        return panel {
            group("General") {
                row { checkBox("Enable markers").bindSelected(s::enabled) }
            }
            group("Palette") {
                buttonsGroup {
                    row { radioButton("Default",       RainbowPalette.DEFAULT) }
                    row { radioButton("Pastel",        RainbowPalette.PASTEL) }
                    row { radioButton("High Contrast", RainbowPalette.HIGH_CONTRAST) }
                    row { radioButton("Synthwave",     RainbowPalette.SYNTHWAVE) }
                }.bind(s::palette)
            }
        }
    }

    override fun apply() {
        super.apply()
        refreshAll()
    }

    private fun refreshAll() {
        val ep = com.intellij.openapi.extensions.ExtensionPointName.create<FileIconProvider>(
            "com.intellij.fileIconProvider"
        )
        ep.extensionList.filterIsInstance<RainbowFileIconProvider>().forEach { it.clearCache() }

        ApplicationManager.getApplication().invokeLater {
            ProjectManager.getInstance().openProjects.forEach { project ->
                if (project.isDisposed) return@forEach
                DaemonCodeAnalyzer.getInstance(project).restart()
                ProjectView.getInstance(project).refresh()
                val fem = FileEditorManager.getInstance(project)
                fem.openFiles.forEach { vf -> fem.updateFilePresentation(vf) }
            }
        }
    }
}
