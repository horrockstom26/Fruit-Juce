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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.data.stats.ListeningStats
import com.music.bitchord.data.stats.ReplayPeriod
import com.music.bitchord.ui.components.pressScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

private data class GoodArtistData(
    val name: String,
    val browseId: String?,
    val artworkUrl: String?,
    val plays: Int,
)

/**
 * Artist pages are the slow part of this tab - a lookup plus a browse request
 * per artist. They are cached by browse id (and by name when there is no id),
 * so the tab redraws instantly after the first visit instead of refetching
 * every artist each time it is opened.
 */
private val artistPageCache = ConcurrentHashMap<String, ArtistPage>()
private val artistIdCache = ConcurrentHashMap<String, String>()

private fun cacheKey(name: String, browseId: String?): String =
    browseId ?: "name:$name"

@Composable
fun GoodArtistScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onPlaySong: (Song, String) -> Unit,
    onPlaySongs: (List<Song>, Int, String) -> Unit,
    onOpenBrowse: (String, String, String?, String?) -> Unit,
    onOpenDeepDive: (DeepDiveTarget) -> Unit = { t ->
        onOpenBrowse(t.browseId ?: "", t.name, null, t.artwork)
    },
    onOpenWrapped: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val topCount by AppSettings.goodArtistTopCount.collectAsStateWithLifecycle()
    // Only the listening summary is awaited here - names, artwork and play
    // counts come straight out of the local stats files. Artist pages are
    // fetched per section, as each one scrolls into view, so the tab opens on
    // the first frame rather than behind a full spinner.
    val artistsState by produceState<List<GoodArtistData>?>(initialValue = null, topCount) {
        value = withContext(Dispatchers.IO) {
            val summary = runCatching { ListeningStats.summary(ReplayPeriod.THIS_MONTH) }.getOrNull()
                ?.takeUnless { it.isEmpty }
                ?: runCatching { ListeningStats.summary(ReplayPeriod.ALL_TIME) }.getOrNull()
            summary?.artists.orEmpty().take(topCount).map { artist ->
                GoodArtistData(
                    name = artist.title,
                    browseId = artist.browseId,
                    artworkUrl = artist.artworkUrl,
                    plays = artist.plays,
                )
            }
        }
    }
    // The Wrapped poster only earns its place when this year has listening.
    val wrappedReady by produceState(initialValue = false) {
        value = runCatching { ListeningStats.summary(ReplayPeriod.THIS_YEAR) }.getOrNull()
            ?.takeUnless { it.isEmpty } != null
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 8.dp,
            bottom = contentPadding.calculateBottomPadding() + 120.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Text("Top Artists", color = MaterialTheme.colorScheme.onBackground, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "Your top ${topCount} artists, with popular songs, albums, and more.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
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
                        .pressScale(pressedScale = 1.02f),
                )
            }
        }

        val artists = artistsState
        if (artists == null) {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        } else if (artists.isEmpty()) {
            item {
                Text(
                    "No top artists yet. Listen to more music and come back here.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                    fontSize = 15.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                )
            }
        } else {
            itemsIndexed(artists, key = { i, a -> "ta:${a.browseId ?: a.name}:$i" }) { _, artist ->
                GoodArtistSection(
                    artist = artist,
                    onPlaySong = onPlaySong,
                    onPlaySongs = onPlaySongs,
                    onOpenBrowse = onOpenBrowse,
                    onOpenDeepDive = onOpenDeepDive,
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
    onOpenDeepDive: (DeepDiveTarget) -> Unit,
) {
    // Composed only when this section scrolls into the viewport, so the tab
    // shows its headings first and fills the shelves in as they are reached.
    val pageState by produceState<ArtistPage?>(
        initialValue = artistPageCache[cacheKey(artist.name, artist.browseId)],
        artist.browseId,
        artist.name,
    ) {
        if (value != null) return@produceState
        value = withContext(Dispatchers.IO) {
            val key = cacheKey(artist.name, artist.browseId)
            artistPageCache[key]?.let { return@withContext it }
            val id = artist.browseId
                ?: artistIdCache[artist.name]
                ?: runCatching { YtMusicRepository.findArtistPageId(artist.name).getOrNull() }
                    .getOrNull()
                    ?.also { artistIdCache[artist.name] = it }
            val loaded = id?.let { browseId ->
                runCatching { YtMusicRepository.artistPage(browseId).getOrNull() }.getOrNull()
            }
            if (loaded != null) {
                artistPageCache[key] = loaded
                if (id != null) artistPageCache[id] = loaded
            }
            loaded
        }
    }
    val page = pageState
    val artworkUrl = page?.thumbnailUrl ?: artist.artworkUrl
    val popularSongs = page?.songs.orEmpty().take(8)
    val about = page?.description?.takeIf { it.isNotBlank() }
    val sections = page?.sections.orEmpty()
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .pressScale(pressedScale = 1.01f)
                .clickable {
                    onOpenDeepDive(DeepDiveTarget(artist.browseId, artist.name, artworkUrl))
                }
        ) {
            if (artworkUrl.isNullOrBlank()) {
                Box(Modifier.fillMaxSize().background(Color(0xFF1C1C22)), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(96.dp))
                }
            } else {
                AsyncImage(
                    model = artworkUrl.artworkAt(900),
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
                ).joinToString(" . ")
                if (meta.isNotBlank()) Text(meta, color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
            }
        }

        if (!about.isNullOrBlank()) {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("About", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(about, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.78f), fontSize = 13.sp, maxLines = 4, overflow = TextOverflow.Ellipsis)
            }
        }

        if (popularSongs.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Popular songs", color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
                popularSongs.forEachIndexed { index, song ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .pressScale(pressedScale = 1.015f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f))
                            .clickable { onPlaySongs(popularSongs, index, "Top Artists") }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("${index + 1}", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontSize = 13.sp, modifier = Modifier.width(22.dp))
                        AsyncImage(
                            model = song.thumbnailUrl?.artworkAt(120),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)),
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text(song.title, color = MaterialTheme.colorScheme.onBackground, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(song.artist, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f))
                    }
                }
            }
        }

        // The page is still on its way; a quiet placeholder keeps the section
        // from looking finished at the header.
        if (page == null) {
            Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
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
        Text(shelf.title, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(shelf.items.take(12), key = { (it.browseId ?: it.videoId ?: it.title) + it.hashCode() }) { item ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .pressScale(pressedScale = 1.03f)
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
                                    "Top Artists",
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
                    Text(item.title, color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    if (!item.subtitle.isNullOrBlank()) {
                        Text(item.subtitle ?: "", color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}
