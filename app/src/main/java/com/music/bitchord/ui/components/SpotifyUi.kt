package com.music.bitchord.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.BookmarkAdd
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Cast
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Forward30
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.music.bitchord.R
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.produceState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import coil3.compose.LocalPlatformContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import android.content.Intent
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import androidx.compose.foundation.Image
import com.music.bitchord.data.NerdStats
import com.music.bitchord.data.YtMusicRepository
import com.music.bitchord.data.lyrics.LyricLine
import com.music.bitchord.data.model.ArtistPage
import com.music.bitchord.data.model.HomeShelf
import com.music.bitchord.data.model.PLAYER_ART_PX
import com.music.bitchord.data.model.PlaybackSourceType
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.UiState
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.model.displayArtist
import com.music.bitchord.data.model.needsKisetsuRadioCover
import com.music.bitchord.data.settings.DiscoverSaves
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.playback.AudioOutputStatus
import com.music.bitchord.playback.AudioRouting
import com.music.bitchord.playback.PlaybackPosition
import com.music.bitchord.playback.SleepTimer
import com.music.bitchord.ui.haptics.Haptic
import com.music.bitchord.ui.haptics.rememberHaptics
import com.music.bitchord.ui.player.MarqueeText
import android.os.Build
import android.provider.Settings
import com.music.bitchord.ui.icons.BitChordIcons
import com.music.bitchord.ui.theme.ArtworkPalette
import com.music.bitchord.ui.theme.rememberArtworkPalette
import dev.chrisbanes.haze.HazeState
import java.util.Locale
import kotlin.math.roundToInt

/** Spotify's own dark canvas, used where the app's theme must not leak in. */
private val SpotifyBlack = Color(0xFF121212)
private val SpotifyCard = Color(0xFF242424)
private val SpotifyGreen = Color(0xFF1DB954)
private val SpotifyInk = Color(0xFF101010)

private fun SpotifyMuted(alpha: Float = 0.68f) = Color.White.copy(alpha = alpha)

/** Branded fallback for radio stations with no cover art (Kisetsu logo on red). */
@Composable
private fun KisetsuRadioFallback(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(Color(0xFFFA2D48)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.72f),
        )
    }
}

/**
 * Nested-scroll connection that keeps the player's lists in charge of their own
 * gestures and never lets M3's bottom-sheet settle after a content fling.
 *
 * M3's sheet calls settle() on every post-fling from content, running a fixed
 * 300 ms tween during which it grabs every touch in the Initial pass before the
 * list sees it. The user's first tap after a scroll is then swallowed — the list
 * never gets the down event and nothing moves — which reads as "scroll won't
 * respond" or "takes five swipes to get moving."
 *
 * By returning Velocity.Zero for every onPostFling, the sheet never receives a
 * leftover fling, never calls settle(), and is never "animating." Touches go
 * straight to the list. Every onPostScroll leftover is also consumed, so the
 * sheet is never dragged by content edge-pulls either.
 */
private class PlayerScrollOwner : NestedScrollConnection {
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource,
    ): Offset = Offset.Zero

    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity =
        Velocity.Zero
}

/* ------------------------------------------------------------------------- */
/* Bottom navigation                                                          */
/* ------------------------------------------------------------------------- */

/**
 * The optional Spotify-style tab bar: no pill, no edge, no glass — a soft
 * gradient that rises out of the bottom of the screen and a row of floating
 * glyphs over it. The gradient is the bar; there is nothing solid to see.
 */
