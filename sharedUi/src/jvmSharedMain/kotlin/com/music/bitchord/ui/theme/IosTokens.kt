package com.music.bitchord.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * The iOS 18 numbers, in one place.
 *
 * Kisetsu's screens had grown their own copies of the same greys and the same
 * 22sp bold heading, which is how a design system drifts: each screen is right
 * on its own and the app is wrong together. These are the reference values —
 * the ones Apple Music actually uses — so a new surface can be built from the
 * scale rather than from a screenshot.
 *
 * Nothing here is *required* by MaterialTheme; the app keeps its themes for
 * everything already built. This is the vocabulary for the surfaces that are
 * being brought up to parity, one at a time.
 */
object IosTokens {

    // --- Colour ---------------------------------------------------------

    /** Pure black canvas, as the OLED phones Apple targets. */
    val Canvas = Color(0xFF000000)

    /** Cards, sheets and modals: one step off the canvas, not grey. */
    val Elevated = Color(0xFF1C1C1E)

    /** The hairline that keeps [Elevated] from floating — 5% white. */
    val ElevatedStroke = Color(0x0DFFFFFF)

    /** Apple Music's signature pink/red. */
    val Accent = Color(0xFFFA2D48)

    /** Primary text. */
    val Label = Color(0xFFFFFFFF)

    /** Secondary metadata: `rgba(235,235,245,0.60)` over black. */
    val SecondaryLabel = Color(0x99EBEBF5)

    /** Placeholders and the quietest subtext: `rgba(235,235,245,0.30)`. */
    val TertiaryLabel = Color(0x4DEBEBF5)

    /** `rgba(235,235,245,0.55)` — the scrub timers, a shade under secondary. */
    val TimerLabel = Color(0x8CEBEBF5)

    /** Separators: `rgba(84,84,88,0.36)`. */
    val Separator = Color(0x5C545458)

    /** The floating bars: `rgba(25,25,25,0.75)`, with blur behind them. */
    val GlassBar = Color(0xBF191919)

    /** A frosted pill behind an active tool in the player's bottom deck. */
    val ActiveToolFill = Color(0x2EFFFFFF)

    /** The translucent fill on a secondary action button. */
    val SecondaryButtonFill = Color(0x26FFFFFF)

    // --- Type -----------------------------------------------------------
    //
    // Tracking is converted from the spec's px to Compose's sp (1px ≈ 0.23sp
    // at the 393dp reference width), which lands on the familiar iOS values:
    // 34sp/0.37px → 0.37sp, and so on. Line heights are the spec's 41/34/28…
    // expressed the way Compose wants them.

    /** 34pt bold — "Listen Now", "Search". */
    val LargeTitle = TextStyle(
        fontSize = 34.sp,
        lineHeight = 41.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.37.sp,
    )

    /** 28pt bold. */
    val Title1 = TextStyle(
        fontSize = 28.sp,
        lineHeight = 34.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.34.sp,
    )

    /** 22pt bold — album titles and section headers. */
    val Title2 = TextStyle(
        fontSize = 22.sp,
        lineHeight = 28.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.26).sp,
    )

    /** 20pt semibold — the track title on the player. */
    val Title3 = TextStyle(
        fontSize = 20.sp,
        lineHeight = 25.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.45).sp,
    )

    /** 17pt semibold — song list row titles. */
    val Headline = TextStyle(
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.43).sp,
    )

    /** 17pt regular. */
    val Body = TextStyle(
        fontSize = 17.sp,
        lineHeight = 22.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.43).sp,
    )

    /** 16pt regular. */
    val Callout = TextStyle(
        fontSize = 16.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.32).sp,
    )

    /** 15pt regular — artist names under a title. */
    val Subhead = TextStyle(
        fontSize = 15.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.24).sp,
    )

    /** 13pt regular — timers, durations, track numbers. */
    val Footnote = TextStyle(
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = (-0.08).sp,
    )

    /** 12pt regular — year, track count, runtimes. */
    val Caption1 = TextStyle(
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.sp,
    )

    /** 10pt medium — the bottom dock's labels. */
    val TabLabel = TextStyle(
        fontSize = 10.sp,
        lineHeight = 12.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.12.sp,
    )
}
