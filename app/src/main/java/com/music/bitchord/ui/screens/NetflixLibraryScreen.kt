package com.music.bitchord.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.music.bitchord.ui.components.MessageState
import com.music.bitchord.ui.components.PullToRefresh

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
    links: List<Any> = emptyList(),
    deviceItems: List<ShelfItem> = emptyList(),
    showTitle: Boolean = true,
    onOpenRadio: (() -> Unit)? = null,
    radioArtworkUrl: String? = null,
    onOpenDiscover: (() -> Unit)? = null,
    continueItems: List<ShelfItem> = emptyList(),
    podcastRecentItems: List<ShelfItem> = emptyList(),
    podcastListenLaterItems: List<ShelfItem> = emptyList(),
    insightItems: List<ShelfItem> = emptyList(),
    insightTitle: String = "Kisetsu picks",
    downloadStatusText: String? = null,
) {
    val bg = Brush.verticalGradient(listOf(Color(0xFF0E0E10), Color(0xFF070708), Color.Black))
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
                is UiState.Loading -> item(key = "nlib-loading") {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Box(Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(18.dp)).background(Color.White.copy(alpha = 0.06f)))
                        repeat(3) {
                            Box(Modifier.width(180.dp).height(18.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.08f)))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                repeat(3) { Box(Modifier.size(140.dp, 210.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.06f))) }
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
                    val heroItem = continueItems.firstOrNull()
                        ?: deviceItems.firstOrNull()
                        ?: page.shelves.firstOrNull { it.items.isNotEmpty() }?.items?.firstOrNull()

                    item(key = "nlib-hero") {
                        NetflixLibraryHero(
                            heroItem = heroItem,
                            title = if (showTitle) "Library" else "Your collection",
                            subtitle = if (signedIn) "Your music, shows, and saved worlds — curated like a streaming library." else "Sign in to unlock your full cinematic library.",
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

                    item(key = "nlib-actions") {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            if (onImportSpotifyPlaylist != null) {
                                OutlinedButton(onClick = onImportSpotifyPlaylist, shape = RoundedCornerShape(10.dp)) {
                                    Text("Import Spotify")
                                }
                            }
                            if (onOpenDiscover != null) {
                                OutlinedButton(onClick = onOpenDiscover, shape = RoundedCornerShape(10.dp)) {
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
                        item(key = "nlib-downloads") {
                            Text(
                                text = downloadStatusText,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            )
                        }
                    }

                    if (continueItems.isNotEmpty()) {
                        item(key = "nlib-continue") {
                            NetflixLibraryRail(
                                title = "Continue watching & listening",
                                items = continueItems,
                                onItemClick = onShelfItemClick,
                                onItemLongPress = onShelfItemLongPress,
                            )
                        }
                    }

                    if (deviceItems.isNotEmpty()) {
                        item(key = "nlib-device") {
                            NetflixLibraryRail(
                                title = "On this device",
                                items = deviceItems,
                                onItemClick = onShelfItemClick,
                                onItemLongPress = onShelfItemLongPress,
                            )
                        }
                    }

                    page.shelves.forEachIndexed { index, shelf ->
                        if (shelf.items.isEmpty()) return@forEachIndexed
                        item(key = "nlib-shelf-${shelf.title}-$index") {
                            Column(Modifier.fillMaxWidth()) {
                                NetflixLibraryRail(
                                    title = shelf.title,
                                    items = shelf.items,
                                    onItemClick = onShelfItemClick,
                                    onItemLongPress = onShelfItemLongPress,
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    OutlinedButton(onClick = { onShowAll(shelf) }, shape = RoundedCornerShape(10.dp)) {
                                        Text("Show all")
                                    }
                                }
                            }
                        }
                    }

                    if (podcastRecentItems.isNotEmpty()) {
                        item(key = "nlib-pod-recent") {
                            NetflixLibraryRail("Recently played podcasts", podcastRecentItems, onShelfItemClick, onShelfItemLongPress)
                        }
                    }
                    if (podcastListenLaterItems.isNotEmpty()) {
                        item(key = "nlib-pod-later") {
                            NetflixLibraryRail("Listen later", podcastListenLaterItems, onShelfItemClick, onShelfItemLongPress)
                        }
                    }
                    if (insightItems.isNotEmpty()) {
                        item(key = "nlib-insights") {
                            NetflixLibraryRail(insightTitle, insightItems, onShelfItemClick, onShelfItemLongPress)
                        }
                    }

                    if (page.isEmpty && continueItems.isEmpty() && deviceItems.isEmpty()) {
                        item(key = "nlib-empty") {
                            MessageState(
                                message = "Your library is ready for its first season. Save music, create playlists, or discover something new.",
                                actionLabel = if (onOpenDiscover != null) "Discover" else "New playlist",
                                onAction = onOpenDiscover ?: onNewPlaylist,
                                modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
                            )
                        }
                    }
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
                Column(
                    modifier = Modifier
                        .width(150.dp)
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
                            .background(Color(0xFF1E1E22))
                    ) {
                        AsyncImage(
                            model = item.thumbnailUrl?.artworkAt(500),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
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
