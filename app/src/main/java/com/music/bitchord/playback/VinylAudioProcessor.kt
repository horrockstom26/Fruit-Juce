package com.music.bitchord.playback

import com.music.bitchord.playback.audio.AudioBlock
import com.music.bitchord.playback.audio.FloatAudioProcessor
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

/**
 * Subtle vinyl-mode coloration: a small low-shelf bass boost plus a tiny room/reverb tail.
 * Designed to be lightweight and safe in the real-time DSP chain.
 */
class VinylAudioProcessor : FloatAudioProcessor {
    @Volatile
    var enabled: Boolean = false

    private var sampleRate = 44100
    private var channelCount = 2

    // Low-shelf coefficients/state (stereo)
    private var b0 = 1f; private var b1 = 0f; private var b2 = 0f
    private var a1 = 0f; private var a2 = 0f
    private var x1 = FloatArray(2); private var x2 = FloatArray(2)
    private var y1 = FloatArray(2); private var y2 = FloatArray(2)

    // Very small comb delays for a subtle reverb/warmth
    private var comb1 = FloatArray(1); private var comb2 = FloatArray(1)
    private var i1 = 0; private var i2 = 0
    private var g1 = 0.18f; private var g2 = 0.14f

    override fun configure(sampleRate: Int, channelCount: Int) {
        this.sampleRate = sampleRate.coerceAtLeast(8000)
        this.channelCount = channelCount.coerceAtLeast(1)
        setupShelf()
        setupReverb()
        resetState()
    }

    override fun process(block: AudioBlock) {
        if (!enabled || block.frameCount <= 0) return
        val samples = block.samples
        val frames = block.frameCount
        val ch = block.channelCount.coerceAtLeast(1)
        val c1 = comb1; val c2 = comb2
        val n1 = c1.size; val n2 = c2.size
        var p1 = i1; var p2 = i2

        for (f in 0 until frames) {
            val base = f * ch
            for (c in 0 until ch.coerceAtMost(2)) {
                val idx = base + c
                val x = samples[idx]

                // Low shelf bass boost
                val yShelf = b0 * x + b1 * x1[c] + b2 * x2[c] - a1 * y1[c] - a2 * y2[c]
                x2[c] = x1[c]; x1[c] = x
                y2[c] = y1[c]; y1[c] = yShelf

                // Tiny reverb (mono-ish mix using two comb taps)
                val d1 = c1[p1]
                val d2 = c2[p2]
                val wet = (d1 + d2) * 0.5f
                c1[p1] = yShelf + d1 * g1
                c2[p2] = yShelf + d2 * g2

                val mixed = yShelf + wet * 0.08f
                // soft clamp to avoid clipping on boosted material
                samples[idx] = mixed.coerceIn(-1.2f, 1.2f)
            }
            p1++; if (p1 >= n1) p1 = 0
            p2++; if (p2 >= n2) p2 = 0
        }
        i1 = p1; i2 = p2
    }

    override fun flush() {
        resetState()
    }

    override fun reset() {
        resetState()
    }

    private fun resetState() {
        x1.fill(0f); x2.fill(0f); y1.fill(0f); y2.fill(0f)
        comb1.fill(0f); comb2.fill(0f)
        i1 = 0; i2 = 0
    }

    private fun setupShelf() {
        // ~+2.5dB low shelf at 120Hz
        val fs = sampleRate.toFloat()
        val f0 = 120f
        val gainDb = 2.5f
        val S = 1f
        val A = exp((gainDb / 40f) * ln10()).toFloat()
        val w0 = (2.0 * PI * f0 / fs).toFloat()
        val cosw0 = cos(w0)
        val sinw0 = sin(w0)
        val alpha = (sinw0 / 2f) * kotlin.math.sqrt((A + 1f / A) * (1f / S - 1f) + 2f)
        val twoSqrtAAlpha = 2f * kotlin.math.sqrt(A) * alpha

        var bb0 =    A * ((A + 1f) - (A - 1f) * cosw0 + twoSqrtAAlpha)
        var bb1 =  2f*A * ((A - 1f) - (A + 1f) * cosw0)
        var bb2 =    A * ((A + 1f) - (A - 1f) * cosw0 - twoSqrtAAlpha)
        var aa0 =        (A + 1f) + (A - 1f) * cosw0 + twoSqrtAAlpha
        var aa1 =   -2f * ((A - 1f) + (A + 1f) * cosw0)
        var aa2 =        (A + 1f) + (A - 1f) * cosw0 - twoSqrtAAlpha

        b0 = bb0 / aa0; b1 = bb1 / aa0; b2 = bb2 / aa0
        a1 = aa1 / aa0; a2 = aa2 / aa0
    }

    private fun setupReverb() {
        val sr = sampleRate
        comb1 = FloatArray((sr * 0.0297f).toInt().coerceAtLeast(1))
        comb2 = FloatArray((sr * 0.0371f).toInt().coerceAtLeast(1))
        i1 = 0; i2 = 0
    }

    private fun ln10(): Double = 2.302585092994046
}