package com.music.bitchord.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.music.bitchord.R
import com.music.bitchord.data.YtMusicRepository
import com.music.bitchord.data.model.ArtistPage
import com.music.bitchord.data.model.HomeShelf
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.data.stats.ListeningStats
import com.music.bitchord.data.stats.ReplayPeriod
import com.music.bitchord.ui.components.pressScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

private data class GoodArtistData(
    val name: String,
    val browseId: String?,
    val artworkUrl: String?,
    val plays: Int,
    val page: ArtistPage?,
)

@Composable
fun GoodArtistScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onPlaySong: (Song, String) -> Unit,
    onPlaySongs: (List<Song>, Int, String) -> Unit,
    onOpenBrowse: (String, String, String?, String?) -> Unit,
    onOpenWrapped: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val topCount by AppSettings.goodArtistTopCount.collectAsStateWithLifecycle()
    val artistsState by produceState<List<GoodArtistData>?>(initialValue = null, topCount) {
        value = withContext(Dispatchers.IO) {
            val summary = runCatching { ListeningStats.summary(ReplayPeriod.THIS_MONTH) }.getOrNull()
                ?: runCatching { ListeningStats.summary(ReplayPeriod.ALL_TIME) }.getOrNull()
            val ranked = summary?.artists.orEmpty().take(topCount)
            coroutineScope {
                ranked.map { artist ->
                    async {
                        val browseId = artist.browseId
                            ?: runCatching { YtMusicRepository.findArtistPageId(artist.title).getOrNull() }.getOrNull()
                        val page = browseId?.let { id ->
                            runCatching { YtMusicRepository.artistPage(id).getOrNull() }.getOrNull()
                        }
                        GoodArtistData(
                            name = artist.title,
                            browseId = browseId,
                            artworkUrl = page?.thumbnailUrl ?: artist.artworkUrl,
                            plays = artist.plays,
                            page = page,
                        )
                    }
                }.awaitAll()
            }
        }
    }
    // The Wrapped poster only earns its place when this year has listening.
    val wrappedReady by produceState(initialValue = false) {
        value = runCatching { ListeningStats.summary(ReplayPeriod.THIS_YEAR) }.getOrNull()
            ?.takeUnless { it.isEmpty } != null
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Color.Black),
        contentPadding = PaddingValues(top = contentPadding.calculateTopPadding() + 8.dp, bottom = contentPadding.calculateBottomPadding() + 120.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text("Good Artist", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "Your top ${topCount} artists, with popular songs, albums, and more.",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 14.sp,
                )
            }
        }

        if (wrappedReady) {
            item {
                com.music.bitchord.ui.wrapped.KisetsuWrappedEntry(
                    onOpen = onOpenWrapped,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .pressScale(pressedScale = 0.98f),
                )
            }
        }

        val artists = artistsState
        if (artists == null) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
        } else if (artists.isEmpty()) {
            item {
                Text(
                    "No top artists yet. Listen to more music and come back here.",
                    color = Color.White.copy(alpha = 0.72f),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        } else {
            itemsIndexed(artists, key = { i, a -> "ga:${a.browseId ?: a.name}:$i" }) { _, artist ->
                GoodArtistSection(
                    artist = artist,
                    onPlaySong = onPlaySong,
                    onPlaySongs = onPlaySongs,
                    onOpenBrowse = onOpenBrowse,
                )
            }
        }
    }
}

@Composable
private fun GoodArtistSection(
    artist: GoodArtistData,
    onPlaySong: (Song, String) -> Unit,
    onPlaySongs: (List<Song>, Int, String) -> Unit,
    onOpenBrowse: (String, String, String?, String?) -> Unit,
) {
    val page = artist.page
    val popularSongs = page?.songs.orEmpty().take(8)
    val about = page?.description?.takeIf { it.isNotBlank() }
    val sections = page?.sections.orEmpty()
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .pressScale(pressedScale = 0.99f)
                .clickable(enabled = artist.browseId != null) {
                    artist.browseId?.let { onOpenBrowse(it, artist.name, null, artist.artworkUrl) }
                }
        ) {
            if (artist.artworkUrl.isNullOrBlank()) {
                Box(Modifier.fillMaxSize().background(Color(0xFF1C1C22)), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(96.dp))
                }
            } else {
                AsyncImage(
                    model = artist.artworkUrl.artworkAt(900),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f), Color.Black.copy(alpha = 0.85f)))))
            Column(Modifier.align(Alignment.BottomStart).padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(artist.name, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                val meta = listOfNotNull(
                    if (artist.plays > 0) "${artist.plays} plays" else null,
                    page?.monthlyListenerCount,
                    page?.subscriberCountText,
                ).joinToString(" • ")
                if (meta.isNotBlank()) Text(meta, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            }
        }

        if (!about.isNullOrBlank()) {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("About", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(about, color = Color.White.copy(alpha = 0.78f), fontSize = 13.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)
            }
        }

        if (popularSongs.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Popular songs", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                popularSongs.forEachIndexed { index, song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .pressScale(pressedScale = 0.985f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .clickable { onPlaySongs(popularSongs, index, "Good Artist") }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("${index + 1}", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, modifier = Modifier.width(22.dp))
                        AsyncImage(
                            model = song.thumbnailUrl?.artworkAt(120),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)),
                        )
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

        sections.filter { it.items.isNotEmpty() }.take(4).forEach { shelf ->
            ArtistShelfRow(
                shelf = shelf,
                onOpenBrowse = onOpenBrowse,
                onPlaySong = onPlaySong,
            )
        }
    }
}

@Composable
private fun ArtistShelfRow(
    shelf: HomeShelf,
    onOpenBrowse: (String, String, String?, String?) -> Unit,
    onPlaySong: (Song, String) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(shelf.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(shelf.items.take(12), key = { (it.browseId ?: it.videoId ?: it.title) + it.hashCode() }) { item ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .pressScale(pressedScale = 0.97f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val browseId = item.browseId
                            if (browseId != null) {
                                onOpenBrowse(browseId, item.title, item.subtitle, item.thumbnailUrl)
                            } else if (item.videoId != null) {
                                onPlaySong(
                                    Song(
                                        videoId = item.videoId!!,
                                        title = item.title,
                                        artist = item.subtitle ?: "",
                                        thumbnailUrl = item.thumbnailUrl,
                                    ),
                                    "Good Artist",
                                )
                            }
                        }
                ) {
                    AsyncImage(
                        model = item.thumbnailUrl?.artworkAt(400),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E1E22)),
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
