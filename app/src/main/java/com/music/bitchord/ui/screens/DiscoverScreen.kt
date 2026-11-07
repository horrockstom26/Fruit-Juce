package com.music.bitchord.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.music.bitchord.data.model.SearchResult
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.model.needsKisetsuRadioCover
import com.music.bitchord.data.settings.DiscoverSave
import com.music.bitchord.data.settings.DiscoverSaves
import com.music.bitchord.data.settings.HomePin
import com.music.bitchord.data.settings.HomePins
import com.music.bitchord.data.sources.RadioApi
import com.music.bitchord.data.sources.SourceKind
import com.music.bitchord.data.sources.SourceRegistry
import com.music.bitchord.data.stats.PodcastBookmark
import com.music.bitchord.data.stats.PodcastResume
import com.music.bitchord.ui.components.rememberRemoteArtworkUrl
import com.music.bitchord.ui.theme.rememberArtworkPalette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val KisetsuRed = Color(0xFFFA2D48)

private fun radioConfigId(): String = SourceRegistry.configs.value.firstOrNull { it.kind == SourceKind.RADIO }?.id ?: "radio"
private fun radioSave(st: RadioApi.RadioStation): DiscoverSave {
    val vid = SourceRegistry.trackKey(radioConfigId(), st.uuid)
    return DiscoverSave(
        key = vid, kind = DiscoverSave.KIND_RADIO, title = st.name,
        subtitle = listOfNotNull(st.country, st.tags?.split(',')?.firstOrNull()?.trim()).filter { !it.isNullOrBlank() }.joinToString(" · ").ifBlank { "Live radio" },
        thumbnailUrl = st.favicon, videoId = vid,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiscoverScreen(
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
    val bgTop = if (kisetsu) KisetsuRed else palette.wash
    val bgMid = if (kisetsu) Color(0xFF5C1020) else palette.background

    var query by rememberSaveable { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<DiscoverSave>>(emptyList()) }
    var searching by remember { mutableStateOf(false) }

    LaunchedEffect(query) {
        val q = query.trim()
        if (q.length < 2) { searchResults = emptyList(); searching = false; return@LaunchedEffect }
        searching = true
        val radios = runCatching { withContext(Dispatchers.IO) { RadioApi.search(q, 15).map(::radioSave) } }.getOrDefault(emptyList())
        val podcasts = runCatching {
            YtMusicRepository.search(q, com.music.bitchord.data.model.SearchFilter.PODCASTS).getOrNull().orEmpty().take(10)
                .mapNotNull { r ->
                    when (r) {
                        is SearchResult.Track -> DiscoverSave.ofPodcast(r.song)
                        is SearchResult.Browse -> DiscoverSave(key = r.item.browseId, kind = DiscoverSave.KIND_PODCAST, title = r.item.title, subtitle = r.item.subtitle, thumbnailUrl = r.item.thumbnailUrl, browseId = r.item.browseId, albumId = r.item.browseId, isPodcastEpisode = true)
                        else -> null
                    }
                }
        }.getOrDefault(emptyList()) + runCatching {
            YtMusicRepository.search(q, com.music.bitchord.data.model.SearchFilter.EPISODES).getOrNull().orEmpty().take(8)
                .mapNotNull { (it as? SearchResult.Track)?.song?.let(DiscoverSave::ofPodcast) }
        }.getOrDefault(emptyList())
        searchResults = (radios + podcasts).distinctBy { it.key }
        searching = false
    }

    val topPad = contentPadding.calculateTopPadding()
    val bottomPad = contentPadding.calculateBottomPadding() + 120.dp

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(0f to bgTop.copy(alpha = 0.95f), 0.28f to bgMid, 0.7f to Color(0xFF121212), 1f to Color(0xFF090909))),
        contentPadding = PaddingValues(top = topPad, bottom = bottomPad),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            Text("Discover", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(10.dp))
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
            Spacer(Modifier.height(6.dp))
        }

        if (query.trim().length >= 2) {
            item { SectionHeader(if (searching) "Searching…" else "Results") }
            item { DiscoverShelfRow(searchResults, onPlay = { playDiscover(it, onPlaySong, onOpenPodcast) }, onLongPressSave = null) }
            if (!searching && searchResults.isEmpty()) item { EmptyHint("No radios or podcasts found.") }
        } else {
            item { MyLibrarySection(onPlaySong, onOpenPodcast) }
            item { LikedSection(onPlaySong, onOpenPodcast) }
            item { RecentSection(onPlaySong, onOpenPodcast) }
            item { JumpBackInSection(onResumePodcast) }
            item { ForYouSection(onPlaySong, onOpenPodcast) }
            item { SuggestedSection(onPlaySong, onOpenPodcast) }
        }
    }
}

