package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.music.bitchord.data.YtMusicRepository
import com.music.bitchord.data.model.SearchResult
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.model.needsKisetsuRadioCover
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.data.settings.DiscoverSave
import com.music.bitchord.data.settings.DiscoverSaves
import com.music.bitchord.data.sources.RadioApi
import com.music.bitchord.data.sources.SourceKind
import com.music.bitchord.data.sources.SourceRegistry
import com.music.bitchord.data.stats.PodcastBookmark
import com.music.bitchord.data.stats.PodcastResume
import com.music.bitchord.ui.components.rememberRemoteArtworkUrl
import com.music.bitchord.ui.theme.rememberArtworkPalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private val NetflixModes = listOf("For you", "Episodes", "Radio", "Podcasts")

private fun nRadioConfigId(): String = SourceRegistry.configs.value.firstOrNull { it.kind == SourceKind.RADIO }?.id ?: "radio"
private fun nRadioSave(st: RadioApi.RadioStation): DiscoverSave {
    val vid = SourceRegistry.trackKey(nRadioConfigId(), st.uuid)
    return DiscoverSave(
        key = vid, kind = DiscoverSave.KIND_RADIO, title = st.name,
        subtitle = listOfNotNull(st.country, st.tags?.split(',')?.firstOrNull()?.trim()).filter { !it.isNullOrBlank() }.joinToString(" · ").ifBlank { "Live radio" },
        thumbnailUrl = st.favicon, videoId = vid,
    )
}

