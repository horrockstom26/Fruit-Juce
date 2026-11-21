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
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Radio
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
import com.music.bitchord.data.YtMusicRepository
import com.music.bitchord.data.model.ArtistPage
import com.music.bitchord.data.model.HomeShelf
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.stats.ListeningStats
import com.music.bitchord.data.stats.ReplayPeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class DeepDiveTarget(val browseId: String?, val name: String, val artwork: String?)

private data class DeepDiveData(val page: ArtistPage?, val yourPlays: Int, val yourMinutes: Long)

@Composable
fun DeepDiveScreen(
    target: DeepDiveTarget,
    contentPadding: PaddingValues = PaddingValues(),
    onPlaySong: (Song, String) -> Unit,
    onPlaySongs: (List<Song>, Int, String) -> Unit,
    onOpenBrowse: (String, String, String?, String?) -> Unit,
    onStartRadio: (Song) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dataState by produceState<DeepDiveData?>(initialValue = null, target.browseId, target.name) {
        value = withContext(Dispatchers.IO) {
            val browseId = target.browseId
                ?: runCatching { YtMusicRepository.findArtistPageId(target.name).getOrNull() }.getOrNull()
            val page = browseId?.let { id ->
                runCatching { YtMusicRepository.artistPage(id).getOrNull() }.getOrNull()
            }
            val summary = runCatching { ListeningStats.summary(ReplayPeriod.ALL_TIME) }.getOrNull()
            val match = summary?.artists?.firstOrNull { it.title.equals(target.name, true) || (browseId != null && it.browseId == browseId) }
            DeepDiveData(page = page, yourPlays = match?.plays ?: 0, yourMinutes = match?.let { it.ms / 60_000 } ?: 0)
        }
    }

    val data = dataState
    LazyColumn(
        modifier = modifier.fillMaxSize().background(Color.Black),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding() + 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (data == null) {
            item {
                Box(Modifier.fillMaxWidth().height(320.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            return@LazyColumn
        }
        val page = data.page
        val heroArt = page?.thumbnailUrl ?: target.artwork
        item {
            Box(Modifier.fillMaxWidth().height(320.dp)) {
                if (heroArt != null) {
                    AsyncImage(model = heroArt.artworkAt(1000), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF1F2937), Color.Black))))
                }
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.3f), Color.Black))))
                Column(Modifier.align(Alignment.BottomStart).padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("DEEP DIVE", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Text(page?.name ?: target.name, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    val meta = listOfNotNull(
                        page?.monthlyListenerCount,
                        page?.subscriberCountText,
                        if (data.yourPlays > 0) "${data.yourPlays} plays by you" else null,
                        if (data.yourMinutes > 0) "${data.yourMinutes} min with you" else null,
                    ).joinToString(" . ")
                    if (meta.isNotBlank()) Text(meta, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
                    val radioSeed = page?.songs?.firstOrNull()
                    if (radioSeed != null) {
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White).clickable { onStartRadio(radioSeed) }.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Rounded.Radio, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Start radio", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        val about = page?.description?.takeIf { it.isNotBlank() }
        if (!about.isNullOrBlank()) {
            item {
                Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("About", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(about, color = Color.White.copy(alpha = 0.78f), fontSize = 14.sp, maxLines = 6, overflow = TextOverflow.Ellipsis)
                }
            }
        }

        val popular = page?.songs.orEmpty().take(10)
        if (popular.isNotEmpty()) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Essentials", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                    popular.forEachIndexed { index, song ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .clickable { onPlaySongs(popular, index, "Deep Dive: ${page?.name ?: target.name}") }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("${index + 1}", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, modifier = Modifier.width(22.dp))
                            AsyncImage(model = song.thumbnailUrl?.artworkAt(120), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(song.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(song.artist, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.White.copy(alpha = 0.9f))
                        }
                    }
                }
            }
        }

        val sections = page?.sections.orEmpty().filter { it.items.isNotEmpty() }
        items(sections, key = { it.title + it.items.size }) { shelf ->
            DeepDiveShelf(shelf = shelf, onOpenBrowse = onOpenBrowse, onPlaySong = onPlaySong)
        }

        if (page == null) {
            item {
                Text("Couldn't load the full artist page right now.", color = Color.White.copy(alpha = 0.6f), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }
}

@Composable
private fun DeepDiveShelf(
    shelf: HomeShelf,
    onOpenBrowse: (String, String, String?, String?) -> Unit,
    onPlaySong: (Song, String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(shelf.title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(shelf.items.take(12), key = { (it.browseId ?: it.videoId ?: it.title) + it.hashCode() }) { item ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val browseId = item.browseId
                            if (browseId != null) {
                                onOpenBrowse(browseId, item.title, item.subtitle, item.thumbnailUrl)
                            } else if (item.videoId != null) {
                                onPlaySong(
                                    Song(videoId = item.videoId!!, title = item.title, artist = item.subtitle ?: "", thumbnailUrl = item.thumbnailUrl),
                                    "Deep Dive",
                                )
                            }
                        },
                ) {
                    AsyncImage(
                        model = item.thumbnailUrl?.artworkAt(400),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(12.dp)).background(Color(0xFF1E1E22)),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(item.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    if (!item.subtitle.isNullOrBlank()) {
                        Text(item.subtitle ?: "", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}
