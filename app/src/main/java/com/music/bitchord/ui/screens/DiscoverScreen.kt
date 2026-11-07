package com.music.bitchord.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.music.bitchord.data.model.PlaybackSourceType
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.model.displayArtist
import com.music.bitchord.data.model.needsKisetsuRadioCover
import com.music.bitchord.data.settings.DiscoverKind
import com.music.bitchord.data.settings.DiscoverSave
import com.music.bitchord.data.settings.DiscoverSaves
import com.music.bitchord.data.settings.HomePin
import com.music.bitchord.data.settings.HomePins
import com.music.bitchord.data.sources.RadioApi
import com.music.bitchord.data.sources.SourceKind
import com.music.bitchord.data.sources.SourceRegistry
import com.music.bitchord.data.stats.PodcastResume
import com.music.bitchord.ui.components.rememberRemoteArtworkUrl
import com.music.bitchord.ui.theme.rememberArtworkPalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val KisetsuRed = Color(0xFFFA2D48)
private fun radioConfigId(): String = SourceRegistry.configs.value.firstOrNull { it.kind == SourceKind.RADIO }?.id ?: "radio"
private fun radioSave(st: RadioApi.RadioStation): DiscoverSave {
    val vid = SourceRegistry.trackKey(radioConfigId(), st.uuid)
    return DiscoverSave(key = vid, kind = DiscoverSave.KIND_RADIO, title = st.name,
        subtitle = listOfNotNull(st.country, st.tags?.split(',')?.firstOrNull()?.trim()).filter { !it.isNullOrBlank() }.joinToString(" · ").ifBlank { "Live radio" },
        thumbnailUrl = st.favicon, videoId = vid)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverScreen(
    currentSong: Song?,
    onPlaySong: (Song, String) -> Unit,
    onPlaySongs: (List<Song>, Int, String) -> Unit,
    onOpenPodcast: (String?, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val art = rememberRemoteArtworkUrl(currentSong)
    val kisetsu = currentSong?.needsKisetsuRadioCover() == true
    val palette = rememberArtworkPalette(if (kisetsu) null else art, dark = true)
    val bgTop = if (kisetsu) KisetsuRed else palette.wash
    val bgMid = if (kisetsu) Color(0xFF5C1020) else palette.background

    var query by rememberSaveable { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<DiscoverSave>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }

    LaunchedEffect(query) {
        val q = query.trim()
        if (q.length < 2) { searchResults = emptyList(); searching = false; return@LaunchedEffect }
        searching = true
        val radioConfigId = SourceRegistry.configs.value.firstOrNull { it.kind == SourceKind.RADIO }?.id ?: "radio"
        val radios = runCatching {
            withContext(Dispatchers.IO) {
                RadioApi.search(q, 18).map { st ->
                    val vid = SourceRegistry.trackKey(radioConfigId, st.uuid)
                    DiscoverSave(
                        key = vid,
                        kind = DiscoverSave.KIND_RADIO,
                        title = st.name,
                        subtitle = listOfNotNull(st.country, st.tags?.split(',')?.firstOrNull()?.trim()).filter { !it.isNullOrBlank() }.joinToString(" · ").ifBlank { "Live radio" },
                        thumbnailUrl = st.favicon,
                        videoId = vid,
                    )
                }
            }
        }.getOrDefault(emptyList())
        val podcasts = runCatching {
            YtMusicRepository.search(q, com.music.bitchord.data.model.SearchFilter.PODCASTS).getOrNull().orEmpty()
                .take(12)
                .mapNotNull { r ->
                    when (r) {
                        is com.music.bitchord.data.model.SearchResult.Track -> DiscoverSave.ofPodcast(r.song)
                        is com.music.bitchord.data.model.SearchResult.Browse -> DiscoverSave(
                            key = r.item.browseId,
                            kind = DiscoverSave.KIND_PODCAST,
                            title = r.item.title,
                            subtitle = r.item.subtitle,
                            thumbnailUrl = r.item.thumbnailUrl,
                            browseId = r.item.browseId,
                            albumId = r.item.browseId,
                            isPodcastEpisode = true,
                        )
                        else -> null
                    }
                }
        }.getOrDefault(emptyList()) + runCatching {
            YtMusicRepository.search(q, com.music.bitchord.data.model.SearchFilter.EPISODES).getOrNull().orEmpty()
                .take(10)
                .mapNotNull { (it as? com.music.bitchord.data.model.SearchResult.Track)?.song?.let(DiscoverSave::ofPodcast) }
        }.getOrDefault(emptyList())
        searchResults = (radios + podcasts).distinctBy { it.key }
        searching = false
    }

    var menuSave by remember { mutableStateOf<DiscoverSave?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(0f to bgTop.copy(alpha = 0.95f), 0.28f to bgMid, 0.7f to Color(0xFF121212), 1f to Color(0xFF090909))),
        contentPadding = PaddingValues(bottom = 120.dp),
    ) {
        item {
            Spacer(Modifier.height(14.dp))
            Text("Discover", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 16.dp))
            Text("Radio & podcasts", color = Color.White.copy(alpha = 0.72f), fontSize = 14.sp, modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 2.dp))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search radios and podcasts", color = Color.White.copy(alpha = 0.55f)) },
                leadingIcon = { Icon(Icons.Rounded.Search, null, tint = Color.White.copy(alpha = 0.7f)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White.copy(alpha = 0.28f), unfocusedBorderColor = Color.White.copy(alpha = 0.14f),
                    focusedContainerColor = Color.White.copy(alpha = 0.10f), unfocusedContainerColor = Color.White.copy(alpha = 0.06f),
                    cursorColor = Color.White,
                ),
            )
            Spacer(Modifier.height(10.dp))
        }

        if (query.trim().length >= 2) {
            item { SectionTitle("Results" + if (searching) "…" else "") }
            items(searchResults, key = { "res:${it.key}" }) { save ->
                DiscoverRow(save, onPlay = { playDiscover(save, onPlaySong, onOpenPodcast) }, onLongPress = { menuSave = save })
            }
            if (!searching && searchResults.isEmpty()) item { EmptyHint("No radios or podcasts found.") }
        } else {
            item { FestiveSection(onPlaySong, onOpenPodcast) { menuSave = it } }
            item { MyLibrarySection(onPlaySong, onOpenPodcast) { menuSave = it } }
            item { LikedSection(onPlaySong, onOpenPodcast) { menuSave = it } }
            item { RecentSection(onPlaySong, onOpenPodcast) { menuSave = it } }
            item { JumpBackInSection(onPlaySong) }
            item { ForYouSection(onPlaySong, onOpenPodcast) { menuSave = it } }
            item { SuggestedPopularSection(onPlaySong, onOpenPodcast) { menuSave = it } }
        }
    }

    val pending = menuSave
    if (pending != null) {
        val isRadio = pending.kind == DiscoverSave.KIND_RADIO
        AlertDialog(
            onDismissRequest = { menuSave = null },
            title = { Text(pending.title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
            text = { Text("Add to Quick Launch (Home), pin to Discover library, or save to liked ${if (isRadio) "radios" else "podcasts"}.") },
            confirmButton = {
                TextButton(onClick = {
                    HomePins.toggle(HomePin(videoId = pending.videoId, browseId = pending.browseId, title = pending.title, subtitle = pending.subtitle, thumbnailUrl = pending.thumbnailUrl, kind = if (isRadio) HomePin.KIND_RADIO else HomePin.KIND_SHOW))
                    menuSave = null
                }) { Text("Home Quick Launch") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = {
                        if (isRadio) DiscoverSaves.toggleLibraryRadio(pending.toSong()) else DiscoverSaves.toggleLibraryPodcast(pending.toSong())
                        menuSave = null
                    }) { Text("Discover Library") }
                    TextButton(onClick = {
                        if (isRadio) DiscoverSaves.toggleLikedRadio(pending.toSong()) else DiscoverSaves.toggleLikedPodcast(pending.toSong())
                        menuSave = null
                    }) { Text("Like") }
                }
            },
        )
    }
}

