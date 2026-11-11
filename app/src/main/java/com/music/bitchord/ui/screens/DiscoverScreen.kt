package com.music.bitchord.ui.screens

import androidx.activity.compose.BackHandler
import coil3.compose.AsyncImagePainter
import com.music.bitchord.ui.components.resolvedSongArtwork
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
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
    var discoverFilter by rememberSaveable { mutableIntStateOf(0) } // 0 all, 1 radios, 2 podcasts

    val scope = rememberCoroutineScope()
    BackHandler(enabled = query.isNotBlank()) { query = "" }

    LaunchedEffect(query) {
        val q = query.trim()
        if (q.length < 2) { searchResults = emptyList(); searching = false; return@LaunchedEffect }
        searching = true
        val radios = runCatching { withContext(Dispatchers.IO) { RadioApi.search(q, 15).map(::radioSave) } }.getOrDefault(emptyList())
        val podcasts = runCatching {
            YtMusicRepository.search(q, com.music.bitchord.data.model.SearchFilter.PODCASTS).getOrNull().orEmpty().take(10)
                .mapNotNull { r ->
                    when (r) {
                        is SearchResult.Track -> DiscoverSave.ofEpisode(r.song)
                        is SearchResult.Browse -> DiscoverSave(key = r.item.browseId, kind = DiscoverSave.KIND_PODCAST, title = r.item.title, subtitle = r.item.subtitle, thumbnailUrl = r.item.thumbnailUrl, browseId = r.item.browseId, albumId = r.item.browseId, isPodcastEpisode = false)
                        else -> null
                    }
                }
        }.getOrDefault(emptyList()) + runCatching {
            YtMusicRepository.search(q, com.music.bitchord.data.model.SearchFilter.EPISODES).getOrNull().orEmpty().take(10)
                .mapNotNull { (it as? SearchResult.Track)?.song?.let(DiscoverSave::ofEpisode) }
        }.getOrDefault(emptyList()) + runCatching {
            YtMusicRepository.search(q, com.music.bitchord.data.model.SearchFilter.VIDEOS).getOrNull().orEmpty().take(10)
                .mapNotNull { (it as? SearchResult.Track)?.song?.let { song ->
                    val label = song.artist.takeIf { it.isNotBlank() }?.let { "YouTube · $it" } ?: "YouTube"
                    DiscoverSave.ofEpisode(song, subtitle = label)
                } }
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
                placeholder = { Text("Search radios and podcasts", color = Color.White.copy(alpha = 0.6f)) },
                leadingIcon = { Icon(Icons.Rounded.Search, null, tint = Color.White.copy(alpha = 0.75f)) },
                trailingIcon = if (query.isNotEmpty()) {
                    { Icon(Icons.Rounded.Close, contentDescription = "Back", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.clickable { query = "" }) }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent, errorBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White.copy(alpha = 0.14f), unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                    cursorColor = Color.White,
                ),
            )
            Spacer(Modifier.height(6.dp))
        }

        if (query.trim().length >= 2) {
            val filtered = when (discoverFilter) {
                1 -> searchResults.filter { it.kind == DiscoverSave.KIND_RADIO }
                2 -> searchResults.filter { it.kind != DiscoverSave.KIND_RADIO }
                else -> searchResults
            }
            item { SectionHeader(if (searching) "Searching…" else "Results") }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    listOf("All" to 0, "Radios" to 1, "Podcasts" to 2).forEach { (label, idx) ->
                        val selected = discoverFilter == idx
                        val accent = MaterialTheme.colorScheme.primary
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) accent else Color.White.copy(alpha = 0.12f))
                                .clickable { discoverFilter = idx }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(label, color = if (selected) MaterialTheme.colorScheme.onPrimary else Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (!searching && filtered.isEmpty()) {
                item { EmptyHint("No radios or podcasts found.") }
            } else {
                items(filtered, key = { "sr:${it.key}" }) { save ->
                    DiscoverResultRow(
                        save = save,
                        onPlay = { playDiscover(save, onPlaySong, onOpenPodcast) },
                        menuContent = { dismiss ->
                            MenuCommon(save, dismiss)
                            val radio = save.kind == DiscoverSave.KIND_RADIO
                            DropdownMenuItem(text = { Text(if (radio) (if (DiscoverSaves.isLikedRadio(save.key)) "Remove from liked radios" else "Add to liked radios") else (if (DiscoverSaves.isLikedPodcast(save.key)) "Remove from liked podcasts" else "Add to liked podcasts")) }, onClick = {
                                if (radio) DiscoverSaves.toggleLikedRadio(save.toSong()) else DiscoverSaves.toggleLikedPodcast(save.toSong()); dismiss()
                            })
                        },
                    )
                }
            }
        } else {
            item {
                val genres = listOf("Lo-fi", "Jazz", "Pop", "Rock", "Classical", "Hip-Hop", "News", "Chill")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp, bottom = 4.dp),
                ) {
                    item {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.16f))
                                .clickable {
                                    scope.launch {
                                        val picks = listOf("top hits", "popular", "chill", "jazz", "lofi")
                                        val term = picks.random()
                                        val station = runCatching { withContext(Dispatchers.IO) { RadioApi.search(term, 20).randomOrNull() } }.getOrNull()
                                        if (station != null) {
                                            val save = radioSave(station)
                                            DiscoverSaves.addRecentRadio(save.toSong())
                                            onPlaySong(save.toSong(), "Discover")
                                        }
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text("Surprise me", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    items(genres) { g ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(Color.White.copy(alpha = 0.10f))
                                .clickable { query = g }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                        ) {
                            Text(g, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            item { MyLibrarySection(onPlaySong, onOpenPodcast) }
            item { LikedSection(onPlaySong, onOpenPodcast) }
            item { ListenLaterSection(onPlaySong, onOpenPodcast) }
            item { RecentSection(onPlaySong, onOpenPodcast) }
            item { JumpBackInSection(onResumePodcast) }
            item { ForYouSection(onPlaySong, onOpenPodcast) }
            item { SuggestedSection(onPlaySong, onOpenPodcast) }
        }
    }
}

private fun playDiscover(save: DiscoverSave, onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit) {
    if (save.kind == DiscoverSave.KIND_PODCAST) {
        val playableEpisode = !save.videoId.isNullOrBlank() && save.browseId.isNullOrBlank() && (save.key == save.videoId || save.albumId.isNullOrBlank())
        if (!playableEpisode) {
            val showId = save.browseId ?: save.albumId
            if (!showId.isNullOrBlank()) {
                onOpenPodcast(showId, save.title)
                return
            }
        }
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
    val browseCovers by com.music.bitchord.data.cover.BrowseThumbnails.covers.collectAsStateWithLifecycle()
    val browseId = (save.browseId ?: save.albumId
        ?: save.key.takeIf { k -> k.startsWith("MP") || k.startsWith("VL") || k.startsWith("UC") || k.startsWith("PL") })
        ?.takeUnless { it.startsWith("local:") || it.startsWith("src:") }
    val resolvedBrowse = browseId?.let { browseCovers[it] }
    LaunchedEffect(browseId, save.thumbnailUrl, resolvedBrowse) {
        if (browseId != null && save.thumbnailUrl.isNullOrBlank() && resolvedBrowse.isNullOrBlank()) {
            val thumb = com.music.bitchord.data.YtMusicRepository.browseSongs(browseId)
                .getOrNull()?.header?.thumbnailUrl
            if (!thumb.isNullOrBlank()) com.music.bitchord.data.cover.BrowseThumbnails.put(browseId, thumb)
        }
    }
    val effectiveThumb = save.thumbnailUrl ?: resolvedBrowse
        ?: resolvedSongArtwork(save.videoId, save.thumbnailUrl)
    // A directory favicon can be handed over and then fail to load — a dead
    // link, an http-only host. That is a blank tile just as much as no URL at
    // all is, and drawn with the same fallback once the load fails.
    var artFailed by remember(effectiveThumb) { mutableStateOf(false) }
    val coverless = effectiveThumb.isNullOrBlank() || artFailed
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
                AsyncImage(
                    model = effectiveThumb?.artworkAt(240),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onState = { state -> if (state is AsyncImagePainter.State.Error) artFailed = true },
                )
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
    if (!radio) {
        DropdownMenuItem(text = { Text(if (DiscoverSaves.isListenLaterPodcast(save.key)) "Remove from Listen later" else "Listen later") }, onClick = {
            DiscoverSaves.toggleListenLaterPodcast(save.toSong()); dismiss()
        })
    }
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
    ScrollableTabRow(selectedTabIndex = tab, containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary, edgePadding = 16.dp, divider = {}) {
        Tab(selected = tab == 0, onClick = { tab = 0 }, selectedContentColor = MaterialTheme.colorScheme.primary, unselectedContentColor = Color.White.copy(alpha = 0.9f), text = { Text("Radios") })
        Tab(selected = tab == 1, onClick = { tab = 1 }, selectedContentColor = MaterialTheme.colorScheme.primary, unselectedContentColor = Color.White.copy(alpha = 0.9f), text = { Text("Podcasts") })
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
    ScrollableTabRow(selectedTabIndex = tab, containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.primary, edgePadding = 16.dp, divider = {}) {
        Tab(selected = tab == 0, onClick = { tab = 0 }, selectedContentColor = MaterialTheme.colorScheme.primary, unselectedContentColor = Color.White.copy(alpha = 0.9f), text = { Text("Radios") })
        Tab(selected = tab == 1, onClick = { tab = 1 }, selectedContentColor = MaterialTheme.colorScheme.primary, unselectedContentColor = Color.White.copy(alpha = 0.9f), text = { Text("Podcasts") })
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
            val bmArt = resolvedSongArtwork(bm.videoId, bm.thumbnailUrl)
            val save = DiscoverSave(key = bm.videoId, kind = DiscoverSave.KIND_PODCAST, title = bm.title, subtitle = bm.showName, thumbnailUrl = bmArt, videoId = bm.videoId, isPodcastEpisode = true)
            Box {
                Column(Modifier.width(118.dp).combinedClickable(onClick = { onResumePodcast(bm) }, onLongClick = { menuOpen = true })) {
                    Box(Modifier.size(118.dp).clip(RoundedCornerShape(10.dp)).background(if (bmArt.isNullOrBlank()) KisetsuRed else Color.White.copy(alpha = 0.08f))) {
                        if (bmArt.isNullOrBlank()) {
                            Image(painterResource(R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(72.dp))
                        } else {
                            AsyncImage(model = bmArt.artworkAt(240), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                        }
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
private fun ListenLaterSection(onPlaySong: (Song, String) -> Unit, onOpenPodcast: (String?, String) -> Unit) {
    val list by DiscoverSaves.listenLaterPodcasts.collectAsStateWithLifecycle()
    SectionHeader("Listen later", "Podcasts saved for later")
    if (list.isEmpty()) {
        EmptyHint("Save podcasts to listen later from search or menus.")
    } else {
        DiscoverShelfRow(list, onPlay = { playDiscover(it, onPlaySong, onOpenPodcast) }, menuFor = { save, dismiss ->
            MenuCommon(save, dismiss)
            DropdownMenuItem(text = { Text("Remove from Listen later") }, onClick = {
                DiscoverSaves.removeListenLaterPodcast(save.key); dismiss()
            })
        })
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
                .mapNotNull { r -> (r as? SearchResult.Track)?.song?.let(DiscoverSave::ofEpisode) ?: (r as? SearchResult.Browse)?.let { DiscoverSave(key = it.item.browseId, kind = DiscoverSave.KIND_PODCAST, title = it.item.title, subtitle = it.item.subtitle, thumbnailUrl = it.item.thumbnailUrl, browseId = it.item.browseId, albumId = it.item.browseId, isPodcastEpisode = false) } }
        }.getOrDefault(emptyList()) + runCatching {
            YtMusicRepository.search("short stories", com.music.bitchord.data.model.SearchFilter.VIDEOS).getOrNull().orEmpty().take(4)
                .mapNotNull { (it as? SearchResult.Track)?.song?.let { s -> DiscoverSave.ofEpisode(s, subtitle = s.artist.takeIf { it.isNotBlank() }?.let { "YouTube · $it" } ?: "YouTube") } }
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
        val pods = runCatching { YtMusicRepository.search("trending podcast", com.music.bitchord.data.model.SearchFilter.EPISODES).getOrNull().orEmpty().take(6).mapNotNull { (it as? SearchResult.Track)?.song?.let(DiscoverSave::ofEpisode) } }.getOrDefault(emptyList()) + runCatching {
            YtMusicRepository.search("story", com.music.bitchord.data.model.SearchFilter.VIDEOS).getOrNull().orEmpty().take(4)
                .mapNotNull { (it as? SearchResult.Track)?.song?.let { s -> DiscoverSave.ofEpisode(s, subtitle = s.artist.takeIf { it.isNotBlank() }?.let { "YouTube · $it" } ?: "YouTube") } }
        }.getOrDefault(emptyList())
        value = (radios + pods).distinctBy { it.key }.take(14)
    }
    SectionHeader("Popular", "Radios and podcasts")
    if (popular.isEmpty()) EmptyHint("Loading…") else DiscoverShelfRow(popular, onPlay = { playDiscover(it, onPlaySong, onOpenPodcast) }, menuFor = { save, dismiss -> MenuCommon(save, dismiss) })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DiscoverResultRow(
    save: DiscoverSave,
    onPlay: () -> Unit,
    menuContent: @Composable (dismiss: () -> Unit) -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    val radio = save.kind == DiscoverSave.KIND_RADIO
    var artFailed by remember(save.thumbnailUrl) { mutableStateOf(false) }
    val coverless = save.thumbnailUrl.isNullOrBlank() || artFailed
    val shape = if (radio) CircleShape else RoundedCornerShape(10.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onPlay, onLongClick = { menuOpen = true })
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(shape)
                .background(if (coverless) KisetsuRed else Color.White.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center,
        ) {
            if (coverless) {
                Image(painterResource(R.drawable.ic_launcher_foreground), contentDescription = null, modifier = Modifier.size(34.dp))
            } else {
                AsyncImage(
                    model = save.thumbnailUrl?.artworkAt(180),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    onState = { state -> if (state is AsyncImagePainter.State.Error) artFailed = true },
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(save.title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                save.subtitle.ifBlank { if (radio) "Radio" else "Podcast" },
                color = Color.White.copy(alpha = 0.62f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Box {
            Text(if (radio) "Radio" else "Podcast", color = Color.White.copy(alpha = 0.45f), fontSize = 11.sp)
            DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                menuContent { menuOpen = false }
            }
        }
    }
}