@Composable
fun NetflixDiscoverScreen(
    currentSong: Song?,
    contentPadding: PaddingValues = PaddingValues(),
    onPlaySong: (Song, String) -> Unit,
    onPlaySongs: (List<Song>, Int, String) -> Unit,
    onOpenPodcast: (String?, String) -> Unit,
    onResumePodcast: (PodcastBookmark) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val art = rememberRemoteArtworkUrl(currentSong)
    val kisetsu = currentSong?.needsKisetsuRadioCover() == true
    val palette = rememberArtworkPalette(if (kisetsu) null else art, dark = true)
    val accent = palette.accent

    var query by rememberSaveable { mutableStateOf("") }
    var mode by rememberSaveable { mutableIntStateOf(0) }
    var searching by remember { mutableStateOf(false) }
    var searchResults by remember { mutableStateOf<List<DiscoverSave>>(emptyList()) }

    val likedRadios by DiscoverSaves.likedRadios.collectAsStateWithLifecycle()
    val likedPodcasts by DiscoverSaves.likedPodcasts.collectAsStateWithLifecycle()
    val libraryRadios by DiscoverSaves.libraryRadios.collectAsStateWithLifecycle()
    val libraryPodcasts by DiscoverSaves.libraryPodcasts.collectAsStateWithLifecycle()
    val recentRadios by DiscoverSaves.recentRadios.collectAsStateWithLifecycle()
    val recentPodcasts by DiscoverSaves.recentPodcasts.collectAsStateWithLifecycle()

    val liked = (likedRadios + likedPodcasts).distinctBy { it.key }
    val recent = (recentRadios + recentPodcasts).distinctBy { it.key }
    val podcasts = (libraryPodcasts + likedPodcasts).distinctBy { it.key }
    val radios = (libraryRadios + likedRadios).distinctBy { it.key }
    val episodes = (likedPodcasts + recentPodcasts + libraryPodcasts).filter { it.isPodcastEpisode }.distinctBy { it.key }
    val continuePodcasts by PodcastResume.entries.collectAsStateWithLifecycle()

    LaunchedEffect(query, mode) {
        val q = query.trim()
        if (q.length < 2) { searchResults = emptyList(); searching = false; return@LaunchedEffect }
        delay(250)
        searching = true
        val radioResults = runCatching { withContext(Dispatchers.IO) { RadioApi.search(q, 12).map(::nRadioSave) } }.getOrDefault(emptyList())
        val podcastResults = runCatching {
            YtMusicRepository.search(q, com.music.bitchord.data.model.SearchFilter.PODCASTS).getOrNull().orEmpty().take(8)
                .mapNotNull { r ->
                    when (r) {
                        is SearchResult.Track -> DiscoverSave.ofEpisode(r.song)
                        is SearchResult.Browse -> DiscoverSave(key = r.item.browseId, kind = DiscoverSave.KIND_PODCAST, title = r.item.title, subtitle = r.item.subtitle, thumbnailUrl = r.item.thumbnailUrl, browseId = r.item.browseId, albumId = r.item.browseId, isPodcastEpisode = false)
                        else -> null
                    }
                }
        }.getOrDefault(emptyList())
        val episodeResults = runCatching {
            YtMusicRepository.search(q, com.music.bitchord.data.model.SearchFilter.EPISODES).getOrNull().orEmpty().take(8)
                .mapNotNull { (it as? SearchResult.Track)?.song?.let(DiscoverSave::ofEpisode) }
        }.getOrDefault(emptyList()) + runCatching {
            YtMusicRepository.search(q, com.music.bitchord.data.model.SearchFilter.VIDEOS).getOrNull().orEmpty().take(8)
                .mapNotNull { (it as? SearchResult.Track)?.song?.let { song -> DiscoverSave.ofEpisode(song, subtitle = "YouTube · ${song.artist}".trimEnd(' ', '·')) } }
        }.getOrDefault(emptyList())
        val merged = when (mode) {
            1 -> episodeResults
            2 -> radioResults
            3 -> podcastResults
            else -> radioResults + podcastResults + episodeResults
        }
        searchResults = merged.distinctBy { it.key }
        searching = false
    }

    val topPad = contentPadding.calculateTopPadding()
    val bottomPad = contentPadding.calculateBottomPadding() + 120.dp

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(0f to Color(0xFF120A10), 0.18f to accent.copy(alpha = 0.20f), 0.45f to Color(0xFF090909), 1f to Color.Black)),
        contentPadding = PaddingValues(top = topPad, bottom = bottomPad),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Discover", color = Color.White, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 16.dp))
            Text("Cinematic radios, podcasts, episodes, and stories.", color = Color.White.copy(alpha = 0.72f), fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search radios, podcasts, episodes…", color = Color.White.copy(alpha = 0.55f)) },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null, tint = Color.White.copy(alpha = 0.8f)) },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        Icon(Icons.Rounded.Close, contentDescription = "Clear", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.clickable { query = "" })
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.10f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                    focusedBorderColor = accent.copy(alpha = 0.7f),
                    unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = accent,
                ),
            )
            Spacer(Modifier.height(10.dp))
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NetflixModes.size) { i ->
                    val selected = mode == i
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (selected) Color.White else Color.White.copy(alpha = 0.10f))
                            .clickable { mode = i }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) { Text(NetflixModes[i], color = if (selected) Color.Black else Color.White, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium) }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        if (query.trim().length >= 2) {
            item { NSection("Search results") }
            if (searching) {
                item { Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = accent) } }
            } else if (searchResults.isEmpty()) {
                item { Text("No results found.", color = Color.White.copy(alpha = 0.72f), modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) }
            } else {
                item {
                    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(searchResults, key = { "sr:${it.key}" }) { item ->
                            NPosterCard(
                                title = item.title,
                                subtitle = item.subtitle ?: if (item.kind == DiscoverSave.KIND_RADIO) "Live radio" else "Podcast",
                                art = item.thumbnailUrl,
                                onClick = { nPlayDiscoverSave(item, onPlaySong, onOpenPodcast) },
                            )
                        }
                    }
                }
            }
        }

        if (continuePodcasts.isNotEmpty()) {
            item { NSection("Continue listening") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(continuePodcasts.take(10), key = { "cp:${it.videoId}" }) { b ->
                        NPosterCard(
                            title = b.title,
                            subtitle = b.showName.ifBlank { "Resume podcast" },
                            art = b.thumbnailUrl,
                            onClick = { onResumePodcast(b) },
                        )
                    }
                }
            }
        }

        if (radios.isNotEmpty() && (mode == 0 || mode == 2)) {
            item { NSection("Live radio") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(radios.take(12), key = { "r:${it.key}" }) { item -> NPosterCard(item.title, item.subtitle ?: "Live radio", item.thumbnailUrl, onClick = { nPlayDiscoverSave(item, onPlaySong, onOpenPodcast) }) }
                }
            }
        }

        if (podcasts.isNotEmpty() && (mode == 0 || mode == 3)) {
            item { NSection("Podcasts & shows") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(podcasts.take(12), key = { "p:${it.key}" }) { item -> NPosterCard(item.title, item.subtitle ?: "Podcast", item.thumbnailUrl, onClick = { nPlayDiscoverSave(item, onPlaySong, onOpenPodcast) }) }
                }
            }
        }

        if (episodes.isNotEmpty() && (mode == 0 || mode == 1)) {
            item { NSection("Episodes & videos") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(episodes.take(12), key = { "e:${it.key}" }) { item -> NPosterCard(item.title, item.subtitle ?: "Episode", item.thumbnailUrl, onClick = { nPlayDiscoverSave(item, onPlaySong, onOpenPodcast) }) }
                }
            }
        }

        if (liked.isNotEmpty() && mode == 0) {
            item { NSection("Liked") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(liked.take(12), key = { "l:${it.key}" }) { item -> NPosterCard(item.title, item.subtitle ?: "Saved", item.thumbnailUrl, onClick = { nPlayDiscoverSave(item, onPlaySong, onOpenPodcast) }) }
                }
            }
        }

        if (recent.isNotEmpty() && mode == 0) {
            item { NSection("Recently played") }
            item {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(recent.take(12), key = { "rec:${it.key}" }) { item -> NPosterCard(item.title, item.subtitle ?: "Recent", item.thumbnailUrl, onClick = { nPlayDiscoverSave(item, onPlaySong, onOpenPodcast) }) }
                }
            }
        }
    }
}

