package com.music.bitchord.playback.haptics

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.music.bitchord.data.haptics.MusicHapticsIntensity
import com.music.bitchord.data.haptics.MusicHapticsMode
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.playback.audio.AudioBlock
import com.music.bitchord.playback.audio.FloatAudioProcessor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sqrt

/** Which part of the mix a burst came from — the two bands Music Haptics taps. */
enum class MusicHapticsBand { BASS, MID }

/**
 * Music Haptics, on Android's motor: the iOS 18 feature translated rather than
 * copied.
 *
 * iOS analyses the decoded stream with an `AnalyserNode` and plays prepared
 * haptic *tracks*. Android has neither, so the same idea is done where the
 * signal already exists: [MusicHapticsProcessor] sits at the end of the DSP
 * chain, filters the mix into the two bands the feature cares about, and
 * reports transients; this class turns each transient into a short vibration.
 *
 * The shapes follow the spec — a kick is a heavy two-beat `[40, 20, 60]`, a
 * vocal or melodic hit is a single crisp `15ms` tap — and both scale with the
 * listener's intensity choice when the motor can do amplitudes. A motor that
 * can't gets the timing only, which is what any phone without amplitude
 * control can honestly offer.
 *
 * Nothing here allocates per burst, and every burst is refused if it would
 * land on top of the previous one: a vibration that outlasts the finger stops
 * reading as the music and starts reading as an alarm.
 */
class MusicHapticsEngine(context: Context) {

    private val vibrator: Vibrator? = resolveVibrator(context)
        ?.takeIf { it.hasVibrator() }

    private val amplitudeControl: Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            runCatching { vibrator?.hasAmplitudeControl() == true }.getOrDefault(false)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    @Volatile
    var enabled: Boolean = false
        private set

    @Volatile
    var mode: MusicHapticsMode = MusicHapticsMode.FULL_MIX
        private set

    @Volatile
    var intensity: MusicHapticsIntensity = MusicHapticsIntensity.MEDIUM
        private set

    /** True while the motor should be answering the music at all. */
    val ready: Boolean
        get() = enabled && vibrator != null

    private var lastBassAt = 0L
    private var lastMidAt = 0L
    private var lastAnyAt = 0L

    fun start() {
        scope.launch {
            AppSettings.musicHapticsEnabled.collect { on ->
                enabled = on
                // Fully qualified on purpose. Inside `scope.launch` the lambda
                // receiver is a CoroutineScope, so a bare `cancel()` resolves to
                // the *scope's* cancel() — which killed all three collectors on
                // the first emission (off), and left the feature permanently
                // deaf to the switch. This is the motor's cancel.
                if (!on) this@MusicHapticsEngine.cancel()
            }
        }
        scope.launch {
            AppSettings.musicHapticsMode.collect { mode = it }
        }
        scope.launch {
            AppSettings.musicHapticsIntensity.collect { intensity = it }
        }
    }

    fun release() {
        cancel()
        scope.cancel()
    }

    /** Stops whatever is running — toggled off, paused, or the service going away. */
    fun cancel() {
        runCatching { vibrator?.cancel() }
        lastBassAt = 0L
        lastMidAt = 0L
        lastAnyAt = 0L
    }

    /**
     * One transient, from the audio thread.
     *
     * [strength] is how far above the band's own running average the hit was,
     * normalised to `0.2..1`; it is used as the amplitude, not the volume.
     */
    fun burst(band: MusicHapticsBand, strength: Float) {
        val motor = vibrator ?: return
        if (!enabled) return
        // Vocals Only is the mid band and nothing else — that is the whole
        // promise of the mode.
        if (mode == MusicHapticsMode.VOCALS_ONLY && band != MusicHapticsBand.MID) return

        val now = SystemClock.uptimeMillis()
        val refractory = if (band == MusicHapticsBand.BASS) BASS_REFRACTORY_MS else MID_REFRACTORY_MS
        val last = if (band == MusicHapticsBand.BASS) lastBassAt else lastMidAt
        if (now - last < refractory) return
        // A kick and a vocal landing together is one gesture, not two.
        if (now - lastAnyAt < GLOBAL_GAP_MS) return

        val level = (strength * intensity.scale).coerceIn(0.35f, 1f)
        val effect = if (!amplitudeControl) {
            VibrationEffect.createOneShot(
                if (band == MusicHapticsBand.BASS) BASS_FALLBACK_MS else MID_DURATION_MS,
                VibrationEffect.DEFAULT_AMPLITUDE,
            )
        } else if (band == MusicHapticsBand.BASS) {
            // `[40ms on, 20ms off, 60ms on]`, the spec's kick.
            VibrationEffect.createWaveform(
                longArrayOf(0L, 40L, 20L, 60L),
                intArrayOf(0, amplitude(level, 255), 0, amplitude(level * 0.85f, 255)),
                -1,
            )
        } else {
            // One crisp tap for a vocal or a melodic hit.
            VibrationEffect.createOneShot(MID_DURATION_MS, amplitude(level, 190))
        }

        runCatching { motor.vibrate(effect) }
        lastAnyAt = now
        if (band == MusicHapticsBand.BASS) lastBassAt = now else lastMidAt = now
    }

