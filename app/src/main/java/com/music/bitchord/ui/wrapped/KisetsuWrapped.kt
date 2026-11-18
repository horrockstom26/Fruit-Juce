package com.music.bitchord.ui.wrapped

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.HEADER_ART_PX
import com.music.bitchord.data.model.ROW_ART_PX
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.stats.ListeningStats
import com.music.bitchord.data.stats.ReplayPeriod
import com.music.bitchord.data.stats.ReplaySummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

/** How long one Wrapped card holds the screen before the clock turns it. */
private const val SLIDE_MS = 7_000L

/**
 * The Wrapped poster on the Good Artist tab: a loud gradient card that opens
 * the slideshow. Shown only when this year actually has listening behind it.
 */
@Composable
fun KisetsuWrappedEntry(
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val year = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yy"))
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFE1339B), Color(0xFF7A2AE6), Color(0xFF2E1B5E))))
            .clickable(onClick = onOpen)
            .padding(20.dp),
    ) {
        val drift = rememberInfiniteTransition(label = "entryDrift")
        val blob by drift.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(7_000), RepeatMode.Reverse),
            label = "entryBlob",
        )
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .size(170.dp)
                .scale(0.6f + blob * 0.4f)
                .alpha(0.18f)
                .clip(CircleShape)
                .background(Color.White),
        )
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(
                text = "KISETSU WRAPPED",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
            )
            Text(
                text = "'$year",
                color = Color.White,
                fontSize = 58.sp,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = "Your year in music, as a story — tap to play it",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

/** The two-digit year the story is titled with. */
private fun wrappedYear(summary: ReplaySummary): String =
    if (summary.period == ReplayPeriod.THIS_YEAR) {
        summary.label.takeLast(2)
    } else {
        java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yy"))
    }

/**
 * Kisetsu Wrapped: the year's listening as an animated full-screen slideshow,
 * in the shape people know from Spotify's own Wrapped — a run of loud,
 * gradient-backed cards that each give one fact the whole screen, tap through
 * and turn by themselves.
 *
 * The numbers come from the same on-device listening stats the Replay page
 * reads; this year is preferred, and all time stands in for a listener whose
 * only plays are from earlier years.
 *
 * ## The look
 *
 * Wrapped, not Replay: every card owns a different saturated gradient, the
 * type is set huge and black, numbers count up as they land, and the artwork
 * turns slowly like it is being shown off rather than merely displayed.
 *
 * ## The mechanics people already know
 *
 * Tap the right of the screen to move on, the left to go back, hold anywhere
 * to stop the clock, swipe to move by hand, back or the cross to leave. The
 * run stops at the last card rather than closing, so the play button is still
 * there when someone reaches for it.
 */
@Composable
fun KisetsuWrappedStories(
    onClose: () -> Unit,
    onPlaySong: (com.music.bitchord.data.model.Song, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val summary by produceState<ReplaySummary?>(initialValue = null) {
        val year = runCatching { ListeningStats.summary(ReplayPeriod.THIS_YEAR) }.getOrNull()
        value = if (year != null && !year.isEmpty) {
            year
        } else {
            runCatching { ListeningStats.summary(ReplayPeriod.ALL_TIME) }.getOrNull()
                ?.takeUnless { it.isEmpty }
        }
    }
    summary?.let { data ->
        KisetsuWrappedPager(
            summary = data,
            onClose = onClose,
            onPlaySong = onPlaySong,
            modifier = modifier,
        )
    }
}

/** One card in the run. */
private sealed interface WrappedSlide {
    /** The splash: the title and nothing else. */
    data object Title : WrappedSlide

    /** The headline number: minutes listened. */
    data object Minutes : WrappedSlide

    /** The number one song, given the whole card. */
    data class TopSong(val rank: Int) : WrappedSlide

    /** The runners-up, ranked. */
    data class TopSongs(val from: Int, val to: Int) : WrappedSlide

    /** The number one artist, given the whole card. */
    data class TopArtist(val rank: Int) : WrappedSlide

    /** The next artists, ranked. */
    data class TopArtists(val from: Int, val to: Int) : WrappedSlide

    /** The release this year belonged to. */
    data object TopAlbum : WrappedSlide

    /** The taste: top genre and when the listening happened. */
    data object Taste : WrappedSlide

    /** The send-off, with the play button. */
    data object Outro : WrappedSlide
}

/** Wrapped's loud card-to-card palettes, one per slide. */
private data class Palette(val colors: List<Color>)

private fun palettes(count: Int): List<Palette> = listOf(
    Palette(listOf(Color(0xFFE1339B), Color(0xFF7A2AE6), Color(0xFF2E1B5E))),
    Palette(listOf(Color(0xFF1DB954), Color(0xFF0B5A2A), Color(0xFF05230F))),
    Palette(listOf(Color(0xFFFF6A3D), Color(0xFFE1339B), Color(0xFF4A1240))),
    Palette(listOf(Color(0xFF2E77FF), Color(0xFF19C9D6), Color(0xFF0B2A5E))),
    Palette(listOf(Color(0xFFB14BFF), Color(0xFF5E35F2), Color(0xFF1B1040))),
    Palette(listOf(Color(0xFFFFC93D), Color(0xFFFF5F6D), Color(0xFF4A1030))),
    Palette(listOf(Color(0xFF00C2A8), Color(0xFF0B7BE0), Color(0xFF03203C))),
    Palette(listOf(Color(0xFFFF3D77), Color(0xFF8A2BE2), Color(0xFF2A0A4A))),
    Palette(listOf(Color(0xFF1DB954), Color(0xFF191919), Color(0xFF050505))),
).let { base -> List(count) { base[it % base.size] } }

@Composable
private fun KisetsuWrappedPager(
    summary: ReplaySummary,
    onClose: () -> Unit,
    onPlaySong: (com.music.bitchord.data.model.Song, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val slides = remember(summary) { buildSlides(summary) }
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()
    var paused by remember { mutableStateOf(false) }
    var sharing by remember { mutableStateOf(false) }
    val colors = remember(slides) { palettes(slides.size) }

    // The card's clock. Keyed on the page so a manual turn resets it, and on
    // [paused] so a held finger freezes the fill where it is.
    val progress = remember(pagerState.currentPage) { Animatable(0f) }
    LaunchedEffect(pagerState.currentPage, paused) {
        if (paused) return@LaunchedEffect
        val remaining = ((1f - progress.value) * SLIDE_MS).toLong().coerceAtLeast(1)
        progress.animateTo(1f, tween(remaining.toInt(), easing = LinearEasing))
        val next = pagerState.currentPage + 1
        if (next < slides.size && !pagerState.isScrollInProgress) {
            // Launched on the screen's scope, not this effect's: the page
            // change restarts the effect, and an animation cancelled by its
            // own scope is exactly why the auto-advance used to stop half-way
            // through a card and leave the pager awkwardly between pages.
            scope.launch {
                pagerState.animateScrollToPage(next, animationSpec = tween(500))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(pagerState) {
                detectTapGestures(
                    onPress = {
                        paused = true
                        try {
                            awaitRelease()
                        } finally {
                            paused = false
                        }
                    },
                    onTap = { pos ->
                        val next = pagerState.currentPage + 1
                        val prev = pagerState.currentPage - 1
                        scope.launch {
                            if (pos.x > size.width * 0.62f) {
                                if (next < slides.size) pagerState.animateScrollToPage(next)
                            } else if (prev >= 0) {
                                pagerState.animateScrollToPage(prev)
                            }
                        }
                    },
                )
            },
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            WrappedCard(
                slide = slides[page],
                palette = colors[page],
                summary = summary,
                onPlaySong = onPlaySong,
                onShare = { sharing = true },
            )
        }

        // The story's signature: segmented progress along the top.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 14.dp, end = 14.dp, top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            slides.forEachIndexed { index, _ ->
                Box(modifier = Modifier.weight(1f).height(3.5.dp), contentAlignment = Alignment.CenterStart) {
                    Box(Modifier.fillMaxSize().clip(CircleShape).background(Color.White.copy(alpha = 0.28f)))
                    val fill = when {
                        index < pagerState.currentPage -> 1f
                        index > pagerState.currentPage -> 0f
                        else -> progress.value
                    }
                    Box(
                        Modifier
                            .fillMaxWidth(fill.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(Color.White),
                    )
                }
            }
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(34.dp).padding(start = 2.dp),
            ) {
                Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.White)
            }
        }

        if (sharing) {
            WrappedSharePage(summary = summary, onDismiss = { sharing = false })
        }
    }
}

/** The slides this year has earned, in the order the story tells them. */
private fun buildSlides(summary: ReplaySummary): List<WrappedSlide> {
    val slides = mutableListOf<WrappedSlide>(WrappedSlide.Title, WrappedSlide.Minutes)
    summary.songs.firstOrNull()?.let { slides += WrappedSlide.TopSong(0) }
    if (summary.songs.size > 1) slides += WrappedSlide.TopSongs(1, minOf(5, summary.songs.size) - 1)
    summary.artists.firstOrNull()?.let { slides += WrappedSlide.TopArtist(0) }
    if (summary.artists.size > 1) slides += WrappedSlide.TopArtists(1, minOf(5, summary.artists.size) - 1)
    if (summary.albums.isNotEmpty()) slides += WrappedSlide.TopAlbum
    slides += WrappedSlide.Taste
    slides += WrappedSlide.Outro
    return slides
}

/** One full-screen Wrapped card: gradient, big type, one fact. */
@Composable
private fun WrappedCard(
    slide: WrappedSlide,
    palette: Palette,
    summary: ReplaySummary,
    onPlaySong: (com.music.bitchord.data.model.Song, String) -> Unit,
    onShare: () -> Unit,
) {
    // Every card lands with a small pop: the content scales up and fades in
    // as the card arrives, which is what makes the run feel alive.
    val entrance = remember(slide) { Animatable(0f) }
    LaunchedEffect(slide) {
        entrance.snapTo(0f)
        entrance.animateTo(1f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(palette.colors)),
    ) {
        // Two drifting lights behind the content: depth without detail.
        val drift = rememberInfiniteTransition(label = "drift")
        val blobA by drift.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(9_000), RepeatMode.Reverse),
            label = "blobA",
        )
        val blobB by drift.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(tween(12_000), RepeatMode.Reverse),
            label = "blobB",
        )
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp)
                .size(260.dp)
                .scale(0.7f + blobA * 0.3f)
                .alpha(0.20f)
                .clip(CircleShape)
                .background(Color.White),
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 60.dp)
                .size(320.dp)
                .scale(0.65f + blobB * 0.35f)
                .alpha(0.14f)
                .clip(CircleShape)
                .background(Color.White),
        )

        Column(
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 26.dp)
                .statusBarsPadding()
                .padding(top = 64.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                Modifier
                    .scale(0.92f + 0.08f * entrance.value)
                    .alpha(entrance.value),
            ) {
                when (slide) {
                    WrappedSlide.Title -> TitleCard(summary)
                    WrappedSlide.Minutes -> MinutesCard(summary)
                    is WrappedSlide.TopSong -> TopSongCard(summary, slide.rank, onPlaySong)
                    is WrappedSlide.TopSongs -> TopSongsCard(summary, slide.from, slide.to)
                    is WrappedSlide.TopArtist -> TopArtistCard(summary, slide.rank)
                    is WrappedSlide.TopArtists -> TopArtistsCard(summary, slide.from, slide.to)
                    WrappedSlide.TopAlbum -> TopAlbumCard(summary)
                    WrappedSlide.Taste -> TasteCard(summary)
                    WrappedSlide.Outro -> OutroCard(summary, onPlaySong, onShare)
                }
            }
        }
    }
}