private fun playDiscover(save: DiscoverSave, onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit) {
    if (save.kind == DiscoverSave.KIND_PODCAST && save.videoId == null && save.browseId != null) {
        onOpenPodcast(save.browseId, save.title); return
    }
    val song = save.toSong()
    if (song.videoId.startsWith("src:")) DiscoverSaves.addRecentRadio(song) else DiscoverSaves.addRecentPodcast(song)
    onPlaySong(song, "Discover")
}

@Composable
private fun SectionHeader(title: String, subtitle: String? = null) {
    Column(Modifier.padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 6.dp)) {
        Text(title, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        if (!subtitle.isNullOrBlank()) Text(subtitle, color = Color.White.copy(alpha = 0.62f), fontSize = 12.sp)
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(text, color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DiscoverShelfRow(
    saves: List<DiscoverSave>,
    onPlay: (DiscoverSave) -> Unit,
    onLongPressSave: ((DiscoverSave) -> Unit)? = null,
    menuFor: (@Composable (DiscoverSave, () -> Unit) -> Unit)? = null,
) {
    if (saves.isEmpty()) return
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(saves.take(20), key = { it.key }) { save ->
            var menuOpen by remember { mutableStateOf(false) }
            Box {
                ShelfCard(save = save, onClick = { onPlay(save) }, onLongClick = {
                    if (menuFor != null) menuOpen = true else onLongPressSave?.invoke(save)
                })
                if (menuFor != null) {
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                        menuFor(save) { menuOpen = false }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShelfCard(save: DiscoverSave, onClick: () -> Unit, onLongClick: () -> Unit) {
    val radio = save.kind == DiscoverSave.KIND_RADIO
    val coverless = radio && save.thumbnailUrl.isNullOrBlank()
    val shape = if (radio) CircleShape else RoundedCornerShape(10.dp)
    Column(
        modifier = Modifier
            .width(118.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
    ) {
        Box(
            modifier = Modifier
                .size(118.dp)
                .clip(shape)
                .background(if (coverless) KisetsuRed else Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            if (coverless) {
                Image(painterResource(R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(72.dp))
            } else {
                AsyncImage(model = save.thumbnailUrl?.artworkAt(240), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(save.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Text(save.subtitle.ifBlank { if (radio) "Radio" else "Podcast" }, color = Color.White.copy(alpha = 0.60f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun MenuCommon(save: DiscoverSave, dismiss: () -> Unit) {
    val radio = save.kind == DiscoverSave.KIND_RADIO
    DropdownMenuItem(text = { Text("Add to Home Quick Launch") }, onClick = {
        HomePins.toggle(HomePin(videoId = save.videoId, browseId = save.browseId, title = save.title, subtitle = save.subtitle, thumbnailUrl = save.thumbnailUrl, kind = if (radio) HomePin.KIND_RADIO else HomePin.KIND_SHOW))
        dismiss()
    })
    DropdownMenuItem(text = { Text(if (radio) (if (DiscoverSaves.isLibraryRadio(save.key)) "Remove from Discover Library" else "Pin to Discover Library") else (if (DiscoverSaves.isLibraryPodcast(save.key)) "Remove from Discover Library" else "Pin to Discover Library")) }, onClick = {
        if (radio) DiscoverSaves.toggleLibraryRadio(save.toSong()) else DiscoverSaves.toggleLibraryPodcast(save.toSong()); dismiss()
    })
}

@Composable
private fun MyLibrarySection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit) {
    val radios by DiscoverSaves.libraryRadios.collectAsStateWithLifecycle()
    val pods by DiscoverSaves.libraryPodcasts.collectAsStateWithLifecycle()
    val all = (radios + pods).sortedByDescending { it.savedAt }
    SectionHeader("My Library", "Radios and podcasts")
    if (all.isEmpty()) { EmptyHint("Long-press items to pin here.") } else {
        DiscoverShelfRow(all, onPlay = { playDiscover(it, onPlaySong, onOpenPodcast) }, menuFor = { save, dismiss ->
            MenuCommon(save, dismiss)
            val radio = save.kind == DiscoverSave.KIND_RADIO
            DropdownMenuItem(text = { Text("Remove") }, onClick = {
                if (radio) DiscoverSaves.removeLibraryRadio(save.key) else DiscoverSaves.removeLibraryPodcast(save.key); dismiss()
            })
        })
    }
}

@Composable
private fun LikedSection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit) {
    val radios by DiscoverSaves.likedRadios.collectAsStateWithLifecycle()
    val pods by DiscoverSaves.likedPodcasts.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    SectionHeader("Liked")
    ScrollableTabRow(selectedTabIndex = tab, containerColor = Color.Transparent, contentColor = Color.White, edgePadding = 16.dp, divider = {}) {
        Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Radios") })
        Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Podcasts") })
    }
    Spacer(Modifier.height(6.dp))
    val list = if (tab == 0) radios else pods
    if (list.isEmpty()) EmptyHint(if (tab == 0) "No liked radios yet." else "No liked podcasts yet.")
    else DiscoverShelfRow(list, onPlay = { playDiscover(it, onPlaySong, onOpenPodcast) }, menuFor = { save, dismiss ->
        MenuCommon(save, dismiss)
        DropdownMenuItem(text = { Text("Remove from liked") }, onClick = {
            if (save.kind == DiscoverSave.KIND_RADIO) DiscoverSaves.removeLikedRadio(save.key) else DiscoverSaves.removeLikedPodcast(save.key); dismiss()
        })
    })
}

@Composable
private fun RecentSection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit) {
    val radios by DiscoverSaves.recentRadios.collectAsStateWithLifecycle()
    val pods by DiscoverSaves.recentPodcasts.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableIntStateOf(0) }
    SectionHeader("Recent")
    ScrollableTabRow(selectedTabIndex = tab, containerColor = Color.Transparent, contentColor = Color.White, edgePadding = 16.dp, divider = {}) {
        Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Radios") })
        Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Podcasts") })
    }
    Spacer(Modifier.height(6.dp))
    val list = if (tab == 0) radios else pods
    if (list.isEmpty()) EmptyHint(if (tab == 0) "No recent radios yet." else "No recent podcasts yet.")
    else DiscoverShelfRow(list, onPlay = { playDiscover(it, onPlaySong, onOpenPodcast) }, menuFor = { save, dismiss ->
        MenuCommon(save, dismiss)
        DropdownMenuItem(text = { Text("Remove from recent") }, onClick = {
            if (save.kind == DiscoverSave.KIND_RADIO) DiscoverSaves.removeRecentRadio(save.key) else DiscoverSaves.removeRecentPodcast(save.key); dismiss()
        })
    })
}

@Composable
private fun JumpBackInSection(onResumePodcast: (PodcastBookmark) -> Unit) {
    val resumes by PodcastResume.entries.collectAsStateWithLifecycle()
    if (resumes.isEmpty()) return
    SectionHeader("Jump back in", "Podcasts")
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(resumes.take(20), key = { it.videoId }) { bm ->
            var menuOpen by remember { mutableStateOf(false) }
            val save = DiscoverSave(key = bm.videoId, kind = DiscoverSave.KIND_PODCAST, title = bm.title, subtitle = bm.showName, thumbnailUrl = bm.thumbnailUrl, videoId = bm.videoId, isPodcastEpisode = true)
            Box {
                Column(Modifier.width(118.dp).combinedClickable(onClick = { onResumePodcast(bm) }, onLongClick = { menuOpen = true })) {
                    Box(Modifier.size(118.dp).clip(RoundedCornerShape(10.dp)).background(Color.White.copy(alpha = 0.08f))) {
                        AsyncImage(model = bm.thumbnailUrl?.artworkAt(240), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(bm.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    val left = if (bm.durationMs > 0) "${((bm.durationMs - bm.positionMs).coerceAtLeast(0L)) / 60000}m left" else "Resume"
                    Text(left, color = Color.White.copy(alpha = 0.60f), fontSize = 11.sp, maxLines = 1)
                }
                DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    DropdownMenuItem(text = { Text("Resume") }, onClick = { onResumePodcast(bm); menuOpen = false })
                    DropdownMenuItem(text = { Text("Start from beginning") }, onClick = { onResumePodcast(bm.copy(positionMs = 0L)); menuOpen = false })
                    DropdownMenuItem(text = { Text("Remove from Jump back in") }, onClick = { PodcastResume.remove(bm.videoId); menuOpen = false })
                }
            }
        }
    }
}

@Composable
private fun ForYouSection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit) {
    val recentRadios by DiscoverSaves.recentRadios.collectAsStateWithLifecycle()
    val seed = recentRadios.firstOrNull()?.title?.substringBefore(" ") ?: "music"
    val picks by produceState<List<DiscoverSave>>(initialValue = emptyList(), seed) {
        val radios = runCatching { withContext(Dispatchers.IO) { RadioApi.search(seed, 8).map(::radioSave) } }.getOrDefault(emptyList())
        val pods = runCatching {
            YtMusicRepository.search("podcast", com.music.bitchord.data.model.SearchFilter.PODCASTS).getOrNull().orEmpty().take(6)
                .mapNotNull { r -> (r as? SearchResult.Track)?.song?.let(DiscoverSave::ofPodcast) ?: (r as? SearchResult.Browse)?.let { DiscoverSave(key = it.item.browseId, kind = DiscoverSave.KIND_PODCAST, title = it.item.title, subtitle = it.item.subtitle, thumbnailUrl = it.item.thumbnailUrl, browseId = it.item.browseId, albumId = it.item.browseId, isPodcastEpisode = true) } }
        }.getOrDefault(emptyList())
        value = (radios + pods).distinctBy { it.key }.take(14)
    }
    SectionHeader("For You", "Radios and podcasts")
    if (picks.isEmpty()) EmptyHint("Suggestions will appear here.") else DiscoverShelfRow(picks, onPlay = { playDiscover(it, onPlaySong, onOpenPodcast) }, menuFor = { save, dismiss -> MenuCommon(save, dismiss) })
}

@Composable
private fun SuggestedSection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit) {
    val popular by produceState<List<DiscoverSave>>(initialValue = emptyList()) {
        val radios = runCatching { withContext(Dispatchers.IO) { RadioApi.search("pop", 8).map(::radioSave) } }.getOrDefault(emptyList())
        val pods = runCatching { YtMusicRepository.search("trending podcast", com.music.bitchord.data.model.SearchFilter.PODCASTS).getOrNull().orEmpty().take(6).mapNotNull { (it as? SearchResult.Track)?.song?.let(DiscoverSave::ofPodcast) } }.getOrDefault(emptyList())
        value = (radios + pods).distinctBy { it.key }.take(14)
    }
    SectionHeader("Popular", "Radios and podcasts")
    if (popular.isEmpty()) EmptyHint("Loading…") else DiscoverShelfRow(popular, onPlay = { playDiscover(it, onPlaySong, onOpenPodcast) }, menuFor = { save, dismiss -> MenuCommon(save, dismiss) })
}
