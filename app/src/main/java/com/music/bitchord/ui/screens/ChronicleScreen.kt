package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.CompareArrows
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.stats.ListeningStats
import com.music.bitchord.data.stats.OnThisDayEntry
import com.music.bitchord.data.stats.ReplayPeriod
import com.music.bitchord.data.stats.ReplaySummary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.max

private data class ChronicleData(
    val cover: CoverData?,
    val yearProgress: YearProgress?,
    val milestones: Milestones?,
    val comparisons: List<Comparison>,
    val daypart: DaypartInsight?,
    val albumHighlights: List<AlbumHighlight>,
    val forgottenFavorites: List<SongPick>,
    val firstListenAnniversaries: List<Anniversary>,
    val onThisDay: List<OnThisDayEntry>,
    val chapters: List<Chapter>,
)

private data class CoverData(
    val totalMinutes: Long,
    val totalPlays: Int,
    val distinctArtists: Int,
    val topArtist: String?,
    val topArtistArt: String?,
    val firstMonth: YearMonth?,
)

private data class YearProgress(
    val year: Int,
    val minutes: Long,
    val plays: Int,
    val topArtist: String?,
    val topSong: Song?,
    val lastYearMinutes: Long?,
)

private data class Milestones(
    val biggestDay: Pair<LocalDate, Long>?, // date, ms
    val biggestMonth: Pair<YearMonth, Long>?,
    val listeningDays: Int,
    val longestStreak: Int,
    val topRepeatedSong: SongPick?,
)

private data class Comparison(val title: String, val leftLabel: String, val rightLabel: String, val left: ReplaySummary, val right: ReplaySummary)

private data class DaypartInsight(
    val peakLabel: String,
    val morningMin: Long,
    val afternoonMin: Long,
    val eveningMin: Long,
    val nightMin: Long,
)

private data class AlbumHighlight(val month: YearMonth, val album: String, val artist: String, val art: String?, val song: Song?)
private data class SongPick(val song: Song, val reason: String)
private data class Anniversary(val label: String, val song: Song?, val artist: String?, val month: YearMonth)
private data class Chapter(
    val month: YearMonth,
    val title: String,
    val subtitle: String,
    val summary: ReplaySummary,
    val mixtape: List<Song>,
)