@Composable
fun SpotifyNavBar(
    tabs: List<BottomTab>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberHaptics()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.42f to SpotifyBlack.copy(alpha = 0.72f),
                    1f to SpotifyBlack,
                ),
            )
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(top = 6.dp, bottom = 4.dp, start = 8.dp, end = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = index == selectedIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            if (!selected) haptics.play(Haptic.Select)
                            onTabSelected(index)
                        }
                        .padding(vertical = 2.dp),
                ) {
                    Box(
                        modifier = Modifier.size(40.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (tab.emoji != null) {
                            Text(tab.emoji, fontSize = 19.sp)
                        } else {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                tint = if (selected) Color.White else SpotifyMuted(0.78f),
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = tab.label,
                        color = if (selected) Color.White else SpotifyMuted(0.62f),
                        fontSize = 10.5.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/* ------------------------------------------------------------------------- */
/* Mini player                                                                */
/* ------------------------------------------------------------------------- */

/**
 * The floating Spotify mini player: a dark rounded card above the tab bar with
 * the cover, the track, a hairline playhead and cast / play controls.
 */
@Composable
fun SpotifyMiniPlayer(
    song: Song,
    isPlaying: Boolean,
    isLoading: Boolean,
    position: PlaybackPosition,
    durationMs: Long,
    liked: Boolean = false,
    onPlayPause: () -> Unit,
    onToggleLike: () -> Unit = {},
    onExpand: () -> Unit,
    onNext: () -> Unit = {},
    onPrevious: () -> Unit = {},
    controlsLocked: Boolean = false,
    onBlockedControl: () -> Unit = {},
    pull: MiniPlayerPull? = null,
    onLongPress: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberHaptics()
    val kisetsuCover = song.needsKisetsuRadioCover()
    val artwork = rememberRemoteArtworkUrl(song)
    val shape = RoundedCornerShape(10.dp)
    // The artwork's own colour, deepened until the text on top of it is
    // readable — Spotify's mini player is a solid colour, not glass.
    val palette = rememberArtworkPalette(if (kisetsuCover) null else artwork)
    val barColor = when {
        kisetsuCover -> Color(0xFF6D1225)
        artwork != null -> palette.wash
        else -> SpotifyCard
    }
    val deviceLine = rememberOutputLabel()
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .shadow(14.dp, shape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(shape)
            .background(barColor)
            .miniPlayerGestures(
                pull = pull,
                onNext = {
                    haptics.play(Haptic.SkipNext)
                    onNext()
                },
                onPrevious = {
                    haptics.play(Haptic.SkipPrevious)
                    onPrevious()
                },
                locked = controlsLocked,
                onBlocked = onBlockedControl,
            )
            .combinedClickable(
                onClick = {
                    haptics.play(Haptic.Expand)
                    onExpand()
                },
                onLongClick = onLongPress?.let {
                    {
                        haptics.play(Haptic.Select)
                        it()
                    }
                },
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 4.dp, top = 8.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (song.needsKisetsuRadioCover()) {
                KisetsuRadioFallback(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(5.dp)),
                )
            } else {
                AsyncImage(
                    model = artwork?.artworkAt(240),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.White.copy(alpha = 0.08f)),
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                // Song • Artist on one scrolling line, like Spotify's bar.
                val isLiveMini = song.videoId.startsWith("src:")
                MarqueeText(
                    text = buildAnnotatedString {
                        if (isLiveMini) {
                            withStyle(SpanStyle(color = SpotifyGreen, fontWeight = FontWeight.Bold)) {
                                append("LIVE")
                            }
                            append("  ")
                        }
                        append(song.title)
                        append(" ")
                        withStyle(SpanStyle(color = Color.White.copy(alpha = 0.45f))) {
                            append("•")
                        }
                        append(" ")
                        append(song.displayArtist())
                    },
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    color = Color.White,
                    enabled = true,
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = deviceLine,
                    color = SpotifyGreen,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            // Like / add (Discover for radio/podcast)
            val isRadio = song.videoId.startsWith("src:")
            if (isRadio) {
                val discoverLiked by DiscoverSaves.likedRadios.collectAsStateWithLifecycle()
                SpotifyLikeBadge(liked = discoverLiked.any { it.key == song.videoId }, size = 30.dp, addContentDescription = "Add to Discover", removeContentDescription = "Remove from Discover", onClick = { DiscoverSaves.toggleLikedRadio(song) })
            } else if (song.isPodcastEpisode) {
                val likedPods by DiscoverSaves.likedPodcasts.collectAsStateWithLifecycle()
                SpotifyLikeBadge(liked = likedPods.any { it.key == (song.albumId ?: song.videoId) }, size = 30.dp, addContentDescription = "Add to Discover", removeContentDescription = "Remove from Discover", onClick = { DiscoverSaves.toggleLikedPodcast(song) })
            } else {
                SpotifyLikeBadge(liked = liked, size = 30.dp, onClick = { onToggleLike() })
            }
            Spacer(Modifier.width(6.dp))
            Box(Modifier.size(42.dp), contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clickable {
                                haptics.play(if (isPlaying) Haptic.Pause else Haptic.Resume)
                                onPlayPause()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        SpotifyPlayPauseGlyph(
                            isPlaying = isPlaying,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
        SpotifyProgressLine(
            position = position,
            durationMs = durationMs,
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp),
            isLive = song.videoId.startsWith("src:"),
        )
    }
}

/**
 * What the playback is coming out of right now — the connected Bluetooth or
 * wired device's own name, or the phone's name when nothing is. Spotify's bar
 * wears this line in green beneath the track.
 */
@Composable
private fun rememberOutputLabel(): String {
    val snapshot by AudioOutputStatus.current.collectAsStateWithLifecycle()
    val context = LocalPlatformContext.current
    return remember(snapshot.activeDeviceId, snapshot.routeKind, snapshot.deviceName) {
        val external = snapshot.activeDeviceId != null && snapshot.routeKind != AudioRouting.Kind.PHONE
        // A missing or "System default" report means the phone's own speaker.
        val device = if (external) snapshot.deviceName.takeIf { it.isNotBlank() } else null
        device
            ?: Settings.Global.getString(context.contentResolver, Settings.Global.DEVICE_NAME)
                ?.takeIf { it.isNotBlank() }
            ?: Build.MODEL
            ?: "This phone"
    }
}

/** The mini player's thin progress line — read here so ticks never touch the page. */
@Composable
private fun SpotifyProgressLine(
    position: PlaybackPosition,
    durationMs: Long,
    modifier: Modifier = Modifier,
    isLive: Boolean = false,
) {
    val fraction = if (isLive) 1f else if (durationMs > 0L) {
        (position.positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
    } else {
        0f
    }
    Box(
        modifier = modifier
            .background(if (isLive) Color(0xFF1DB954).copy(alpha = 0.22f) else Color.White.copy(alpha = 0.22f)),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction)
                .fillMaxHeight()
                .background(if (isLive) Color(0xFF1DB954).copy(alpha = 0.55f) else Color.White.copy(alpha = 0.82f)),
        )
    }
}

/* ------------------------------------------------------------------------- */
/* Full Now Playing                                                           */
/* ------------------------------------------------------------------------- */

/** The heading over the track on the Spotify player. */
fun playingFromHeading(type: PlaybackSourceType?): String = when (type) {
    PlaybackSourceType.HOME -> "Playing from Home"
    PlaybackSourceType.SEARCH -> "Playing from Search"
    PlaybackSourceType.HISTORY -> "Playing from History"
    PlaybackSourceType.REPLAY -> "Playing from Replay"
    PlaybackSourceType.EXPLORE -> "Playing from Explore"
    PlaybackSourceType.BROWSE -> "Playing from Playlist"
    PlaybackSourceType.SHARED_LINK -> "Playing from a shared link"
    PlaybackSourceType.QUEUE -> "Playing from the queue"
    null -> "Playing from your library"
}

/** The source's own name, shown under the heading. */
fun playingFromTitle(song: Song): String =
    song.playbackSource?.takeIf { it.isNotBlank() }
        ?: song.albumName?.takeIf { it.isNotBlank() }
        ?: song.artist

/**
 * The optional Spotify player, rebuilt to the reference: a fixed player page
 * that fills the window, and — below it — the same page Spotify calls the Now
 * Playing view: lyrics, the artist, similar songs, the source playlist and
 * credits. Swiping up on the player reveals them; swiping back down returns.
 */
@Composable
fun SpotifyNowPlayingScreen(
    song: Song,
    isPlaying: Boolean,
    isLoading: Boolean,
    position: PlaybackPosition,
    durationMs: Long,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    hasPrevious: Boolean,
    hasNext: Boolean,
    liked: Boolean,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onSeekFraction: (Float) -> Unit,
    onToggleLike: () -> Unit,
    onClose: () -> Unit,
    onMore: () -> Unit,
    onShare: () -> Unit,
    onShowQueue: () -> Unit = {},
    lyrics: List<LyricLine>? = null,
    onSkipBack: () -> Unit = {},
    onSkipForward: () -> Unit = {},
    playbackSpeed: Float = 1f,
    onCycleSpeed: () -> Unit = {},
    onSeekMs: (Long) -> Unit = {},
    onOpenArtist: (browseId: String?, name: String) -> Unit = { _, _ -> },
    onToggleSubscribe: (channelId: String, subscribe: Boolean) -> Unit = { _, _ -> },
    onOpenAlbum: (browseId: String?, name: String) -> Unit = { _, _ -> },
    onPlaySongs: (List<Song>, Int) -> Unit = { _, _ -> },
    onOpenPlaybackSource: () -> Unit = {},
    onOpenStationInfo: () -> Unit = {},
    podcastBookmarksEnabled: Boolean = true,
    onAddBookmark: ((Long) -> Unit)? = null,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberHaptics()
    val context = LocalContext.current
    val artwork = rememberRemoteArtworkUrl(song)
    val kisetsuCover = song.needsKisetsuRadioCover()
    val basePalette = rememberArtworkPalette(
        imageUrl = if (kisetsuCover) null else artwork,
        dark = true,
        artPx = PLAYER_ART_PX,
    )
    val palette = if (kisetsuCover) basePalette.copy(
        background = Color(0xFF5C1020),
        wash = Color(0xFFFA2D48),
        elevated = Color(0xFF7A1A2E),
        accent = Color(0xFFFFB3BE),
    ) else basePalette
    val reduceAnimation by AppSettings.reduceAnimation.collectAsStateWithLifecycle()
    var showCast by remember { mutableStateOf(false) }
    var showLyrics by remember(song.videoId) { mutableStateOf(false) }


    // The Now Playing view's extras. Fetched once per track, quietly, so the
    // first swipe down already has something to show.
    var artistPage by remember(song.videoId) { mutableStateOf<ArtistPage?>(null) }
    var artistBrowseId by remember(song.videoId) { mutableStateOf<String?>(null) }
    var similar by remember(song.videoId) { mutableStateOf<List<Song>>(emptyList()) }
    LaunchedEffect(song.videoId, song.artist) {
        artistPage = null
        artistBrowseId = null
        similar = emptyList()
        val id = song.artistId
            ?: song.artists.firstOrNull()?.browseId
            ?: runCatching { YtMusicRepository.findArtistPageId(song.artist) }.getOrNull()
                ?.getOrNull()
        artistBrowseId = id
        if (id != null) {
            artistPage = runCatching { YtMusicRepository.artistPage(id) }.getOrNull()?.getOrNull()
        }
        similar = runCatching { YtMusicRepository.radio(song.videoId) }.getOrNull()
            ?.getOrNull()
            ?.filterNot { it.videoId == song.videoId }
            ?.take(12)
            ?.ifEmpty { artistPage?.songs.orEmpty() }
            .orEmpty()
    }

    BackHandler(enabled = showLyrics) { showLyrics = false }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to palette.wash,
                    0.44f to palette.background,
                    0.82f to SpotifyBlack,
                    1f to Color(0xFF090909),
                ),
            ),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
        ) {
            val density = LocalDensity.current
            val listState = rememberLazyListState()
            val statusBarHeight = with(density) { WindowInsets.safeDrawing.getTop(density).toDp() }
            val scrolledPastHeader by remember {
                derivedStateOf {
                    listState.firstVisibleItemIndex > 0 ||
                        listState.firstVisibleItemScrollOffset > with(density) { 210.dp.toPx() }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(PlayerScrollOwner()),
                contentPadding = PaddingValues(top = statusBarHeight),
            ) {
                item(key = "player", contentType = "player") {
                    SpotifyPlayerPage(
                        song = song,
                        isPlaying = isPlaying,
                        isLoading = isLoading,
                        position = position,
                        durationMs = durationMs,
                        shuffleEnabled = shuffleEnabled,
                        repeatMode = repeatMode,
                        liked = liked,
                        palette = palette,
                        lyrics = lyrics,
                        onPlayPause = onPlayPause,
                        onNext = onNext,
                        onPrevious = onPrevious,
                        onToggleShuffle = onToggleShuffle,
                        onCycleRepeat = onCycleRepeat,
                        onSeekFraction = onSeekFraction,
                        onToggleLike = onToggleLike,
                        onClose = onClose,
                        onMore = onMore,
                        onShare = onShare,
                        onShowQueue = onShowQueue,
                        onShowLyrics = { showLyrics = true },
                        onCast = { showCast = true },
                        onOpenAlbum = { onOpenAlbum(song.albumId, song.albumName ?: "Album") },
                        onSkipBack = onSkipBack,
                        onSkipForward = onSkipForward,
                        playbackSpeed = playbackSpeed,
                        onCycleSpeed = onCycleSpeed,
                        podcastBookmarksEnabled = podcastBookmarksEnabled,
                        onAddBookmark = onAddBookmark,
                        // The LazyColumn owns the viewport height, so the page
                        // cannot be measured against a height that changes
                        // underneath the scroll.
                        modifier = Modifier.fillParentMaxHeight(),
                    )
                }
                spotifyInfoItems(
                    song = song,
                    artistPage = artistPage,
                    artistBrowseId = artistBrowseId,
                    similar = similar,
                    palette = palette,
                    onOpenArtist = onOpenArtist,
                    onToggleSubscribe = onToggleSubscribe,
                    onOpenAlbum = onOpenAlbum,
                    onPlaySongs = onPlaySongs,
                    onOpenPlaybackSource = onOpenPlaybackSource,
                    onOpenStationInfo = onOpenStationInfo,
                )
            }

            // The condensed bar that takes over once the player has scrolled
            // away, exactly as Spotify keeps the track and pause within reach.
            // Wrapped in a fixed-height box to prevent layout thrashing during
            // fast scrolling.
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(56.dp + statusBarHeight),
            ) {
                AnimatedVisibility(
                    visible = scrolledPastHeader,
                    enter = fadeIn(tween(180)),
                    exit = fadeOut(tween(180)),
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(statusBarHeight)
                                .background(SpotifyBlack.copy(alpha = 0.96f)),
                        )
                        SpotifyCondensedHeader(
                            song = song,
                            isPlaying = isPlaying,
                            liked = liked,
                            onPlayPause = onPlayPause,
                            onToggleLike = onToggleLike,
                            onMore = onMore,
                        )
                    }
                }
            }
        }

        if (showCast) {
            CastDialog(hazeState = hazeState, onDismiss = { showCast = false })
        }

        AnimatedVisibility(
            visible = showLyrics,
            enter = fadeIn(tween(220)) +
                slideInVertically(tween(320)) { it / 5 },
            exit = fadeOut(tween(180)) +
                slideOutVertically(tween(260)) { it / 5 },
        ) {
            SpotifyLyricsScreen(
                song = song,
                lyrics = lyrics.orEmpty(),
                position = position,
                durationMs = durationMs,
                isPlaying = isPlaying,
                palette = palette,
                onClose = { showLyrics = false },
                onPlayPause = onPlayPause,
                onSeekFraction = onSeekFraction,
                onShare = onShare,
                onMore = onMore,
            )
        }
    }
}

/* ------------------------------------------------------------------------- */
/* The player page                                                            */
/* ------------------------------------------------------------------------- */

@Composable
private fun SpotifyPlayerPage(
    song: Song,
    isPlaying: Boolean,
    isLoading: Boolean,
    position: PlaybackPosition,
    durationMs: Long,
    shuffleEnabled: Boolean,
    repeatMode: Int,
    liked: Boolean,
    palette: ArtworkPalette,
    lyrics: List<LyricLine>?,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeat: () -> Unit,
    onSeekFraction: (Float) -> Unit,
    onToggleLike: () -> Unit,
    onClose: () -> Unit,
    onMore: () -> Unit,
    onShare: () -> Unit,
    onShowQueue: () -> Unit = {},
    onShowLyrics: () -> Unit = {},
    onCast: () -> Unit,
    onOpenAlbum: () -> Unit = {},
    onSkipBack: () -> Unit = {},
    onSkipForward: () -> Unit = {},
    playbackSpeed: Float = 1f,
    onCycleSpeed: () -> Unit = {},
    podcastBookmarksEnabled: Boolean = true,
    onAddBookmark: ((Long) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberHaptics()
    val artwork = rememberRemoteArtworkUrl(song)
    // Lyrics PiP: toggled here, honoring onUserLeaveHint.
    val pipOn by AppSettings.pipLyricPreview.collectAsStateWithLifecycle()
    Column(modifier = modifier.padding(horizontal = 8.dp)) {
        // ---- Top bar ---------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose, modifier = Modifier.size(46.dp)) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp),
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = playingFromHeading(song.playbackSourceType),
                    color = SpotifyMuted(0.66f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(1.dp))
                Text(
                    text = playingFromTitle(song),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            IconButton(onClick = onMore, modifier = Modifier.size(46.dp)) {
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "More",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp),
                )
            }
        }

        // ---- Sleeve ----------------------------------------------------
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clipToBounds()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            val side = minOf(maxWidth, maxHeight * 0.85f)
            if (song.videoId.startsWith("src:") && song.thumbnailUrl.isNullOrBlank()) {
                KisetsuRadioFallback(
                    modifier = Modifier
                        .size(side)
                        .shadow(24.dp, RoundedCornerShape(4.dp), ambientColor = Color.Black, spotColor = Color.Black)
                        .clip(RoundedCornerShape(4.dp)),
                )
            } else {
                AsyncImage(
                    model = artwork?.artworkAt(PLAYER_ART_PX),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(side)
                        .shadow(24.dp, RoundedCornerShape(4.dp), ambientColor = Color.Black, spotColor = Color.Black)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.06f)),
                )
            }
        }

        // ---- Title, artist, save ---------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                val isRadioTitle = song.videoId.startsWith("src:")
                Text(
                    text = song.title,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = if (isRadioTitle) Modifier else Modifier.clickable { onOpenAlbum() },
                )
                Spacer(Modifier.height(2.dp))
                val isLive = song.videoId.startsWith("src:")
                val isPodcast = song.isPodcastEpisode
                Text(
                    text = when {
                        isLive -> "LIVE · ${song.artist}"
                        isPodcast -> song.displayArtist()
                        else -> song.artist
                    },
                    color = if (isLive) SpotifyGreen.copy(alpha = 0.9f) else SpotifyMuted(0.70f),
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier,
                )
            }
            Spacer(Modifier.width(12.dp))
            val isRadio = song.videoId.startsWith("src:")
            val isPodcast = song.isPodcastEpisode
            when {
                isRadio -> {
                    val discoverLiked by DiscoverSaves.likedRadios.collectAsStateWithLifecycle()
                    val inDiscover = discoverLiked.any { it.key == song.videoId }
                    SpotifyLikeBadge(
                        liked = inDiscover,
                        addContentDescription = "Add to Discover",
                        removeContentDescription = "Remove from Discover",
                        onClick = { DiscoverSaves.toggleLikedRadio(song) },
                    )
                }
                isPodcast -> {
                    val likedPods by DiscoverSaves.likedPodcasts.collectAsStateWithLifecycle()
                    val podKey = song.albumId ?: song.videoId
                    val inDiscover = likedPods.any { it.key == podKey }
                    SpotifyLikeBadge(
                        liked = inDiscover,
                        addContentDescription = "Add to Discover",
                        removeContentDescription = "Remove from Discover",
                        onClick = { DiscoverSaves.toggleLikedPodcast(song) },
                    )
                }
                else -> {
                    SpotifyLikeBadge(
                        liked = liked,
                        onClick = { onToggleLike() },
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        // ---- Scrubber --------------------------------------------------
        var scrubFraction by remember { mutableStateOf<Float?>(null) }
        val showQuality by AppSettings.showPlayerQuality.collectAsStateWithLifecycle()
        val nerdStats by NerdStats.current.collectAsStateWithLifecycle()
        val crossfadeSec by AppSettings.crossfadeSeconds.collectAsStateWithLifecycle()
        val automixEngine by AppSettings.automixEngine.collectAsStateWithLifecycle()
        val isRadio = song.videoId.startsWith("src:")
        Column(Modifier.padding(horizontal = 8.dp)) {
            SpotifyScrubber(
                position = position,
                durationMs = durationMs,
                onSeekFraction = onSeekFraction,
                onScrubFraction = { scrubFraction = it },
                isLiveRadio = isRadio,
            )
            if (!isRadio) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SpotifyElapsed(position = position, durationMs = durationMs, scrubFraction = scrubFraction)
                    Text(
                        text = formatSpotifyTime(durationMs),
                        color = SpotifyMuted(0.62f),
                        fontSize = 11.5.sp,
                    )
                }
            }
            val transitionLabel = when {
                isRadio -> null
                automixEngine == com.music.bitchord.data.settings.AutomixEngine.V2 && crossfadeSec > 0 ->
                    "Automix · ${crossfadeSec}s"
                crossfadeSec > 0 -> "Crossfade · ${crossfadeSec}s"
                else -> null
            }
            if (showQuality || transitionLabel != null) {
                val q = nerdStats
                val quality = when {
                    isRadio -> "Live radio"
                    q?.isHiRes == true -> "Hi-Res Lossless"
                    q?.isLossless == true -> "Lossless"
                    q?.isHiQuality == true -> "High"
                    q?.isLowQuality == true || q?.isMediumQuality == true -> if (q.isLowQuality) "Low" else "High"
                    q?.isDolbyAtmos == true -> "Dolby Atmos"
                    song.sourceQuality == "LOSSLESS" || song.sourceQuality == "HI_RES_LOSSLESS" -> "Lossless"
                    song.sourceQuality == "DOLBY" -> "Dolby Atmos"
                    song.videoId.startsWith("local:") -> song.sourceQuality ?: "Local"
                    else -> "High"
                }
                Text(
                    text = listOfNotNull(quality.takeIf { showQuality || isRadio }, transitionLabel).joinToString("  ·  "),
                    color = Color.White.copy(alpha = 0.38f),
                    fontSize = 11.5.sp,
                    modifier = Modifier.padding(top = 1.dp),
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // ---- Transport -------------------------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SpotifyModeControl(
                icon = BitChordIcons.PlayerShuffle,
                contentDescription = "Shuffle",
                active = shuffleEnabled,
                onClick = onToggleShuffle,
            )
            val skipTint = if (song.videoId.startsWith("src:")) Color.White.copy(alpha = 0.35f) else Color.White
            IconButton(
                onClick = {
                    if (song.isPodcastEpisode) {
                        haptics.play(Haptic.SkipPrevious)
                        onSkipBack()
                    } else {
                        haptics.play(Haptic.SkipPrevious)
                        onPrevious()
                    }
                },
                enabled = !song.videoId.startsWith("src:") || song.isPodcastEpisode,
                modifier = Modifier.size(64.dp),
            ) {
                Icon(
                    imageVector = if (song.isPodcastEpisode) Icons.Rounded.Replay10 else Icons.Filled.SkipPrevious,
                    contentDescription = if (song.isPodcastEpisode) "Back 10 seconds" else "Previous",
                    tint = skipTint,
                    modifier = Modifier.size(48.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        if (!isLoading) {
                            haptics.play(if (isPlaying) Haptic.Pause else Haptic.Resume)
                            onPlayPause()
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = SpotifyInk,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(26.dp),
                    )
                } else {
                    SpotifyPlayPauseGlyph(
                        isPlaying = isPlaying,
                        tint = SpotifyInk,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }
            IconButton(
                onClick = {
                    if (song.isPodcastEpisode) {
                        haptics.play(Haptic.SkipNext)
                        onSkipForward()
                    } else {
                        haptics.play(Haptic.SkipNext)
                        onNext()
                    }
                },
                enabled = !song.videoId.startsWith("src:") || song.isPodcastEpisode,
                modifier = Modifier.size(64.dp),
            ) {
                Icon(
                    imageVector = if (song.isPodcastEpisode) Icons.Rounded.Forward30 else Icons.Filled.SkipNext,
                    contentDescription = if (song.isPodcastEpisode) "Forward 30 seconds" else "Next",
                    tint = skipTint,
                    modifier = Modifier.size(48.dp),
                )
            }
            SpotifyModeControl(
                icon = BitChordIcons.PlayerRepeat,
                contentDescription = "Repeat",
                active = repeatMode != 0,
                onClick = onCycleRepeat,
            )
        }

        Spacer(Modifier.height(2.dp))

        // ---- Foot: cast, share and queue -------------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SpotifyPipToggle(
                enabled = pipOn,
                onClick = {
                    haptics.play(if (pipOn) Haptic.ToggleOff else Haptic.ToggleOn)
                    AppSettings.setPipLyricPreview(!pipOn)
                },
            )
            Spacer(Modifier.width(10.dp))
            SpotifySleepCountdown(
                position = position,
                durationMs = durationMs,
            )
            if (song.isPodcastEpisode) {
                Spacer(Modifier.width(6.dp))
                SpotifySpeedChip(
                    speed = playbackSpeed,
                    onClick = {
                        haptics.play(Haptic.Select)
                        onCycleSpeed()
                    },
                )

            }
            Spacer(Modifier.weight(1f))
            if (song.isPodcastEpisode && podcastBookmarksEnabled && onAddBookmark != null) {
                IconButton(
                    onClick = {
                        haptics.play(Haptic.Select)
                        onAddBookmark(position.positionMs)
                    },
                    modifier = Modifier.size(44.dp),
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BookmarkAdd,
                        contentDescription = "Add bookmark",
                        tint = SpotifyMuted(0.82f),
                        modifier = Modifier.size(23.dp),
                    )
                }
            }
            IconButton(onClick = onShare, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = "Share",
                    tint = SpotifyMuted(0.82f),
                    modifier = Modifier.size(23.dp),
                )
            }
            IconButton(onClick = onShowQueue, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Rounded.QueueMusic,
                    contentDescription = "Queue",
                    tint = SpotifyMuted(0.82f),
                    modifier = Modifier.size(23.dp),
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // ---- Lyrics preview (not available for live radio) -------------
        if (!song.videoId.startsWith("src:")) {
            SpotifyLyricsPreview(
                palette = palette,
                onShowLyrics = onShowLyrics,
            )
        }

        Spacer(Modifier.height(6.dp))


    }
}

/* ------------------------------------------------------------------------- */
/* Now Playing view extras                                                    */
/* ------------------------------------------------------------------------- */

@Composable
private fun SpotifyCondensedHeader(
    song: Song,
    isPlaying: Boolean,
    liked: Boolean,
    onPlayPause: () -> Unit,
    onToggleLike: () -> Unit,
    onMore: () -> Unit,
) {
    val haptics = rememberHaptics()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0f to SpotifyBlack.copy(alpha = 0.96f),
                    1f to SpotifyBlack.copy(alpha = 0.82f),
                ),
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = song.displayArtist(),
                color = SpotifyMuted(0.72f),
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (song.videoId.startsWith("src:")) {
            val discoverLiked by DiscoverSaves.likedRadios.collectAsStateWithLifecycle()
            SpotifyLikeBadge(liked = discoverLiked.any { it.key == song.videoId }, size = 28.dp, addContentDescription = "Add to Discover", removeContentDescription = "Remove from Discover", onClick = { DiscoverSaves.toggleLikedRadio(song) })
        } else if (song.isPodcastEpisode) {
            val likedPods by DiscoverSaves.likedPodcasts.collectAsStateWithLifecycle()
            SpotifyLikeBadge(liked = likedPods.any { it.key == (song.albumId ?: song.videoId) }, size = 28.dp, addContentDescription = "Add to Discover", removeContentDescription = "Remove from Discover", onClick = { DiscoverSaves.toggleLikedPodcast(song) })
        } else {
            SpotifyLikeBadge(liked = liked, size = 28.dp, onClick = { onToggleLike() })
        }
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = {
            haptics.play(if (isPlaying) Haptic.Pause else Haptic.Resume)
            onPlayPause()
        }, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = if (isPlaying) BitChordIcons.PlayerPause else BitChordIcons.PlayerPlay,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }
        IconButton(onClick = {
            haptics.play(Haptic.Tap)
            onMore()
        }, modifier = Modifier.size(40.dp)) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "More",
                tint = Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/**
 * The Now Playing extras, one LazyColumn item per card.
 *
 * Kept as separate items rather than one tall column so a section arriving
 * late (the artist page, the similar tracks) only composes that section, and
 * so the list never has to measure a card taller than the viewport before it
 * can scroll.
 */
