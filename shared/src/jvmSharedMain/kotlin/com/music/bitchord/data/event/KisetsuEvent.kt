package com.music.bitchord.data.event

import java.util.Calendar
import java.util.Date

/**
 * Kisetsu's seasonal dressing.
 *
 * The app wears one festival at a time, and the date is the whole switch. In
 * October the home screen runs a Halloween event — pumpkin instead of the
 * blossom, the heart replaced by a jack-o'-lantern, and a Kisetsu Picks shelf
 * of the classics. On 1 November the dressing comes off by itself and the
 * shelf turns to the rock and roll the event was always going to hand over to.
 *
 * The phase is computed from the clock rather than stored, so the change
 * happens on the first launch after midnight without an update, a migration or
 * anything left behind in preferences. [phaseAt] takes the date as a parameter
 * so the boundaries are testable.
 */
object KisetsuEvent {

    /** The heading the special shelf carries, whatever its season. */
    const val PICKS_TITLE = "Kisetsu Picks"

    /**
     * Which festival the app is wearing on a given day.
     *
     * [halloween] runs through the whole of October. [halloweenDay] is the
     * 31st alone — the one day the greeting card appears.
     */
    data class Phase(
        val halloween: Boolean,
        val halloweenDay: Boolean,
    ) {
        /** The mark beside the app's name: a pumpkin in October, blossom after. */
        val emoji: String get() = if (halloween) "🎃" else "🌸"
    }

    fun phaseAt(date: Date = Date()): Phase {
        val calendar = Calendar.getInstance().apply { time = date }
        val halloween = calendar.get(Calendar.MONTH) == Calendar.OCTOBER
        val day = calendar.get(Calendar.DAY_OF_MONTH) == 31
        return Phase(halloween = halloween, halloweenDay = halloween && day)
    }

    val phase: Phase get() = phaseAt()

    val halloween: Boolean get() = phase.halloween

    val halloweenDay: Boolean get() = phase.halloweenDay

    val emoji: String get() = phase.emoji

    /**
     * What the Kisetsu Picks shelf searches for today.
     *
     * The shelf is resolved at runtime — a query per card, best song hit taken
     * — rather than carrying video ids that rot. In October these are the
     * perennial party records; from 1 November the event hands over to the
     * rock and roll it was always building towards, Elvis first.
     */
    val picksQueries: List<String>
        get() = if (phase.halloween) HALLOWEEN_PICKS else ROCK_AND_ROLL_PICKS

    private val HALLOWEEN_PICKS = listOf(
        "Thriller Michael Jackson",
        "Monster Mash Bobby Pickett",
        "Ghostbusters Ray Parker Jr",
        "This Is Halloween The Nightmare Before Christmas",
        "Somebody's Watching Me Rockwell",
        "Werewolves of London Warren Zevon",
        "I Put a Spell on You Screamin' Jay Hawkins",
        "Time Warp Rocky Horror Picture Show",
        "Superstition Stevie Wonder",
        "Disturbia Rihanna",
        "Spooky Dusty Springfield",
        "The Addams Family theme song",
        "Dead Man's Party Oingo Boingo",
        "Heads Will Roll Yeah Yeah Yeahs",
    )

    private val ROCK_AND_ROLL_PICKS = listOf(
        "Jailhouse Rock Elvis Presley",
        "Hound Dog Elvis Presley",
        "Blue Suede Shoes Elvis Presley",
        "Johnny B. Goode Chuck Berry",
        "Tutti Frutti Little Richard",
        "Rock Around the Clock Bill Haley",
        "Peggy Sue Buddy Holly",
        "Great Balls of Fire Jerry Lee Lewis",
        "Blueberry Hill Fats Domino",
        "Be-Bop-A-Lula Gene Vincent",
        "Suspicious Minds Elvis Presley",
        "Can't Help Falling in Love Elvis Presley",
        "That's All Right Elvis Presley",
        "Good Golly Miss Molly Little Richard",
    )
}
