package rainbowclasses

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

enum class RainbowPalette { DEFAULT, PASTEL, HIGH_CONTRAST, SYNTHWAVE }

@State(name = "RainbowClassesSettings", storages = [Storage("rainbow-classes.xml")])
class RainbowClassesSettings : PersistentStateComponent<RainbowClassesSettings.State> {

    data class State(
        var enabled: Boolean = true,
        var palette: RainbowPalette = RainbowPalette.DEFAULT
    )

    private var state = State()

    override fun getState(): State = state
    override fun loadState(s: State) { state = s }

    var enabled: Boolean      get() = state.enabled; set(v) { state.enabled = v }
    var palette: RainbowPalette get() = state.palette; set(v) { state.palette = v }

    companion object {
        fun getInstance(): RainbowClassesSettings =
            ApplicationManager.getApplication().getService(RainbowClassesSettings::class.java)
    }
}