private fun playDiscover(save: DiscoverSave, onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit) {
    if (save.kind == DiscoverSave.KIND_PODCAST && save.videoId == null && save.browseId != null) {
        onOpenPodcast(save.browseId, save.title); return
    }
    val song = save.toSong()
    if (song.videoId.startsWith("src:")) DiscoverSaves.addRecentRadio(song) else if (song.isPodcastEpisode) DiscoverSaves.addRecentPodcast(song)
    onPlaySong(song, "Discover")
}

@Composable private fun SectionTitle(text: String) {
    Text(text, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 8.dp))
}
@Composable private fun EmptyHint(text: String) {
    Text(text, color = Color.White.copy(alpha = 0.55f), fontSize = 13.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable private fun DiscoverRow(save: DiscoverSave, onPlay: () -> Unit, onLongPress: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().combinedClickable(onClick = onPlay, onLongClick = onLongPress).padding(horizontal = 16.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val song = save.toSong()
        val radio = save.kind == DiscoverSave.KIND_RADIO
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)).background(if (song.needsKisetsuRadioCover() || (radio && save.thumbnailUrl.isNullOrBlank())) KisetsuRed else Color.White.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
            if (song.needsKisetsuRadioCover() || (radio && save.thumbnailUrl.isNullOrBlank())) {
                Image(painterResource(R.drawable.ic_launcher_foreground), null, modifier = Modifier.size(34.dp))
            } else {
                AsyncImage(model = save.thumbnailUrl?.artworkAt(160), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(save.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(if (radio) Icons.Rounded.Radio else Icons.Rounded.Mic, null, tint = Color.White.copy(alpha = 0.55f), modifier = Modifier.size(12.dp))
                Spacer(Modifier.width(4.dp))
                Text(save.subtitle.ifBlank { if (radio) "Live radio" else "Podcast" }, color = Color.White.copy(alpha = 0.62f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable private fun FestiveSection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit, onLongPress: (DiscoverSave) -> Unit) {
    SectionTitle("🎄 Festive")
    Text("Christmas-oriented radios and podcasts", color = Color.White.copy(alpha = 0.62f), fontSize = 13.sp, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp))
    val festive by produceState<List<DiscoverSave>>(initialValue = emptyList()) {
        val radios = runCatching { withContext(Dispatchers.IO) { RadioApi.search("christmas", 10).map(::radioSave) } }.getOrDefault(emptyList())
        val pods = runCatching { YtMusicRepository.search("christmas podcast", com.music.bitchord.data.model.SearchFilter.EPISODES).getOrNull().orEmpty().take(8).mapNotNull { (it as? com.music.bitchord.data.model.SearchResult.Track)?.song?.let(DiscoverSave::ofPodcast) } }.getOrDefault(emptyList())
        value = (radios + pods).distinctBy { it.key }.take(16)
    }
    if (festive.isEmpty()) EmptyHint("Loading festive picks…") else festive.forEach { DiscoverRow(it, { playDiscover(it, onPlaySong, onOpenPodcast) }, { onLongPress(it) }) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable private fun MyLibrarySection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit, onLongPress: (DiscoverSave) -> Unit) {
    val radios by DiscoverSaves.libraryRadios.collectAsStateWithLifecycle()
    val pods by DiscoverSaves.libraryPodcasts.collectAsStateWithLifecycle()
    val all = (radios + pods).sortedByDescending { it.savedAt }
    SectionTitle("My Library")
    Text("Pinned radios and podcasts", color = Color.White.copy(alpha = 0.62f), fontSize = 13.sp, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp))
    if (all.isEmpty()) { EmptyHint("Long-press a radio or podcast and add it to Discover Library.") ; return }
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(all, key = { "lib:${it.key}" }) { save ->
            Column(Modifier.width(92.dp).clip(RoundedCornerShape(10.dp)).combinedClickable(onClick = { playDiscover(save, onPlaySong, onOpenPodcast) }, onLongClick = { onLongPress(save) }).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                val radio = save.kind == DiscoverSave.KIND_RADIO
                val coverless = radio && save.thumbnailUrl.isNullOrBlank()
                Box(Modifier.size(76.dp).clip(if (radio) CircleShape else RoundedCornerShape(12.dp)).background(if (coverless) KisetsuRed else Color.White.copy(alpha = 0.08f)), contentAlignment = Alignment.Center) {
                    if (coverless) Image(painterResource(R.drawable.ic_launcher_foreground), null, modifier = Modifier.size(48.dp))
                    else AsyncImage(model = save.thumbnailUrl?.artworkAt(220), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                }
                Spacer(Modifier.height(6.dp))
                Text(save.title, color = Color.White, fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable private fun LikedSection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit, onLongPress: (DiscoverSave) -> Unit) {
    val radios by DiscoverSaves.likedRadios.collectAsStateWithLifecycle()
    val pods by DiscoverSaves.likedPodcasts.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(0) }
    SectionTitle("Liked")
    ScrollableTabRow(selectedTabIndex = tab, containerColor = Color.Transparent, contentColor = Color.White, edgePadding = 16.dp, divider = {}) {
        Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Radios") })
        Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Podcasts") })
    }
    val list = if (tab == 0) radios else pods
    if (list.isEmpty()) EmptyHint(if (tab == 0) "No liked radios yet." else "No liked podcasts yet.")
    else list.take(20).forEach { DiscoverRow(it, { playDiscover(it, onPlaySong, onOpenPodcast) }, { onLongPress(it) }) }
}

@Composable private fun RecentSection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit, onLongPress: (DiscoverSave) -> Unit) {
    val radios by DiscoverSaves.recentRadios.collectAsStateWithLifecycle()
    val pods by DiscoverSaves.recentPodcasts.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableStateOf(0) }
    SectionTitle("Recent")
    ScrollableTabRow(selectedTabIndex = tab, containerColor = Color.Transparent, contentColor = Color.White, edgePadding = 16.dp, divider = {}) {
        Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Radios") })
        Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Podcasts") })
    }
    val list = if (tab == 0) radios else pods
    if (list.isEmpty()) EmptyHint(if (tab == 0) "No recent radios yet." else "No recent podcasts yet.")
    else list.take(15).forEach { DiscoverRow(it, { playDiscover(it, onPlaySong, onOpenPodcast) }, { onLongPress(it) }) }
}

@Composable private fun JumpBackInSection(onPlaySong: (Song, String) -> Unit) {
    val resumes by PodcastResume.entries.collectAsStateWithLifecycle()
    if (resumes.isEmpty()) return
    SectionTitle("Jump back in")
    Text("Podcasts — pick up where you left off", color = Color.White.copy(alpha = 0.62f), fontSize = 13.sp, modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp))
    resumes.take(10).forEach { bm ->
        val save = DiscoverSave(key = bm.videoId, kind = DiscoverSave.KIND_PODCAST, title = bm.title, subtitle = bm.showName ?: "Podcast", thumbnailUrl = bm.thumbnailUrl, videoId = bm.videoId, isPodcastEpisode = true)
        DiscoverRow(save, onPlay = { onPlaySong(save.toSong().copy(videoId = bm.videoId), "Discover") }, onLongPress = {})
    }
}

@Composable private fun ForYouSection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit, onLongPress: (DiscoverSave) -> Unit) {
    val recentRadios by DiscoverSaves.recentRadios.collectAsStateWithLifecycle()
    val liked by DiscoverSaves.likedPodcasts.collectAsStateWithLifecycle()
    SectionTitle("For You")
    val seed = recentRadios.firstOrNull()?.title?.substringBefore(" ") ?: "music"
    val picks by produceState<List<DiscoverSave>>(initialValue = emptyList(), seed) {
        val radios = runCatching { withContext(Dispatchers.IO) { RadioApi.search(seed, 8).map(::radioSave) } }.getOrDefault(emptyList())
        val pods = runCatching { YtMusicRepository.search("popular podcast", com.music.bitchord.data.model.SearchFilter.PODCASTS).getOrNull().orEmpty().take(6).mapNotNull { r -> (r as? com.music.bitchord.data.model.SearchResult.Track)?.song?.let(DiscoverSave::ofPodcast) ?: (r as? com.music.bitchord.data.model.SearchResult.Browse)?.let { DiscoverSave(key=it.item.browseId, kind=DiscoverSave.KIND_PODCAST, title=it.item.title, subtitle=it.item.subtitle, thumbnailUrl=it.item.thumbnailUrl, browseId=it.item.browseId, albumId=it.item.browseId, isPodcastEpisode=true) } } }.getOrDefault(emptyList())
        value = (radios + pods).distinctBy { it.key }.take(12)
    }
    if (picks.isEmpty()) EmptyHint("Suggestions will appear as you listen.") else picks.forEach { DiscoverRow(it, { playDiscover(it, onPlaySong, onOpenPodcast) }, { onLongPress(it) }) }
}

@Composable private fun SuggestedPopularSection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit, onLongPress: (DiscoverSave) -> Unit) {
    SectionTitle("Suggested radios & podcasts")
    val popular by produceState<List<DiscoverSave>>(initialValue = emptyList()) {
        val radios = runCatching { withContext(Dispatchers.IO) {
            val body = okhttp3.OkHttpClient().newCall(okhttp3.Request.Builder().url("https://de1.api.radio-browser.info/json/stations/topvote/limit/12").header("User-Agent", RadioApi.USER_AGENT).build()).execute().body?.string().orEmpty()
            kotlinx.serialization.json.Json { ignoreUnknownKeys = true }.parseToJsonElement(body).let { it as? kotlinx.serialization.json.JsonArray }.orEmpty().mapNotNull { el ->
                val s = el as? kotlinx.serialization.json.JsonObject ?: return@mapNotNull null
                val uuid = s["stationuuid"]?.toString()?.trim('"') ?: return@mapNotNull null
                val stream = s["url_resolved"]?.toString()?.trim('"') ?: s["url"]?.toString()?.trim('"') ?: return@mapNotNull null
                val vid = SourceRegistry.trackKey(radioConfigId(), uuid)
                DiscoverSave(key=vid, kind=DiscoverSave.KIND_RADIO, title=s["name"]?.toString()?.trim('"').orEmpty().ifBlank{"Radio"}, subtitle=s["country"]?.toString()?.trim('"').orEmpty().ifBlank{"Live radio"}, thumbnailUrl=s["favicon"]?.toString()?.trim('"')?.takeIf { it.startsWith("http") }, videoId=vid)
            }
        } }.getOrDefault(emptyList())
        val pods = runCatching { YtMusicRepository.search("news podcast", com.music.bitchord.data.model.SearchFilter.PODCASTS).getOrNull().orEmpty().take(8).mapNotNull { (it as? com.music.bitchord.data.model.SearchResult.Track)?.song?.let(DiscoverSave::ofPodcast) } }.getOrDefault(emptyList())
        value = (radios + pods).distinctBy { it.key }
    }
    if (popular.isEmpty()) EmptyHint("Loading popular stations and podcasts…") else popular.forEach { DiscoverRow(it, { playDiscover(it, onPlaySong, onOpenPodcast) }, { onLongPress(it) }) }
}
