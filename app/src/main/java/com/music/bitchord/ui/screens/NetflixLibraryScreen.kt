package com.music.bitchord.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.HomeShelf
import com.music.bitchord.data.model.LibraryPage
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.data.model.UiState
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.model.needsKisetsuRadioCover
import com.music.bitchord.ui.components.MessageState
import com.music.bitchord.ui.components.PullToRefresh
import com.music.bitchord.ui.components.pressScale
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.music.bitchord.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NetflixLibraryScreen(
    signedIn: Boolean,
    state: UiState<LibraryPage>,
    listState: LazyListState,
    onShelfItemClick: (ShelfItem) -> Unit,
    onShelfItemLongPress: (ShelfItem) -> Unit,
    onNewPlaylist: () -> Unit,
    onImportSpotifyPlaylist: (() -> Unit)? = null,
    onShowAll: (HomeShelf) -> Unit,
    replay: @Composable () -> Unit,
    onSignIn: () -> Unit,
    onRetry: () -> Unit,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    pullState: PullToRefreshState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    links: List<LibraryLink> = emptyList(),
    deviceItems: List<ShelfItem> = emptyList(),
    showTitle: Boolean = true,
    onOpenRadio: (() -> Unit)? = null,
    radioArtworkUrl: String? = null,
    onOpenDiscover: (() -> Unit)? = null,
    continueItems: List<ShelfItem> = emptyList(),
    podcastRecentItems: List<ShelfItem> = emptyList(),
    podcastListenLaterItems: List<ShelfItem> = emptyList(),
    recentlyPlayedItems: List<ShelfItem> = emptyList(),
    insightItems: List<ShelfItem> = emptyList(),
    insightTitle: String = "Kisetsu picks",
    downloadStatusText: String? = null,
    /** Opens the full Spotify library screen. */
    onOpenSpotify: () -> Unit = {},
    /** Opens a Spotify playlist as a detail page. */
    onOpenPlaylist: (com.music.bitchord.data.spotify.SpotifyPlaylist) -> Unit = {},
    /** Spotify playlists merged into the Playlists tab (`From Spotify` subtitle). */
    spotifyPlaylists: List<ShelfItem> = emptyList(),
) {
    val bg = Brush.verticalGradient(listOf(Color(0xFF0E0E10), Color(0xFF070708), Color.Black))
    val libraryTabs = listOf("All", "Playlists", "Artists", "Albums", "Songs", "Downloads")
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    if (selectedTab !in libraryTabs.indices) selectedTab = 0

    PullToRefresh(
        state = pullState,
        refreshing = refreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize().background(bg),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding() + 28.dp,
            ),
        ) {
            when (state) {
                is UiState.Loading -> {
                    item(key = "nlib-hero-loading") {
                        Box(Modifier.fillMaxWidth().padding(12.dp).height(320.dp).clip(RoundedCornerShape(22.dp)).background(Color.White.copy(alpha = 0.06f)))
                    }
                    item(key = "nlib-loading") {
                        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            repeat(3) {
                                Box(Modifier.width(180.dp).height(18.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    repeat(3) { Box(Modifier.size(140.dp, 210.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.06f))) }
                                }
                            }
                        }
                    }
                }

                is UiState.Error -> item(key = "nlib-error") {
                    if (!signedIn) {
                        MessageState(
                            message = "Sign in to see your library, playlists, and saved music in a cinematic view.",
                            actionLabel = "Sign in",
                            onAction = onSignIn,
                            modifier = Modifier.fillMaxWidth().padding(top = 100.dp),
                        )
                    } else {
                        MessageState(
                            message = "Couldn't load your library. Pull to refresh or try again.",
                            actionLabel = "Retry",
                            onAction = onRetry,
                            modifier = Modifier.fillMaxWidth().padding(top = 100.dp),
                        )
                    }
                }

                is UiState.Success -> {
                    val page = state.data
                    val shelves = page.shelves.filter { shelf ->
                        val t = shelf.title.lowercase()
                        !t.contains("trending") && !t.contains("albums and singles") && !t.contains("singles")
                    }
                    fun findShelf(vararg names: String): HomeShelf? = shelves.firstOrNull { s ->
                        names.any { s.title.equals(it, ignoreCase = true) || s.title.contains(it, ignoreCase = true) }
                    }
                    val heroItem = continueItems.firstOrNull()
                        ?: recentlyPlayedItems.firstOrNull()
                        ?: deviceItems.firstOrNull()
                        ?: shelves.firstOrNull { it.items.isNotEmpty() }?.items?.firstOrNull()

                    item(key = "nlib-hero") {
                        NetflixLibraryHero(
                            heroItem = heroItem,
                            title = if (showTitle) "Library" else "Your collection",
                            subtitle = if (signedIn) "Your music, shows, and saved worlds - curated like a streaming library." else "Sign in to unlock your full cinematic library.",
                            onPrimary = {
                                when {
                                    heroItem != null -> onShelfItemClick(heroItem)
                                    onOpenRadio != null -> onOpenRadio()
                                    onOpenDiscover != null -> onOpenDiscover()
                                    else -> onRefresh()
                                }
                            },
                            primaryLabel = if (heroItem != null) "Play" else "Refresh",
                            onSecondary = onNewPlaylist,
                            secondaryLabel = "New playlist",
                        )
                    }

                    item(key = "nlib-spotify") {
                        com.music.bitchord.ui.screens.SpotifyHomeSection(
                            onOpenSpotify = onOpenSpotify,
                            onOpenPlaylist = onOpenPlaylist,
                            dark = true,
                        )
                    }

                    item(key = "nlib-actions") {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            if (onImportSpotifyPlaylist != null) {
                                OutlinedButton(onClick = onImportSpotifyPlaylist, shape = RoundedCornerShape(10.dp)) { Text("Import Spotify") }
                            }
                            if (onOpenDiscover != null) {
                                OutlinedButton(onClick = onOpenDiscover, shape = RoundedCornerShape(10.dp)) {
                                    Icon(Icons.Rounded.Explore, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Discover")
                                }
                            }
                            OutlinedButton(onClick = onRefresh, shape = RoundedCornerShape(10.dp)) {
                                Icon(Icons.Rounded.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Refresh")
                            }
                        }
                    }

                    item(key = "nlib-replay") {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp)) { replay() }
                    }

                    if (downloadStatusText != null) {
                        item(key = "nlib-download-status") {
                            Text(
                                text = downloadStatusText,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            )
                        }
                    }

                    item(key = "nlib-tabs") {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            itemsIndexed(libraryTabs) { idx, tab ->
                                val selected = selectedTab == idx
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(if (selected) Color.White else Color.White.copy(alpha = 0.10f))
                                        .clickable { selectedTab = idx }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                ) {
                                    Text(
                                        tab,
                                        color = if (selected) Color.Black else Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    )
                                }
                            }
                        }
                    }

                    if (links.isNotEmpty() && (selectedTab == 0 || selectedTab == 5)) {
                        item(key = "nlib-links") {
                            NetflixLibraryLinks(links = links, onClick = onShelfItemClick)
                        }
                    }

                    if (continueItems.isNotEmpty() && (selectedTab == 0 || selectedTab == 3 || selectedTab == 5)) {
                        item(key = "nlib-continue") {
                            NetflixLibraryRail("Continue Hub", continueItems, onShelfItemClick, onShelfItemLongPress)
                        }
                    }
                    if (recentlyPlayedItems.isNotEmpty() && selectedTab == 0) {
                        item(key = "nlib-recent") {
                            NetflixLibraryRail("Recently played", recentlyPlayedItems, onShelfItemClick, onShelfItemLongPress)
                        }
                    }
                    if (deviceItems.isNotEmpty() && (selectedTab == 0 || selectedTab == 5)) {
                        item(key = "nlib-device") {
                            NetflixLibraryRail("On this device", deviceItems, onShelfItemClick, onShelfItemLongPress)
                        }
                    }

                    if (signedIn) {
                        when (selectedTab) {
                            0 -> {
                                shelves.forEachIndexed { index, shelf ->
                                    if (shelf.items.isEmpty()) return@forEachIndexed
                                    item(key = "nlib-shelf-all-${shelf.title}-$index") {
                                        Column(Modifier.fillMaxWidth()) {
                                            NetflixLibraryRail(shelf.title, shelf.items, onShelfItemClick, onShelfItemLongPress)
                                            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
                                                OutlinedButton(onClick = { onShowAll(shelf) }, shape = RoundedCornerShape(10.dp)) { Text("Show all") }
                                            }
                                        }
                                    }
                                }
                            }
                            1 -> {
                                val shelf = findShelf("Playlists", "Playlist") ?: HomeShelf("Playlists", emptyList())
                                val merged = if (spotifyPlaylists.isEmpty()) shelf else HomeShelf("Playlists", shelf.items + spotifyPlaylists)
                                item(key = "nlib-playlists") {
                                    Column(Modifier.fillMaxWidth()) {
                                        NetflixLibraryRail(merged.title.ifBlank { "Playlists" }, merged.items, onShelfItemClick, onShelfItemLongPress)
                                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
                                            OutlinedButton(onClick = { onShowAll(merged) }, shape = RoundedCornerShape(10.dp)) { Text("Show all") }
                                        }
                                    }
                                }
                                if (merged.items.isEmpty()) item(key = "nlib-playlists-empty") {
                                    Text("No playlists yet. Create one to get started.", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                                }
                            }
                            2 -> {
                                val shelf = findShelf("Artists", "Subscriptions", "Artist")
                                if (shelf != null) item(key = "nlib-artists") {
                                    Column(Modifier.fillMaxWidth()) {
                                        NetflixLibraryRail(shelf.title, shelf.items, onShelfItemClick, onShelfItemLongPress)
                                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
                                            OutlinedButton(onClick = { onShowAll(shelf) }, shape = RoundedCornerShape(10.dp)) { Text("Show all") }
                                        }
                                    }
                                } else item(key = "nlib-artists-empty") { Text("No artists yet.", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(16.dp)) }
                            }
                            3 -> {
                                val shelf = findShelf("Albums", "Album")
                                if (shelf != null) item(key = "nlib-albums") {
                                    Column(Modifier.fillMaxWidth()) {
                                        NetflixLibraryRail(shelf.title, shelf.items, onShelfItemClick, onShelfItemLongPress)
                                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
                                            OutlinedButton(onClick = { onShowAll(shelf) }, shape = RoundedCornerShape(10.dp)) { Text("Show all") }
                                        }
                                    }
                                } else item(key = "nlib-albums-empty") { Text("No albums yet.", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(16.dp)) }
                            }
                            4 -> {
                                val shelf = findShelf("Songs", "Song", "Liked")
                                if (shelf != null) item(key = "nlib-songs") {
                                    Column(Modifier.fillMaxWidth()) {
                                        NetflixLibraryRail(shelf.title, shelf.items, onShelfItemClick, onShelfItemLongPress)
                                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
                                            OutlinedButton(onClick = { onShowAll(shelf) }, shape = RoundedCornerShape(10.dp)) { Text("Show all") }
                                        }
                                    }
                                } else item(key = "nlib-songs-empty") { Text("No songs shelf available.", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(16.dp)) }
                            }
                            5 -> {
                                val dl = findShelf("Downloaded", "Downloads")
                                if (dl != null) item(key = "nlib-downloaded-shelf") {
                                    Column(Modifier.fillMaxWidth()) {
                                        NetflixLibraryRail(dl.title, dl.items, onShelfItemClick, onShelfItemLongPress)
                                        Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
                                            OutlinedButton(onClick = { onShowAll(dl) }, shape = RoundedCornerShape(10.dp)) { Text("Show all") }
                                        }
                                    }
                                }
                                if (links.isEmpty() && deviceItems.isEmpty() && dl == null) item(key = "nlib-downloads-empty") {
                                    Text("Nothing downloaded yet. Save music for offline listening, or find podcasts and live radio in Discover.", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, modifier = Modifier.padding(16.dp))
                                }
                            }
                        }
                    } else {
                        if (selectedTab == 0 || selectedTab == 1) {
                            item(key = "nlib-signin-note") {
                                MessageState(
                                    message = "Sign in to access playlists, artists, albums, and your saved library.",
                                    actionLabel = "Sign in",
                                    onAction = onSignIn,
                                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                )
                            }
                        } else if (selectedTab == 5) {
                            if (links.isEmpty() && deviceItems.isEmpty()) item(key = "nlib-offline-empty") {
                                Text("Nothing downloaded yet. Save music for offline listening, or find podcasts and live radio in Discover.", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, modifier = Modifier.padding(16.dp))
                            }
                        } else {
                            item(key = "nlib-tab-signin") {
                                Text("Sign in to see this section.", color = Color.White.copy(alpha = 0.7f), modifier = Modifier.padding(16.dp))
                            }
                        }
                    }

                    if (selectedTab == 0) {
                        if (podcastRecentItems.isNotEmpty()) item(key = "nlib-pod-recent") {
                            NetflixLibraryRail("Recently played podcasts", podcastRecentItems, onShelfItemClick, onShelfItemLongPress)
                        }
                        if (podcastListenLaterItems.isNotEmpty()) item(key = "nlib-pod-later") {
                            NetflixLibraryRail("Listen later", podcastListenLaterItems, onShelfItemClick, onShelfItemLongPress)
                        }
                        if (insightItems.isNotEmpty()) item(key = "nlib-insights") {
                            NetflixLibraryRail(insightTitle, insightItems, onShelfItemClick, onShelfItemLongPress)
                        }
                    }

                    if (selectedTab == 0 && page.isEmpty && continueItems.isEmpty() && deviceItems.isEmpty() && recentlyPlayedItems.isEmpty() && podcastRecentItems.isEmpty() && podcastListenLaterItems.isEmpty() && insightItems.isEmpty() && !signedIn) {
                        item(key = "nlib-empty") {
                            MessageState(
                                message = "Your library is ready for its first season. Save music, create playlists, or discover something new.",
                                actionLabel = if (onOpenDiscover != null) "Discover" else "New playlist",
                                onAction = onOpenDiscover ?: onNewPlaylist,
                                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NetflixLibraryLinks(
    links: List<LibraryLink>,
    onClick: (ShelfItem) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Folders & sources",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            links.forEach { link ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressScale(pressedScale = 1.015f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onClick(link.item) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.10f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = link.icon,
                            contentDescription = null,
                            tint = link.tint ?: Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = link.item.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (link.item.subtitle.isNotBlank()) {
                            Text(
                                text = link.item.subtitle,
                                color = Color.White.copy(alpha = 0.72f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NetflixLibraryHero(
    heroItem: ShelfItem?,
    title: String,
    subtitle: String,
    onPrimary: () -> Unit,
    primaryLabel: String,
    onSecondary: () -> Unit,
    secondaryLabel: String,
) {
    val art = heroItem?.thumbnailUrl
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xFF141414))
    ) {
        if (art != null) {
            AsyncImage(
                model = art.artworkAt(1000),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().blur(18.dp),
            )
        }
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(alpha = 0.25f), Color.Black.copy(alpha = 0.45f), Color.Black.copy(alpha = 0.85f))
                )
            )
        )
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(title, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, color = Color.White.copy(alpha = 0.86f), fontSize = 14.sp, lineHeight = 19.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
            heroItem?.let {
                Text(it.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (!it.subtitle.isNullOrBlank()) {
                    Text(it.subtitle.orEmpty(), color = Color.White.copy(alpha = 0.72f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onPrimary,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Icon(if (heroItem != null) Icons.Rounded.PlayArrow else Icons.Rounded.Refresh, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(primaryLabel, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(onClick = onSecondary, shape = RoundedCornerShape(10.dp)) {
                    Icon(Icons.Rounded.Add, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(secondaryLabel)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NetflixLibraryRail(
    title: String,
    items: List<ShelfItem>,
    onItemClick: (ShelfItem) -> Unit,
    onItemLongPress: (ShelfItem) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 10.dp)) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(items, key = { (it.videoId ?: it.browseId ?: it.title) + "|" + (it.subtitle ?: "") + "|" + it.hashCode() }) { item ->
                val radioCover = item.needsKisetsuRadioCover() || (item.thumbnailUrl.isNullOrBlank() && item.videoId?.startsWith("src:") == true)
                Column(
                    modifier = Modifier
                        .width(150.dp)
                        .pressScale(pressedScale = 1.03f)
                        .clip(RoundedCornerShape(12.dp))
                        .combinedClickable(
                            onClick = { onItemClick(item) },
                            onLongClick = { onItemLongPress(item) },
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (radioCover) Color(0xFFFA2D48) else Color(0xFF1E1E22))
                    ) {
                        if (radioCover) {
                            Image(
                                painter = painterResource(R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().padding(18.dp),
                            )
                        } else {
                            AsyncImage(
                                model = item.thumbnailUrl?.artworkAt(500),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.18f), Color.Black.copy(alpha = 0.45f)))))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(item.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis, lineHeight = 18.sp)
                    if (!item.subtitle.isNullOrBlank()) {
                        Text(item.subtitle.orEmpty(), color = Color.White.copy(alpha = 0.72f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}
