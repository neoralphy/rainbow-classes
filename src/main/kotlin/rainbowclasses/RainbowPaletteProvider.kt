package rainbowclasses

import java.awt.Color

object RainbowPaletteProvider {

    private val DEFAULT = listOf(
        Color(0xE84040),  //   0° red
        Color(0xE88030),  //  30° orange
        Color(0xD4BE20),  //  60° yellow
        Color(0x7CC430),  //  90° lime
        Color(0x28C455),  // 120° green
        Color(0x20B890),  // 150° teal
        Color(0x20B4D4),  // 180° cyan
        Color(0x3888E0),  // 210° sky blue
        Color(0x5855D0),  // 240° indigo
        Color(0x9B50D0),  // 270° purple
        Color(0xCC3CB8),  // 300° magenta
        Color(0xE03878),  // 330° rose
    )

    private val PASTEL = listOf(
        Color(0xF08080),  //   0° soft red
        Color(0xF0A870),  //  30° soft orange
        Color(0xE8D870),  //  60° soft yellow
        Color(0xA8D870),  //  90° soft lime
        Color(0x70D090),  // 120° soft green
        Color(0x70D0B0),  // 150° soft teal
        Color(0x70C8D8),  // 180° soft cyan
        Color(0x70A0E0),  // 210° soft blue
        Color(0x8880D8),  // 240° soft indigo
        Color(0xB070D8),  // 270° soft purple
        Color(0xD870C8),  // 300° soft magenta
        Color(0xE870A0),  // 330° soft rose
    )

    private val HIGH_CONTRAST = listOf(
        Color(0xFF2828),  //   0° vivid red
        Color(0xFF8800),  //  30° vivid orange
        Color(0xFFD000),  //  60° vivid yellow
        Color(0x88FF00),  //  90° vivid lime
        Color(0x00FF44),  // 120° vivid green
        Color(0x00FFAA),  // 150° vivid teal
        Color(0x00EEFF),  // 180° vivid cyan
        Color(0x0088FF),  // 210° vivid blue
        Color(0x4444FF),  // 240° vivid indigo
        Color(0xAA00FF),  // 270° vivid purple
        Color(0xFF00CC),  // 300° vivid magenta
        Color(0xFF0066),  // 330° vivid rose
    )

    private val SYNTHWAVE = listOf(
        Color(0xFF1744),  //   0° neon red
        Color(0xFF6600),  //  30° neon orange
        Color(0xFFE000),  //  60° neon yellow
        Color(0xAAFF00),  //  90° electric lime
        Color(0x00FF66),  // 120° matrix green
        Color(0x00FFCC),  // 150° neon teal
        Color(0x00D4FF),  // 180° electric cyan
        Color(0x0088FF),  // 210° electric blue
        Color(0x6600FF),  // 240° deep violet
        Color(0xCC00FF),  // 270° electric purple
        Color(0xFF00CC),  // 300° hot magenta
        Color(0xFF2D78),  // 330° hot pink
    )

    fun palette(preset: RainbowPalette): List<Color> = when (preset) {
        RainbowPalette.DEFAULT       -> DEFAULT
        RainbowPalette.PASTEL        -> PASTEL
        RainbowPalette.HIGH_CONTRAST -> HIGH_CONTRAST
        RainbowPalette.SYNTHWAVE     -> SYNTHWAVE
    }
}
