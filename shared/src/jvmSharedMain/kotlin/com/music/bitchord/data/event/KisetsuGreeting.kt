package com.music.bitchord.data.event

import java.util.Calendar
import java.util.Date

/**
 * The line under the app's name on the home screen.
 *
 * It used to be a static place-of-origin caption; it now greets the listener
 * with the time of day instead. The small hours get their own line — anyone
 * opening a music app at four in the morning is making a decision — and the
 * mark beside it turns into a moon, whatever the season.
 *
 * Boundaries are fixed and testable: 05:00–11:59 morning, 12:00–17:59
 * afternoon, 18:00–22:59 night, and 23:00–04:59 the all-nighter.
 */
object KisetsuGreeting {

    enum class Daypart(val text: String) {
        NIGHT("Fancy an all-nighter?"),
        MORNING("Good morning"),
        AFTERNOON("Good afternoon"),
        EVENING("Good night"),
    }

    fun daypartAt(date: Date = Date()): Daypart {
        val hour = Calendar.getInstance().apply { time = date }.get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 5..11 -> Daypart.MORNING
            in 12..17 -> Daypart.AFTERNOON
            in 18..22 -> Daypart.EVENING
            else -> Daypart.NIGHT
        }
    }

    /** `Good morning 🎃`, `Fancy an all-nighter? 🌙`, and so on. */
    fun lineAt(date: Date = Date(), seasonalEmoji: String): String =
        "${daypartAt(date).text} ${markAt(date, seasonalEmoji)}"

    /**
     * The season's mark by day, a moon through the small hours. The moon is
     * not tied to October: an all-nighter is an all-nighter in November too.
     */
    fun markAt(date: Date = Date(), seasonalEmoji: String): String =
        if (daypartAt(date) == Daypart.NIGHT) "🌙" else seasonalEmoji
}
