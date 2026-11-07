package com.music.bitchord.playback

import android.os.SystemClock
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Sleep timer. Holds a deadline; [PlaybackService] watches it and pauses
 * playback once it passes.
 *
 * A deadline rather than a countdown: nothing has to tick for the timer to
 * stay accurate, so it survives the player UI being dismissed, and any
 * observer can work out how long is left for itself. elapsedRealtime, not
 * wall clock, so changing the system time can't cut a timer short.
 */
object SleepTimer {

    /** Deadline on [SystemClock.elapsedRealtime], or null when no timer is set. */
    val deadline = MutableStateFlow<Long?>(null)

    /** The preset that was chosen, so the picker can tick it. Null when off. */
    val minutes = MutableStateFlow<Int?>(null)

    /**
     * Pause when the current track ends instead of after a fixed wait.
     *
     * Deliberately not expressed as a deadline of "duration minus position":
     * seeking, crossfade and a queue that reorders itself would all leave that
     * number wrong, whereas the track ending is an event the player reports.
     */
    val afterTrack = MutableStateFlow(false)

    /** Pause when the queue reaches its end (playback finishes). */
    val endOfQueue = MutableStateFlow(false)

    /** Durations offered in the picker. */
    val PRESETS = listOf(15, 30, 45, 60)

    /** Whether any kind of timer is currently armed. */
    val isRunning: Boolean get() = deadline.value != null || afterTrack.value || endOfQueue.value

    fun start(minutes: Int) {
        afterTrack.value = false
        endOfQueue.value = false
        this.minutes.value = minutes
        deadline.value = SystemClock.elapsedRealtime() + minutes * 60_000L
    }

    /** Adds [minutes] to the running deadline timer (a no-op without one). */
    fun extend(minutes: Int) {
        val current = deadline.value ?: return
        afterTrack.value = false
        endOfQueue.value = false
        this.minutes.value = ((current - SystemClock.elapsedRealtime()) / 60_000L + minutes)
            .toInt()
            .coerceAtLeast(1)
        deadline.value = current + minutes * 60_000L
    }

    /** Pause once the track playing right now finishes. */
    fun startAfterTrack() {
        minutes.value = null
        deadline.value = null
        endOfQueue.value = false
        afterTrack.value = true
    }

    /** Pause when the queue finishes playing. */
    fun startEndOfQueue() {
        minutes.value = null
        deadline.value = null
        afterTrack.value = false
        endOfQueue.value = true
    }

    fun cancel() {
        minutes.value = null
        deadline.value = null
        afterTrack.value = false
        endOfQueue.value = false
    }

    /** How long is left, or null when no timer is running. */
    fun remainingMs(): Long? =
        deadline.value?.let { (it - SystemClock.elapsedRealtime()).coerceAtLeast(0L) }
}
