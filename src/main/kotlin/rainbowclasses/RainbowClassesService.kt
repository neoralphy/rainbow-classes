package rainbowclasses

import java.awt.Color

/** Deterministic visual identity for a PHP class-like element. */
data class RainbowMarker(val color: Color)

object RainbowClassesService {

    fun classMarker(fqcn: String, preset: RainbowPalette): RainbowMarker {
        val hash = murmur3(fqcn)
        val palette = RainbowPaletteProvider.palette(preset)
        val colorIndex = (hash and 0x7FFFFFFF) % palette.size
        return RainbowMarker(color = palette[colorIndex])
    }

    /** MurmurHash3 (32-bit, x86). Deterministic across JVM restarts. */
    fun murmur3(key: String, seed: Int = 0): Int {
        val data = key.toByteArray(Charsets.UTF_8)
        val len = data.size
        var h = seed
        val nblocks = len / 4

        for (i in 0 until nblocks) {
            var k = data.getIntLE(i * 4)
            k = k * 0xCC9E2D51.toInt()
            k = k.rotateLeft(15)
            k = k * 0x1B873593
            h = h xor k
            h = h.rotateLeft(13)
            h = h * 5 + 0xE6546B64.toInt()
        }

        var tail = 0
        val tailStart = nblocks * 4
        when (len and 3) {
            3 -> { tail = tail or (data[tailStart + 2].toInt() and 0xFF shl 16)
                   tail = tail or (data[tailStart + 1].toInt() and 0xFF shl 8)
                   tail = tail or (data[tailStart    ].toInt() and 0xFF) }
            2 -> { tail = tail or (data[tailStart + 1].toInt() and 0xFF shl 8)
                   tail = tail or (data[tailStart    ].toInt() and 0xFF) }
            1 -> { tail = tail or (data[tailStart    ].toInt() and 0xFF) }
        }
        if ((len and 3) != 0) {
            tail = tail * 0xCC9E2D51.toInt()
            tail = tail.rotateLeft(15)
            tail = tail * 0x1B873593
            h = h xor tail
        }

        h = h xor len
        h = fmix32(h)
        return h
    }

    private fun ByteArray.getIntLE(offset: Int): Int =
        (this[offset    ].toInt() and 0xFF)        or
        ((this[offset + 1].toInt() and 0xFF) shl 8)  or
        ((this[offset + 2].toInt() and 0xFF) shl 16) or
        ((this[offset + 3].toInt() and 0xFF) shl 24)

    private fun fmix32(h: Int): Int {
        var x = h
        x = x xor (x ushr 16); x = x * 0x85EBCA6B.toInt()
        x = x xor (x ushr 13); x = x * 0xC2B2AE35.toInt()
        x = x xor (x ushr 16)
        return x
    }
}