@Composable
private fun NSection(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
private fun NPosterCard(
    title: String,
    subtitle: String,
    art: String?,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF1C1C22))
        ) {
            AsyncImage(
                model = art?.artworkAt(500),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.12f), Color.Black.copy(alpha = 0.45f)))))
            Box(
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp).size(30.dp).clip(RoundedCornerShape(999.dp)).background(Color.White.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
        Text(subtitle, color = Color.White.copy(alpha = 0.72f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private fun nPlayDiscoverSave(
    save: DiscoverSave,
    onPlaySong: (Song, String) -> Unit,
    onOpenPodcast: (String?, String) -> Unit,
) {
    when {
        save.kind == DiscoverSave.KIND_RADIO && save.videoId != null -> {
            val song = Song(
                videoId = save.videoId!!,
                title = save.title,
                artist = save.subtitle ?: "Live radio",
                thumbnailUrl = save.thumbnailUrl,
                durationText = "LIVE",
            )
            onPlaySong(song, "Discover")
        }
        save.isPodcastEpisode -> {
            val song = Song(
                videoId = save.videoId ?: save.key,
                title = save.title,
                artist = save.subtitle ?: "",
                thumbnailUrl = save.thumbnailUrl,
            )
            onPlaySong(song, "Discover")
        }
        save.kind == DiscoverSave.KIND_PODCAST || save.browseId != null -> {
            onOpenPodcast(save.browseId ?: save.albumId ?: save.key, save.title)
        }
        save.videoId != null -> {
            val song = Song(
                videoId = save.videoId!!,
                title = save.title,
                artist = save.subtitle ?: "",
                thumbnailUrl = save.thumbnailUrl,
            )
            onPlaySong(song, "Discover")
        }
    }
}