private fun LazyListScope.spotifyInfoItems(
    song: Song,
    artistPage: ArtistPage?,
    artistBrowseId: String?,
    similar: List<Song>,
    palette: ArtworkPalette,
    onOpenArtist: (String?, String) -> Unit,
    onToggleSubscribe: (String, Boolean) -> Unit,
    onPlaySongs: (List<Song>, Int) -> Unit,
    onOpenPlaybackSource: () -> Unit,
    onOpenAlbum: (String?, String) -> Unit = { _, _ -> },
    onOpenStationInfo: () -> Unit = {},
) {
    val artist = artistPage?.name?.takeIf { it.isNotBlank() } ?: song.displayArtist()
    val isRadio = song.videoId.startsWith("src:")
    val isPodcast = song.isPodcastEpisode

    // ---- Playing from ------------------------------------------------------
    item(key = "info-playing-from") {
        SpotifyInfoInset {
            SpotifySectionCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (isRadio && song.thumbnailUrl.isNullOrBlank()) {
                        KisetsuRadioFallback(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(4.dp)),
                        )
                    } else {
                        AsyncImage(
                            model = rememberRemoteArtworkUrl(song)?.artworkAt(160),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.White.copy(alpha = 0.08f)),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = playingFromHeading(song.playbackSourceType),
                            color = SpotifyMuted(0.62f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = playingFromTitle(song),
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    SpotifyPill(
                        text = "Listen now",
                        onClick = onOpenPlaybackSource,
                    )
                }
            }
        }
    }

    if (isRadio) {
        item(key = "info-radio-about") {
            SpotifyInfoInset {
                SpotifySectionCard {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("About this station", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            SpotifyPill(text = "Station info", onClick = onOpenStationInfo)
                        }
                        Spacer(Modifier.height(10.dp))
                        Text(
                            text = "${song.title} · LIVE",
                            color = Color.White.copy(alpha = 0.92f),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        song.artist.takeIf { it.isNotBlank() && it != song.title }?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(it, color = SpotifyMuted(0.72f), fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Streaming live on Kisetsu. Use Discover to find more radio stations and podcasts, save favorites, and jump back in anytime.",
                            color = SpotifyMuted(0.78f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                        )
                    }
                }
            }
        }
        item(key = "info-radio-discover") {
            SpotifyInfoInset {
                SpotifySectionCard {
                    val likedRadios by com.music.bitchord.data.settings.DiscoverSaves.likedRadios.collectAsStateWithLifecycle()
                    val saved = likedRadios.any { it.key == song.videoId }
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Add to Discover", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text(if (saved) "Saved in your liked radios" else "Save this station to Discover", color = SpotifyMuted(0.68f), fontSize = 13.sp)
                        }
                        SpotifyPill(text = if (saved) "Saved" else "Save", onClick = { com.music.bitchord.data.settings.DiscoverSaves.toggleLikedRadio(song) })
                    }
                }
            }
        }
        if (similar.isNotEmpty()) {
            item(key = "info-radio-similar-songs") {
                SpotifyInfoInset {
                    SpotifySectionCard {
                        Column(Modifier.padding(16.dp)) {
                            Text("Find similar songs", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text("Songs with a similar vibe to this station.", color = SpotifyMuted(0.68f), fontSize = 13.sp)
                            Spacer(Modifier.height(12.dp))
                            SpotifyPill(text = "Play similar songs", onClick = { onPlaySongs(similar, 0) })
                        }
                    }
                }
            }
        }
        item(key = "info-radio-more-stations") {
            SpotifyInfoInset {
                SpotifySectionCard {
                    Column(Modifier.padding(16.dp)) {
                        Text("Similar radio stations", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Discover more live stations and podcasts in the Discover tab.", color = SpotifyMuted(0.68f), fontSize = 13.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Search for stations by name, genre, or mood in Discover → Radios.", color = SpotifyMuted(0.78f), fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
        item(key = "info-radio-tips") {
            SpotifyInfoInset {
                SpotifySectionCard {
                    Column(Modifier.padding(16.dp)) {
                        Text("Live radio tips", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("• Live stations can’t be scrubbed or skipped.\n• Save stations to Discover for quick access.\n• Pin favorites to Quick Launch from the menu.", color = SpotifyMuted(0.78f), fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
    } else {
        // ---- About the artist --------------------------------------------------
        item(key = "info-artist") {
            SpotifyInfoInset {
                SpotifyArtistCard(
                    artist = artist,
                    artistId = artistBrowseId,
                    page = artistPage,
                    fallbackArtwork = rememberRemoteArtworkUrl(song),
                    palette = palette,
                    onOpenArtist = onOpenArtist,
                    onToggleSubscribe = onToggleSubscribe,
                    showFollow = !isPodcast,
                )
            }
        }

        // ---- Album card --------------------------------------------------------
        if (song.albumId != null || song.albumName != null) {
            item(key = "info-album") {
                SpotifyInfoInset {
                    SpotifySectionCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                model = rememberRemoteArtworkUrl(song)?.artworkAt(160),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(58.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color.White.copy(alpha = 0.08f)),
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    text = song.albumName ?: "Album",
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = "Album by ${song.artist}",
                                    color = SpotifyMuted(0.62f),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            SpotifyPill(
                                text = "Go to album",
                                onClick = { onOpenAlbum(song.albumId, song.albumName ?: "Album") },
                            )
                        }
                    }
                }
            }
        }

        // ---- Explore -----------------------------------------------------------
        if (artistPage != null || similar.isNotEmpty()) {
            item(key = "info-explore") {
                SpotifyInfoInset {
                    SpotifySectionCard {
                        Column(Modifier.padding(vertical = 16.dp)) {
                            Text(
                                text = "Explore $artist",
                                color = Color.White,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp),
                            )
                            Spacer(Modifier.height(12.dp))
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                val artistSongs = artistPage?.songs.orEmpty()
                                if (artistSongs.isNotEmpty()) {
                                    item(key = "explore-songs") {
                                        SpotifyExploreCard(
                                            imageUrl = rememberRemoteArtworkUrl(artistSongs.first()),
                                            title = "Songs by\n$artist",
                                            onClick = { onOpenArtist(artistBrowseId, artist) },
                                        )
                                    }
                                }
                                if (similar.isNotEmpty()) {
                                    item(key = "explore-similar") {
                                        SpotifyExploreCard(
                                            imageUrl = rememberRemoteArtworkUrl(similar.first()),
                                            title = "Similar to\n${song.title}",
                                            onClick = { onPlaySongs(similar, 0) },
                                        )
                                    }
                                }
                                item(key = "explore-playing-from") {
                                    SpotifyExploreCard(
                                        imageUrl = rememberRemoteArtworkUrl(song),
                                        title = "Playing from\n${playingFromTitle(song)}",
                                        onClick = onOpenPlaybackSource,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // ---- Credits -----------------------------------------------------------
        item(key = "info-credits") {
            SpotifyInfoInset {
                SpotifySectionCard {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Credits",
                                color = Color.White,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f),
                            )
                            if (isPodcast) {
                                val showId = song.albumId
                                SpotifyPill(text = "View podcast", onClick = {
                                    if (showId != null) onOpenAlbum(showId, song.albumName ?: song.title)
                                })
                            } else {
                                SpotifyPill(text = "View artist", onClick = {
                                    onOpenArtist(artistBrowseId, artist)
                                })
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                        SpotifyCreditRow(song.displayArtist(), if (isPodcast) "Host" else "Main artist")
                        song.albumName?.takeIf { it.isNotBlank() }?.let {
                            Spacer(Modifier.height(12.dp))
                            SpotifyCreditRow(it, if (isPodcast) "Podcast" else "Album")
                        }
                        if (!isPodcast) {
                            artistPage?.subscriberCountText?.takeIf { it.isNotBlank() }?.let {
                                Spacer(Modifier.height(12.dp))
                                SpotifyCreditRow(it, "YouTube channel")
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        SpotifyCreditRow("Kisetsu", "Player")
                    }
                }
            }
        }
    }

    item(key = "info-tail") { Spacer(Modifier.height(40.dp)) }
}

/** The gutter every info card shares, and the gap between one and the next. */
@Composable
private fun SpotifyInfoInset(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        content()
    }
}

@Composable
private fun SpotifySectionCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SpotifyCard),
    ) {
        content()
    }
}

@Composable
private fun SpotifyArtistCard(
    artist: String,
    artistId: String?,
    page: ArtistPage?,
    fallbackArtwork: String?,
    palette: ArtworkPalette,
    onOpenArtist: (String?, String) -> Unit,
    onToggleSubscribe: (String, Boolean) -> Unit,
    showFollow: Boolean = true,
) {
    val haptics = rememberHaptics()
    val image = page?.thumbnailUrl ?: fallbackArtwork
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SpotifyCard),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp),
        ) {
            AsyncImage(
                model = image?.artworkAt(600),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.36f),
                            0.6f to Color.Transparent,
                            1f to SpotifyCard,
                        ),
                    ),
            )
            Text(
                text = "About the artist",
                color = Color.White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp),
            )
        }
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = artist,
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
                Spacer(Modifier.width(6.dp))
                SpotifyVerifiedBadge()
            }
            page?.monthlyListenerCount?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(3.dp))
                Text(it, color = SpotifyMuted(0.62f), fontSize = 12.sp)
            }
            page?.description?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = it,
                    color = SpotifyMuted(0.78f),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (showFollow) {
                Spacer(Modifier.height(14.dp))
                // Seed from the real YouTube subscription state; the page carries
                // it whenever the artist's header offered a subscribe button.
                val subscription = page?.subscription
                var following by remember(subscription?.subscribed, subscription?.channelId) {
                    mutableStateOf(subscription?.subscribed ?: false)
                }
                SpotifyPill(
                    text = if (following) "Following" else "Follow",
                    onClick = {
                        haptics.play(Haptic.Select)
                        val channelId = subscription?.channelId
                        val next = !following
                        following = next
                        if (channelId != null) {
                            onToggleSubscribe(channelId, next)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun SpotifyCreditRow(name: String, role: String) {
    Column {
        Text(
            text = name,
            color = Color.White,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = role,
            color = SpotifyMuted(0.58f),
            fontSize = 12.5.sp,
        )
    }
}

@Composable
private fun SpotifyExploreCard(
    imageUrl: String?,
    title: String,
    onClick: () -> Unit,
) {
    val haptics = rememberHaptics()
    Column(
        modifier = Modifier
            .width(142.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .clickable {
                haptics.play(Haptic.Tap)
                onClick()
            },
    ) {
        AsyncImage(
            model = imageUrl?.artworkAt(360),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(Color.White.copy(alpha = 0.06f)),
        )
        Text(
            text = title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 18.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(10.dp),
        )
    }
}

@Composable
private fun SpotifyVerifiedBadge() {
    Box(
        modifier = Modifier
            .size(17.dp)
            .clip(CircleShape)
            .background(SpotifyGreen),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            tint = SpotifyInk,
            modifier = Modifier.size(12.dp),
        )
    }
}

/* ------------------------------------------------------------------------- */
/* Lyrics preview                                                             */
/* ------------------------------------------------------------------------- */

@Composable
private fun SpotifyLyricsPreview(
    palette: ArtworkPalette,
    onShowLyrics: () -> Unit,
) {
    val haptics = rememberHaptics()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    0f to palette.wash.copy(alpha = 0.82f),
                    1f to palette.background.copy(alpha = 0.92f),
                ),
            )
            .clickable {
                haptics.play(Haptic.Tap)
                onShowLyrics()
            }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Lyrics preview",
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
        SpotifyPill(
            text = "Show lyrics",
            onClick = onShowLyrics,
            filled = true,
        )
    }
}

/* ------------------------------------------------------------------------- */
/* Full lyrics                                                                */
/* ------------------------------------------------------------------------- */

/**
 * Spotify's lyrics view: warm artwork-tinted ink on the sleeve's own gradient,
 * large tight lines, the sung line settling to white and everything behind it
 * falling into a dusty rose. Lines follow the playhead with a smooth scroll,
 * and the whole page can be dragged when you want to read ahead.
 */
@Composable
private fun SpotifyLyricsScreen(
    song: Song,
    lyrics: List<LyricLine>,
    position: PlaybackPosition,
    durationMs: Long,
    isPlaying: Boolean,
    palette: ArtworkPalette,
    onClose: () -> Unit,
    onPlayPause: () -> Unit,
    onSeekFraction: (Float) -> Unit,
    onShare: () -> Unit,
    onMore: () -> Unit,
) {
    val synced = remember(lyrics) { lyrics.any { it.timeMs > 0L } }
    val activeIndex = if (synced) activeLyricIndex(lyrics, position.positionMs) else -1
    val listState = rememberLazyListState()
    // Any manual scroll mutes the auto-snap for a moment, so the lyrics view
    // never steals a drag back toward the active line mid-gesture — the usual
    // face of the "scroll won't respond" report.
    val lastManualScroll = remember { mutableLongStateOf(0L) }
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }.collect { scrolling ->
            if (scrolling) lastManualScroll.value = System.currentTimeMillis()
        }
    }
    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0 && System.currentTimeMillis() - lastManualScroll.value > 4000L) {
            listState.animateScrollToItem(activeIndex)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    0f to palette.wash,
                    0.45f to palette.background,
                    1f to SpotifyBlack,
                ),
            ),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onClose, modifier = Modifier.size(46.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = "Close lyrics",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = song.title,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = song.artist,
                        color = SpotifyMuted(0.66f),
                        fontSize = 12.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.width(46.dp))
            }

            if (lyrics.isEmpty() || lyrics.all { it.isGap }) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Lyrics aren't available for this track yet.",
                        color = SpotifyMuted(0.62f),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp),
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .nestedScroll(PlayerScrollOwner()),
                    contentPadding = PaddingValues(top = 150.dp, bottom = 220.dp),
                ) {
                    itemsIndexed(lyrics) { index, line ->
                        if (line.isGap) {
                            Spacer(Modifier.height(14.dp))
                        } else {
                            SpotifyLyricLine(
                                text = line.text,
                                active = index == activeIndex,
                                past = activeIndex >= 0 && index < activeIndex,
                                onClick = {
                                    if (line.timeMs > 0L && durationMs > 0L) {
                                        onSeekFraction(
                                            (line.timeMs.toFloat() / durationMs)
                                                .coerceIn(0f, 1f),
                                        )
                                    }
                                },
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onShare, modifier = Modifier.size(44.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.Share,
                        contentDescription = "Share",
                        tint = SpotifyMuted(0.80f),
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = onMore, modifier = Modifier.size(44.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "More",
                        tint = SpotifyMuted(0.80f),
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            SpotifyScrubber(
                position = position,
                durationMs = durationMs,
                onSeekFraction = onSeekFraction,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                SpotifyElapsed(position = position, durationMs = durationMs)
                Text(
                    text = formatSpotifyTime(durationMs),
                    color = SpotifyMuted(0.62f),
                    fontSize = 11.5.sp,
                )
            }
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(onClick = onPlayPause),
                contentAlignment = Alignment.Center,
            ) {
                SpotifyPlayPauseGlyph(
                    isPlaying = isPlaying,
                    tint = SpotifyInk,
                    modifier = Modifier.size(26.dp),
                )
            }
            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun SpotifyLyricLine(
    text: String,
    active: Boolean,
    past: Boolean,
    onClick: () -> Unit,
) {
    val color by animateColorAsState(
        targetValue = when {
            active -> Color.White
            past -> Color(0xFFE7B3AD)
            else -> Color.White.copy(alpha = 0.42f)
        },
        animationSpec = tween(280),
        label = "lyricColor",
    )
    val scale by animateFloatAsState(
        targetValue = if (active) 1f else 0.97f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
        label = "lyricScale",
    )
    Text(
        text = text,
        color = color,
        fontSize = if (active) 27.sp else 24.sp,
        fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Bold,
        lineHeight = 32.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = 9.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0f, 0.5f)
            },
    )
}

/* ------------------------------------------------------------------------- */
/* Shared bits                                                                */
/* ------------------------------------------------------------------------- */

/**
 * The lyrics-PiP toggle where the like badge sat: standing alone like the
 * badge was, and lit green while the floating window is armed.
 */
@Composable
private fun SpotifyPipToggle(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (enabled) SpotifyGreen else Color.Transparent)
            .then(
                if (enabled) {
                    Modifier
                } else {
                    Modifier.border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                },
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Rounded.Animation,
            contentDescription = "Show lyric picture-in-picture",
            tint = if (enabled) SpotifyInk else Color.White,
            modifier = Modifier.size(17.dp),
        )
    }
}

/**
 * The armed sleep timer, shown on the player as a tiny moon-and-clock beside
 * the lyrics toggle: a bedtime moon plus the same small grey figures the
 * scrubber times use, ticking once a second toward the pause. A timer set to
 * "after this song" has no countdown, so it shows the song's remaining time —
 * the most honest number there is to show.
 */
@Composable
private fun SpotifySleepCountdown(
    position: PlaybackPosition,
    durationMs: Long,
) {
    val haptics = rememberHaptics()
    val afterTrack by SleepTimer.afterTrack.collectAsStateWithLifecycle()
    val endOfQueue by SleepTimer.endOfQueue.collectAsStateWithLifecycle()
    val deadline by SleepTimer.deadline.collectAsStateWithLifecycle()
    val remaining by produceState<Long?>(initialValue = SleepTimer.remainingMs(), deadline) {
        while (deadline != null) {
            value = SleepTimer.remainingMs()
            delay(1_000)
        }
        value = null
    }
    val afterTrackRemaining = if (afterTrack) (durationMs - position.positionMs).coerceAtLeast(0L) else null
    val ms = remaining ?: afterTrackRemaining
    val active = ms != null || endOfQueue || afterTrack
    var menuOpen by remember { mutableStateOf(false) }
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) {
                    haptics.play(Haptic.Select)
                    menuOpen = true
                }
                .padding(horizontal = 4.dp, vertical = 4.dp),
        ) {
            Icon(
                imageVector = Icons.Rounded.Bedtime,
                contentDescription = "Sleep timer",
                tint = if (active) SpotifyGreen else SpotifyMuted(0.82f),
                modifier = Modifier.size(18.dp),
            )
            if (active) {
                Spacer(Modifier.width(4.dp))
                Text(
                    text = ms?.let { "%d:%02d".format(Locale.ROOT, it / 60_000, it / 1000 % 60) } ?: "End",
                    color = SpotifyMuted(0.72f),
                    fontSize = 11.5.sp,
                )
            }
        }
        DropdownMenu(
            expanded = menuOpen,
            onDismissRequest = { menuOpen = false },
        ) {
            if (active) {
                if (deadline != null) {
                    DropdownMenuItem(text = { Text("+5 min") }, onClick = { SleepTimer.extend(5); menuOpen = false })
                    DropdownMenuItem(text = { Text("+15 min") }, onClick = { SleepTimer.extend(15); menuOpen = false })
                }
                DropdownMenuItem(text = { Text("Cancel timer") }, onClick = { SleepTimer.cancel(); menuOpen = false })
            } else {
                DropdownMenuItem(text = { Text("After this song") }, onClick = { SleepTimer.startAfterTrack(); menuOpen = false })
                DropdownMenuItem(text = { Text("End of queue") }, onClick = { SleepTimer.startEndOfQueue(); menuOpen = false })
                SleepTimer.PRESETS.forEach { minutes ->
                    DropdownMenuItem(text = { Text("$minutes minutes") }, onClick = { SleepTimer.start(minutes); menuOpen = false })
                }
            }
        }
    }
}