/** The sentence that opens every card, Wrapped-style. */
@Composable
private fun Kicker(text: String) {
    Text(
        text = text.uppercase(Locale.ROOT),
        color = Color.White.copy(alpha = 0.85f),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.5.sp,
        modifier = Modifier.padding(bottom = 10.dp),
    )
}

@Composable
private fun Headline(text: String, size: Int = 52) {
    Text(
        text = text,
        color = Color.White,
        fontSize = size.sp,
        fontWeight = FontWeight.Black,
        lineHeight = (size * 1.12).sp,
    )
}

/** Slow-turning artwork, shown off rather than merely displayed. */
@Composable
private fun SpinningArtwork(url: String?, sizeDp: Int) {
    val spin = rememberInfiniteTransition(label = "spin")
    val angle by spin.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(24_000, easing = LinearEasing)),
        label = "angle",
    )
    Box(contentAlignment = Alignment.Center) {
        if (url.isNullOrBlank()) {
            Box(Modifier.size(sizeDp.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f)))
        } else {
            AsyncImage(
                model = url.artworkAt(HEADER_ART_PX),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(sizeDp.dp).rotate(angle).clip(CircleShape),
            )
        }
        // The spindle at the centre turns a cover into a record.
        Box(Modifier.size(sizeDp.dp / 12).clip(CircleShape).background(Color.Black.copy(alpha = 0.85f)))
    }
}

