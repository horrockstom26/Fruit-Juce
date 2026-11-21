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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.music.bitchord.data.stats.OnThisDayEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun OnThisDayScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onPlaySong: (Song, String) -> Unit,
    onOpenArtist: (String?, String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    val entriesState by produceState<List<OnThisDayEntry>?>(initialValue = null) {
        value = runCatching { ListeningStats.onThisDay(today) }.getOrDefault(emptyList())
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Color(0xFF0B0B10)),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.History, contentDescription = null, tint = Color(0xFFFFB74D))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "On This Day",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    today.format(DateTimeFormatter.ofPattern("MMMM d", Locale.getDefault())) +
                        " - what you were playing in years past.",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 14.sp,
                )
            }
        }

        val entries = entriesState
        when {
            entries == null -> item {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            entries.isEmpty() -> item {
                EmptyMemoryCard(
                    "No memories for this day yet.",
                    "Keep listening - when this date comes around again, you'll have something to look back on.",
                )
            }
            else -> {
                items(entries, key = { it.date.toString() }) { entry ->
                    OnThisDayCard(
                        entry = entry,
                        onPlaySong = onPlaySong,
                        onOpenArtist = onOpenArtist,
                    )
                }
            }
        }
    }
}

@Composable
private fun OnThisDayCard(
    entry: OnThisDayEntry,
    onPlaySong: (Song, String) -> Unit,
    onOpenArtist: (String?, String) -> Unit,
) {
    val summary = entry.monthSummary
    val art = summary?.songs?.firstOrNull()?.song?.thumbnailUrl
        ?: summary?.artists?.firstOrNull()?.artworkUrl
    val minutes = (entry.ms / 60_000).coerceAtLeast(1)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF171720)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
        ) {
            if (art != null) {
                AsyncImage(
                    model = art.artworkAt(800),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF2A1B4D), Color(0xFF101018)))))
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xCC0B0B10)))),
            )
            Column(Modifier.align(Alignment.BottomStart).padding(14.dp)) {
                Text(
                    entry.date.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.getDefault())),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "$minutes minutes on this day . ${entry.date.year}",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                )
            }
        }
        if (summary != null) {
            val topSongs = summary.songs.take(5)
            if (topSongs.isNotEmpty()) {
                Text(
                    "The soundtrack of that month",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                )
                topSongs.forEachIndexed { index, ranked ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPlaySong(ranked.song, "On This Day") }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = ranked.song.thumbnailUrl?.artworkAt(120),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(42.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)),
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(ranked.song.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(ranked.song.artist, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
            val topArtists = summary.artists.take(6)
            if (topArtists.isNotEmpty()) {
                Spacer(Modifier.height(6.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(topArtists, key = { it.title }) { artist ->
                        Column(
                            modifier = Modifier
                                .width(96.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onOpenArtist(artist.browseId, artist.title) }
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AsyncImage(
                                model = artist.artworkUrl?.artworkAt(200),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(36.dp)).background(Color.White.copy(alpha = 0.08f)),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(artist.title, color = Color.White, fontSize = 12.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
            Spacer(Modifier.height(6.dp))
        }
    }
}

@Composable
private fun EmptyMemoryCard(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF171720))
            .padding(20.dp),
    ) {
        Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
    }
}