/**
 * The podcast playback speed, as a plain "1.5×" chip in the foot row: tap to
 * step through the speeds, exactly the control every podcast app puts on its
 * player. Only shown for podcast episodes.
 */
@Composable
private fun SpotifySpeedChip(
    speed: Float,
    onClick: () -> Unit,
) {
    Text(
        text = if (speed == speed.toInt().toFloat()) {
            "${speed.toInt()}×"
        } else {
            "$speed×"
        },
        color = if (speed != 1f) SpotifyGreen else SpotifyMuted(0.82f),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp),
    )
}

@Composable
private fun SpotifyLikeBadge(
    liked: Boolean,
    size: androidx.compose.ui.unit.Dp = 30.dp,
    addContentDescription: String = "Add to Liked Songs",
    removeContentDescription: String = "Remove from Liked Songs",
    onClick: () -> Unit,
) {
    val haptics = rememberHaptics()
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (liked) SpotifyGreen else Color.Transparent)
            .then(
                if (liked) {
                    Modifier
                } else {
                    Modifier.border(2.dp, Color.White.copy(alpha = 0.85f), CircleShape)
                },
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                haptics.play(Haptic.Select)
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (liked) Icons.Rounded.Check else Icons.Rounded.Add,
            contentDescription = if (liked) removeContentDescription else addContentDescription,
            tint = if (liked) SpotifyInk else Color.White,
            modifier = Modifier.size(size * 0.62f),
        )
    }
}

