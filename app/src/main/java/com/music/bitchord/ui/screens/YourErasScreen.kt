package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.PlayArrow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.stats.ListeningStats
import com.music.bitchord.data.stats.ReplaySummary
import java.time.YearMonth
import java.util.Locale

data class EraChapter(
    val month: YearMonth,
    val title: String,
    val subtitle: String,
    val summary: ReplaySummary,
)

@Composable
fun YourErasScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onPlaySong: (Song, String) -> Unit,
    onOpenArtist: (String?, String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val erasState by produceState<List<EraChapter>?>(initialValue = null) {
        value = runCatching {
            val months = ListeningStats.months().sortedDescending()
            val summaries = months.mapNotNull { ym ->
                val s = ListeningStats.summaryForMonth(ym)
                if (s.isEmpty) null else ym to s
            }
            buildEras(summaries)
        }.getOrDefault(emptyList())
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Color(0xFF090C14)),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AutoStories, contentDescription = null, tint = Color(0xFF7DD3FC))
                    Spacer(Modifier.width(8.dp))
                    Text("Your Eras", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    "Chapters of your listening - the months and phases that defined your sound.",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 14.sp,
                )
            }
        }

        val eras = erasState
        when {
            eras == null -> item {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            eras.isEmpty() -> item {
                Column(Modifier.padding(16.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF121826)).padding(20.dp)) {
                    Text("No eras yet", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Listen for a while and your chapters will appear here.", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }
            }
            else -> items(eras, key = { it.month.toString() }) { era ->
                EraCard(era = era, onPlaySong = onPlaySong, onOpenArtist = onOpenArtist)
            }
        }
    }
}

private fun buildEras(monthSummaries: List<Pair<YearMonth, ReplaySummary>>): List<EraChapter> {
    if (monthSummaries.isEmpty()) return emptyList()
    // Take notable months: top by minutes and recent ones, deduped, keep order desc.
    val byMinutes = monthSummaries.sortedByDescending { it.second.totalMs }.take(8).map { it.first }.toSet()
    val recent = monthSummaries.take(6).map { it.first }.toSet()
    val picked = monthSummaries.filter { it.first in byMinutes || it.first in recent }
        .distinctBy { it.first }
        .sortedByDescending { it.first }
        .take(12)
    return picked.map { (ym, summary) ->
        val topArtist = summary.artists.firstOrNull()?.title
        val topSong = summary.songs.firstOrNull()?.song?.title
        val monthName = ym.month.name.lowercase(Locale.ROOT).replaceFirstChar { it.uppercase(Locale.ROOT) }
        val season = when (ym.monthValue) {
            in 3..5 -> "Spring"
            in 6..8 -> "Summer"
            in 9..11 -> "Autumn"
            else -> "Winter"
        }
        val title = when {
            topArtist != null && summary.artists.first().ms > summary.totalMs * 0.35f -> "$topArtist Phase"
            else -> "$season '$${ym.year % 100}".replace("$", "")
        }.let { if (it.contains("Phase")) "$monthName ${ym.year}: $it" else "$monthName ${ym.year}" }
        val subtitle = buildString {
            append(String.format(Locale.getDefault(), "%,d min", summary.minutes))
            if (topArtist != null) append(" . Top: $topArtist")
            if (topSong != null) append(" . " + topSong)
        }
        EraChapter(ym, title, subtitle, summary)
    }
}

@Composable
private fun EraCard(
    era: EraChapter,
    onPlaySong: (Song, String) -> Unit,
    onOpenArtist: (String?, String) -> Unit,
) {
    val art = era.summary.songs.firstOrNull()?.song?.thumbnailUrl
        ?: era.summary.artists.firstOrNull()?.artworkUrl
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF121826)),
    ) {
        Box(Modifier.fillMaxWidth().height(140.dp)) {
            if (art != null) {
                AsyncImage(model = art.artworkAt(800), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            } else {
                Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A)))))
            }
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC090C14)))))
            Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                Text(era.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(era.subtitle, color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        val topSong = era.summary.songs.firstOrNull()?.song
        if (topSong != null) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onPlaySong(topSong, "Your Eras") }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = topSong.thumbnailUrl?.artworkAt(120),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)),
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Play top of this era", color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                    Text(topSong.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
            }
        }
        val topArtist = era.summary.artists.firstOrNull()
        if (topArtist != null) {
            Text(
                "Top artist: ${topArtist.title}",
                color = Color(0xFF7DD3FC),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp).clickable { onOpenArtist(topArtist.browseId, topArtist.title) },
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}
