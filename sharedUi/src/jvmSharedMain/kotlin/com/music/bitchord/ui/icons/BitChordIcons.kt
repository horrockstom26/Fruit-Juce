package com.music.bitchord.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Thick-stroke, round-capped icons in the spirit of Telegram's modern icon set.
 * Drawn as strokes so the 2.2px weight + round joins read as a single polished
 * family. Tint is applied by [androidx.compose.material3.Icon].
 *
 * [Home] is the exception and says why it is one.
 */
object BitChordIcons {

    private const val STROKE = 2.2f
    private val stroke = SolidColor(Color.Black)

    /** Compact list and quotation-bubble shapes used by the player controls. */
    val Queue: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_queue", defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(stroke = stroke, strokeLineWidth = 2f, strokeLineCap = StrokeCap.Round) {
                for (y in listOf(6f, 12f, 18f)) {
                    moveTo(8f, y)
                    lineTo(21f, y)
                }
            }
            path(fill = stroke) {
                for (y in listOf(6f, 12f, 18f)) {
                    moveTo(4.35f, y)
                    arcToRelative(1.25f, 1.25f, 0f, true, true, -2.5f, 0f)
                    arcToRelative(1.25f, 1.25f, 0f, true, true, 2.5f, 0f)
                    close()
                }
            }
        }.build()
    }

    val LyricsQuote: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_lyrics_quote", defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke, strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round, strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(6f, 3.5f)
                lineTo(18f, 3.5f)
                quadTo(21f, 3.5f, 21f, 6.5f)
                lineTo(21f, 15f)
                quadTo(21f, 18f, 18f, 18f)
                lineTo(10f, 18f)
                lineTo(6f, 21f)
                lineTo(6f, 18f)
                quadTo(3f, 18f, 3f, 15f)
                lineTo(3f, 6.5f)
                quadTo(3f, 3.5f, 6f, 3.5f)
                close()
            }
            path(fill = stroke) {
                for (x in listOf(7.6f, 12.8f)) {
                    moveTo(x, 8f)
                    lineTo(x + 3f, 8f)
                    lineTo(x + 3f, 11.1f)
                    quadTo(x + 3f, 13.2f, x + 0.5f, 13.7f)
                    lineTo(x + 0.5f, 12.3f)
                    quadTo(x + 1.7f, 12f, x + 1.7f, 11f)
                    lineTo(x, 11f)
                    close()
                }
            }
        }.build()
    }

    val Play: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_play",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                fill = stroke,
            ) {
                moveTo(6.8f, 4.8f)
                lineTo(19.2f, 12f)
                lineTo(6.8f, 19.2f)
                close()
            }
        }.build()
    }

    /**
     * The dock's Home, redrawn for v7.3 against Apple Music's own tab bar: a
     * solid house, a shade wider than it is tall, with a door notch cut up out
     * of the floor.
     *
     * Filled *and* stroked, with the same path. The outline of a house is all
     * corners — an apex, two shoulders, two door jambs — and stroking the fill
     * with a round-joined pen is what rounds every one of them at once, for a
     * geometry that stays nine straight lines. The numbers below are therefore
     * inset by half the stroke: the pen puts back what they give up, and the
     * door notch is drawn oversized for the same reason, since the pen closes
     * in on it from both jambs.
     */
    val Home: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_home",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                fill = stroke,
                stroke = stroke,
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Roof, flush with the walls rather than overhanging them: an
                // eave narrower than the pen it is drawn with comes out as a
                // blob on the corner instead of a ledge.
                moveTo(12f, 3.6f)
                lineTo(20.4f, 11.2f)
                // Right wall, down to the floor
                lineTo(20.4f, 20.1f)
                // Door, cut up out of the bottom edge
                lineTo(14.75f, 20.1f)
                lineTo(14.75f, 15.55f)
                lineTo(9.25f, 15.55f)
                lineTo(9.25f, 20.1f)
                // Left wall, back up to the apex
                lineTo(3.6f, 20.1f)
                lineTo(3.6f, 11.2f)
                close()
            }
        }.build()
    }

    /**
     * The preview bar's Explore glyph — the 2x2 grid, kept for the debug
     * previews that still show it. The live tab bar draws [TabCreate] instead.
     *
     * Taken from the outline of Flutter's cupertino_icons font (MIT),
     * square_grid_2x2, and laid out the way the font sets it: the 512-unit em
     * box is the viewport, the baseline sits at the font's ascent share of it,
     * and the advance is centred.
     */
    val TabExplore: ImageVector by lazy {
        fontGlyph(
            "bc_tab_explore",
            "M99.5 230.24Q58.5 230.24 58.5 188.24V93.24Q58.5 51.24 99.5 51.24H197.5Q238.5 51.24 238.5 93.24V188.24Q238.5 230.24 197.5 230.24Z" +
                "M314.5 230.24Q273.5 230.24 273.5 188.24V93.24Q273.5 51.24 314.5 51.24H412.5Q453.5 51.24 453.5 93.24V188.24Q453.5 230.24 412.5 230.24Z" +
                "M99.5 200.24H196.5Q207.5 200.24 207.5 188.24V93.24Q207.5 81.24 196.5 81.24H99.5Q88.5 81.24 88.5 93.24V188.24Q88.5 200.24 99.5 200.24Z" +
                "M314.5 200.24H411.5Q422.5 200.24 422.5 188.24V93.24Q422.5 81.24 411.5 81.24H314.5Q303.5 81.24 303.5 93.24V188.24Q303.5 200.24 314.5 200.24Z" +
                "M99.5 445.24Q58.5 445.24 58.5 403.24V307.24Q58.5 266.24 99.5 266.24H197.5Q238.5 266.24 238.5 307.24V403.24Q238.5 445.24 197.5 445.24Z" +
                "M314.5 445.24Q273.5 445.24 273.5 403.24V307.24Q273.5 266.24 314.5 266.24H412.5Q453.5 266.24 453.5 307.24V403.24Q453.5 445.24 412.5 445.24Z" +
                "M99.5 415.24H196.5Q207.5 415.24 207.5 403.24V308.24Q207.5 296.24 196.5 296.24H99.5Q88.5 296.24 88.5 308.24V403.24Q88.5 415.24 99.5 415.24Z" +
                "M314.5 415.24H411.5Q422.5 415.24 422.5 403.24V308.24Q422.5 296.24 411.5 296.24H314.5Q303.5 296.24 303.5 308.24V403.24Q303.5 415.24 314.5 415.24Z",
        )
    }

    /**
     * The Kisetsu v7.3 tab family — Home, Create, Library, Account, Search —
     * redrawn from scratch against Apple Music's dock rather than resized from
     * the Cupertino font.
     *
     * One 24-unit box, one pen weight, one corner radius: the five are a set at
     * the bar's 25dp optical size in a way a mixed bag of borrowed outlines was
     * not. Each is filled *and* stroked with the same round-joined pen, the
     * trick [Home] uses, so the fill carries the weight and the pen rounds
     * every corner the geometry has.
     */

    /** Four rounded squares — Apple Music's New mark, standing in for Create. */
    val TabCreate: ImageVector by lazy {
        filledGlyph(
            "bc_tab_create",
            "M5 3H8.4Q10.4 3 10.4 5V8.4Q10.4 10.4 8.4 10.4H5Q3 10.4 3 8.4V5Q3 3 5 3Z" +
                "M15.6 3H19Q21 3 21 5V8.4Q21 10.4 19 10.4H15.6Q13.6 10.4 13.6 8.4V5Q13.6 3 15.6 3Z" +
                "M5 13.6H8.4Q10.4 13.6 10.4 15.6V19Q10.4 21 8.4 21H5Q3 21 3 19V15.6Q3 13.6 5 13.6Z" +
                "M15.6 13.6H19Q21 13.6 21 15.6V19Q21 21 19 21H15.6Q13.6 21 13.6 19V15.6Q13.6 13.6 15.6 13.6Z",
        )
    }

    /**
     * A solid tile with a quarter note punched out of it — Apple Music's
     * Library. The tile is filled and the note is a second, opposite subpath
     * under [PathFillType.EvenOdd], so the note shows the bar behind it rather
     * than being tinted a second colour the app has no way to supply.
     */
    val TabLibrary: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_tab_library",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(fill = stroke, pathFillType = PathFillType.EvenOdd) {
                // Tile: a rounded square, inset so the pen's growth lands it
                // on the same optical edge as the other four.
                moveTo(6.1f, 2.9f)
                lineTo(17.9f, 2.9f)
                quadTo(21.1f, 2.9f, 21.1f, 6.1f)
                lineTo(21.1f, 17.9f)
                quadTo(21.1f, 21.1f, 17.9f, 21.1f)
                lineTo(6.1f, 21.1f)
                quadTo(2.9f, 21.1f, 2.9f, 17.9f)
                lineTo(2.9f, 6.1f)
                quadTo(2.9f, 2.9f, 6.1f, 2.9f)
                close()
                // Note: stem down the right side, then the head as four quads.
                // One subpath, so the even-odd rule leaves exactly one hole.
                moveTo(10.55f, 7.9f)
                lineTo(11.55f, 7.9f)
                lineTo(11.55f, 16.1f)
                quadTo(11.55f, 17.95f, 9.35f, 17.95f)
                quadTo(7.15f, 17.95f, 7.15f, 16.1f)
                quadTo(7.15f, 14.25f, 9.35f, 14.25f)
                quadTo(10.55f, 14.25f, 10.55f, 14.6f)
                close()
            }
        }.build()
    }

    /**
     * A person in a disc — the account stands where Apple Music puts its
     * profile button.
     *
     * v7.4: filled disc, person knocked out of it under [PathFillType.EvenOdd].
     * The earlier ring-and-figure left the person floating small in a void —
     * at 25dp the head was a dot and the shoulders stopped well short of the
     * rim. Knocking the figure out of a solid disc is the same mark Apple's
     * `person.crop.circle.fill` draws, and it fills the box by definition.
     */
    val TabAccount: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_tab_account",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(fill = stroke, pathFillType = PathFillType.EvenOdd) {
                // The disc.
                moveTo(21.6f, 12f)
                arcToRelative(9.6f, 9.6f, 0f, isMoreThanHalf = true, isPositiveArc = true, -19.2f, 0f)
                arcToRelative(9.6f, 9.6f, 0f, isMoreThanHalf = true, isPositiveArc = true, 19.2f, 0f)
                close()
                // Head, a good share of the disc's width.
                moveTo(15.5f, 9f)
                arcToRelative(3.5f, 3.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -7f, 0f)
                arcToRelative(3.5f, 3.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 7f, 0f)
                close()
                // Shoulders. The feet stop on the disc's own chord at this
                // height rather than past it: the even-odd rule fills whatever
                // is outside the disc and inside this subpath, so a shoulder
                // that overshot the rim would paint a bar across the tab.
                moveTo(7.3f, 20.2f)
                quadTo(7.9f, 14.7f, 12f, 14.7f)
                quadTo(16.1f, 14.7f, 16.7f, 20.2f)
                close()
            }
        }.build()
    }

    /**
     * A magnifier — Apple Music's Search.
     *
     * v7.4: the handle leaves from the lens's rim instead of starting inside
     * it. The old handle began a full unit in from the edge at a 45° angle,
     * which read as a stem driven through the glass; here the ring and the
     * handle meet at the one point they should.
     */
    val TabSearch: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_tab_search",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = 2.1f,
            ) {
                moveTo(17f, 10.8f)
                arcToRelative(6.2f, 6.2f, 0f, isMoreThanHalf = true, isPositiveArc = true, -12.4f, 0f)
                arcToRelative(6.2f, 6.2f, 0f, isMoreThanHalf = true, isPositiveArc = true, 12.4f, 0f)
            }
            path(
                stroke = stroke,
                strokeLineWidth = 2.3f,
                strokeLineCap = StrokeCap.Round,
            ) {
                // Starts on the rim at 45° — 10.8 + 6.2·cos45 — and runs out at
                // the same angle, so the two strokes meet flush.
                moveTo(15.35f, 15.35f)
                lineTo(20.4f, 20.4f)
            }
        }.build()
    }

    /**
     * A filled outline in the 24-unit tab box — see the family note above.
     * [TabCreate] is the only one that needs nothing but fill.
     */
    private fun filledGlyph(name: String, pathData: String): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).addPath(
            pathData = addPathNodes(pathData),
            fill = stroke,
            stroke = stroke,
            strokeLineWidth = 1.3f,
            strokeLineJoin = StrokeJoin.Round,
        ).build()

    /** A filled glyph outline in the 512-unit em box — see [TabExplore]. */
    private fun fontGlyph(name: String, pathData: String): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 512f, viewportHeight = 512f,
        ).addPath(pathData = addPathNodes(pathData), fill = stroke).build()

    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_search",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Lens (full circle from two arcs)
                moveTo(4.6f, 11f)
                arcToRelative(6.4f, 6.4f, 0f, isMoreThanHalf = true, isPositiveArc = true, 12.8f, 0f)
                arcToRelative(6.4f, 6.4f, 0f, isMoreThanHalf = true, isPositiveArc = true, -12.8f, 0f)
                // Handle
                moveTo(15.9f, 15.9f)
                lineTo(20.4f, 20.4f)
            }
        }.build()
    }

    val Explore: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_explore",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Compass dial
                moveTo(3.4f, 12f)
                arcToRelative(8.6f, 8.6f, 0f, isMoreThanHalf = true, isPositiveArc = true, 17.2f, 0f)
                arcToRelative(8.6f, 8.6f, 0f, isMoreThanHalf = true, isPositiveArc = true, -17.2f, 0f)
                // Needle
                moveTo(15.4f, 8.6f)
                lineTo(13.6f, 13.6f)
                lineTo(8.6f, 15.4f)
                lineTo(10.4f, 10.4f)
                close()
            }
        }.build()
    }

    val Shuffle: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_shuffle",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Strand that crosses downwards, with its arrow head
                moveTo(3.4f, 7.4f); lineTo(7f, 7.4f); lineTo(16.6f, 16.6f); lineTo(20.6f, 16.6f)
                moveTo(18.1f, 14.1f); lineTo(20.6f, 16.6f); lineTo(18.1f, 19.1f)
                // Strand that crosses upwards, broken around the intersection
                moveTo(3.4f, 16.6f); lineTo(7f, 16.6f); lineTo(9.8f, 13.9f)
                moveTo(13.9f, 10.1f); lineTo(16.6f, 7.4f); lineTo(20.6f, 7.4f)
                moveTo(18.1f, 4.9f); lineTo(20.6f, 7.4f); lineTo(18.1f, 9.9f)
            }
        }.build()
    }

    val Repeat: ImageVector by lazy { repeatLoop("bc_repeat") }

    /**
     * Two straight runs joined by semicircles, with the arrow heads lying flat
     * at the ends of the straights. Putting them on the curves instead — as a
     * first pass did — makes the glyph read as a refresh/sync symbol.
     */
    private fun repeatLoop(name: String): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(8.6f, 7.6f)
                lineTo(15.4f, 7.6f)
                arcToRelative(4.4f, 4.4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 8.8f)
                lineTo(8.6f, 16.4f)
                arcToRelative(4.4f, 4.4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -8.8f)
                close()
                // Direction of travel: right along the top, left along the bottom.
                moveTo(13.5f, 5.7f); lineTo(15.4f, 7.6f); lineTo(13.5f, 9.5f)
                moveTo(10.5f, 14.5f); lineTo(8.6f, 16.4f); lineTo(10.5f, 18.3f)
            }
        }.build()

    /** AutoPlay's lemniscate. */
    val Infinity: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_infinity",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 12f)
                curveTo(10.1f, 9.1f, 8.7f, 8f, 7.1f, 8f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 8f)
                curveTo(8.7f, 16f, 10.1f, 14.9f, 12f, 12f)
                curveTo(13.9f, 9.1f, 15.3f, 8f, 16.9f, 8f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 8f)
                curveTo(15.3f, 16f, 13.9f, 14.9f, 12f, 12f)
            }
        }.build()
    }

    /** Beamed pair of notes, for instrumental stretches in the lyrics. */
    val MusicNote: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_music_note",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            // Heads are solid; stems and beam keep the family's stroke weight.
            path(fill = stroke) {
                moveTo(4.2f, 17.7f)
                arcToRelative(2.9f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 5.8f, 0f)
                arcToRelative(2.9f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -5.8f, 0f)
                close()
                moveTo(14.2f, 15.9f)
                arcToRelative(2.9f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, 5.8f, 0f)
                arcToRelative(2.9f, 2.5f, 0f, isMoreThanHalf = true, isPositiveArc = true, -5.8f, 0f)
                close()
            }
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(10f, 17.7f); lineTo(10f, 6.7f)
                moveTo(20f, 15.9f); lineTo(20f, 4.9f)
                moveTo(10f, 6.7f); lineTo(20f, 4.9f)
            }
        }.build()
    }

    /** Speech bubble with two lines of words. */
    val Lyrics: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_lyrics",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(6.2f, 4.6f)
                lineTo(17.8f, 4.6f)
                arcToRelative(2.8f, 2.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.8f, 2.8f)
                lineTo(20.6f, 13.6f)
                arcToRelative(2.8f, 2.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.8f, 2.8f)
                lineTo(10.6f, 16.4f)
                lineTo(6.8f, 19.6f)
                lineTo(6.8f, 16.4f)
                lineTo(6.2f, 16.4f)
                arcToRelative(2.8f, 2.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2.8f, -2.8f)
                lineTo(3.4f, 7.4f)
                arcToRelative(2.8f, 2.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2.8f, -2.8f)
                close()
                moveTo(7.6f, 9f); lineTo(16.4f, 9f)
                moveTo(7.6f, 12.1f); lineTo(13.2f, 12.1f)
            }
        }.build()
    }

    /** Plain chevron — a disclosure hint, not a directional arrow. */
    val ChevronRight: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_chevron_right",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(9.5f, 6.2f)
                lineTo(15.3f, 12f)
                lineTo(9.5f, 17.8f)
            }
        }.build()
    }

    /**
     * The player's like control, in two weights.
     *
     * Filled rather than merely tinted when set: the player draws every glyph
     * white on artwork, where a colour change alone is the one signal the
     * backdrop can swallow. A shape change survives any album cover.
     */
    val Heart: ImageVector by lazy { heart("bc_heart", filled = false) }

    val HeartFilled: ImageVector by lazy { heart("bc_heart_filled", filled = true) }

    private fun heart(name: String, filled: Boolean): ImageVector =
        ImageVector.Builder(
            name = name,
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
                fill = if (filled) stroke else null,
            ) {
                // Two lobes meeting at the top notch, falling to a single point.
                moveTo(12f, 20f)
                curveTo(12f, 20f, 3.2f, 14.6f, 3.2f, 8.9f)
                arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.8f, -1.5f)
                arcToRelative(4.5f, 4.5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 8.8f, 1.5f)
                curveTo(20.8f, 14.6f, 12f, 20f, 12f, 20f)
                close()
            }
        }.build()

    /** Adding something — a new playlist, on the library shelf. */
    val Plus: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_plus",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 5f); lineTo(12f, 19f)
                moveTo(5f, 12f); lineTo(19f, 12f)
            }
        }.build()
    }

    /**
     * A tick — the other half of [Plus]. Saving something to the library swaps
     * one for the other in place, so the two are drawn on the same 14-unit span
     * and at the same weight; a tick sized to its own bounding box would jump.
     */
    val Check: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_check",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(5f, 12.8f); lineTo(9.6f, 17.4f); lineTo(19f, 6.9f)
            }
        }.build()
    }

    /** Arrow pointing down into a tray — offline download. */
    val Download: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_download",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Vertical stem
                moveTo(12f, 4.5f); lineTo(12f, 15.5f)
                // Arrow head
                moveTo(7.5f, 11f); lineTo(12f, 15.5f); lineTo(16.5f, 11f)
                // Tray base
                moveTo(4.5f, 18f); lineTo(19.5f, 18f)
            }
        }.build()
    }

    /**
     * Clock face with two hands — a download asked for but not yet on disk.
     *
     * The dial is the same 8.6 radius as [Explore]'s, so the two sit at the same
     * optical weight when they appear in the same row of header buttons. The
     * hands are one polyline through the centre rather than two strokes: the
     * round join is then the pivot, which is what stops the middle reading as a
     * pair of lines that happen to cross.
     */
    val Clock: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_clock",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Dial (full circle from two arcs)
                moveTo(3.4f, 12f)
                arcToRelative(8.6f, 8.6f, 0f, isMoreThanHalf = true, isPositiveArc = true, 17.2f, 0f)
                arcToRelative(8.6f, 8.6f, 0f, isMoreThanHalf = true, isPositiveArc = true, -17.2f, 0f)
                // Minute hand up, hour hand down to the right
                moveTo(12f, 7.4f); lineTo(12f, 12f); lineTo(15.4f, 13.8f)
            }
        }.build()
    }

    /**
     * A pushpin, not a map marker: a round head with a straight needle
     * kicked out to one side, the way it sits once it's stuck into something.
     * A teardrop reads as "location" the moment it's this small — the two
     * disjoint strokes here are what keep it read as "pinned" instead.
     */
    val Pin: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_pin",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Round head.
                moveTo(9.6f, 8.4f)
                arcToRelative(3.4f, 3.4f, 0f, isMoreThanHalf = true, isPositiveArc = true, 6.8f, 0f)
                arcToRelative(3.4f, 3.4f, 0f, isMoreThanHalf = true, isPositiveArc = true, -6.8f, 0f)
                // Needle, kicked out from the underside of the head to a point.
                moveTo(10.8f, 10.8f)
                lineTo(5.6f, 19.6f)
            }
        }.build()
    }

    /** Speed gauge used by high-performance display settings. */
    val Performance: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_performance",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Open gauge, leaving room for the base line.
                moveTo(4.1f, 16.8f)
                arcToRelative(8.4f, 8.4f, 0f, isMoreThanHalf = true, isPositiveArc = true, 15.8f, 0f)
                // Needle and hub.
                moveTo(12f, 15.8f)
                lineTo(16.7f, 9.3f)
                moveTo(10.9f, 16.9f)
                arcToRelative(1.1f, 1.1f, 0f, isMoreThanHalf = true, isPositiveArc = true, 2.2f, 0f)
                arcToRelative(1.1f, 1.1f, 0f, isMoreThanHalf = true, isPositiveArc = true, -2.2f, 0f)
                // Stable baseline.
                moveTo(5.4f, 19.5f)
                lineTo(18.6f, 19.5f)
            }
        }.build()
    }

    /** Four frame-time bars, used by refresh-rate selection. */
    val FrameRate: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_frame_rate",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(5f, 15.5f); lineTo(5f, 8.5f)
                moveTo(9.7f, 18.5f); lineTo(9.7f, 5.5f)
                moveTo(14.3f, 16.5f); lineTo(14.3f, 7.5f)
                moveTo(19f, 14.5f); lineTo(19f, 9.5f)
            }
        }.build()
    }

    val Library: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_library",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Three upright spines + one leaning — Apple Music's library glyph, thickened
                moveTo(4.6f, 4.8f); lineTo(4.6f, 19.2f)
                moveTo(9.2f, 4.8f); lineTo(9.2f, 19.2f)
                moveTo(13.8f, 4.8f); lineTo(13.8f, 19.2f)
                moveTo(17.2f, 5.6f); lineTo(20.6f, 18.9f)
            }
        }.build()
    }

    /** 2x2 grid icon (4 rounded squares) for switching layout to grid view. */
    val GridView: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_grid_view",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Top-left
                moveTo(6.5f, 4.5f)
                lineTo(8.5f, 4.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 2f)
                lineTo(10.5f, 8.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
                lineTo(6.5f, 10.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, -2f)
                lineTo(4.5f, 6.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
                close()

                // Top-right
                moveTo(15.5f, 4.5f)
                lineTo(17.5f, 4.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 2f)
                lineTo(19.5f, 8.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
                lineTo(15.5f, 10.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, -2f)
                lineTo(13.5f, 6.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
                close()

                // Bottom-left
                moveTo(6.5f, 13.5f)
                lineTo(8.5f, 13.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 2f)
                lineTo(10.5f, 17.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
                lineTo(6.5f, 19.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, -2f)
                lineTo(4.5f, 15.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
                close()

                // Bottom-right
                moveTo(15.5f, 13.5f)
                lineTo(17.5f, 13.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, 2f)
                lineTo(19.5f, 17.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
                lineTo(15.5f, 19.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, -2f)
                lineTo(13.5f, 15.5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
                close()
            }
        }.build()
    }

    /** List view icon (horizontal rows) for switching layout to list view. */
    val ListView: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_list_view",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = STROKE,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Three horizontal lines with bullet markers
                moveTo(4.5f, 6.5f); lineTo(5.5f, 6.5f)
                moveTo(9.5f, 6.5f); lineTo(19.5f, 6.5f)

                moveTo(4.5f, 12f); lineTo(5.5f, 12f)
                moveTo(9.5f, 12f); lineTo(19.5f, 12f)

                moveTo(4.5f, 17.5f); lineTo(5.5f, 17.5f)
                moveTo(9.5f, 17.5f); lineTo(19.5f, 17.5f)
            }
        }.build()
    }

    // ---- Player -----------------------------------------------------------
    //
    // Hand-built lookalikes for the Now Playing screen: one optical family for
    // the transport, bottom row, volume, like and menu controls. These are
    // vectors rather than vendored drawables so their weights, corners and
    // proportions can move together. They deliberately are not SF Symbols.

    /** A filled play triangle, with rounded points. */
    val PlayerPlay: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_play",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                // Stroke thickened for v4: half the stroke width is the
                // rounding radius on every join and cap, so the play wedge's
                // points land where Apple's do — generous, not needle-sharp.
                fill = stroke,
                stroke = stroke,
                strokeLineWidth = 2.5f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(4.8f, 1.3f)
                lineTo(21.0f, 12f)
                lineTo(4.8f, 22.6f)
                close()
            }
        }.build()
    }

    /** Two wide, rounded pause bars, matched to [PlayerPlay]. v5: shortened
     *  and pulled in so the stroke's own round caps do the corner rounding —
     *  a pause bar is a rounded rectangle, not a sharp-edged slab. */
    val PlayerPause: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_pause",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                fill = stroke,
                stroke = stroke,
                // Half the stroke (~2.9dp at display size) is every corner's
                // rounding radius: caps and joins ride the rect edges.
                strokeLineWidth = 3.6f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(2.6f, 2.2f)
                lineTo(9.0f, 2.2f)
                lineTo(9.0f, 21.8f)
                lineTo(2.6f, 21.8f)
                close()
                moveTo(15.0f, 2.2f)
                lineTo(21.4f, 2.2f)
                lineTo(21.4f, 21.8f)
                lineTo(15.0f, 21.8f)
                close()
            }
        }.build()
    }

    /**
     * Step back — Apple Music's shape: two left-pointing triangles with rounded
     * points, closed by a rounded bar at the outer edge.
     *
     * Redrawn from scratch for v7.3 rather than reused from the vendored
     * `apple_skip_previous` drawable. The old art had needle-sharp apexes and a
     * bar flush with the triangles; at the player's 46dp optical size the pair
     * read as a solid wedge. The rounded pen here is the same one [PlayerPlay]
     * uses, so the whole transport shares one weight.
     */
    val PlayerSkipPrevious: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_skip_previous",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            // Outer bar. Round cap, so its ends are as soft as the triangles.
            path(
                stroke = stroke,
                strokeLineWidth = 2.6f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(3.4f, 5.2f)
                lineTo(3.4f, 18.8f)
            }
            path(
                fill = stroke,
                stroke = stroke,
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                // Right triangle, apex left.
                moveTo(21.4f, 5.4f)
                lineTo(12.5f, 12f)
                lineTo(21.4f, 18.6f)
                close()
                // Left triangle, apex against the bar.
                moveTo(12.4f, 5.4f)
                lineTo(4.9f, 12f)
                lineTo(12.4f, 18.6f)
                close()
            }
        }.build()
    }

    /** The mirror of [PlayerSkipPrevious]. */
    val PlayerSkipNext: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_skip_next",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = 2.6f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(20.6f, 5.2f)
                lineTo(20.6f, 18.8f)
            }
            path(
                fill = stroke,
                stroke = stroke,
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(2.6f, 5.4f)
                lineTo(11.5f, 12f)
                lineTo(2.6f, 18.6f)
                close()
                moveTo(11.6f, 5.4f)
                lineTo(19.1f, 12f)
                lineTo(11.6f, 18.6f)
                close()
            }
        }.build()
    }

    /** Three rows of a dot and a line — Apple Music's queue mark. */
    val PlayerQueue: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_queue",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
            ) {
                for (y in listOf(6.4f, 12f, 17.6f)) {
                    moveTo(8.7f, y)
                    lineTo(20.5f, y)
                }
            }
            path(fill = stroke) {
                for (y in listOf(6.4f, 12f, 17.6f)) {
                    circle(4.7f, y, 1.0f)
                }
            }
        }.build()
    }

    /** A rounded quote bubble with two marks: the lyrics button. */
    val PlayerLyrics: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_lyrics",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = 2.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(7.2f, 4.4f)
                lineTo(16.8f, 4.4f)
                quadTo(20.6f, 4.4f, 20.6f, 8.2f)
                lineTo(20.6f, 14.4f)
                quadTo(20.6f, 18.2f, 16.8f, 18.2f)
                lineTo(10.8f, 18.2f)
                lineTo(6.9f, 21.6f)
                lineTo(6.9f, 18.2f)
                quadTo(3.4f, 18.2f, 3.4f, 14.4f)
                lineTo(3.4f, 8.2f)
                quadTo(3.4f, 4.4f, 7.2f, 4.4f)
                close()
            }
            path(fill = stroke) {
                quoteMark(7.6f)
                quoteMark(12.4f)
            }
        }.build()
    }

    /** Three dots, with no disc of their own — [CircleGlyph] paints that. */
    val PlayerEllipsis: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_ellipsis",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(fill = stroke) {
                circle(5.9f, 12f, 2.15f)
                circle(12f, 12f, 2.15f)
                circle(18.1f, 12f, 2.15f)
            }
        }.build()
    }

    /** Apple's sharp, five-point outline star. */
    val PlayerStar: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_star",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = 1.8f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                star()
            }
        }.build()
    }

    /** The filled state of [PlayerStar]. */
    val PlayerStarFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_star_filled",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                fill = stroke,
                stroke = stroke,
                strokeLineWidth = 1.2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                star()
            }
        }.build()
    }

    /** A heart with soft round lobes and joins — the like button's shape. */
    val PlayerHeart: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_heart",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = 2.3f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                heart()
            }
        }.build()
    }

    /** The filled state of [PlayerHeart]. */
    val PlayerHeartFilled: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_heart_filled",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                fill = stroke,
                stroke = stroke,
                strokeLineWidth = 1.6f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                heart()
            }
        }.build()
    }

    /** AirPlay: concentric rings with a filled triangle at the base. */
    val PlayerAirPlay: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_airplay",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = 2.1f,
            ) {
                circle(12f, 11.8f, 9.2f)
                circle(12f, 11.8f, 5.8f)
                circle(12f, 11.8f, 2.8f)
            }
            path(
                fill = stroke,
                stroke = stroke,
                strokeLineWidth = 1.2f,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(7.0f, 20.9f)
                lineTo(17.0f, 20.9f)
                lineTo(12f, 12.2f)
                close()
            }
        }.build()
    }

    /** A small filled speaker cone, the body shared by the volume glyphs. */
    val PlayerVolumeLow: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_volume_low",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                fill = stroke,
                stroke = stroke,
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                speakerBody()
            }
        }.build()
    }

    /** The low speaker plus two sound-wave arcs. */
    val PlayerVolumeHigh: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_volume_high",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                fill = stroke,
                stroke = stroke,
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                speakerBody()
            }
            path(
                stroke = stroke,
                strokeLineWidth = 1.7f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(14.4f, 8.9f)
                quadTo(16.5f, 12f, 14.4f, 15.1f)
                moveTo(17.5f, 6.6f)
                quadTo(20.8f, 12f, 17.5f, 17.4f)
            }
        }.build()
    }

    /** The low speaker with a small cross where the waves would be. */
    val PlayerVolumeMuted: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_volume_muted",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                fill = stroke,
                stroke = stroke,
                strokeLineWidth = 1.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                speakerBody()
            }
            path(
                stroke = stroke,
                strokeLineWidth = 1.7f,
                strokeLineCap = StrokeCap.Round,
            ) {
                moveTo(14.9f, 9.8f)
                lineTo(19.1f, 14.2f)
                moveTo(19.1f, 9.8f)
                lineTo(14.9f, 14.2f)
            }
        }.build()
    }

    /** Two crossing arrows, matching the wider, lighter player row. */
    val PlayerShuffle: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_shuffle",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(3.3f, 7.2f)
                lineTo(6.7f, 7.2f)
                lineTo(17.3f, 16.8f)
                lineTo(19.2f, 16.8f)
                moveTo(3.3f, 16.8f)
                lineTo(6.7f, 16.8f)
                lineTo(9.4f, 14.4f)
                moveTo(13.9f, 9.6f)
                lineTo(17.3f, 7.2f)
                lineTo(19.2f, 7.2f)
            }
            path(fill = stroke) {
                moveTo(17.7f, 5.0f); lineTo(21.0f, 7.2f); lineTo(17.7f, 9.4f); close()
                moveTo(17.7f, 14.6f); lineTo(21.0f, 16.8f); lineTo(17.7f, 19.0f); close()
            }
        }.build()
    }

    /** A repeat loop with arrowheads on its straight runs. */
    val PlayerRepeat: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_repeat",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(8.2f, 7.2f)
                lineTo(16.0f, 7.2f)
                arcToRelative(4.8f, 4.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 9.6f)
                lineTo(8.2f, 16.8f)
                arcToRelative(4.8f, 4.8f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -9.6f)
                close()
            }
            path(fill = stroke) {
                moveTo(14.0f, 5.0f); lineTo(17.4f, 7.2f); lineTo(14.0f, 9.4f); close()
                moveTo(10.4f, 14.6f); lineTo(7.0f, 16.8f); lineTo(10.4f, 19.0f); close()
            }
        }.build()
    }

    /** Autoplay's lemniscate, widened to sit with [PlayerRepeat]. */
    val PlayerAutoplay: ImageVector by lazy {
        ImageVector.Builder(
            name = "bc_player_autoplay",
            defaultWidth = 24.dp, defaultHeight = 24.dp,
            viewportWidth = 24f, viewportHeight = 24f,
        ).apply {
            path(
                stroke = stroke,
                strokeLineWidth = 1.9f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(12f, 12f)
                curveTo(10.12f, 9.1f, 8.7f, 8f, 7.1f, 8f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = false, 0f, 8f)
                curveTo(8.7f, 16f, 10.12f, 14.9f, 12f, 12f)
                curveTo(13.88f, 9.1f, 15.3f, 8f, 16.9f, 8f)
                arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 8f)
                curveTo(15.3f, 16f, 13.88f, 14.9f, 12f, 12f)
            }
        }.build()
    }

    private fun PathBuilder.circle(cx: Float, cy: Float, radius: Float) {
        moveTo(cx - radius, cy)
        arcToRelative(radius, radius, 0f, isMoreThanHalf = true, isPositiveArc = true, radius * 2f, 0f)
        arcToRelative(radius, radius, 0f, isMoreThanHalf = true, isPositiveArc = true, -radius * 2f, 0f)
        close()
    }

    private fun PathBuilder.speakerBody() {
        moveTo(3.3f, 9.2f)
        lineTo(5.9f, 9.2f)
        lineTo(10.4f, 5.5f)
        lineTo(10.4f, 18.5f)
        lineTo(5.9f, 14.8f)
        lineTo(3.3f, 14.8f)
        close()
    }

    private fun PathBuilder.quoteMark(x: Float) {
        moveTo(x + 0.5f, 9.0f)
        curveTo(x + 2.2f, 9.0f, x + 2.6f, 10.0f, x + 2.6f, 10.9f)
        curveTo(x + 2.6f, 12.3f, x + 1.7f, 13.4f, x + 0.5f, 14.2f)
        lineTo(x - 0.2f, 13.4f)
        curveTo(x + 0.8f, 12.7f, x + 1.2f, 12.0f, x + 1.2f, 11.3f)
        lineTo(x + 0.5f, 11.3f)
        curveTo(x - 0.3f, 11.3f, x - 0.3f, 10.5f, x - 0.3f, 10.1f)
        curveTo(x - 0.3f, 9.4f, x + 0.1f, 9.0f, x + 0.5f, 9.0f)
        close()
    }

    private fun PathBuilder.star() {
        val cx = 12f
        val cy = 12.3f
        val outer = 10.3f
        val inner = 4.35f
        moveTo(cx, cy - outer)
        for (index in 1..10) {
            val radius = if (index % 2 == 1) inner else outer
            val angle = -PI / 2 + index * PI / 5
            lineTo(
                (cx + radius * cos(angle)).toFloat(),
                (cy + radius * sin(angle)).toFloat(),
            )
        }
        close()
    }

    /** A heart: two round lobes meeting in a soft point at the bottom. */
    private fun PathBuilder.heart() {
        moveTo(12f, 21.35f)
        lineTo(10.55f, 20.03f)
        curveTo(5.4f, 15.36f, 2f, 12.28f, 2f, 8.5f)
        curveTo(2f, 5.42f, 4.42f, 3f, 7.5f, 3f)
        curveTo(9.24f, 3f, 10.91f, 3.81f, 12f, 5.09f)
        curveTo(13.09f, 3.81f, 14.76f, 3f, 16.5f, 3f)
        curveTo(19.58f, 3f, 22f, 5.42f, 22f, 8.5f)
        curveTo(22f, 12.28f, 18.6f, 15.36f, 13.45f, 20.04f)
        close()
    }

}