@Composable
private fun SpotifyModeControl(
    icon: ImageVector,
    contentDescription: String,
    active: Boolean,
    onClick: () -> Unit,
) {
    val haptics = rememberHaptics()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                haptics.play(Haptic.Select)
                onClick()
            }
            .padding(horizontal = 4.dp, vertical = 4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (active) SpotifyGreen else Color.White,
            modifier = Modifier.size(26.dp),
        )
        Spacer(Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .size(3.dp)
                .clip(CircleShape)
                .background(if (active) SpotifyGreen else Color.Transparent),
        )
    }
}

/** A sharp play triangle or the two crisp pause bars Spotify draws. */
@Composable
private fun SpotifyPlayPauseGlyph(
    isPlaying: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier) {
        if (isPlaying) {
            val barWidth = size.width * 0.30f
            val gap = size.width * 0.08f
            val top = size.height * 0.10f
            val barHeight = size.height * 0.80f
            val radius = CornerRadius(1.5.dp.toPx())
            drawRoundRect(
                color = tint,
                topLeft = Offset((size.width - barWidth * 2 - gap) / 2f, top),
                size = Size(barWidth, barHeight),
                cornerRadius = radius,
            )
            drawRoundRect(
                color = tint,
                topLeft = Offset((size.width - barWidth * 2 - gap) / 2f + barWidth + gap, top),
                size = Size(barWidth, barHeight),
                cornerRadius = radius,
            )
        } else {
            val path = Path().apply {
                moveTo(size.width * 0.18f, size.height * 0.08f)
                lineTo(size.width * 0.90f, size.height * 0.5f)
                lineTo(size.width * 0.18f, size.height * 0.92f)
                close()
            }
            drawPath(path, tint)
        }
    }
}

