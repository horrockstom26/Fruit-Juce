package com.music.bitchord.data.settings

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Juce Jar — Fruit Juce's sleep timer, and the app's signature way to end an
 * evening: pick a fill and the music drains out with it. Lives outside
 * [AppSettings] because a jar is a countdown, not a preference — nothing to
 * save, nothing to restore after a process death.
 *
 * One owner, two dials: [minutes] is what the picker shows, [endsAt] is the
 * wall-clock moment the music stops. The engine in MainActivity watches
 * [endsAt] only.
 */
object SleepTimer {

    /** The last fill chosen, in minutes, 0 meaning off. Display only. */
    val minutes = MutableStateFlow(0)

    /** The wall-clock moment playback pauses, or null when the jar is empty. */
    val endsAt = MutableStateFlow<Long?>(null)

    /**
     * Fills the jar for [value] minutes. Zero empties it, cancelling any
     * countdown already running.
     */
    fun set(value: Int) {
        val clamped = value.coerceIn(0, 480)
        minutes.value = clamped
        endsAt.value = if (clamped <= 0) {
            null
        } else {
            System.currentTimeMillis() + clamped * 60_000L
        }
    }

    /** Empties the jar, leaving the minutes dial where the user left it. */
    fun clear() {
        endsAt.value = null
    }
}
