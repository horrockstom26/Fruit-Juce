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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.music.bitchord.data.YtMusicRepository
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.stats.ListeningStats
import com.music.bitchord.data.stats.ReplayPeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class CratePick(val song: Song, val reason: String)

@Composable
fun DiscoveryCrateScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onPlaySong: (Song, String) -> Unit,
    onPlaySongs: (List<Song>, Int, String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var refreshTick by remember { mutableIntStateOf(0) }
    val crateState by produceState<List<CratePick>?>(initialValue = null, refreshTick) {
        value = withContext(Dispatchers.IO) { buildCrate() }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Color(0xFF0A0F0B)),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Inventory2, contentDescription = null, tint = Color(0xFF86EFAC))
                        Spacer(Modifier.width(8.dp))
                        Text("Discovery Crate", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "A drop of new finds from the orbit of what you love - songs you haven't worn out yet.",
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 14.sp,
                    )
                }
                IconButton(onClick = { refreshTick++ }) {
                    Icon(Icons.Rounded.Refresh, contentDescription = "Refresh crate", tint = Color.White)
                }
            }
        }

        val crate = crateState
        when {
            crate == null -> item {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            crate.isEmpty() -> item {
                Column(Modifier.padding(16.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF121A14)).padding(20.dp)) {
                    Text("Crate's empty for now", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(6.dp))
                    Text("Listen to a few artists so we know what to dig for, then check back.", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }
            }
            else -> {
                item {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF14532D), Color(0xFF052E16))))
                            .clickable { onPlaySongs(crate.map { it.song }, 0, "Discovery Crate") }
                            .padding(16.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(8.dp))
                            Text("Play all ${crate.size} picks", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
                itemsIndexed(crate, key = { i, p -> "${p.song.videoId}:$i" }) { index, pick ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF121A14))
                            .clickable { onPlaySongs(crate.map { it.song }, index, "Discovery Crate") }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = pick.song.thumbnailUrl?.artworkAt(120),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.08f)),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(pick.song.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(pick.song.artist, color = Color.White.copy(alpha = 0.75f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(pick.reason, color = Color(0xFF86EFAC), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
    }
}

private suspend fun buildCrate(): List<CratePick> {
    val summary = runCatching { ListeningStats.summary(ReplayPeriod.ALL_TIME) }.getOrNull()
        ?: runCatching { ListeningStats.summary(ReplayPeriod.THIS_YEAR) }.getOrNull()
        ?: return emptyList()
    val playedIds = summary.songs.map { it.song.videoId }.toSet()
    val seeds = summary.artists.take(5)
    if (seeds.isEmpty()) return emptyList()

    val picks = LinkedHashMap<String, CratePick>()
    for (seed in seeds) {
        val reason = "Because you love ${seed.title}"
        // Try artist page songs first (deep cuts/popular)
        val artistId = seed.browseId ?: runCatching { YtMusicRepository.findArtistPageId(seed.title).getOrNull() }.getOrNull()
        val pageSongs = if (artistId != null) {
            runCatching { YtMusicRepository.artistPage(artistId).getOrNull()?.songs.orEmpty() }.getOrDefault(emptyList())
        } else emptyList()
        for (song in pageSongs) {
            if (song.videoId in playedIds || song.videoId in picks) continue
            picks[song.videoId] = CratePick(song, reason)
            if (picks.size >= 20) return picks.values.toList()
        }
        // Fallback: radio off a top song by this artist
        val seedSong = summary.songs.firstOrNull { it.song.artist.contains(seed.title, ignoreCase = true) }?.song
        if (seedSong != null) {
            val radio = runCatching { YtMusicRepository.radio(seedSong.videoId).getOrNull().orEmpty() }.getOrDefault(emptyList())
            for (song in radio) {
                if (song.videoId in playedIds || song.videoId in picks) continue
                picks[song.videoId] = CratePick(song, "From ${seed.title}'s orbit")
                if (picks.size >= 20) return picks.values.toList()
            }
        }
        if (picks.size >= 20) break
    }
    return picks.values.take(20).toList()
}