@Composable
private fun SpotifyPill(
    text: String,
    onClick: () -> Unit,
    filled: Boolean = false,
) {
    val haptics = rememberHaptics()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (filled) Color.White else Color.Transparent)
            .border(
                width = if (filled) 0.dp else 1.dp,
                color = if (filled) Color.Transparent else Color.White.copy(alpha = 0.55f),
                shape = RoundedCornerShape(50),
            )
            .clickable {
                haptics.play(Haptic.Tap)
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (filled) SpotifyInk else Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun SpotifyElapsed(
    position: PlaybackPosition,
    durationMs: Long,
    scrubFraction: Float? = null,
) {
    val ms = if (scrubFraction != null && durationMs > 0L) {
        (scrubFraction * durationMs).toLong()
    } else {
        position.positionMs
    }.coerceIn(0L, durationMs.coerceAtLeast(0L))
    Text(
        text = formatSpotifyTime(ms),
        color = SpotifyMuted(0.62f),
        fontSize = 11.5.sp,
    )
}

private fun formatSpotifyTime(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val totalSeconds = ms / 1000L
    val minutes = totalSeconds / 60L
    val seconds = totalSeconds % 60L
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

/** The line being sung at [positionMs], or the first line before the first stamp. */
private fun activeLyricIndex(lines: List<LyricLine>, positionMs: Long): Int {
    if (lines.isEmpty()) return -1
    var low = 0
    var high = lines.lastIndex
    var found = -1
    while (low <= high) {
        val mid = (low + high) ushr 1
        if (lines[mid].timeMs <= positionMs) {
            found = mid
            low = mid + 1
        } else {
            high = mid - 1
        }
    }
    return found.coerceAtLeast(0)
}

/**
 * The floating picture-in-picture card: the album cover sitting on a dark
 * gradient, with the lyric line being sung right now (or the title when there
 * are no lyrics) beneath it — Spotify's little always-on window.
 *
 * This is the ONLY thing drawn while the activity is in PiP mode, so the full
 * player (scrubber, buttons, three dots) never gets squished into the tiny
 * PiP window.
 */
@Composable
fun SpotifyPipOverlay(
    song: com.music.bitchord.data.model.Song?,
    lyrics: List<LyricLine>?,
    position: com.music.bitchord.playback.PlaybackPosition,
    modifier: Modifier = Modifier,
) {
    // Poll the playhead so the singing line advances even when the activity is
    // paused in PiP and normal snapshot recomposition would stall. Reads the
    // live position every 300 ms and writes it into local state, guaranteeing
    // the lyric line changes the moment the playhead moves.
    var positionMs by remember { mutableLongStateOf(position.positionMs) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(300L)
            positionMs = position.positionMs
        }
    }

    val activeIndex = if (lyrics.isNullOrEmpty()) -1 else activeLyricIndex(lyrics, positionMs)
    val lyricLine = lyrics?.getOrNull(activeIndex)?.text.orEmpty()

    Box(
        modifier
            .background(
                Brush.verticalGradient(
                    0f to Color(0xFF202020),
                    1f to Color.Black,
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .aspectRatio(1f)
                    .shadow(18.dp, RoundedCornerShape(10.dp), spotColor = Color.Black)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Black),
            ) {
                if (song != null) {
                    AsyncImage(
                        model = song.thumbnailUrl,
                        contentDescription = song.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Spacer(Modifier.height(22.dp))
            Text(
                text = lyricLine.ifBlank { song?.title.orEmpty() },
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (lyricLine.isBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = song?.artist.orEmpty(),
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * A thin, draggable Spotify scrubber.
 *
 * The playhead is read inside this composable so the twice-a-second tick
 * recomposes the bar and the elapsed label, not the whole player.
 */
@Composable
private fun SpotifyScrubber(
    position: PlaybackPosition,
    durationMs: Long,
    onSeekFraction: (Float) -> Unit,
    onScrubFraction: (Float?) -> Unit = {},
    modifier: Modifier = Modifier,
    isLiveRadio: Boolean = false,
) {
    val haptics = rememberHaptics()
    var dragFraction by remember { mutableStateOf<Float?>(null) }
    var widthPx by remember { mutableIntStateOf(1) }

    val liveFraction = if (isLiveRadio) 1f else if (durationMs > 0L) {
        (position.positionMs.toFloat() / durationMs).coerceIn(0f, 1f)
    } else {
        0f
    }
    val fraction = if (isLiveRadio) 1f else (dragFraction ?: liveFraction)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp)
            .onSizeChanged { widthPx = it.width.coerceAtLeast(1) }
            .then(
                if (isLiveRadio) {
                    Modifier
                } else {
                    Modifier
                        .pointerInput(widthPx, durationMs) {
                            detectTapGestures { offset ->
                                if (durationMs > 0L) {
                                    haptics.play(Haptic.Tick)
                                    onSeekFraction((offset.x / widthPx).coerceIn(0f, 1f))
                                }
                            }
                        }
                        .pointerInput(widthPx, durationMs) {
                            fun at(x: Float) = (x / widthPx).coerceIn(0f, 1f)
                            detectHorizontalDragGestures(
                                onDragStart = { offset ->
                                    dragFraction = at(offset.x)
                                    onScrubFraction(dragFraction)
                                },
                                onDragEnd = {
                                    haptics.play(Haptic.Select)
                                    dragFraction?.let(onSeekFraction)
                                    dragFraction = null
                                    onScrubFraction(null)
                                },
                                onDragCancel = {
                                    dragFraction = null
                                    onScrubFraction(null)
                                },
                            ) { change, _ ->
                                change.consume()
                                dragFraction = at(change.position.x)
                                onScrubFraction(dragFraction)
                            }
                        }
                },
            ),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(if (isLiveRadio) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.24f)),
        )
        Box(
            Modifier
                .fillMaxWidth(fraction)
                .height(4.dp)
                .clip(CircleShape)
                .background(if (isLiveRadio) Color.White.copy(alpha = 0.34f) else Color.White.copy(alpha = 0.90f)),
        )
        if (!isLiveRadio) {
            Box(
                Modifier
                    .offset {
                        IntOffset(
                            x = (fraction * widthPx - 6.dp.toPx()).roundToInt()
                                .coerceIn(0, (widthPx - 12.dp.toPx().roundToInt()).coerceAtLeast(0)),
                            y = 0,
                        )
                    }
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            )
        }
    }
}

/* ------------------------------------------------------------------------- */
/* Spotify Home                                                               */
/* ------------------------------------------------------------------------- */

// The filter tabs across the top of the Spotify home page. Mixes — the
// personal mix, radio and flow shelves — sits next to All as Spotify does;
// audiobooks have no shelf here, so its tab is gone.
private val SpotifyHomePills = listOf(
    "All",
    "Mixes",
    "Music",
    "Podcasts",
    "Artists",
)

/**
 * The optional Spotify home page.
 *
 * It wears the existing home feed rather than a new one: the pill row filters
 * the shelves the app already loaded, artist shelves become circular portraits,
 * and everything else is drawn as Spotify's own horizontal card rows. Pulling
 * down refreshes the real feed, so the page behaves like the rest of the app.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifyHomeScreen(
    state: UiState<List<HomeShelf>>,
    listState: LazyListState,
    onItemClick: (ShelfItem, String) -> Unit,
    onRetry: () -> Unit,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    pullState: PullToRefreshState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    onItemLongPress: ((ShelfItem) -> Unit)? = null,
    onPodcastResume: (com.music.bitchord.data.stats.PodcastBookmark) -> Unit = {},
    onPodcastRestart: (com.music.bitchord.data.stats.PodcastBookmark) -> Unit = {},
    onPinTap: (com.music.bitchord.data.settings.HomePin) -> Unit = {},
) {
    var selectedPill by rememberSaveable { mutableStateOf(SpotifyHomePills.first()) }
    // A pill saved under an older list (e.g. the retired Audiobooks tab) falls
    // back to All rather than an empty page.
    if (selectedPill !in SpotifyHomePills) selectedPill = SpotifyHomePills.first()

    val shelves = (state as? UiState.Success)?.data.orEmpty()
    val filtered = remember(shelves, selectedPill) {
        if (selectedPill == "All") {
            shelves
        } else {
            shelves.filter { spotifyShelfGroup(it) == selectedPill }
        }
    }
    // When the account's home feed has no podcast shelves of its own — the
    // common case — the Podcasts tab borrows YouTube's own Podcasts
    // destination so the tab is never dead. Fetched lazily, only when the
    // user actually opens the tab.
    val podcastFallback = produceState<List<HomeShelf>?>(initialValue = null, selectedPill) {
        if (selectedPill == "Podcasts") {
            value = YtMusicRepository.podcastShelves().getOrNull().orEmpty()
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyBlack),
        contentPadding = contentPadding,
    ) {
            item(key = "home-header") {
                Column(Modifier.padding(top = 0.dp, bottom = 4.dp)) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(SpotifyHomePills) { pill ->
                            SpotifyHomePill(
                                label = pill,
                                selected = pill == selectedPill,
                                onClick = { selectedPill = pill },
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // 8-rectangle grid of recent playlists (2 per line, small)
                    val recentItems = remember(shelves) {
                        shelves.flatMap { it.items }.take(8)
                    }
                    if (recentItems.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            recentItems.chunked(2).forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    rowItems.forEach { item ->
                                        SpotifyHomeGridCard(
                                            item = item,
                                            onClick = { onItemClick(item, "") },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(14.dp))
                    }
                }
            }

            // The Quick launch panel and the podcast "Jump back in" row sit
            // directly under the grid — the shortcut row and the half-heard
            // episodes Spotify puts right at the top of the home feed. Both
            // belong to the listener's own device rather than to YouTube's
            // feed, so they show on the All tab only: a Music tab filtered to
            // music has no business wearing pins to radio stations.
            if (selectedPill == "All") {
                item(key = "quick-launch") {
                    SpotifyQuickLaunch(onPinTap = onPinTap)
                }
                item(key = "podcast-jump-back") {
                    SpotifyPodcastJumpBackIn(onResume = onPodcastResume, onRestart = onPodcastRestart)
                }
            }

            item(key = "jump-back-in") {
                // Spotify's "jump back in" shelf sits right under the grid:
                // the artist, album and song the listener plays the most,
                // read off the phone's own listening ledger — nothing more.
                SpotifyJumpBackIn(onItemClick = onItemClick)
            }
            when (state) {
                is UiState.Loading -> item(key = "home-loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 80.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = SpotifyGreen, strokeWidth = 2.dp)
                    }
                }
                is UiState.Error -> item(key = "home-error") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = state.message,
                            color = SpotifyMuted(0.72f),
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        SpotifyPill(text = "Retry", onClick = onRetry, filled = true)
                    }
                }
                is UiState.Success -> {
                     val podcastsToShow = selectedPill == "Podcasts" && filtered.isEmpty() &&
                        !podcastFallback.value.isNullOrEmpty()
                    if (podcastsToShow) {
                        item(key = "podcast-fallback-header") {
                            Text(
                                text = "Podcasts for you",
                                style = MaterialTheme.typography.titleMedium,
                                color = SpotifyMuted(0.80f),
                                modifier = Modifier.padding(horizontal = PAGE_GUTTER, vertical = 8.dp),
                            )
                        }
                        itemsIndexed(
                            podcastFallback.value.orEmpty(),
                            key = { index, shelf -> "fallback-$index-${shelf.title}" },
                        ) { _, shelf ->
                            SpotifyHomeShelf(
                                shelf = shelf,
                                onItemClick = { item -> onItemClick(item, shelf.title) },
                                onItemLongPress = onItemLongPress,
                            )
                        }
                    } else if (filtered.isEmpty()) {
                        item(key = "home-empty") {
                            Text(
                                text = "Nothing here yet.",
                                color = SpotifyMuted(0.60f),
                                fontSize = 15.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 56.dp),
                            )
                        }
                    } else {
                        itemsIndexed(
                            filtered,
                            // Index in the key: shelves repeat titles — an artist
                            // page can carry two "about" shelves (bio, fixes,
                            // etc.), and one duplicate key crashes the whole
                            // list the moment the second shelf is measured.
                            key = { index, shelf -> "shelf-$index-${shelf.title}" },
                        ) { _, shelf ->
                            SpotifyHomeShelf(
                                shelf = shelf,
                                onItemClick = { item -> onItemClick(item, shelf.title) },
                                onItemLongPress = onItemLongPress,
                            )
                        }
                    }
                }
            }

            item(key = "home-tail") { Spacer(Modifier.height(24.dp)) }
        }
}

/**
 * The Quick launch panel: the listener's own pins, as a row of round tiles
 * right under the recent grid — Spotify's old shortcuts, but the listener
 * chooses what sits here. Pins arrive by holding a song, an episode, a
 * station, a release, an artist or a show anywhere its menu opens and
 * choosing "Pin to home"; holding a tile here unpins it. Renders nothing
 * until something is pinned.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpotifyQuickLaunch(
    onPinTap: (com.music.bitchord.data.settings.HomePin) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pins by com.music.bitchord.data.settings.HomePins.pins.collectAsStateWithLifecycle()
    var confirmPin by remember { mutableStateOf<com.music.bitchord.data.settings.HomePin?>(null) }
    if (pins.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth().padding(top = 4.dp, bottom = 4.dp)) {
        Text(
            text = "Quick launch",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 10.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            items(pins, key = { it.key }) { pin ->
                Column(
                    modifier = Modifier
                        .width(68.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .combinedClickable(
                            onClick = { onPinTap(pin) },
                            onLongClick = { confirmPin = pin },
                        )
                        .padding(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    val kisetsuRadio = pin.kind == com.music.bitchord.data.settings.HomePin.KIND_RADIO && pin.thumbnailUrl.isNullOrBlank() ||
                        (pin.videoId?.startsWith("src:") == true && pin.thumbnailUrl.isNullOrBlank())
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (kisetsuRadio) Color(0xFFFA2D48) else SpotifyCard)
                            .border(1.dp, if (kisetsuRadio) Color.Transparent else SpotifyMuted(0.28f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            kisetsuRadio -> androidx.compose.foundation.Image(
                                painter = painterResource(com.music.bitchord.R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = Modifier.size(38.dp),
                            )
                            pin.thumbnailUrl != null -> AsyncImage(
                                model = pin.thumbnailUrl.artworkAt(120),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                            else -> Icon(
                                imageVector = when (pin.kind) {
                                    com.music.bitchord.data.settings.HomePin.KIND_RADIO -> Icons.Rounded.Radio
                                    com.music.bitchord.data.settings.HomePin.KIND_ARTIST -> Icons.Rounded.Person
                                    else -> BitChordIcons.MusicNote
                                },
                                contentDescription = null,
                                tint = SpotifyMuted(0.8f),
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = pin.title,
                        color = Color.White,
                        fontSize = 10.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }

    val pending = confirmPin
    if (pending != null) {
        AlertDialog(
            onDismissRequest = { confirmPin = null },
            title = { Text("Remove from Quick launch?") },
            text = { Text("Are you sure you want to delete \"${pending.title}\" from Quick launch?") },
            confirmButton = {
                TextButton(onClick = {
                    com.music.bitchord.data.settings.HomePins.remove(pending.key)
                    confirmPin = null
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { confirmPin = null }) { Text("No") }
            },
        )
    }
}

/**
 * "Jump back in" for podcasts: the episodes this device was last heard
 * playing, each card carrying the minute it stopped at. Tapping one goes
 * back to that minute — never to the top — and holding one tells it to leave
 * the row. Up to twenty are kept; renders nothing until something has been
 * heard.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpotifyPodcastJumpBackIn(
    onResume: (com.music.bitchord.data.stats.PodcastBookmark) -> Unit,
    onRestart: (com.music.bitchord.data.stats.PodcastBookmark) -> Unit,
    modifier: Modifier = Modifier,
) {
    val entries by com.music.bitchord.data.stats.PodcastResume.entries.collectAsStateWithLifecycle()
    if (entries.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth().padding(top = 6.dp, bottom = 4.dp)) {
        Text(
            text = "Jump back in",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 2.dp),
        )
        Text(
            text = "Podcasts · picks up where you left off",
            color = SpotifyMuted(0.78f),
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(entries, key = { it.videoId }) { bookmark ->
                PodcastJumpCard(
                    bookmark = bookmark,
                    onClick = { onResume(bookmark) },
                    onRestart = { onRestart(bookmark) },
                )
            }
        }
    }
}

/** One episode card: art, progress bar, and long-press actions (restart/pin/delete). */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PodcastJumpCard(
    bookmark: com.music.bitchord.data.stats.PodcastBookmark,
    onClick: () -> Unit,
    onRestart: () -> Unit,
) {
    var menuOpen by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    val homePins by com.music.bitchord.data.settings.HomePins.pins.collectAsStateWithLifecycle()
    val pin = remember(bookmark) {
        com.music.bitchord.data.settings.HomePin(
            videoId = bookmark.videoId,
            title = bookmark.title,
            subtitle = bookmark.showName,
            thumbnailUrl = bookmark.thumbnailUrl,
            kind = com.music.bitchord.data.settings.HomePin.KIND_EPISODE,
        )
    }
    val pinned = homePins.any { it.key == pin.key }

    Box {
        Column(
            modifier = Modifier
                .width(118.dp)
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(onClick = onClick, onLongClick = { menuOpen = true })
                .padding(4.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpotifyCard),
            ) {
                if (bookmark.thumbnailUrl != null) {
                    AsyncImage(
                        model = bookmark.thumbnailUrl.artworkAt(240),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(bookmark.fraction.takeIf { it > 0f } ?: 0f)
                        .height(3.dp)
                        .background(SpotifyGreen),
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = bookmark.title,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(1.dp))
            Text(
                text = bookmark.showName,
                color = SpotifyMuted(0.66f),
                fontSize = 10.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
            DropdownMenuItem(
                text = { Text("Restart") },
                onClick = {
                    menuOpen = false
                    onRestart()
                },
            )
            DropdownMenuItem(
                text = { Text(if (pinned) "Remove from quick launch" else "Add to quick launch") },
                onClick = {
                    menuOpen = false
                    com.music.bitchord.data.settings.HomePins.toggle(pin)
                },
            )
            DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    menuOpen = false
                    confirmDelete = true
                },
            )
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete episode?") },
            text = { Text("Are you sure you wanna delete this from Jump back in?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    com.music.bitchord.data.stats.PodcastResume.remove(bookmark.videoId)
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("No") }
            },
        )
    }
}

/**
 * The "Jump back in" shelf family: what the listener plays the most of, right
 * there under the grid on Spotify's home. Three horizontally scrolling rows —
 * songs, artists and albums — read off the listening ledger on the device, so
 * it knows the works of the phone in the listener's pocket and nothing else.
 * Renders nothing until it has something to show.
 */
@Composable
private fun SpotifyJumpBackIn(
    onItemClick: (ShelfItem, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val allTime by produceState<com.music.bitchord.data.stats.ReplaySummary?>(initialValue = null) {
        value = try {
            com.music.bitchord.data.stats.ListeningStats.summary(
                com.music.bitchord.data.stats.ReplayPeriod.ALL_TIME,
            )
        } catch (_: Exception) {
            null
        }
    }
    val thisMonth by produceState<com.music.bitchord.data.stats.ReplaySummary?>(initialValue = null) {
        value = try {
            com.music.bitchord.data.stats.ListeningStats.summary(
                com.music.bitchord.data.stats.ReplayPeriod.THIS_MONTH,
            )
        } catch (_: Exception) {
            null
        }
    }
    val allData = allTime?.takeIf { !it.isEmpty }
    val monthData = thisMonth?.takeIf { !it.isEmpty }
    if (allData == null && monthData == null) return
    val monthSongs = remember(monthData) { monthData?.songs?.take(10).orEmpty() }
    val monthIds = remember(monthSongs) { monthSongs.map { it.song.videoId }.toSet() }
    val heavySongs = remember(allData, monthIds) {
        val all = allData?.songs.orEmpty()
        val filtered = all.filterNot { it.song.videoId in monthIds }
        (if (filtered.size >= 6) filtered else all).take(12)
    }
    val artists = remember(allData) { allData?.artists?.take(12).orEmpty() }
    val albums = remember(allData) { allData?.albums?.take(12).orEmpty() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 4.dp),
    ) {
        Text(
            text = "For you",
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 2.dp),
        )
        if (monthSongs.isNotEmpty()) {
            JumpJumpRowHeader("On repeat this month")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(monthSongs, key = { "m_${it.song.videoId}" }) { ranked ->
                    val song = ranked.song
                    JumpCard(
                        artwork = song.thumbnailUrl,
                        title = song.title,
                        subtitle = song.artist,
                        circular = false,
                        kisetsuFallback = song.videoId.startsWith("src:"),
                        onClick = {
                            onItemClick(
                                ShelfItem(song.title, song.artist, song.thumbnailUrl, song.videoId, null),
                                "On repeat this month",
                            )
                        },
                    )
                }
            }
        }
        if (heavySongs.isNotEmpty()) {
            SongJumpRow(
                songs = heavySongs,
                onItemClick = onItemClick,
                title = if (monthSongs.isNotEmpty()) "Heavy rotation" else "Most played songs",
                showHeader = true,
                shelfLabel = "For you",
            )
        }
        if (artists.isNotEmpty()) {
            ArtistJumpRow(artists, onItemClick, showHeader = true, shelfLabel = "For you")
        }
        if (albums.isNotEmpty()) {
            AlbumJumpRow(albums, onItemClick, showHeader = true, shelfLabel = "For you")
        }
        Spacer(Modifier.height(6.dp))
    }
}

@Composable
private fun JumpJumpRowHeader(title: String) {
    Text(
        text = title,
        color = SpotifyMuted(0.78f),
        fontSize = 12.5.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 8.dp),
    )
}

@Composable
private fun SongJumpRow(
    songs: List<com.music.bitchord.data.stats.RankedSong>,
    onItemClick: (ShelfItem, String) -> Unit,
    title: String = "Most played songs",
    showHeader: Boolean = true,
    shelfLabel: String = "Jump back in",
) {
    if (songs.isEmpty()) return
    if (showHeader) JumpJumpRowHeader(title)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(songs, key = { "${shelfLabel}_${it.song.videoId}" }) { ranked ->
            val song = ranked.song
            JumpCard(
                artwork = song.thumbnailUrl,
                title = song.title,
                subtitle = song.artist,
                circular = false,
                kisetsuFallback = song.videoId.startsWith("src:"),
                onClick = {
                    onItemClick(
                        ShelfItem(song.title, song.artist, song.thumbnailUrl, song.videoId, null),
                        shelfLabel,
                    )
                },
            )
        }
    }
}

