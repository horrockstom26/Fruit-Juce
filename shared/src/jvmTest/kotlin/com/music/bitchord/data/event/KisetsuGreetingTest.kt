package com.music.bitchord.data.event

import com.music.bitchord.data.event.KisetsuGreeting.Daypart
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar
import java.util.Date

/** The greeting switches on the hour, and the small hours get the moon. */
class KisetsuGreetingTest {

    private fun at(hour: Int): Date =
        Calendar.getInstance().apply {
            clear()
            set(2026, Calendar.OCTOBER, 28, hour, 30, 0)
        }.time

    @Test
    fun morning() {
        assertEquals(Daypart.MORNING, KisetsuGreeting.daypartAt(at(5)))
        assertEquals(Daypart.MORNING, KisetsuGreeting.daypartAt(at(11)))
        assertEquals("Good morning 🎃", KisetsuGreeting.lineAt(at(8), "🎃"))
    }

    @Test
    fun afternoon() {
        assertEquals(Daypart.AFTERNOON, KisetsuGreeting.daypartAt(at(12)))
        assertEquals("Good afternoon 🎃", KisetsuGreeting.lineAt(at(16), "🎃"))
    }

    @Test
    fun night() {
        assertEquals(Daypart.EVENING, KisetsuGreeting.daypartAt(at(18)))
        assertEquals("Good night 🎃", KisetsuGreeting.lineAt(at(21), "🎃"))
    }

    @Test
    fun small_hours_offer_an_all_nighter_and_a_moon() {
        assertEquals(Daypart.NIGHT, KisetsuGreeting.daypartAt(at(23)))
        assertEquals(Daypart.NIGHT, KisetsuGreeting.daypartAt(at(0)))
        assertEquals(Daypart.NIGHT, KisetsuGreeting.daypartAt(at(4)))
        assertEquals("Fancy an all-nighter? 🌙", KisetsuGreeting.lineAt(at(3), "🎃"))
        assertEquals("Fancy an all-nighter? 🌙", KisetsuGreeting.lineAt(at(3), "🌸"))
    }
}