@Composable
fun ChronicleScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onPlaySong: (Song, String) -> Unit,
    onPlaySongs: (List<Song>, Int, String) -> Unit,
    onOpenArtist: (String?, String) -> Unit,
    onOpenMonthlyRecap: (YearMonth) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dataState by produceState<ChronicleData?>(initialValue = null) {
        value = withContext(Dispatchers.IO) { buildChronicle() }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Color(0xFF070814)),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 40.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        val data = dataState
        if (data == null) {
            item {
                Box(Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            return@LazyColumn
        }

        data.cover?.let { cover ->
            item { CoverSection(cover, data.yearProgress) }
        }

        data.yearProgress?.let { yp ->
            item { YearProgressSection(yp, onPlaySong) }
        }

        data.milestones?.let { ms ->
            item { SectionCard("Milestones & Streaks", Icons.Rounded.EmojiEvents, Color(0xFFF59E0B)) {
                MilestonesContent(ms)
            } }
        }

        if (data.comparisons.isNotEmpty()) {
            item { SectionCard("Then vs Now", Icons.Rounded.CompareArrows, Color(0xFF60A5FA)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    data.comparisons.forEach { ComparisonRow(it) }
                }
            } }
        }

        data.daypart?.let { dp ->
            item { SectionCard("Your Daypart", Icons.Rounded.WbSunny, Color(0xFFF472B6)) {
                DaypartContent(dp)
            } }
        }

        if (data.albumHighlights.isNotEmpty()) {
            item { SectionCard("Albums of Your Months", Icons.Rounded.CalendarMonth, Color(0xFF34D399)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    data.albumHighlights.forEach { h ->
                        AlbumHighlightRow(h, onPlaySong)
                    }
                }
            } }
        }

        if (data.onThisDay.isNotEmpty() || data.firstListenAnniversaries.isNotEmpty()) {
            item { SectionCard("Memories & Anniversaries", Icons.Rounded.History, Color(0xFFFFB74D)) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    data.onThisDay.take(3).forEach { entry ->
                        val mins = (entry.ms / 60_000).coerceAtLeast(1)
                        val label = "${entry.date.year} . $mins min on this day"
                        val sub = entry.monthSummary?.artists?.firstOrNull()?.title?.let { "Soundtracked by $it" } ?: entry.monthSummary?.label ?: ""
                        MemoryRow(label, sub, entry.monthSummary?.songs?.firstOrNull()?.song?.thumbnailUrl) {
                            // open that month's recap context if desired: no-op lightweight
                        }
                    }
                    data.firstListenAnniversaries.take(4).forEach { ann ->
                        MemoryRow(ann.label, ann.artist ?: "", ann.song?.thumbnailUrl) {
                            ann.song?.let { onPlaySong(it, "Chronicle") }
                        }
                    }
                }
            } }
        }

        if (data.forgottenFavorites.isNotEmpty()) {
            item { SectionCard("Forgotten Favorites & Time Capsules", Icons.Rounded.Replay, Color(0xFFA78BFA)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val songs = data.forgottenFavorites.map { it.song }
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.08f)).clickable {
                            if (songs.isNotEmpty()) onPlaySongs(songs, 0, "Chronicle: Time Capsule")
                        }.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.width(8.dp))
                        Text("Play this time capsule", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                    data.forgottenFavorites.forEach { pick ->
                        SongPickRow(pick, onPlaySong)
                    }
                }
            } }
        }

        if (data.chapters.isNotEmpty()) {
            item {
                Text(
                    "Your Chapters",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
            items(data.chapters, key = { it.month.toString() }) { chapter ->
                ChapterCard(chapter, onPlaySong, onPlaySongs, onOpenArtist, onOpenMonthlyRecap)
            }
        }

        if (data.cover == null) {
            item {
                Text(
                    "Start listening to build your Chronicle - your story will appear here.",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }
    }
}

@Composable
private fun CoverSection(cover: CoverData, yp: YearProgress?) {
    val art = cover.topArtistArt
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(260.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF111827)),
    ) {
        if (art != null) {
            AsyncImage(model = art.artworkAt(1000), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1D2671), Color(0xFFC33764)))))
        }
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC070814), Color(0xFF070814)))))
        Column(Modifier.align(Alignment.BottomStart).padding(18.dp)) {
            Text("KISETSU CHRONICLE", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Spacer(Modifier.height(4.dp))
            Text("The story of your sound", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(6.dp))
            val parts = buildList {
                add("${String.format(Locale.getDefault(), "%,d", cover.totalMinutes)} minutes all time")
                if (cover.totalPlays > 0) add("${String.format(Locale.getDefault(), "%,d", cover.totalPlays)} plays")
                cover.topArtist?.let { add("Top: $it") }
            }
            Text(parts.joinToString(" . "), color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            cover.firstMonth?.let {
                val label = it.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " ${it.year}"
                Text("Since $label", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun YearProgressSection(yp: YearProgress, onPlaySong: (Song, String) -> Unit) {
    SectionCard("Year in Progress . ${yp.year}", Icons.Rounded.Bolt, Color(0xFF22D3EE)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("${String.format(Locale.getDefault(), "%,d", yp.minutes)} minutes so far", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            val delta = yp.lastYearMinutes?.let { yp.minutes - it }
            val line = buildString {
                append("${String.format(Locale.getDefault(), "%,d", yp.plays)} plays")
                yp.topArtist?.let { append(" . Top: $it") }
                if (delta != null) {
                    val sign = if (delta >= 0) "+" else ""
                    append(" . $sign${String.format(Locale.getDefault(), "%,d", delta / 60_000)} min vs last year")
                }
            }
            Text(line, color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
            yp.topSong?.let { song ->
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.08f)).clickable { onPlaySong(song, "Chronicle") }.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(model = song.thumbnailUrl?.artworkAt(120), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)))
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Your #1 this year", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                        Text(song.title, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, icon: ImageVector, tint: Color, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF101425))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        content()
    }
}

@Composable
private fun MilestonesContent(ms: Milestones) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ms.biggestDay?.let { (d, msVal) ->
            StatLine("Biggest day", "${d.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${d.dayOfMonth}, ${d.year} . ${msVal / 60_000} min")
        }
        ms.biggestMonth?.let { (m, msVal) ->
            val label = m.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " ${m.year}"
            StatLine("Biggest month", "$label . ${msVal / 60_000} min")
        }
        StatLine("Days with music", "${ms.listeningDays}")
        StatLine("Longest streak", "${ms.longestStreak} days in a row")
        ms.topRepeatedSong?.let { pick ->
            StatLine("Most repeated", "${pick.song.title} - ${pick.reason}")
        }
    }
}

@Composable private fun StatLine(label: String, value: String) {
    Column {
        Text(label.uppercase(Locale.ROOT), color = Color.White.copy(alpha = 0.55f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Text(value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ComparisonRow(c: Comparison) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(c.title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            CompareBox(c.leftLabel, c.left, Modifier.weight(1f))
            CompareBox(c.rightLabel, c.right, Modifier.weight(1f))
        }
    }
}

@Composable
private fun CompareBox(label: String, s: ReplaySummary, modifier: Modifier = Modifier) {
    Column(modifier.clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.06f)).padding(10.dp)) {
        Text(label, color = Color(0xFF93C5FD), fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("${String.format(Locale.getDefault(), "%,d", s.minutes)} min", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        s.artists.firstOrNull()?.let { Text(it.title, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
        s.songs.firstOrNull()?.let { Text(it.song.title, color = Color.White.copy(alpha = 0.65f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis) }
    }
}

@Composable
private fun DaypartContent(dp: DaypartInsight) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("You peak in the ${dp.peakLabel}", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        val total = (dp.morningMin + dp.afternoonMin + dp.eveningMin + dp.nightMin).coerceAtLeast(1)
        @Composable
        fun bar(label: String, min: Long) {
            val frac = (min.toFloat() / total).coerceIn(0f, 1f)
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(label, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text("$min min", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                }
                Box(Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.12f))) {
                    Box(Modifier.fillMaxWidth(frac).height(6.dp).clip(CircleShape).background(Color(0xFFF472B6)))
                }
            }
        }
        bar("Morning (5-11)", dp.morningMin)
        bar("Afternoon (12-16)", dp.afternoonMin)
        bar("Evening (17-21)", dp.eveningMin)
        bar("Night (22-4)", dp.nightMin)
    }
}

@Composable
private fun AlbumHighlightRow(h: AlbumHighlight, onPlaySong: (Song, String) -> Unit) {
    val monthLabel = h.month.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " ${h.month.year}"
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).clickable(enabled = h.song != null) {
            h.song?.let { onPlaySong(it, "Chronicle") }
        }.padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(model = h.art?.artworkAt(120), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text("Album of $monthLabel", color = Color(0xFF6EE7B7), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(h.album, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(h.artist, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        if (h.song != null) Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun MemoryRow(title: String, subtitle: String, art: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.05f)).clickable(onClick = onClick).padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(model = art?.artworkAt(120), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle.isNotBlank()) Text(subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun SongPickRow(pick: SongPick, onPlaySong: (Song, String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { onPlaySong(pick.song, "Chronicle") }.padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(model = pick.song.thumbnailUrl?.artworkAt(120), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(42.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(pick.song.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(pick.reason, color = Color.White.copy(alpha = 0.65f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
private fun ChapterCard(
    chapter: Chapter,
    onPlaySong: (Song, String) -> Unit,
    onPlaySongs: (List<Song>, Int, String) -> Unit,
    onOpenArtist: (String?, String) -> Unit,
    onOpenMonthlyRecap: (YearMonth) -> Unit,
) {
    val art = chapter.summary.songs.firstOrNull()?.song?.thumbnailUrl ?: chapter.summary.artists.firstOrNull()?.artworkUrl
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF12162A)),
    ) {
        Box(Modifier.fillMaxWidth().height(140.dp)) {
            if (art != null) {
                AsyncImage(model = art.artworkAt(800), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1D2671), Color(0xFF0F172A)))))
            }
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC070814)))))
            Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                Text(chapter.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(chapter.subtitle, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        Row(Modifier.padding(horizontal = 14.dp, vertical = 10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            if (chapter.mixtape.isNotEmpty()) {
                ActionPill("Play chapter", Icons.Rounded.PlayArrow) {
                    onPlaySongs(chapter.mixtape, 0, "Chronicle: ${chapter.title}")
                }
            }
            ActionPill("Recap", Icons.Rounded.AutoStories) { onOpenMonthlyRecap(chapter.month) }
        }
        val topArtist = chapter.summary.artists.firstOrNull()
        if (topArtist != null) {
            Text(
                "Top artist: ${topArtist.title}",
                color = Color(0xFF93C5FD),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp).clickable { onOpenArtist(topArtist.browseId, topArtist.title) },
            )
        }
        val topSong = chapter.summary.songs.firstOrNull()?.song
        if (topSong != null) {
            Spacer(Modifier.height(6.dp))
            SongPickRow(SongPick(topSong, "Defining track . ${chapter.summary.songs.first().plays} plays"), onPlaySong = onPlaySong)
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun ActionPill(label: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.12f)).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

// â”€â”€ Data building â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

private suspend fun buildChronicle(): ChronicleData {
    val months = ListeningStats.months().sorted()
    if (months.isEmpty()) {
        return ChronicleData(null, null, null, emptyList(), null, emptyList(), emptyList(), emptyList(), emptyList(), emptyList())
    }
    val today = LocalDate.now()
    val summaries = months.map { it to ListeningStats.summaryForMonth(it) }.filter { !it.second.isEmpty }

    val allTime = runCatching { ListeningStats.summary(ReplayPeriod.ALL_TIME) }.getOrNull()
    val cover = allTime?.takeUnless { it.isEmpty }?.let {
        val top = it.artists.firstOrNull()
        CoverData(
            totalMinutes = it.minutes,
            totalPlays = it.totalPlays,
            distinctArtists = it.distinctArtists,
            topArtist = top?.title,
            topArtistArt = top?.artworkUrl ?: it.songs.firstOrNull()?.song?.thumbnailUrl,
            firstMonth = months.firstOrNull(),
        )
    }

    val thisYear = runCatching { ListeningStats.summary(ReplayPeriod.THIS_YEAR) }.getOrNull()?.takeUnless { it.isEmpty }
    val lastYearMinutes = months.filter { it.year == today.year - 1 }
        .mapNotNull { ym -> runCatching { ListeningStats.readMonth(ym)?.let { b -> b.days.values.sum() / 60_000L } }.getOrNull() }
        .sum()
    val yearProgress = thisYear?.let {
        YearProgress(today.year, it.minutes, it.totalPlays, it.artists.firstOrNull()?.title, it.songs.firstOrNull()?.song, lastYearMinutes.takeIf { v -> v > 0 })
    }

    val milestones = computeMilestones(months)
    val comparisons = buildComparisons(months, summaries, today)
    val daypart = allTime?.let { computeDaypart(it) }
    val albumHighlights = summaries.takeLast(6).mapNotNull { (ym, s) ->
        val albumEntry = s.albums.firstOrNull() ?: return@mapNotNull null
        val repSong = s.songs.firstOrNull { it.song.albumName == albumEntry.title }?.song ?: s.songs.firstOrNull()?.song
        AlbumHighlight(ym, albumEntry.title, albumEntry.subtitle ?: "", albumEntry.artworkUrl ?: repSong?.thumbnailUrl, repSong)
    }.reversed()

    val onThisDay = runCatching { ListeningStats.onThisDay(today) }.getOrDefault(emptyList())
    val forgotten = computeForgotten(summaries, today)
    val anniversaries = computeAnniversaries(summaries, today)

    val chapters = summaries.sortedByDescending { it.first }.take(12).map { (ym, s) ->
        val topArtist = s.artists.firstOrNull()?.title
        val monthName = ym.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val season = seasonName(ym.monthValue)
        val title = when {
            topArtist != null && s.artists.first().ms > s.totalMs * 0.35f -> "$monthName ${ym.year}: $topArtist phase"
            else -> "$season '$${(ym.year % 100).toString().padStart(2, '0')}".replace("$", "") + " - $monthName ${ym.year}"
        }
        val subtitle = buildString {
            append("${String.format(Locale.getDefault(), "%,d", s.minutes)} min")
            if (topArtist != null) append(" . Top: $topArtist")
            s.songs.firstOrNull()?.let { append(" . " + it.song.title) }
        }
        Chapter(ym, title, subtitle, s, s.songs.take(8).map { it.song })
    }

    return ChronicleData(cover, yearProgress, milestones, comparisons, daypart, albumHighlights, forgotten, anniversaries, onThisDay, chapters)
}

private suspend fun computeMilestones(months: List<YearMonth>): Milestones? {
    var biggestDay: Pair<LocalDate, Long>? = null
    var biggestMonth: Pair<YearMonth, Long>? = null
    var listeningDays = 0
    var longestStreak = 0
    var currentStreak = 0
    var prevDate: LocalDate? = null
    var topRepeated: SongPick? = null
    var topPlays = 0

    val allDates = mutableListOf<LocalDate>()
    for (ym in months) {
        val bucket = ListeningStats.readMonth(ym) ?: continue
        val monthMs = bucket.days.values.sum()
        if (biggestMonth == null || monthMs > biggestMonth!!.second) biggestMonth = ym to monthMs
        bucket.days.forEach { (day, ms) ->
            val d = LocalDate.of(ym.year, ym.month, day.coerceIn(1, ym.lengthOfMonth()))
            allDates.add(d)
            if (ms > 0) {
                listeningDays++
                if (biggestDay == null || ms > biggestDay!!.second) biggestDay = d to ms
            }
        }
        bucket.tracks.forEach { t ->
            if (t.plays > topPlays) {
                topPlays = t.plays
                topRepeated = SongPick(t.toSong(), "${t.plays} plays")
            }
        }
    }
    val sortedDays = allDates.filter { it != null }.distinct().sorted()
    for (d in sortedDays) {
        if (prevDate != null && prevDate!!.plusDays(1) == d) currentStreak++ else currentStreak = 1
        if (currentStreak > longestStreak) longestStreak = currentStreak
        prevDate = d
    }
    if (listeningDays == 0 && biggestMonth == null) return null
    return Milestones(biggestDay, biggestMonth, listeningDays, longestStreak, topRepeated)
}

private suspend fun buildComparisons(
    months: List<YearMonth>,
    summaries: List<Pair<YearMonth, ReplaySummary>>,
    today: LocalDate,
): List<Comparison> {
    val list = mutableListOf<Comparison>()
    val current = YearMonth.from(today)
    val prev = current.minusMonths(1)
    val sCur = summaries.firstOrNull { it.first == current }?.second
    val sPrev = summaries.firstOrNull { it.first == prev }?.second
    if (sCur != null && sPrev != null) {
        list += Comparison("This month vs last month", labelOf(current), labelOf(prev), sCur, sPrev)
    } else {
        val lastTwo = summaries.takeLast(2)
        if (lastTwo.size == 2) list += Comparison("Recent months", labelOf(lastTwo[1].first), labelOf(lastTwo[0].first), lastTwo[1].second, lastTwo[0].second)
    }
    val thisYear = runCatching { ListeningStats.summary(ReplayPeriod.THIS_YEAR) }.getOrNull()
    val lastYearMonths = months.filter { it.year == today.year - 1 }
    if (thisYear != null && !thisYear.isEmpty && lastYearMonths.isNotEmpty()) {
        val mergedLast = mergeMonths(lastYearMonths)
        if (mergedLast != null && !mergedLast.isEmpty) {
            list += Comparison("This year vs last year", "${today.year}", "${today.year - 1}", thisYear, mergedLast)
        }
    }
    return list.take(2)
}

private suspend fun mergeMonths(months: List<YearMonth>): ReplaySummary? {
    if (months.isEmpty()) return null
    var base: ReplaySummary? = null
    // approximate merge by summing key fields via building from latest month and overriding totals is complex;
    // simpler: pick the richest month summary? No, better aggregate manually using summaries.
    val sums = summariesAggregate(months)
    return sums
}

private suspend fun summariesAggregate(months: List<YearMonth>): ReplaySummary? {
    val list = months.mapNotNull { ym -> runCatching { ListeningStats.summaryForMonth(ym) }.getOrNull()?.takeUnless { it.isEmpty } }
    if (list.isEmpty()) return null
    // Build a rough aggregate summary: totals summed, top artist/song from combined perspective approximated by largest contributors.
    val totalMs = list.sumOf { it.totalMs }
    val totalPlays = list.sumOf { it.totalPlays }
    val distinctArtists = list.maxOf { it.distinctArtists } // rough
    val distinctSongs = list.maxOf { it.distinctSongs }
    val distinctAlbums = list.maxOf { it.distinctAlbums }
    val topArtist = list.flatMap { it.artists }.groupBy { it.title.lowercase() }.maxByOrNull { (_, v) -> v.sumOf { it.ms } }?.value?.firstOrNull()
    val topSong = list.flatMap { it.songs }.groupBy { it.song.videoId.ifBlank { it.song.title.lowercase() } }.maxByOrNull { (_, v) -> v.sumOf { it.ms } }?.value?.firstOrNull()
    val today = LocalDate.now()
    val since = months.minOrNull()?.toString()
    return ReplaySummary(
        period = ReplayPeriod.ALL_TIME,
        label = "Period",
        totalMs = totalMs,
        totalPlays = totalPlays,
        songs = listOfNotNull(topSong),
        artists = listOfNotNull(topArtist),
        albums = emptyList(),
        genres = emptyList(),
        hourOfDay = List(24) { 0L },
        busiestDay = null,
        busiestDayMs = 0L,
        distinctSongs = distinctSongs,
        distinctArtists = distinctArtists,
        distinctAlbums = distinctAlbums,
        since = since,
    )
}

private fun labelOf(ym: YearMonth): String = ym.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " ${ym.year}"

private fun computeDaypart(summary: ReplaySummary): DaypartInsight {
    val h = summary.hourOfDay
    fun sum(range: IntRange) = range.sumOf { idx -> (h.getOrElse(idx) { 0L } / 60_000L) }
    val morning = sum(5..11)
    val afternoon = sum(12..16)
    val evening = sum(17..21)
    val night = (22..23).sumOf { idx -> (h.getOrElse(idx) { 0L } / 60_000L) } + (0..4).sumOf { idx -> (h.getOrElse(idx) { 0L } / 60_000L) }
    val peak = listOf("morning" to morning, "afternoon" to afternoon, "evening" to evening, "night" to night).maxByOrNull { it.second }?.first ?: "evening"
    return DaypartInsight(peak, morning, afternoon, evening, night)
}

private fun computeForgotten(summaries: List<Pair<YearMonth, ReplaySummary>>, today: LocalDate): List<SongPick> {
    if (summaries.size < 2) return emptyList()
    val recentMonths = summaries.takeLast(2).flatMap { it.second.songs.map { s -> s.song.videoId.ifBlank { s.song.title.lowercase() } } }.toSet()
    val older = summaries.dropLast(1).flatMap { (ym, s) ->
        s.songs.take(5).map { Triple(ym, it.song, it.plays) }
    }
    val picks = LinkedHashMap<String, SongPick>()
    for ((ym, song, plays) in older.sortedByDescending { it.third }) {
        val id = song.videoId.ifBlank { song.title.lowercase() }
        if (id in recentMonths || id in picks) continue
        val label = ym.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()) + " ${ym.year}"
        picks[id] = SongPick(song, "Big in $label . $plays plays")
        if (picks.size >= 6) break
    }
    return picks.values.toList()
}

private fun computeAnniversaries(summaries: List<Pair<YearMonth, ReplaySummary>>, today: LocalDate): List<Anniversary> {
    val currentYm = YearMonth.from(today)
    val out = mutableListOf<Anniversary>()
    for ((ym, s) in summaries) {
        if (ym >= currentYm.minusMonths(11)) continue
        if (ym.month == today.month) {
            val topSong = s.songs.firstOrNull()?.song
            val topArtist = s.artists.firstOrNull()?.title
            val yearsAgo = today.year - ym.year
            if (yearsAgo >= 1) {
                out += Anniversary("$yearsAgo year${if (yearsAgo == 1) "" else "s"} ago this month", topSong, topArtist, ym)
            }
        }
    }
    return out.sortedByDescending { it.month }.take(4)
}

private fun seasonName(month: Int): String = when (month) {
    in 3..5 -> "Spring"
    in 6..8 -> "Summer"
    in 9..11 -> "Autumn"
    else -> "Winter"
}

private fun com.music.bitchord.data.stats.TrackEntry.toSong(): Song = Song(
    videoId = id,
    title = title,
    artist = artist,
    thumbnailUrl = art,
    artistId = artistId,
    albumId = albumId,
    albumName = album,
)