@Composable
private fun ArtistJumpRow(
    artists: List<com.music.bitchord.data.stats.RankedEntry>,
    onItemClick: (ShelfItem, String) -> Unit,
    showHeader: Boolean = true,
    shelfLabel: String = "For you",
) {
    if (artists.isEmpty()) return
    if (showHeader) JumpJumpRowHeader("Most played artists")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(artists, key = { "a_${it.browseId ?: it.title}" }) { artist ->
            JumpCard(
                artwork = artist.artworkUrl,
                title = artist.title,
                subtitle = artist.subtitle,
                circular = true,
                kisetsuFallback = artist.artworkUrl.isNullOrBlank(),
                onClick = {
                    onItemClick(
                        ShelfItem(artist.title, artist.subtitle.orEmpty(), artist.artworkUrl, null, artist.browseId),
                        shelfLabel,
                    )
                },
            )
        }
    }
}

@Composable
private fun AlbumJumpRow(
    albums: List<com.music.bitchord.data.stats.RankedEntry>,
    onItemClick: (ShelfItem, String) -> Unit,
    showHeader: Boolean = true,
    shelfLabel: String = "For you",
) {
    if (albums.isEmpty()) return
    if (showHeader) JumpJumpRowHeader("Most played albums")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(albums, key = { "al_${it.browseId ?: it.title}" }) { album ->
            JumpCard(
                artwork = album.artworkUrl,
                title = album.title,
                subtitle = album.subtitle,
                circular = false,
                onClick = {
                    onItemClick(
                        ShelfItem(album.title, album.subtitle.orEmpty(), album.artworkUrl, null, album.browseId),
                        shelfLabel,
                    )
                },
            )
        }
    }
}