    private fun amplitude(level: Float, base: Int): Int =
        (base * level).toInt().coerceIn(1, 255)

    private fun resolveVibrator(context: Context): Vibrator? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)
                ?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }

    private companion object {
        /** A kick may not repeat inside this window; roughly 9 Hz. */
        const val BASS_REFRACTORY_MS = 110L

        /** Harmonic hits can come faster than kicks, but not much. */
        const val MID_REFRACTORY_MS = 75L

        /** No two bursts of any kind inside this — the motor needs to settle. */
        const val GLOBAL_GAP_MS = 55L

        const val MID_DURATION_MS = 15L
        const val BASS_FALLBACK_MS = 32L
    }
}

/**
 * The audio-side half: a read-only tap that measures the two bands.
 *
 * It runs last in the chain, after every stage that shapes the sound, so the
 * motor follows what the listener actually hears. It never writes to the
 * block, which is what keeps the sink's bit-exactness contract intact while
 * the feature is on.
 *
 * Filtering is two one-pole filters rather than an FFT: a kick is a step in
 * the envelope, and a 1024-point transform per buffer would buy accuracy the
 * motor can't express, at a cost paid on the audio thread.
 */
class MusicHapticsProcessor(
    private val engine: MusicHapticsEngine,
    /**
     * Whether this sink is the one being heard. A crossfade runs two players,
     * and both would otherwise tap the same song into the same motor.
     */
    private val audible: () -> Boolean,
) : FloatAudioProcessor {

    private var sampleRate = 48_000
    private var bassCoefficient = 0f
    private var midHighCoefficient = 0f
    private var midLowCoefficient = 0f

    private var bassState = 0f
    private var midHighState = 0f
    private var midLowState = 0f

    private var bassAverage = 0f
    private var midAverage = 0f

    override fun configure(sampleRate: Int, channelCount: Int) {
        this.sampleRate = sampleRate.coerceAtLeast(8_000)
        bassCoefficient = onePole(150f)
        midHighCoefficient = onePole(300f)
        midLowCoefficient = onePole(3_000f)
        flush()
    }

    private fun onePole(cutoffHz: Float): Float =
        (1.0 - exp(-2.0 * PI * cutoffHz / sampleRate)).toFloat()

    override fun process(block: AudioBlock) {
        // Bypassed entirely while the feature is off: not a multiply, an early
        // return before the first sample is read.
        if (!engine.ready) return
        if (!audible()) return
        val frames = block.frameCount
        if (frames == 0) return

        val samples = block.samples
        val channels = block.channelCount
        var bassEnergy = 0f
        var midEnergy = 0f
        var frame = 0
        while (frame < frames) {
            val base = frame * channels
            var mono = 0f
            var channel = 0
            while (channel < channels) {
                mono += samples[base + channel]
                channel++
            }
            mono /= channels

            bassState += bassCoefficient * (mono - bassState)
            val aboveBass = mono - bassState
            midHighState += midHighCoefficient * (aboveBass - midHighState)
            midLowState += midLowCoefficient * (midHighState - midLowState)

            bassEnergy += bassState * bassState
            midEnergy += midLowState * midLowState
            frame++
        }

        val bassRms = sqrt(bassEnergy / frames)
        val midRms = sqrt(midEnergy / frames)

        val bassHit = isTransient(bassRms, bassAverage, BASS_FLOOR)
        val midHit = isTransient(midRms, midAverage, MID_FLOOR)

        bassAverage = bassAverage * AVERAGE_DECAY + bassRms * (1f - AVERAGE_DECAY)
        midAverage = midAverage * AVERAGE_DECAY + midRms * (1f - AVERAGE_DECAY)

        if (bassHit) {
            engine.burst(MusicHapticsBand.BASS, strength(bassRms, bassAverage))
        }
        if (midHit) {
            engine.burst(MusicHapticsBand.MID, strength(midRms, midAverage))
        }
    }

    /** A hit is signal that is both audible and well above its own recent past. */
    private fun isTransient(rms: Float, average: Float, floor: Float): Boolean =
        rms > floor && rms > average * TRANSIENT_RATIO

    private fun strength(rms: Float, average: Float): Float =
        ((rms - average) / (average + 0.02f)).coerceIn(0.2f, 1f)

    @Suppress("DEPRECATION")
    override fun flush() {
        bassState = 0f
        midHighState = 0f
        midLowState = 0f
        bassAverage = 0f
        midAverage = 0f
    }

    override fun reset() = flush()

    private companion object {
        /** Ignore rumble that is not a hit — a room tone is not a kick. */
        const val BASS_FLOOR = 0.004f

        /** And quiet vocals, which are usually breath rather than a syllable. */
        const val MID_FLOOR = 0.002f

        /** How far above the running average counts as a transient. */
        const val TRANSIENT_RATIO = 1.55f

        /** Envelope memory: ~7 buffers at 20ms, a little under a second. */
        const val AVERAGE_DECAY = 0.86f
    }
}
