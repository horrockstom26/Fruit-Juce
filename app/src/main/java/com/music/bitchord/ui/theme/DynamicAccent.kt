package com.music.bitchord.ui.theme

import android.graphics.Color as AndroidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import kotlin.math.abs

/**
 * Juce Droplet — the engine behind "the app's colours are the album's
 * colours". The immersive canvas already holds a small blurred copy of the
 * playing cover; this object samples it for the hue that carries the artwork,
 * dresses that hue to suite the theme the app is wearing, and publishes it.
 * [com.music.bitchord.ui.theme.BitChordTheme] then paints `primary` with it,
 * which recolours the bottom bar, switches, sliders, the home brand —
 * everything the orange used to reach — in one move.
 *
 * The published colour is a *state*, not a theme rebuild: a track hand-off
 * re-tints the running app, the same navigation stack underneath.
 */
object DynamicAccent {

    /** The playing cover's accent, or null while nothing is playing — null reinstates the Fruit Juce orange. */
    val color = MutableStateFlow<Color?>(null)

    /**
     * Samples [bitmap] — the blurred cover — and publishes the accent.
     * Runs off the main thread; a null bitmap clears the accent.
     */
    suspend fun update(bitmap: ImageBitmap?, dark: Boolean) {
        val next = if (bitmap == null) {
            null
        } else {
            withContext(Dispatchers.Default) { derive(bitmap, dark) }
        }
        color.value = next
    }

    /**
     * The cover's own favourite colour, made app-worthy.
     *
     * Hue votes are bucketed into 24 bins, each vote weighted by saturation
     * and by how well the pixel sits in the mid-tone band — a sleeve's glow
     * background wins over its buried or blown-out edges. The winning bin's
     * pixels are then averaged, and their hue is kept while saturation and
     * brightness are pinned to accent range: too grey and the app loses its
     * spark, too bright and white text on it drowns.
     */
    private fun derive(bitmap: ImageBitmap, dark: Boolean): Color? {
        val source = bitmap.asAndroidBitmap()
        val w = source.width
        val h = source.height
        if (w == 0 || h == 0) return null
        val step = maxOf(1, minOf(w, h) / 64)
        val bins = 24
        val hueScore = FloatArray(bins)
        val hsv = FloatArray(3)

        fun binOf(pixel: Int): Int {
            AndroidColor.colorToHSV(pixel, hsv)
            if (hsv[1] < 0.16f || hsv[2] < 0.15f || hsv[2] > 0.97f) return -1
            val band = if (hsv[2] in 0.35f..0.88f) 1f else 0.55f
            val bin = (hsv[0] * bins / 360f).toInt().coerceIn(0, bins - 1)
            hueScore[bin] += hsv[1] * band
            return bin
        }

        for (y in 0 until h step step) {
            for (x in 0 until w step step) {
                binOf(source.getPixel(x, y))
            }
        }
        var bestBin = -1
        var bestScore = 0f
        for (b in 0 until bins) {
            if (hueScore[b] > bestScore) {
                bestScore = hueScore[b]
                bestBin = b
            }
        }
        if (bestBin < 0 || bestScore <= 0f) return null

        val center = (bestBin + 0.5f) * 360f / bins
        var hueSum = 0f
        var satSum = 0f
        var count = 0
        for (y in 0 until h step step) {
            for (x in 0 until w step step) {
                val pixel = source.getPixel(x, y)
                AndroidColor.colorToHSV(pixel, hsv)
                if (hsv[1] < 0.16f || hsv[2] < 0.15f || hsv[2] > 0.97f) continue
                var delta = abs(hsv[0] - center)
                if (delta > 180f) delta = 360f - delta
                if (delta <= 360f / bins) {
                    hueSum += hsv[0]
                    satSum += hsv[1]
                    count++
                }
            }
        }
        if (count == 0) return null

        val hue = hueSum / count
        val saturation = (satSum / count).coerceIn(0.55f, 0.92f)
        val brightness = if (dark) 0.62f else 0.48f
        return Color(AndroidColor.HSVToColor(floatArrayOf(hue, saturation, brightness)))
    }
}