@Composable
private fun TitleCard(summary: ReplaySummary) {
    Kicker("Your year in music")
    Headline("Kisetsu\nWrapped\n'${wrappedYear(summary)}", size = 64)
    Spacer(Modifier.height(18.dp))
    Text(
        text = "Every number on the next few cards was earned one play at a time.",
        color = Color.White.copy(alpha = 0.85f),
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun MinutesCard(summary: ReplaySummary) {
    Kicker("Time well spent")
    // The headline number counts up as it lands — the fact arrives rather
    // than just being there.
    val counted = remember(summary.totalMs) { Animatable(0f) }
    LaunchedEffect(summary.totalMs) {
        counted.snapTo(0f)
        counted.animateTo(1f, tween(1_400, easing = FastOutSlowInEasing))
    }
    val minutes = (summary.minutes * counted.value).toLong()
    Headline("$minutes", size = 78)
    Headline("minutes listened", size = 30)
    Spacer(Modifier.height(16.dp))
    val facts = buildString {
        append("${summary.hours} hours")
        append(" · ${summary.totalPlays} plays")
        append(" · ${summary.distinctSongs} songs")
        append(" · ${summary.distinctArtists} artists")
    }
    Text(facts, color = Color.White.copy(alpha = 0.85f), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun TopSongCard(
    summary: ReplaySummary,
    rank: Int,
    onPlaySong: (com.music.bitchord.data.model.Song, String) -> Unit,
) {
    val entry = summary.songs.getOrNull(rank) ?: return
    Kicker("No. ${rank + 1} song of the year")
    SpinningArtwork(entry.song.thumbnailUrl, 220)
    Spacer(Modifier.height(22.dp))
    Headline(entry.song.title, size = 36)
    Spacer(Modifier.height(6.dp))
    Text(
        entry.song.artist,
        color = Color.White.copy(alpha = 0.9f),
        fontSize = 19.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
    Spacer(Modifier.height(10.dp))
    Text(
        "${entry.plays} plays · ${entry.ms / 60_000} min",
        color = Color.White.copy(alpha = 0.75f),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
    )
    // The song plays straight from the card: a story about your favourite
    // song should hand it over when asked.
    Spacer(Modifier.height(18.dp))
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White)
            .clickable { onPlaySong(entry.song, "Kisetsu Wrapped") }
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.Black)
        Text(
            "Play it",
            color = Color.Black,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
        )
    }
}

@Composable
private fun TopSongsCard(summary: ReplaySummary, from: Int, to: Int) {
    Kicker("The rest of the top 5")
    Headline("Also on repeat", size = 34)
    Spacer(Modifier.height(16.dp))
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        (from..to).forEach { rank ->
            val entry = summary.songs.getOrNull(rank) ?: return@forEach
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "${rank + 1}",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(34.dp),
                )
                AsyncImage(
                    model = entry.song.thumbnailUrl?.artworkAt(ROW_ART_PX),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.18f)),
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        entry.song.title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        entry.song.artist,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun TopArtistCard(summary: ReplaySummary, rank: Int) {
    val entry = summary.artists.getOrNull(rank) ?: return
    Kicker("No. ${rank + 1} artist of the year")
    SpinningArtwork(entry.artworkUrl, 230)
    Spacer(Modifier.height(22.dp))
    Headline(entry.title, size = 40)
    Spacer(Modifier.height(10.dp))
    val facts = buildString {
        append("${entry.plays} plays")
        entry.ms.takeIf { it > 0 }?.let { append(" · ${it / 60_000} min") }
    }
    Text(facts, color = Color.White.copy(alpha = 0.75f), fontSize = 14.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun TopArtistsCard(summary: ReplaySummary, from: Int, to: Int) {
    Kicker("The rest of the top 5")
    Headline("Kept you company", size = 34)
    Spacer(Modifier.height(16.dp))
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        (from..to).forEach { rank ->
            val entry = summary.artists.getOrNull(rank) ?: return@forEach
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    "${rank + 1}",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.width(34.dp),
                )
                AsyncImage(
                    model = entry.artworkUrl?.artworkAt(ROW_ART_PX),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.18f)),
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        entry.title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        "${entry.plays} plays",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAlbumCard(summary: ReplaySummary) {
    val entry = summary.albums.firstOrNull() ?: return
    Kicker("The album of the year")
    SpinningArtwork(entry.artworkUrl, 220)
    Spacer(Modifier.height(22.dp))
    Headline(entry.title, size = 36)
    Spacer(Modifier.height(6.dp))
    Text(
        entry.subtitle ?: "",
        color = Color.White.copy(alpha = 0.9f),
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun TasteCard(summary: ReplaySummary) {
    Kicker("What it sounded like")
    val genre = summary.genres.firstOrNull()
    if (genre != null) {
        Headline(genre.title, size = 46)
        Spacer(Modifier.height(8.dp))
        Text(
            "your top genre of the year",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(Modifier.height(20.dp))
    }
    summary.peakHour?.let { hour ->
        val clock = String.format(Locale.ROOT, "%d %s", if (hour == 0) 12 else if (hour > 12) hour - 12 else hour, if (hour < 12) "AM" else "PM")
        val kind = when (hour) {
            in 5..11 -> "mornings belonged to it"
            in 12..17 -> "afternoons belonged to it"
            in 18..22 -> "evenings belonged to it"
            else -> "late nights belonged to it"
        }
        Text(
            "$clock — $kind",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun OutroCard(
    summary: ReplaySummary,
    onPlaySong: (com.music.bitchord.data.model.Song, String) -> Unit,
    onShare: () -> Unit,
) {
    Kicker("That was your year")
    Headline("'${wrappedYear(summary)}',\nwrapped.", size = 52)
    Spacer(Modifier.height(18.dp))
    Text(
        "${summary.minutes} minutes · ${summary.totalPlays} plays · ${summary.distinctArtists} artists",
        color = Color.White.copy(alpha = 0.85f),
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
    )
    Spacer(Modifier.height(22.dp))
    summary.songs.firstOrNull()?.let { top ->
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onPlaySong(top.song, "Kisetsu Wrapped") }
                .padding(horizontal = 18.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.Black)
            Text(
                "Play your top song",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
    Spacer(Modifier.height(14.dp))
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.16f))
            .clickable(onClick = onShare)
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(Icons.Rounded.IosShare, contentDescription = null, tint = Color.White)
        Text(
            "Share your Wrapped",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