/** One card for Jump back in: square or circle, same text underneath. */
@Composable
private fun JumpCard(
    artwork: String?,
    title: String,
    subtitle: String?,
    circular: Boolean,
    onClick: () -> Unit,
    kisetsuFallback: Boolean = false,
) {
    val shape = if (circular) CircleShape else RoundedCornerShape(8.dp)
    val useKisetsu = kisetsuFallback || artwork.isNullOrBlank()
    Column(
        modifier = Modifier
            .width(118.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(shape)
                .background(if (useKisetsu) Color(0xFFFA2D48) else SpotifyCard),
            contentAlignment = Alignment.Center,
        ) {
            if (useKisetsu) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = null,
                    modifier = Modifier.size(if (circular) 58.dp else 68.dp),
                )
            } else {
                AsyncImage(
                    model = artwork!!.artworkAt(240),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (subtitle != null) {
            Spacer(Modifier.height(1.dp))
            Text(
                text = subtitle,
                color = SpotifyMuted(0.66f),
                fontSize = 10.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

/**
 * Which home tab a shelf belongs to, read off what the cards *are* rather than
 * what the shelf happens to be called.
 *
 * Title keywords were the first attempt and they lied twice over: a shelf of
 * party mixes titled "Feel-Good R&B Vibes" carries no "mix" in its name, and a
 * "Artists similar to…" shelf full of unrelated tracks does. The cards
 * themselves carry the truth in their navigation ids — artists start with
 * "UC", albums with "MPRE" (or an "OLAK" album wearing a playlist id), the
 * radio mixes YouTube generates start with "RD" once the "VL" wrapper is off,
 * a plain track has a videoId and no browse target at all, and a podcast show
 * starts with "MPSP" or says so in its subtitle.
 *
 * Each shelf goes to the tab that owns the majority of its cards, so a mixed
 * shelf ("Listen again" holds songs *and* the odd mix) still lands somewhere
 * sensible instead of appearing on every tab or none.
 */
private fun spotifyShelfGroup(shelf: HomeShelf): String {
    val items = shelf.items
    if (items.isEmpty()) return "Music"
    if (shelf.title.contains("podcast", ignoreCase = true)) return "Podcasts"

    fun isMix(id: String): Boolean {
        val playlistId = id.removePrefix("VL")
        // RDAMVM/RDEM… radio seeded off a track or artist, RDCLAK5uy… curated
        // editorial mixes, RD… everything else YouTube generates on the fly.
        return playlistId.startsWith("RD")
    }

    val podcasts = items.count { item ->
        item.browseId?.startsWith("MPSP") == true ||
            item.subtitle.contains("podcast", ignoreCase = true) ||
            item.subtitle.contains("episode", ignoreCase = true)
    }
    if (podcasts * 2 >= items.size && podcasts > 0) return "Podcasts"

    val artists = items.count { it.browseId?.startsWith("UC") == true }
    val albums = items.count {
        it.browseId?.startsWith("MPRE") == true || it.browseId?.startsWith("OLAK") == true
    }
    val mixes = items.count { it.browseId?.let(::isMix) == true }
    val songs = items.count { it.videoId != null && it.browseId == null }

    val best = listOf(
        "Artists" to artists + albums,
        "Mixes" to mixes,
        "Music" to songs,
    ).maxByOrNull { it.second }
    if (best != null && best.second > 0) return best.first
    // Nothing in the cards said what the shelf is — fall back to the title
    // before giving up on it.
    val title = shelf.title.lowercase()
    return when {
        "album" in title || "artist" in title -> "Artists"
        "mix" in title || "radio" in title -> "Mixes"
        "podcast" in title || "episode" in title -> "Podcasts"
        else -> "Music"
    }
}

@Composable
private fun SpotifyHomeShelf(
    shelf: HomeShelf,
    onItemClick: (ShelfItem) -> Unit,
    onItemLongPress: ((ShelfItem) -> Unit)?,
) {
    if (shelf.items.isEmpty()) return
    val artists = spotifyShelfGroup(shelf) == "Artists"

    Column(Modifier.padding(bottom = 26.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = shelf.title,
                    color = Color.White,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (shelf.subtitle.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = shelf.subtitle,
                        color = SpotifyMuted(0.60f),
                        fontSize = 12.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(if (artists) 16.dp else 12.dp),
        ) {
            items(shelf.items) { item ->
                if (artists) {
                    SpotifyHomeArtistCard(
                        item = item,
                        onClick = { onItemClick(item) },
                        onLongPress = onItemLongPress?.let { { it(item) } },
                    )
                } else {
                    SpotifyHomeCard(
                        item = item,
                        onClick = { onItemClick(item) },
                        onLongPress = onItemLongPress?.let { { it(item) } },
                    )
                }
            }
        }
    }
}

@Composable
private fun PodcastBookmarksDialog(
    show: Boolean,
    markers: List<com.music.bitchord.data.stats.PodcastBookmarkMarker>,
    onDismiss: () -> Unit,
    onSeek: (Long) -> Unit,
    onRemove: (Long) -> Unit,
) {
    if (!show) return
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Bookmarks") },
        text = {
            if (markers.isEmpty()) {
                Text("No bookmarks yet. Tap + to save this moment.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyColumn(modifier = Modifier.heightIn(max = 320.dp)) {
                    items(markers, key = { it.positionMs }) { m ->
                        val s = m.positionMs / 1000
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSeek(m.positionMs) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "%d:%02d".format(s / 60, s % 60),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.width(56.dp),
                            )
                            Text(
                                text = m.label.ifBlank { "Bookmark" },
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            TextButton(onClick = { onRemove(m.positionMs) }) { Text("Remove") }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}

@Composable
fun RadioStationInfoDialog(
    show: Boolean,
    song: Song,
    onDismiss: () -> Unit,
) {
    if (!show || !song.videoId.startsWith("src:")) return
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val pin = com.music.bitchord.data.settings.HomePin.ofSong(
        song,
        com.music.bitchord.data.settings.HomePin.KIND_RADIO,
    )
    val pinned = com.music.bitchord.data.settings.HomePins.isPinned(pin)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = song.title, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (song.artist.isNotBlank()) {
                    Text(song.artist, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
                Text("Live radio · stream playing", fontSize = 12.sp, color = SpotifyGreen)
                Text(
                    "Station ID: ${song.videoId.removePrefix("src:")}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val nowPinned = com.music.bitchord.data.settings.HomePins.toggle(pin)
                android.widget.Toast.makeText(
                    context,
                    if (nowPinned) "Added to Quick launch" else "Removed from Quick launch",
                    android.widget.Toast.LENGTH_SHORT,
                ).show()
                onDismiss()
            }) { Text(if (pinned) "Remove from Quick launch" else "Add to Quick launch") }
        },
        dismissButton = {
            Row {
                TextButton(onClick = {
                    clipboard.setText(AnnotatedString("${song.title}\n${song.artist}\n${song.videoId}"))
                    android.widget.Toast.makeText(context, "Copied", android.widget.Toast.LENGTH_SHORT).show()
                    onDismiss()
                }) { Text("Copy") }
                TextButton(onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "${song.title}\n${song.artist}")
                    }
                    context.startActivity(Intent.createChooser(send, "Share station"))
                    onDismiss()
                }) { Text("Share") }
            }
        },
    )
}

@Composable
private fun SpotifyHomePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val haptics = rememberHaptics()
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .background(if (selected) SpotifyGreen else SpotifyCard)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                if (!selected) haptics.play(Haptic.Select)
                else haptics.play(Haptic.Tap)
                onClick()
            }
            .padding(horizontal = 15.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpotifyHomeCard(
    item: ShelfItem,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)?,
) {
    val haptics = rememberHaptics()
    Column(
        modifier = Modifier
            .width(146.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                haptics.play(Haptic.Tap)
                onClick()
            }
            .then(
                if (onLongPress != null) {
                    Modifier.combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                        onLongClick = onLongPress,
                    )
                } else {
                    Modifier
                },
            ),
    ) {
        if (item.videoId?.startsWith("src:") == true && item.thumbnailUrl.isNullOrBlank()) {
            KisetsuRadioFallback(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
            )
        } else {
            AsyncImage(
                model = item.thumbnailUrl?.artworkAt(420),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.06f)),
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = item.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 17.sp,
        )
        if (item.subtitle.isNotBlank()) {
            Spacer(Modifier.height(3.dp))
            Text(
                text = item.subtitle,
                color = SpotifyMuted(0.58f),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 15.sp,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SpotifyHomeGridCard(
    item: ShelfItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberHaptics()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SpotifyCard)
            .clickable {
                haptics.play(Haptic.Tap)
                onClick()
            }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.videoId?.startsWith("src:") == true && item.thumbnailUrl.isNullOrBlank()) {
            KisetsuRadioFallback(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
        } else {
            AsyncImage(
                model = item.thumbnailUrl?.artworkAt(120),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(alpha = 0.06f)),
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = item.title,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SpotifyHomeArtistCard(
    item: ShelfItem,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)?,
) {
    val haptics = rememberHaptics()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(126.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable {
                haptics.play(Haptic.Tap)
                onClick()
            }
            .then(
                if (onLongPress != null) {
                    Modifier.combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onClick,
                        onLongClick = onLongPress,
                    )
                } else {
                    Modifier
                },
            )
            .padding(vertical = 2.dp),
    ) {
        AsyncImage(
            model = item.thumbnailUrl?.artworkAt(420),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(126.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f)),
        )
        Spacer(Modifier.height(9.dp))
        Text(
            text = item.title,
            color = Color.White,
            fontSize = 13.5.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        if (item.subtitle.isNotBlank()) {
            Text(
                text = item.subtitle,
                color = SpotifyMuted(0.58f),
                fontSize = 11.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
