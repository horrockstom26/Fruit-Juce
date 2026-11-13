package com.music.bitchord.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
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
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Forward30
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Radio
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Replay10
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
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
import com.music.bitchord.ui.player.PlayerIcons
import androidx.compose.foundation.Image
import com.music.bitchord.data.NerdStats
import com.music.bitchord.data.YtMusicRepository
import com.music.bitchord.data.model.SearchFilter
import com.music.bitchord.data.model.SearchResult
import com.music.bitchord.data.lyrics.LyricLine
import com.music.bitchord.data.model.ArtistPage
import com.music.bitchord.data.model.HomeShelf
import com.music.bitchord.data.model.PLAYER_ART_PX
import com.music.bitchord.data.model.PlaybackSourceType
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.data.canvas.CanvasArtwork
import com.music.bitchord.data.canvas.CanvasRepository
import com.music.bitchord.data.model.Song
import com.music.bitchord.ui.player.CanvasArtworkPlayer
import androidx.compose.ui.unit.IntSize
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
import com.music.bitchord.ui.player.FullArtworkBlurBackdrop
import com.music.bitchord.ui.player.rememberFullArtworkBlurImage
import com.music.bitchord.data.settings.SpotifyPlayerBackgroundMode
import com.music.bitchord.ui.player.MarqueeText
import android.os.Build
import android.provider.Settings
import com.music.bitchord.ui.icons.BitChordIcons
import com.music.bitchord.ui.theme.ArtworkPalette
import com.music.bitchord.ui.theme.rememberArtworkPalette
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    tint: Color? = null,
) {
    val haptics = rememberHaptics()
    val artworkTintedChrome by AppSettings.playerArtworkTintedChrome.collectAsStateWithLifecycle()
    val base = if (artworkTintedChrome && tint != null) tint.copy(alpha = 0.92f) else SpotifyBlack
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.38f to base.copy(alpha = if (artworkTintedChrome && tint != null) 0.78f else 0.72f),
                    1f to base,
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
                        val tabTint = if (selected) Color.White else SpotifyMuted(0.78f)
                        when {
                            tab.customIcon != null -> {
                                Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                                    tab.customIcon.invoke(tabTint, selected, if (selected) 1f else 0f)
                                }
                            }
                            tab.emoji != null -> {
                                Text(tab.emoji, fontSize = 19.sp)
                            }
                            else -> {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    tint = tabTint,
                                    modifier = Modifier.size(32.dp),
                                )
                            }
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
    val miniPlayerRich by AppSettings.miniPlayerRich.collectAsStateWithLifecycle()
    val miniPlayerArtworkTint by AppSettings.miniPlayerArtworkTint.collectAsStateWithLifecycle()
    // The artwork's own colour, deepened until the text on top of it is readable.
    val palette = rememberArtworkPalette(if (kisetsuCover) null else artwork)
    val barColor = when {
        kisetsuCover -> Color(0xFF6D1225)
        miniPlayerArtworkTint && artwork != null -> palette.wash
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
            .then(
                if (miniPlayerRich) {
                    Modifier.miniPlayerGestures(
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
                } else Modifier,
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
            Column(
                Modifier
                    .weight(1f)
                    .graphicsLayer { compositingStrategy = androidx.compose.ui.graphics.CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.horizontalGradient(
                                0.78f to Color.Black,
                                1f to Color.Transparent,
                            ),
                            blendMode = androidx.compose.ui.graphics.BlendMode.DstIn,
                        )
                    },
            ) {
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
        if (miniPlayerRich) {
            SpotifyProgressLine(
                position = position,
                durationMs = durationMs,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (miniPlayerRich) 4.dp else 3.dp),
                isLive = song.videoId.startsWith("src:"),
            )
        }
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

@Composable
private fun LiveBlurredPlayerBackground(
    artworkUrl: String?,
    modifier: Modifier = Modifier,
    reduceAnimation: Boolean = false,
) {
    if (artworkUrl.isNullOrBlank()) return
    val infinite = rememberInfiniteTransition(label = "livePlayerBg")
    val rotation by if (reduceAnimation) {
        remember { mutableStateOf(0f) }
    } else {
        infinite.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 52000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "bgRot",
        )
    }
    val pulse by if (reduceAnimation) {
        remember { mutableStateOf(1.85f) }
    } else {
        infinite.animateFloat(
            initialValue = 1.75f,
            targetValue = 2.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 11000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "bgPulse",
        )
    }
    val twistX by if (reduceAnimation) {
        remember { mutableStateOf(0f) }
    } else {
        infinite.animateFloat(
            initialValue = -6f,
            targetValue = 6f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 14000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "bgTwistX",
        )
    }
    val twistY by if (reduceAnimation) {
        remember { mutableStateOf(0f) }
    } else {
        infinite.animateFloat(
            initialValue = 5f,
            targetValue = -5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 17000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "bgTwistY",
        )
    }
    Box(modifier = modifier.fillMaxSize().clipToBounds().background(Color(0xFF080808))) {
        val bgBlur = if (android.os.Build.VERSION.SDK_INT >= 31) {
            Modifier.graphicsLayer {
                renderEffect = android.graphics.RenderEffect
                    .createBlurEffect(110f, 110f, android.graphics.Shader.TileMode.CLAMP)
                    .asComposeRenderEffect()
            }
        } else Modifier
        Box(Modifier.fillMaxSize().then(bgBlur)) {
            AsyncImage(
                model = artworkUrl.artworkAt(PLAYER_ART_PX),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationZ = rotation
                        rotationX = twistX
                        rotationY = twistY
                        scaleX = pulse * 1.15f
                        scaleY = pulse * 1.15f
                        cameraDistance = 18f * density
                    }
                    .blur(150.dp),
            )
            if (!reduceAnimation) {
                AsyncImage(
                    model = artworkUrl.artworkAt(540),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = -rotation * 0.42f
                            rotationX = -twistY * 0.55f
                            rotationY = twistX * 0.55f
                            scaleX = pulse * 1.28f
                            scaleY = pulse * 1.18f
                            alpha = 0.62f
                            cameraDistance = 20f * density
                        }
                        .blur(170.dp),
                )
                AsyncImage(
                    model = artworkUrl.artworkAt(360),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            rotationZ = rotation * 0.28f
                            rotationX = twistY * 0.4f
                            rotationY = -twistX * 0.4f
                            scaleX = pulse * 1.05f
                            scaleY = pulse * 1.32f
                            alpha = 0.35f
                            cameraDistance = 22f * density
                        }
                        .blur(180.dp),
                )
            }
        }
        // Heavy soft blend so no hard lines/edges show through at all
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.28f),
                            Color.Black.copy(alpha = 0.50f),
                        ),
                        radius = 1400f,
                    ),
                ),
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.12f),
                        0.35f to Color.Black.copy(alpha = 0.06f),
                        0.65f to Color.Black.copy(alpha = 0.16f),
                        1f to Color.Black.copy(alpha = 0.54f),
                    ),
                ),
        )
    }
}

/**
 * The optional Spotify player, rebuilt to the reference: a fixed player page
 * that fills the window, and — below it — the same page Spotify calls the Now
 * Playing view: lyrics, the artist, similar songs, the source playlist and
 * credits. Swiping up on the player reveals them; swiping back down returns.
 */
@OptIn(ExperimentalHazeApi::class, ExperimentalHazeMaterialsApi::class)
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
    onManualTransition: () -> Unit = {},
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
    val reduceAnimationSetting by AppSettings.reduceAnimation.collectAsStateWithLifecycle()
    val visualReducedMotionBattery by AppSettings.visualReducedMotionBattery.collectAsStateWithLifecycle()
    val reduceAnimation = reduceAnimationSetting || visualReducedMotionBattery
    val spotifyBgMode by AppSettings.spotifyPlayerBackgroundMode.collectAsStateWithLifecycle()
    val progressiveBlur by AppSettings.progressivePageBlur.collectAsStateWithLifecycle()
    val reduceDynamicBlur by AppSettings.reduceDynamicBlur.collectAsStateWithLifecycle()
    val callMeApple by AppSettings.spotifyPlayerCallMeApple.collectAsStateWithLifecycle()
    val seekTransport by AppSettings.spotifyPlayerSeekTransport.collectAsStateWithLifecycle()
    val seekBackSec by AppSettings.seekBackSeconds.collectAsStateWithLifecycle()
    val seekForwardSec by AppSettings.seekForwardSeconds.collectAsStateWithLifecycle()
    val simplePlayer by AppSettings.spotifySimplePlayer.collectAsStateWithLifecycle()
    val instaReelsPlayer by AppSettings.spotifyInstaReelsPlayer.collectAsStateWithLifecycle()
    val fullscreenCanvasPlayer by AppSettings.spotifyFullscreenCanvasPlayer.collectAsStateWithLifecycle()
    val appleBgImage = rememberFullArtworkBlurImage(
        if (spotifyBgMode == SpotifyPlayerBackgroundMode.APPLE_DYNAMIC && !kisetsuCover && !(simplePlayer && !instaReelsPlayer) && !fullscreenCanvasPlayer) artwork else null,
        prepare = spotifyBgMode == SpotifyPlayerBackgroundMode.APPLE_DYNAMIC && !kisetsuCover && !(simplePlayer && !instaReelsPlayer) && !fullscreenCanvasPlayer,
    )
    val playerHaze = remember { HazeState() }
    val usePlayerBlur = progressiveBlur && !reduceDynamicBlur
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
            .background(Color.Black)
            .hazeSource(playerHaze),
    ) {
        when {
            simplePlayer && !instaReelsPlayer -> {
                Box(Modifier.matchParentSize().background(palette.background))
            }
            kisetsuCover -> {
                Box(
                    Modifier.matchParentSize().background(
                        Brush.verticalGradient(
                            0f to palette.wash,
                            0.44f to palette.background,
                            0.82f to SpotifyBlack,
                            1f to Color(0xFF090909),
                        ),
                    ),
                )
            }
            spotifyBgMode == SpotifyPlayerBackgroundMode.APPLE_DYNAMIC -> {
                val bgAlpha by animateFloatAsState(
                    targetValue = if (appleBgImage != null) 1f else 0f,
                    animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
                    label = "spotifyAppleBg",
                )
                // The palette gradient sits beneath the blurred cover so the
                // load window (no bitmap yet, or crossfade in) shows the same
                // wash every other background mode starts from, not raw black.
                Box(
                    Modifier.matchParentSize().background(
                        Brush.verticalGradient(
                            0f to palette.wash,
                            0.44f to palette.background,
                            0.82f to SpotifyBlack,
                            1f to Color(0xFF090909),
                        ),
                    ),
                )
                FullArtworkBlurBackdrop(
                    image = appleBgImage,
                    isPlaying = isPlaying,
                    modifier = Modifier.matchParentSize().graphicsLayer { alpha = bgAlpha },
                )
                val pulse = remember { Animatable(0.12f) }
                // Battery: the breathing only runs while the music does — a
                // paused player settles to the pulse's floor and stops paying
                // for the loop.
                LaunchedEffect(isPlaying) {
                    if (!isPlaying) {
                        pulse.animateTo(0.12f, animationSpec = tween(600, easing = FastOutSlowInEasing))
                        return@LaunchedEffect
                    }
                    while (true) {
                        pulse.animateTo(0.30f, animationSpec = tween(3500, easing = FastOutSlowInEasing))
                        pulse.animateTo(0.12f, animationSpec = tween(3500, easing = FastOutSlowInEasing))
                    }
                }
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { alpha = bgAlpha * pulse.value }
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFA2D48).copy(alpha = 0.5f),
                                    Color.Transparent,
                                ),
                                center = androidx.compose.ui.geometry.Offset(0.5f, 0.4f),
                                radius = 0.7f,
                            ),
                        ),
                )
            }
            spotifyBgMode == SpotifyPlayerBackgroundMode.ALBUM_BLUR -> {
                AsyncImage(
                    model = artwork,
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .matchParentSize()
                        .blur(28.dp),
                )
                Box(
                    Modifier.matchParentSize().background(
                        Brush.verticalGradient(
                            0f to Color.Black.copy(alpha = 0.28f),
                            0.45f to Color.Black.copy(alpha = 0.42f),
                            1f to Color.Black.copy(alpha = 0.62f),
                        ),
                    ),
                )
            }
            spotifyBgMode == SpotifyPlayerBackgroundMode.LEGACY -> {
                LiveBlurredPlayerBackground(
                    artworkUrl = artwork,
                    modifier = Modifier.matchParentSize(),
                    reduceAnimation = reduceAnimation,
                )
                Box(
                    Modifier.matchParentSize().background(
                        Brush.verticalGradient(
                            0f to palette.wash.copy(alpha = 0.22f),
                            0.42f to palette.background.copy(alpha = 0.38f),
                            0.82f to SpotifyBlack.copy(alpha = 0.72f),
                            1f to Color(0xFF090909).copy(alpha = 0.88f),
                        ),
                    ),
                )
            }
            else -> {
                Box(
                    Modifier.matchParentSize().background(
                        Brush.verticalGradient(
                            0f to palette.wash,
                            0.44f to palette.background,
                            0.82f to SpotifyBlack,
                            1f to Color(0xFF090909),
                        ),
                    ),
                )
            }
        }
        when {
            fullscreenCanvasPlayer -> {
                SpotifyFullscreenCanvasPlayerPage(
                    song = song,
                    isPlaying = isPlaying,
                    isLoading = isLoading,
                    artwork = artwork,
                    position = position,
                    durationMs = durationMs,
                    liked = liked,
                    repeatMode = repeatMode,
                    onPlayPause = onPlayPause,
                    onPrevious = onPrevious,
                    onNext = onNext,
                    onSeekFraction = onSeekFraction,
                    onToggleLike = onToggleLike,
                    onCycleRepeat = onCycleRepeat,
                    onMore = onMore,
                    onShowQueue = onShowQueue,
                    onManualTransition = onManualTransition,
                    onClose = onClose,
                )
            }
            instaReelsPlayer -> {
                SpotifyInstaReelsPlayerPage(
                    song = song,
                    isPlaying = isPlaying,
                    isLoading = isLoading,
                    artwork = artwork,
                    position = position,
                    durationMs = durationMs,
                    liked = liked,
                    repeatMode = repeatMode,
                    onPlayPause = onPlayPause,
                    onPrevious = onPrevious,
                    onNext = onNext,
                    onSeekFraction = onSeekFraction,
                    onToggleLike = onToggleLike,
                    onCycleRepeat = onCycleRepeat,
                    onMore = onMore,
                    onShowQueue = onShowQueue,
                    onManualTransition = onManualTransition,
                    onClose = onClose,
                    palette = palette,
                )
            }
            simplePlayer -> {
                SpotifySimplePlayerPage(
                    song = song,
                    isPlaying = isPlaying,
                    isLoading = isLoading,
                    artwork = artwork,
                    onPlayPause = onPlayPause,
                    onPrevious = onPrevious,
                    onNext = onNext,
                    onManualTransition = onManualTransition,
                    onClose = onClose,
                )
            }
            else -> {
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
                            onManualTransition = onManualTransition,
                            onShowLyrics = { showLyrics = true },
                            onCast = { showCast = true },
                            onOpenAlbum = { onOpenAlbum(song.albumId, song.albumName ?: "Album") },
                            onSkipBack = onSkipBack,
                            onSkipForward = onSkipForward,
                            playbackSpeed = playbackSpeed,
                            onCycleSpeed = onCycleSpeed,
                            podcastBookmarksEnabled = podcastBookmarksEnabled,
                            onAddBookmark = onAddBookmark,
                            callMeApple = callMeApple,
                            seekTransport = seekTransport,
                            seekBackSec = seekBackSec,
                            seekForwardSec = seekForwardSec,
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

                // Progressive blur at the top of the player
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .height(statusBarHeight + 90.dp)
                        .hazeEffect(state = playerHaze, style = HazeMaterials.thin(SpotifyBlack)) {
                            noiseFactor = 0f
                            inputScale = HazeInputScale.Fixed(0.5f)
                            progressive = HazeProgressive.verticalGradient(
                                easing = EaseInOut,
                                startIntensity = 1f,
                                endIntensity = 0f,
                            )
                        }
                        .background(
                            Brush.verticalGradient(
                                0f to SpotifyBlack.copy(alpha = 0.18f),
                                1f to Color.Transparent,
                            )
                        ),
                )

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
                        Column(
                            modifier = Modifier.background(
                                Brush.verticalGradient(
                                    0f to SpotifyBlack.copy(alpha = 0.72f),
                                    0.55f to SpotifyBlack.copy(alpha = 0.42f),
                                    1f to Color.Transparent,
                                )
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(statusBarHeight),
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

            // Progressive blur at the bottom of the player
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(120.dp)
                    .hazeEffect(state = playerHaze, style = HazeMaterials.thin(SpotifyBlack)) {
                        noiseFactor = 0f
                        inputScale = HazeInputScale.Fixed(0.5f)
                        progressive = HazeProgressive.verticalGradient(
                            easing = EaseInOut,
                            startIntensity = 0f,
                            endIntensity = 1f,
                        )
                    }
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            1f to SpotifyBlack.copy(alpha = 0.18f),
                        )
                    ),
            )

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
    }
}

@OptIn(ExperimentalHazeApi::class, ExperimentalHazeMaterialsApi::class, ExperimentalFoundationApi::class)
@Composable
private fun SpotifyFullscreenCanvasPlayerPage(
    song: Song,
    isPlaying: Boolean,
    isLoading: Boolean,
    artwork: String?,
    position: PlaybackPosition,
    durationMs: Long,
    liked: Boolean,
    repeatMode: Int,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeekFraction: (Float) -> Unit,
    onToggleLike: () -> Unit,
    onCycleRepeat: () -> Unit,
    onMore: () -> Unit,
    onShowQueue: () -> Unit,
    onManualTransition: () -> Unit = {},
    onClose: () -> Unit,
) {
    val haptics = rememberHaptics()
    val hazeState = remember { HazeState() }
    val antiCircle by AppSettings.antiCircleCorners.collectAsStateWithLifecycle()
    val playShape = if (antiCircle) RoundedCornerShape(6.dp) else CircleShape
    val canvas = produceState<CanvasArtwork?>(initialValue = null, song.videoId, song.title, song.artist) {
        value = CanvasRepository.cached(song) ?: CanvasRepository.canvasFor(song)
    }.value
    val coverScale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.92f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "canvas-cover-scale",
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = coverScale
                    scaleY = coverScale
                }
                .hazeSource(hazeState),
        ) {
            if (canvas != null) {
                CanvasArtworkPlayer(
                    canvas = canvas,
                    isPlaying = isPlaying,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                AsyncImage(
                    model = artwork?.artworkAt(PLAYER_ART_PX),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // Soft top darkening for status/close area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.35f),
                        1f to Color.Transparent,
                    )
                ),
        )

        // Progressive blur + dark gradient from just above song title down to bottom
        // for cleaner look and better text visibility.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .hazeEffect(state = hazeState, style = HazeMaterials.thin(Color.Black)) {
                    noiseFactor = 0f
                    inputScale = HazeInputScale.Fixed(0.5f)
                    progressive = HazeProgressive.verticalGradient(
                        easing = EaseInOut,
                        startIntensity = 0f,
                        endIntensity = 1f,
                    )
                }
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.12f to Color.Black.copy(alpha = 0.06f),
                        0.45f to Color.Black.copy(alpha = 0.30f),
                        1f to Color.Black.copy(alpha = 0.78f),
                    )
                ),
        )

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp),
                )
            }
            Spacer(Modifier.weight(1f))
            IconButton(onClick = onMore, modifier = Modifier.size(44.dp)) {
                Icon(Icons.Rounded.MoreVert, contentDescription = "More", tint = Color.White)
            }
        }

        // Bottom controls overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 18.dp, vertical = 18.dp),
        ) {
            Text(
                text = song.title,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = song.artist,
                color = Color.White.copy(alpha = 0.88f),
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(14.dp))

            SpotifyScrubber(
                position = position,
                durationMs = durationMs,
                onSeekFraction = onSeekFraction,
                isLiveRadio = song.videoId.startsWith("src:") && !song.isPodcastEpisode,
                appleStyle = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                SpotifyElapsed(position = position, durationMs = durationMs)
                Text(
                    text = formatSpotifyTime(durationMs),
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 12.sp,
                )
            }
            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = onToggleLike) {
                    SpotifyLikeBadge(
                        liked = liked,
                        size = 28.dp,
                        onClick = onToggleLike,
                        removeContentDescription = "Liked",
                        addContentDescription = "Like",
                    )
                }
                IconButton(onClick = { haptics.play(Haptic.SkipPrevious); onPrevious() }, modifier = Modifier.size(52.dp)) {
                    Icon(Icons.Rounded.SkipPrevious, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(34.dp))
                }
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(playShape)
                        .background(Color.White)
                        .clickable {
                            haptics.play(if (isPlaying) Haptic.Pause else Haptic.Resume)
                            onPlayPause()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.Black, strokeWidth = 2.5.dp, modifier = Modifier.size(28.dp))
                    } else {
                        Icon(
                            imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.Black,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .combinedClickable(
                            onClick = { haptics.play(Haptic.SkipNext); onNext() },
                            onLongClick = { haptics.play(Haptic.Expand); onManualTransition() },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Rounded.SkipNext, contentDescription = "Next (long press to mix)", tint = Color.White, modifier = Modifier.size(34.dp))
                }
                IconButton(onClick = onShowQueue) {
                    Icon(Icons.Rounded.QueueMusic, contentDescription = "Queue", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SpotifyInstaReelsPlayerPage(
    song: Song,
    isPlaying: Boolean,
    isLoading: Boolean,
    artwork: String?,
    position: PlaybackPosition,
    durationMs: Long,
    liked: Boolean,
    repeatMode: Int,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeekFraction: (Float) -> Unit,
    onToggleLike: () -> Unit,
    onCycleRepeat: () -> Unit,
    onMore: () -> Unit,
    onShowQueue: () -> Unit = {},
    onManualTransition: () -> Unit = {},
    onClose: () -> Unit,
    palette: ArtworkPalette,
) {
    val haptics = rememberHaptics()
    val isRadio = song.videoId.startsWith("src:")
    val playerGestureLayer by AppSettings.playerGestureLayer.collectAsStateWithLifecycle()
    val playerUpNextPeek by AppSettings.playerUpNextPeek.collectAsStateWithLifecycle()
    val playerAmbientMode by AppSettings.playerAmbientMode.collectAsStateWithLifecycle()
    val reelsAutoplaySnap by AppSettings.playerReelsAutoplaySnap.collectAsStateWithLifecycle()

    // Autoplay/snap feed: advance to next item near track end (feed-like behavior).
    var autoAdvancedFor by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(reelsAutoplaySnap, isPlaying, position.positionMs, durationMs, song.videoId) {
        val dur = durationMs
        if (!reelsAutoplaySnap || dur <= 0L || !isPlaying) return@LaunchedEffect
        val remaining = dur - position.positionMs
        if (remaining in 0..650 && autoAdvancedFor != song.videoId) {
            autoAdvancedFor = song.videoId
            onNext()
        }
    }

    // Sleep timer
    val sleepDeadline by SleepTimer.deadline.collectAsStateWithLifecycle()
    val sleepAfterTrack by SleepTimer.afterTrack.collectAsStateWithLifecycle()
    val sleepEndOfQueue by SleepTimer.endOfQueue.collectAsStateWithLifecycle()
    val sleepActive = sleepDeadline != null || sleepAfterTrack || sleepEndOfQueue
    var showSleepMenu by remember { mutableStateOf(false) }
    var showCustomSleep by remember { mutableStateOf(false) }
    var customSleep by remember { mutableStateOf("") }

    // Double-tap like animation
    var showLikeBurst by remember { mutableStateOf(false) }
    val likeBurstScale = remember { Animatable(0f) }
    val likeBurstAlpha = remember { Animatable(0f) }
    LaunchedEffect(showLikeBurst) {
        if (showLikeBurst) {
            likeBurstScale.snapTo(0.2f)
            likeBurstAlpha.snapTo(0f)
            launch { likeBurstScale.animateTo(1.15f, animationSpec = spring(dampingRatio = 0.45f, stiffness = 500f)) }
            launch { likeBurstAlpha.animateTo(1f, animationSpec = tween(120)) }
            delay(450)
            likeBurstAlpha.animateTo(0f, animationSpec = tween(220))
            showLikeBurst = false
        }
    }

    val coverScale by animateFloatAsState(
        targetValue = if (isPlaying) 1f else 0.92f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 420f),
        label = "reels-cover-scale",
    )

    // Ambient mode: fade chrome after inactivity
    var chromeVisible by remember { mutableStateOf(true) }
    var interactionTick by remember { androidx.compose.runtime.mutableIntStateOf(0) }
    LaunchedEffect(playerAmbientMode, interactionTick, isPlaying) {
        if (playerAmbientMode && isPlaying) {
            delay(3500)
            chromeVisible = false
        } else {
            chromeVisible = true
        }
    }
    fun bumpChrome() { chromeVisible = true; interactionTick++ }

    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent) // parent background mode shows through
            .pointerInput(playerGestureLayer) {
                if (!playerGestureLayer) return@pointerInput
                detectDragGestures(
                    onDragStart = { dragX = 0f; dragY = 0f; bumpChrome() },
                    onDragCancel = { dragX = 0f; dragY = 0f },
                    onDragEnd = {
                        val ax = kotlin.math.abs(dragX)
                        val ay = kotlin.math.abs(dragY)
                        if (ay > ax && ay > 120f) {
                            // Feed-style vertical navigation (Reels autoplay/snap feed)
                            if (dragY < 0f) {
                                haptics.play(Haptic.SkipNext)
                                onNext()
                            } else {
                                haptics.play(Haptic.SkipPrevious)
                                onPrevious()
                            }
                        }
                        dragX = 0f
                        dragY = 0f
                    },
                ) { _, delta ->
                    bumpChrome()
                    dragX += delta.x
                    dragY += delta.y
                }
            },
    ) {
        // Top bar (close)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp),
                )
            }
            Spacer(Modifier.weight(1f))
        }

        // Center: album art + scrubber beneath it
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val artShape = RoundedCornerShape(10.dp)
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = coverScale
                        scaleY = coverScale
                    }
                    .shadow(24.dp, artShape, ambientColor = Color.Black, spotColor = Color.Black)
                    .clip(artShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .pointerInput(isPlaying, liked) {
                        detectTapGestures(
                            onDoubleTap = {
                                haptics.play(Haptic.ToggleOn)
                                if (!liked) onToggleLike()
                                showLikeBurst = true
                            },
                            onTap = {
                                haptics.play(if (isPlaying) Haptic.Pause else Haptic.Resume)
                                onPlayPause()
                            },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (artwork.isNullOrBlank() && isRadio) {
                    KisetsuRadioFallback(Modifier.fillMaxSize())
                } else {
                    AsyncImage(
                        model = artwork?.artworkAt(PLAYER_ART_PX),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
                if (isLoading) {
                    Box(
                        Modifier
                            .size(64.dp)
                            .background(Color.Black.copy(alpha = 0.35f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(color = Color.White, strokeWidth = 2.5.dp, modifier = Modifier.size(30.dp))
                    }
                }
            }
            Spacer(Modifier.height(14.dp))
            SpotifyScrubber(
                position = position,
                durationMs = durationMs,
                onSeekFraction = onSeekFraction,
                isLiveRadio = isRadio && !song.isPodcastEpisode,
                appleStyle = true,
                modifier = Modifier.fillMaxWidth(0.78f),
            )
            if (!(isRadio && !song.isPodcastEpisode)) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.78f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    SpotifyElapsed(position = position, durationMs = durationMs)
                    Text(
                        text = formatSpotifyTime(durationMs),
                        color = SpotifyMuted(0.62f),
                        fontSize = 11.5.sp,
                    )
                }
            }
        }

        // Bottom-left: song info
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, bottom = 24.dp, end = 84.dp)
                .windowInsetsPadding(WindowInsets.navigationBars),
        ) {
            Text(
                text = song.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (song.isPodcastEpisode) song.displayArtist() else song.artist,
                    color = Color.White.copy(alpha = 0.82f),
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
            if (!song.albumName.isNullOrBlank()) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = song.albumName!!,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        // Right-side action column aligned with bottom info
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 24.dp)
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Like
            val likeTint = if (liked) palette.accent else Color.White
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { haptics.play(if (liked) Haptic.ToggleOff else Haptic.ToggleOn); onToggleLike() }, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = if (liked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = if (liked) "Liked" else "Like",
                        tint = likeTint,
                        modifier = Modifier.size(30.dp),
                    )
                }
                Text(if (liked) "Liked" else "Like", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
            }

            // Sleep timer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box {
                    IconButton(onClick = { showSleepMenu = true }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.Bedtime,
                            contentDescription = "Sleep timer",
                            tint = if (sleepActive) palette.accent else Color.White,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    DropdownMenu(expanded = showSleepMenu, onDismissRequest = { showSleepMenu = false }) {
                        if (sleepActive) {
                            DropdownMenuItem(text = { Text("Cancel timer") }, onClick = { SleepTimer.cancel(); showSleepMenu = false })
                        }
                        DropdownMenuItem(text = { Text("After this song") }, onClick = { SleepTimer.startAfterTrack(); showSleepMenu = false })
                        DropdownMenuItem(text = { Text("End of queue") }, onClick = { SleepTimer.startEndOfQueue(); showSleepMenu = false })
                        SleepTimer.PRESETS.forEach { m ->
                            DropdownMenuItem(text = { Text("$m minutes") }, onClick = { SleepTimer.start(m); showSleepMenu = false })
                        }
                        DropdownMenuItem(text = { Text("Custom…") }, onClick = { showSleepMenu = false; showCustomSleep = true })
                    }
                }
                Text("Sleep", color = if (sleepActive) palette.accent.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
            }

            // Loop / repeat
            val (repeatIcon, repeatLabel, repeatTint) = when (repeatMode) {
                androidx.media3.common.Player.REPEAT_MODE_ONE -> Triple(Icons.Rounded.RepeatOne, "One", palette.accent)
                androidx.media3.common.Player.REPEAT_MODE_ALL -> Triple(Icons.Rounded.Repeat, "All", palette.accent)
                else -> Triple(Icons.Rounded.Repeat, "Loop", Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { haptics.play(Haptic.Tick); onCycleRepeat() }, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = repeatIcon,
                        contentDescription = "Repeat",
                        tint = repeatTint,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Text(repeatLabel, color = repeatTint.copy(alpha = 0.9f), fontSize = 11.sp)
            }

            // Up Next peek
            if (playerUpNextPeek) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconButton(onClick = { bumpChrome(); onShowQueue() }, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.QueueMusic,
                            contentDescription = "Up next",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp),
                        )
                    }
                    Text("Up next", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
                }
            }

            // More (3 dots)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = { bumpChrome(); onMore() }, modifier = Modifier.size(48.dp)) {
                    Icon(
                        imageVector = Icons.Rounded.MoreVert,
                        contentDescription = "More",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Text("More", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
            }
        }

        // Double-tap like burst animation
        if (showLikeBurst || likeBurstAlpha.value > 0f) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = palette.accent.copy(alpha = likeBurstAlpha.value),
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(120.dp)
                    .graphicsLayer {
                        scaleX = likeBurstScale.value
                        scaleY = likeBurstScale.value
                        alpha = likeBurstAlpha.value
                    },
            )
        }

        if (showCustomSleep) {
            AlertDialog(
                onDismissRequest = { showCustomSleep = false },
                title = { Text("Custom sleep timer") },
                text = {
                    OutlinedTextField(
                        value = customSleep,
                        onValueChange = { customSleep = it.filter(Char::isDigit).take(3) },
                        label = { Text("Minutes") },
                        singleLine = true,
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        customSleep.toIntOrNull()?.let { if (it > 0) SleepTimer.start(it) }
                        showCustomSleep = false
                        customSleep = ""
                    }) { Text("Start") }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomSleep = false }) { Text("Cancel") }
                },
            )
        }
    }
}

@Composable
private fun SpotifySimplePlayerPage(
    song: Song,
    isPlaying: Boolean,
    isLoading: Boolean,
    artwork: String?,
    onPlayPause: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onManualTransition: () -> Unit = {},
    onClose: () -> Unit,
) {
    val haptics = rememberHaptics()
    val skipEnabled = !song.videoId.startsWith("src:") || song.isPodcastEpisode
    var dragY by remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (dragY > 140.dp.toPx()) onClose()
                        dragY = 0f
                    },
                    onDragCancel = { dragY = 0f },
                ) { _, delta ->
                    dragY = (dragY + delta).coerceAtLeast(0f)
                }
            }
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(Modifier.weight(1f))
        val artShape = RoundedCornerShape(12.dp)
        AsyncImage(
            model = artwork?.artworkAt(PLAYER_ART_PX),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .aspectRatio(1f)
                .clip(artShape)
                .background(Color.White.copy(alpha = 0.08f)),
        )
        Spacer(Modifier.height(28.dp))
        Text(
            text = song.title,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = if (song.isPodcastEpisode) song.displayArtist() else song.artist,
            color = Color.White.copy(alpha = 0.72f),
            fontSize = 17.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(Modifier.height(36.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(28.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    haptics.play(Haptic.SkipPrevious)
                    onPrevious()
                },
                enabled = skipEnabled,
                modifier = Modifier.size(64.dp),
            ) {
                Icon(
                    painter = PlayerIcons.SkipPrevious.painter(),
                    contentDescription = "Previous",
                    tint = if (skipEnabled) Color.White else Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(38.dp),
                )
            }
            if (isLoading) {
                Box(Modifier.size(72.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.5.dp, modifier = Modifier.size(34.dp))
                }
            } else {
                IconButton(
                    onClick = {
                        haptics.play(if (isPlaying) Haptic.Pause else Haptic.Resume)
                        onPlayPause()
                    },
                    modifier = Modifier.size(76.dp),
                ) {
                    Icon(
                        painter = (if (isPlaying) PlayerIcons.Pause else PlayerIcons.Play).painter(),
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.White,
                        modifier = Modifier.size(46.dp),
                    )
                }
            }
            IconButton(
                onClick = {
                    haptics.play(Haptic.SkipNext)
                    onNext()
                },
                enabled = skipEnabled,
                modifier = Modifier.size(64.dp),
            ) {
                Icon(
                    painter = PlayerIcons.SkipNext.painter(),
                    contentDescription = "Next",
                    tint = if (skipEnabled) Color.White else Color.White.copy(alpha = 0.35f),
                    modifier = Modifier.size(38.dp),
                )
            }
        }
        Spacer(Modifier.weight(1f))
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
    onManualTransition: () -> Unit = {},
    onShowLyrics: () -> Unit = {},
    onCast: () -> Unit,
    onOpenAlbum: () -> Unit = {},
    onSkipBack: () -> Unit = {},
    onSkipForward: () -> Unit = {},
    playbackSpeed: Float = 1f,
    onCycleSpeed: () -> Unit = {},
    podcastBookmarksEnabled: Boolean = true,
    onAddBookmark: ((Long) -> Unit)? = null,
    callMeApple: Boolean = false,
    seekTransport: Boolean = false,
    seekBackSec: Int = 10,
    seekForwardSec: Int = 10,
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
            if (seekTransport) {
                IconButton(onClick = onToggleShuffle, modifier = Modifier.size(40.dp)) {
                    Icon(
                        imageVector = BitChordIcons.PlayerShuffle,
                        contentDescription = "Shuffle",
                        tint = if (shuffleEnabled) SpotifyGreen else SpotifyMuted(0.82f),
                        modifier = Modifier.size(22.dp),
                    )
                }
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
            if (seekTransport) {
                val seekEnabled = !song.videoId.startsWith("src:") || song.isPodcastEpisode
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            enabled = seekEnabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            haptics.play(Haptic.SkipPrevious)
                            onSkipBack()
                        }
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "-${seekBackSec}s",
                        color = if (seekEnabled) Color.White else Color.White.copy(alpha = 0.35f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            } else {
                SpotifyModeControl(
                    icon = BitChordIcons.PlayerShuffle,
                    contentDescription = "Shuffle",
                    active = shuffleEnabled,
                    onClick = onToggleShuffle,
                )
            }
            val skipTint = if (song.videoId.startsWith("src:")) Color.White.copy(alpha = 0.35f) else Color.White
            val useAppleTransport = callMeApple && !song.isPodcastEpisode
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
                if (useAppleTransport) {
                    Icon(
                        painter = PlayerIcons.SkipPrevious.painter(),
                        contentDescription = "Previous",
                        tint = skipTint,
                        modifier = Modifier.size(38.dp),
                    )
                } else {
                    Icon(
                        imageVector = if (song.isPodcastEpisode) Icons.Rounded.Replay10 else Icons.Filled.SkipPrevious,
                        contentDescription = if (song.isPodcastEpisode) "Back 10 seconds" else "Previous",
                        tint = skipTint,
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
            if (callMeApple) {
                if (isLoading) {
                    Box(Modifier.size(68.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(34.dp),
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            haptics.play(if (isPlaying) Haptic.Pause else Haptic.Resume)
                            onPlayPause()
                        },
                        modifier = Modifier.size(72.dp),
                    ) {
                        Icon(
                            painter = (if (isPlaying) PlayerIcons.Pause else PlayerIcons.Play).painter(),
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp),
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.82f))
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
                            color = Color.White.copy(alpha = 0.95f),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(26.dp),
                        )
                    } else {
                        SpotifyPlayPauseGlyph(
                            isPlaying = isPlaying,
                            tint = Color.Transparent,
                            modifier = Modifier.size(32.dp),
                            cutout = true,
                        )
                    }
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
                if (useAppleTransport) {
                    Icon(
                        painter = PlayerIcons.SkipNext.painter(),
                        contentDescription = "Next",
                        tint = skipTint,
                        modifier = Modifier.size(38.dp),
                    )
                } else {
                    Icon(
                        imageVector = if (song.isPodcastEpisode) Icons.Rounded.Forward30 else Icons.Filled.SkipNext,
                        contentDescription = if (song.isPodcastEpisode) "Forward 30 seconds" else "Next",
                        tint = skipTint,
                        modifier = Modifier.size(48.dp),
                    )
                }
            }
            if (seekTransport) {
                val seekEnabled = !song.videoId.startsWith("src:") || song.isPodcastEpisode
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(
                            enabled = seekEnabled,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            haptics.play(Haptic.SkipNext)
                            onSkipForward()
                        }
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "+${seekForwardSec}s",
                        color = if (seekEnabled) Color.White else Color.White.copy(alpha = 0.35f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            } else {
                SpotifyModeControl(
                    icon = BitChordIcons.PlayerRepeat,
                    contentDescription = when (repeatMode) {
                        1 -> "Repeat one"
                        2 -> "Repeat all"
                        else -> "Repeat off"
                    },
                    active = repeatMode != 0,
                    activeDots = when (repeatMode) {
                        1 -> 2 // "double loop"
                        2 -> 1
                        else -> 0
                    },
                    onClick = onCycleRepeat,
                )
            }
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
            if (seekTransport) {
                IconButton(onClick = onCycleRepeat, modifier = Modifier.size(44.dp)) {
                    Icon(
                        imageVector = if (repeatMode == 1) Icons.Rounded.RepeatOne else BitChordIcons.PlayerRepeat,
                        contentDescription = when (repeatMode) {
                            1 -> "Repeat one"
                            2 -> "Repeat all"
                            else -> "Repeat off"
                        },
                        tint = if (repeatMode != 0) SpotifyGreen else SpotifyMuted(0.82f),
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
    val appleLyrics by AppSettings.spotifyPlayerAppleMusicLyrics.collectAsStateWithLifecycle()
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
                                appleStyle = appleLyrics,
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
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.82f))
                    .clickable(onClick = onPlayPause),
                contentAlignment = Alignment.Center,
            ) {
                SpotifyPlayPauseGlyph(
                    isPlaying = isPlaying,
                    tint = Color.Transparent,
                    modifier = Modifier.size(26.dp),
                    cutout = true,
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
    appleStyle: Boolean = false,
) {
    val color by animateColorAsState(
        targetValue = when {
            active -> Color.White
            appleStyle && past -> Color.White.copy(alpha = 0.38f)
            past -> Color(0xFFE7B3AD)
            appleStyle -> Color.White.copy(alpha = 0.22f)
            else -> Color.White.copy(alpha = 0.42f)
        },
        animationSpec = tween(if (appleStyle) 320 else 280),
        label = "lyricColor",
    )
    val scale by animateFloatAsState(
        targetValue = when {
            active && appleStyle -> 1.02f
            active -> 1f
            appleStyle -> 0.94f
            else -> 0.97f
        },
        animationSpec = spring(
            dampingRatio = if (appleStyle) 0.72f else 0.8f,
            stiffness = if (appleStyle) Spring.StiffnessLow else Spring.StiffnessMediumLow,
        ),
        label = "lyricScale",
    )
    val alpha by animateFloatAsState(
        targetValue = when {
            active -> 1f
            appleStyle && past -> 0.55f
            appleStyle -> 0.35f
            else -> 1f
        },
        animationSpec = tween(if (appleStyle) 320 else 220),
        label = "lyricAlpha",
    )
    Text(
        text = text,
        color = color,
        fontSize = when {
            active && appleStyle -> 31.sp
            active -> 27.sp
            appleStyle -> 24.sp
            else -> 24.sp
        },
        fontWeight = if (active) FontWeight.ExtraBold else FontWeight.Bold,
        lineHeight = if (appleStyle) 36.sp else 32.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 22.dp, vertical = if (appleStyle) 11.dp else 9.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
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
    val antiCircle by AppSettings.antiCircleCorners.collectAsStateWithLifecycle()
    val shape = if (antiCircle) RoundedCornerShape(8.dp) else CircleShape
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(shape)
            .background(if (enabled) SpotifyGreen else Color.Transparent)
            .then(
                if (enabled) {
                    Modifier
                } else {
                    Modifier.border(2.dp, Color.White.copy(alpha = 0.85f), shape)
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
    var showCustom by remember { mutableStateOf(false) }
    var customMinutesText by remember { mutableStateOf("") }
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
                DropdownMenuItem(text = { Text("Custom time…") }, onClick = { menuOpen = false; customMinutesText = ""; showCustom = true })
            }
        }
        if (showCustom) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showCustom = false },
                title = { Text("Custom sleep timer") },
                text = {
                    androidx.compose.material3.OutlinedTextField(
                        value = customMinutesText,
                        onValueChange = { customMinutesText = it.filter(Char::isDigit).take(3) },
                        label = { Text("Minutes") },
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    )
                },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = {
                        val mins = customMinutesText.toIntOrNull()?.coerceIn(1, 480)
                        if (mins != null) { SleepTimer.start(mins); showCustom = false }
                    }) { Text("Start") }
                },
                dismissButton = { androidx.compose.material3.TextButton(onClick = { showCustom = false }) { Text("Cancel") } },
            )
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
    activeDots: Int = if (active) 1 else 0,
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
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
            val dots = activeDots.coerceAtLeast(0)
            if (dots == 0) {
                Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(Color.Transparent))
            } else {
                repeat(dots) {
                    Box(modifier = Modifier.size(3.dp).clip(CircleShape).background(SpotifyGreen))
                }
            }
        }
    }
}

/** A sharp play triangle or the two crisp pause bars Spotify draws. */
@Composable
private fun SpotifyPlayPauseGlyph(
    isPlaying: Boolean,
    tint: Color,
    modifier: Modifier = Modifier,
    cutout: Boolean = false,
) {
    Canvas(modifier) {
        val drawBlend = if (cutout) BlendMode.Clear else BlendMode.SrcOver
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
                blendMode = drawBlend,
            )
            drawRoundRect(
                color = tint,
                topLeft = Offset((size.width - barWidth * 2 - gap) / 2f + barWidth + gap, top),
                size = Size(barWidth, barHeight),
                cornerRadius = radius,
                blendMode = drawBlend,
            )
        } else {
            val path = Path().apply {
                moveTo(size.width * 0.18f, size.height * 0.08f)
                lineTo(size.width * 0.90f, size.height * 0.5f)
                lineTo(size.width * 0.18f, size.height * 0.92f)
                close()
            }
            drawPath(path, tint, blendMode = drawBlend)
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
    appleStyle: Boolean = false,
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
    val dragging = dragFraction != null
    val trackHeight = when {
        isLiveRadio -> 4.dp
        appleStyle && dragging -> 10.dp
        appleStyle -> 7.dp
        else -> 4.dp
    }
    val trackBg = if (isLiveRadio) Color.White.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.24f)
    val trackFg = if (isLiveRadio) Color.White.copy(alpha = 0.34f) else Color.White.copy(alpha = 0.90f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(if (appleStyle) 30.dp else 24.dp)
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
                .height(trackHeight)
                .clip(CircleShape)
                .background(trackBg),
        )
        Box(
            Modifier
                .fillMaxWidth(fraction)
                .height(trackHeight)
                .clip(CircleShape)
                .background(trackFg),
        )
        if (!isLiveRadio && !appleStyle) {
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
private val SpotifyHomeModePills = listOf(
    "All",
    "Music",
    "Podcasts",
    "Radio",
    "Episodes",
)

@Composable
private fun SpotifyHomeNowHub(
    onPodcastResume: (com.music.bitchord.data.stats.PodcastBookmark) -> Unit,
    onItemClick: (ShelfItem, String) -> Unit,
) {
    val resumes by com.music.bitchord.data.stats.PodcastResume.entries.collectAsStateWithLifecycle()
    val top = resumes.firstOrNull()
    val recent by produceState<com.music.bitchord.data.stats.ReplaySummary?>(initialValue = null) {
        value = try { com.music.bitchord.data.stats.ListeningStats.summary(com.music.bitchord.data.stats.ReplayPeriod.THIS_MONTH) } catch (_: Exception) { null }
    }
    val topSong = recent?.songs?.firstOrNull()?.song
    val title = when {
        top != null -> "Continue listening"
        topSong != null -> "Ready to play"
        else -> "Your Now hub"
    }
    val subtitle = when {
        top != null -> "${top.title} · ${top.showName}"
        topSong != null -> "${topSong.title} · ${topSong.artist}"
        else -> "Resume podcasts, jump back into music, or explore episodes."
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF2A2A2A), Color(0xFF171717))))
            .padding(16.dp),
    ) {
        Text("NOW", color = SpotifyGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Spacer(Modifier.height(6.dp))
        Text(title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(subtitle, color = SpotifyMuted(0.8f), fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (top != null) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(SpotifyGreen).clickable { onPodcastResume(top) }.padding(horizontal = 14.dp, vertical = 9.dp)
                ) { Text("Resume episode", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            }
            if (topSong != null) {
                val item = ShelfItem(topSong.title, topSong.artist, resolvedSongArtwork(topSong.videoId, topSong.thumbnailUrl), topSong.videoId, null)
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = 0.14f)).clickable { onItemClick(item, "Now hub") }.padding(horizontal = 14.dp, vertical = 9.dp)
                ) { Text(if (top != null) "Play recent" else "Play", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
            }
        }
    }
}

@Composable
private fun SpotifyHomeTimeline(
    onPodcastResume: (com.music.bitchord.data.stats.PodcastBookmark) -> Unit,
    onItemClick: (ShelfItem, String) -> Unit,
) {
    val resumes by com.music.bitchord.data.stats.PodcastResume.entries.collectAsStateWithLifecycle()
    val recent by produceState<com.music.bitchord.data.stats.ReplaySummary?>(initialValue = null) {
        value = try { com.music.bitchord.data.stats.ListeningStats.summary(com.music.bitchord.data.stats.ReplayPeriod.THIS_MONTH) } catch (_: Exception) { null }
    }
    val recentSongs = recent?.songs?.take(6).orEmpty()
    if (resumes.isEmpty() && recentSongs.isEmpty()) return
    Column(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp)) {
        Text("Your day in sound", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        Text("Continue where you left off", color = SpotifyMuted(0.72f), fontSize = 12.5.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(resumes.take(4), key = { "tlp_${it.videoId}" }) { bm ->
                Column(
                    modifier = Modifier.width(170.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.08f)).clickable { onPodcastResume(bm) }.padding(10.dp)
                ) {
                    Text("CONTINUE EPISODE", color = SpotifyGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(bm.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(bm.showName, color = SpotifyMuted(0.7f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            items(recentSongs.take(4), key = { "tls_${it.song.videoId}" }) { ranked ->
                val song = ranked.song
                val item = ShelfItem(song.title, song.artist, resolvedSongArtwork(song.videoId, song.thumbnailUrl), song.videoId, null)
                Column(
                    modifier = Modifier.width(170.dp).clip(RoundedCornerShape(12.dp)).background(Color.White.copy(alpha = 0.08f)).clickable { onItemClick(item, "Timeline") }.padding(10.dp)
                ) {
                    Text("RECENT", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(song.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(song.artist, color = SpotifyMuted(0.7f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun SpotifyHomeExploreBento(onSelectPill: (String) -> Unit) {
    val gradients by AppSettings.homeCardGradients.collectAsStateWithLifecycle()
    val tiles = listOf(
        Triple("Episodes", "Short stories & shows", "Episodes"),
        Triple("Live radio", "Stations right now", "Radio"),
        Triple("Podcasts", "Shows for you", "Podcasts"),
        Triple("Music", "Your mixes & tracks", "Music"),
    )
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text("Explore", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Text("Bento picks", color = SpotifyMuted(0.72f), fontSize = 12.5.sp, modifier = Modifier.padding(bottom = 8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tiles.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    row.forEach { (title, sub, pill) ->
                        Column(
                            modifier = Modifier.weight(1f).height(92.dp).clip(RoundedCornerShape(14.dp))
                                .background(Color.White.copy(alpha = 0.08f)).clickable { onSelectPill(pill) }.padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(sub, color = SpotifyMuted(0.75f), fontSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        }
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
        Box(
            modifier = Modifier.padding(top = 8.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp))
                .background(
                    if (gradients) Brush.horizontalGradient(listOf(Color(0xFF1DB954).copy(alpha = 0.35f), Color(0xFF1E3264).copy(alpha = 0.45f)))
                    else Brush.horizontalGradient(listOf(SpotifyCard, SpotifyCard))
                )
                .clickable { onSelectPill("Episodes") }.padding(14.dp)
        ) {
            Column {
                Text("Spotlight", color = Color.White.copy(alpha = 0.85f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Short stories & YouTube episodes", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("Tap to explore playable episodes", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SpotifyHomeImmersiveHeroBlock(onQuickAction: (String) -> Unit) {
    val gradients by AppSettings.homeCardGradients.collectAsStateWithLifecycle()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(170.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (gradients) Brush.linearGradient(
                    listOf(Color(0xFF2A1458), Color(0xFF123B4A), Color(0xFF121212))
                ) else Brush.linearGradient(listOf(SpotifyCard, SpotifyCard))
            )
            .padding(16.dp)
    ) {
        Column(Modifier.align(Alignment.BottomStart)) {
            Text("IMMERSIVE HOME", color = SpotifyGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text("Your sound, front and center", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Music", "Episodes", "Radio").forEach {
                    Box(
                        Modifier.clip(RoundedCornerShape(999.dp)).background(Color.White.copy(alpha = 0.14f)).clickable { onQuickAction(it.lowercase()) }.padding(horizontal = 12.dp, vertical = 7.dp)
                    ) { Text(it, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                }
            }
        }
    }
}

@Composable
private fun SpotifyHomeMoodLaunchpad(onMood: (String) -> Unit) {
    val gradients by AppSettings.homeCardGradients.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text("Mood launchpad", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(listOf("Focus", "Workout", "Chill", "Commute", "Sleep", "Party", "Study")) { mood ->
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(16.dp))
                        .background(
                            if (gradients) Brush.linearGradient(listOf(Color(0xFF1DB954).copy(alpha = 0.35f), Color.White.copy(alpha = 0.08f)))
                            else Brush.linearGradient(listOf(SpotifyCard, SpotifyCard))
                        )
                        .clickable { onMood(mood) }
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Column {
                        Text(mood, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("One-tap radio", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SpotifyHomeBentoStoryRail(shelves: List<HomeShelf>, onItemClick: (ShelfItem) -> Unit) {
    val items = shelves.flatMap { it.items }.distinctBy { it.videoId ?: it.title }.take(10)
    if (items.isEmpty()) return
    Column(Modifier.fillMaxWidth()) {
        Text("Stories & bento picks", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(Modifier.height(8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(items.take(8)) { item ->
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(72.dp).clickable { onItemClick(item) }) {
                    AsyncImage(model = item.thumbnailUrl?.artworkAt(120), contentDescription = null, contentScale = ContentScale.Crop,
                        modifier = Modifier.size(64.dp).clip(CircleShape).background(SpotifyCard).border(2.dp, SpotifyGreen.copy(alpha = 0.7f), CircleShape))
                    Spacer(Modifier.height(6.dp))
                    Text(item.title, color = Color.White, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items.take(2).forEach { item ->
                Box(Modifier.weight(1f).height(96.dp).clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.08f)).clickable { onItemClick(item) }.padding(10.dp)) {
                    Text(item.title, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.align(Alignment.BottomStart))
                }
            }
        }
    }
}

@Composable
private fun SpotifyHomeNowLaterExplore(
    resumes: List<com.music.bitchord.data.stats.PodcastBookmark>,
    downloadedCount: Int,
    onResume: () -> Unit,
    onSelect: (String) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Now / Later / Explore", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        listOf(
            Triple("Now", if (resumes.isNotEmpty()) "Continue: ${resumes.first().title}" else "Pick something to play", "All"),
            Triple("Later", "$downloadedCount downloaded items ready", "All"),
            Triple("Explore", "Fresh mixes, radio and episodes", "Radio"),
        ).forEach { (zone, subtitle, target) ->
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.07f)).clickable { if (zone == "Now") onResume() else onSelect(target) }.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(zone, color = SpotifyGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = Color.White, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text("Open", color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SpotifyHomeDailyDigest(
    resume: com.music.bitchord.data.stats.PodcastBookmark?,
    downloadedCount: Int,
    onResume: () -> Unit,
    onOpenRadio: () -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Daily digest", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        if (resume != null) {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.08f)).clickable { onResume() }.padding(12.dp)) {
                Text("Continue: ${resume.title} · ${resume.showName}", color = Color.White, fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.08f)).clickable { onOpenRadio() }.padding(12.dp)) {
            Text("Radio picks ready · tap to explore live stations", color = Color.White, fontSize = 14.sp)
        }
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.08f)).padding(12.dp)) {
            Text("Downloaded & ready: $downloadedCount items", color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SpotifyHomeOfflineModeBanner(downloadedCount: Int) {
    val gradients by AppSettings.homeCardGradients.collectAsStateWithLifecycle()
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(14.dp))
            .background(
                if (gradients) Brush.horizontalGradient(listOf(Color(0xFF0F5132), Color(0xFF123B4A)))
                else Brush.horizontalGradient(listOf(SpotifyCard, SpotifyCard))
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("Offline / On-the-go mode", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text("$downloadedCount downloaded items prioritized for quick playback", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp)
        }
        Text("Ready", color = SpotifyGreen, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SpotifyHomeShowsHub(
    resumes: List<com.music.bitchord.data.stats.PodcastBookmark>,
    onResume: (com.music.bitchord.data.stats.PodcastBookmark) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text("Shows hub", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(resumes) { r ->
                Column(Modifier.width(160.dp).clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.08f)).clickable { onResume(r) }.padding(10.dp)) {
                    Text("EPISODE", color = SpotifyGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(r.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(r.showName, color = SpotifyMuted(0.8f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
private fun SpotifyHomeHighlightsRecap(summary: com.music.bitchord.data.stats.ReplaySummary?) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.07f)).padding(14.dp)) {
        Text("Listening highlights", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        if (summary != null && !summary.isEmpty) {
            val topArtist = summary.artists.firstOrNull()?.title ?: "—"
            val topTrack = summary.songs.firstOrNull()?.song?.title ?: "—"
            Text("Minutes listened: ${summary.minutes}", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
            Text("Top artist: $topArtist", color = SpotifyMuted(0.85f), fontSize = 13.sp)
            Text("Top track: $topTrack", color = SpotifyMuted(0.85f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        } else {
            Text("Play more to unlock your recap highlights.", color = SpotifyMuted(0.85f), fontSize = 13.sp)
        }
    }
}

@Composable
private fun SpotifyHomeCustomizableSectionsBlock() {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.06f)).padding(12.dp)) {
        Text("Customize Home", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(6.dp))
        Text("Presets: Minimal · Balanced · Discovery-heavy (toggle sections in Settings → UI updates)", color = SpotifyMuted(0.85f), fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Minimal", "Balanced", "Discovery").forEach {
                Box(Modifier.clip(RoundedCornerShape(999.dp)).background(Color.White.copy(alpha = 0.12f)).padding(horizontal = 12.dp, vertical = 7.dp)) {
                    Text(it, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun SpotifyHomeCommandCenter(
    resume: com.music.bitchord.data.stats.PodcastBookmark?,
    onResume: () -> Unit,
    onQuickAction: (String) -> Unit,
    timeOfDay: Boolean,
) {
    val greeting = remember(timeOfDay) {
        if (!timeOfDay) "Command Center" else {
            val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            when {
                hour < 12 -> "Good morning"
                hour < 18 -> "Good afternoon"
                else -> "Good evening"
            }
        }
    }
    val gradients by AppSettings.homeCardGradients.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (gradients) Brush.linearGradient(
                    listOf(Color(0xFF1E3264).copy(alpha = 0.85f), Color(0xFF121212), SpotifyCard.copy(alpha = 0.9f))
                ) else Brush.linearGradient(listOf(SpotifyCard, SpotifyCard))
            )
            .padding(14.dp),
    ) {
        Text(greeting, color = SpotifyGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
        Spacer(Modifier.height(8.dp))
        if (resume != null) {
            val progress = if (resume.durationMs > 0L) (resume.positionMs.toFloat() / resume.durationMs).coerceIn(0f, 1f) else 0f
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.08f))
                    .clickable { onResume() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(54.dp)) {
                    CircularProgressIndicator(progress = { progress }, strokeWidth = 4.dp, color = SpotifyGreen, trackColor = Color.White.copy(alpha = 0.15f), modifier = Modifier.fillMaxSize())
                    Icon(Icons.Rounded.PlayArrow, contentDescription = "Resume", tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Continue listening", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(resume.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(resume.showName.ifBlank { "Podcast" }, color = SpotifyMuted(0.8f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Spacer(Modifier.height(10.dp))
        } else {
            Text("Jump back in anytime", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Quick picks to start listening", color = SpotifyMuted(0.8f), fontSize = 13.sp)
            Spacer(Modifier.height(10.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            listOf("Episodes" to "episodes", "Radio" to "radio", "Music" to "music", "Downloads" to "downloads").forEach { (label, key) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.10f))
                        .clickable { onQuickAction(key) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                }
            }
        }
    }
}

@Composable
private fun SpotifyHomeEditorialSpotlight(
    shelf: HomeShelf,
    onItemClick: (ShelfItem) -> Unit,
) {
    val item = shelf.items.firstOrNull() ?: return
    val art = rememberRemoteArtworkUrl(Song(item.videoId ?: "", item.title, item.subtitle, item.thumbnailUrl))
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp)).background(Color.White.copy(alpha = 0.07f)).clickable { onItemClick(item) }.padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(model = art?.artworkAt(300) ?: item.thumbnailUrl?.artworkAt(300), contentDescription = null, contentScale = ContentScale.Crop,
            modifier = Modifier.size(88.dp).clip(RoundedCornerShape(12.dp)).background(SpotifyCard))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text("EDITORIAL PICK", color = SpotifyGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Text(shelf.title, color = SpotifyMuted(0.8f), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(item.title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(item.subtitle, color = SpotifyMuted(0.75f), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

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
    val homeNowHub by AppSettings.homeNowHub.collectAsStateWithLifecycle()
    val homeEditorialLayout by AppSettings.homeEditorialLayout.collectAsStateWithLifecycle()
    val homeModeTabs by AppSettings.homeModeTabs.collectAsStateWithLifecycle()
    val homeQuickLaunchEnhanced by AppSettings.homeQuickLaunchEnhanced.collectAsStateWithLifecycle()
    val homeDynamicSectionColors by AppSettings.homeDynamicSectionColors.collectAsStateWithLifecycle()
    val homeTimeline by AppSettings.homeTimeline.collectAsStateWithLifecycle()
    val homeImmersiveHeader by AppSettings.homeImmersiveHeader.collectAsStateWithLifecycle()
    val homeExploreBento by AppSettings.homeExploreBento.collectAsStateWithLifecycle()
    val homeCommandCenter by AppSettings.homeCommandCenter.collectAsStateWithLifecycle()
    val homeTimeOfDay by AppSettings.homeTimeOfDay.collectAsStateWithLifecycle()
    val homeBentoStoryRail by AppSettings.homeBentoStoryRail.collectAsStateWithLifecycle()
    val homeNowLaterExplore by AppSettings.homeNowLaterExplore.collectAsStateWithLifecycle()
    val homeImmersiveHero by AppSettings.homeImmersiveHero.collectAsStateWithLifecycle()
    val homeDailyDigest by AppSettings.homeDailyDigest.collectAsStateWithLifecycle()
    val homeOfflineMode by AppSettings.homeOfflineMode.collectAsStateWithLifecycle()
    val homeShowsHub by AppSettings.homeShowsHub.collectAsStateWithLifecycle()
    val homeHighlightsRecap by AppSettings.homeHighlightsRecap.collectAsStateWithLifecycle()
    val homeCustomizableSections by AppSettings.homeCustomizableSections.collectAsStateWithLifecycle()
    val homeMoodLaunchpad by AppSettings.homeMoodLaunchpad.collectAsStateWithLifecycle()
    val podcastResumes by com.music.bitchord.data.stats.PodcastResume.entries.collectAsStateWithLifecycle()
    val homeHighlightsSummary = produceState<com.music.bitchord.data.stats.ReplaySummary?>(initialValue = null) {
        value = runCatching { com.music.bitchord.data.stats.ListeningStats.summary(com.music.bitchord.data.stats.ReplayPeriod.THIS_MONTH) }.getOrNull()
    }.value
    val homeDownloadedCount = produceState<Int>(initialValue = 0) {
        value = 0
    }.value

    val pills = if (homeModeTabs) SpotifyHomeModePills else SpotifyHomePills
    var selectedPill by rememberSaveable { mutableStateOf(pills.first()) }
    // A pill saved under an older list (e.g. the retired Audiobooks tab) falls
    // back to All rather than an empty page.
    if (selectedPill !in pills) selectedPill = pills.first()

    val shelves = (state as? UiState.Success)?.data.orEmpty()
    val filtered = remember(shelves, selectedPill, homeModeTabs) {
        when {
            selectedPill == "All" -> shelves
            selectedPill == "Music" && homeModeTabs -> shelves.filter {
                val group = spotifyShelfGroup(it)
                group == "Music" || group == "Mixes" || group == "Artists"
            }
            selectedPill == "Episodes" -> shelves.filter {
                spotifyShelfGroup(it) == "Podcasts" || it.title.contains("episode", true) || it.title.contains("podcast", true)
            }
            selectedPill == "Radio" -> shelves.filter {
                it.items.any { item -> item.videoId?.startsWith("src:") == true } ||
                    it.title.contains("radio", true) ||
                    spotifyShelfGroup(it) == "Mixes"
            }
            else -> shelves.filter { spotifyShelfGroup(it) == selectedPill }
        }
    }
    // When the account's home feed has no podcast shelves of its own — the
    // common case — the Podcasts tab borrows YouTube's own Podcasts
    // destination so the tab is never dead. Fetched lazily, only when the
    // user actually opens the tab.
    val podcastFallback = produceState<List<HomeShelf>?>(initialValue = null, selectedPill) {
        if (selectedPill == "Podcasts" || selectedPill == "Episodes") {
            value = YtMusicRepository.podcastShelves().getOrNull().orEmpty()
        }
    }

    // The Episodes and Radio tabs build their own real content — playable
    // episode rows and live radio stations — because the account's home feed
    // rarely carries shelves for them. Loaded lazily, only when their tab is
    // actually opened, so opening home costs nothing extra.
    val episodeShelves = produceState<List<HomeShelf>?>(initialValue = null, selectedPill) {
        if (selectedPill == "Episodes") {
            val built = mutableListOf<HomeShelf>()
            val resumes = com.music.bitchord.data.stats.PodcastResume.entries.value
            if (resumes.isNotEmpty()) {
                built += HomeShelf(
                    title = "Jump back in · Episodes",
                    items = resumes.take(12).map {
                        ShelfItem(it.title, it.showName.ifBlank { "Episode" }, it.thumbnailUrl, it.videoId, null)
                    },
                )
            }
            val episodes = runCatching {
                YtMusicRepository.search("podcast", SearchFilter.EPISODES).getOrNull().orEmpty()
            }.getOrDefault(emptyList())
                .mapNotNull { (it as? SearchResult.Track)?.song }
                .take(12)
            if (episodes.isNotEmpty()) {
                built += HomeShelf(
                    title = "Episodes for you",
                    items = episodes.map { s ->
                        ShelfItem(s.title, s.displayArtist().ifBlank { "Episode" }, s.thumbnailUrl, s.videoId, null)
                    },
                )
            }
            val stories = runCatching {
                YtMusicRepository.search("short stories", SearchFilter.VIDEOS).getOrNull().orEmpty()
            }.getOrDefault(emptyList())
                .mapNotNull { (it as? SearchResult.Track)?.song }
                .take(8)
            if (stories.isNotEmpty()) {
                built += HomeShelf(
                    title = "Short stories",
                    items = stories.map { s ->
                        ShelfItem(s.title, s.artist.ifBlank { "YouTube" }, s.thumbnailUrl, s.videoId, null)
                    },
                )
            }
            value = built
        }
    }
    val radioShelves = produceState<List<HomeShelf>?>(initialValue = null, selectedPill) {
        if (selectedPill == "Radio") {
            val radioConfigId = com.music.bitchord.data.sources.SourceRegistry.configs.value
                .firstOrNull { it.kind == com.music.bitchord.data.sources.SourceKind.RADIO }?.id ?: "radio"
            val items = mutableListOf<ShelfItem>()
            val saved = (com.music.bitchord.data.settings.DiscoverSaves.recentRadios.value +
                com.music.bitchord.data.settings.DiscoverSaves.likedRadios.value)
                .distinctBy { it.key }
            saved.forEach { s ->
                val vid = s.videoId ?: return@forEach
                items += ShelfItem(s.title, s.subtitle.ifBlank { "Radio" }, s.thumbnailUrl, vid, null)
            }
            if (items.size < 6) {
                val stations = runCatching {
                    com.music.bitchord.data.sources.RadioApi.search("pop", 12)
                }.getOrDefault(emptyList())
                stations.forEach { st ->
                    val vid = com.music.bitchord.data.sources.SourceRegistry.trackKey(radioConfigId, st.uuid)
                    items += ShelfItem(
                        title = st.name,
                        subtitle = listOfNotNull(st.country, st.tags?.split(',')?.firstOrNull()?.trim())
                            .filter { !it.isNullOrBlank() }.joinToString(" · ").ifBlank { "Live radio" },
                        thumbnailUrl = st.favicon,
                        videoId = vid,
                        browseId = null,
                    )
                }
            }
            val mixes = shelves.filter { spotifyShelfGroup(it) == "Mixes" && it.items.isNotEmpty() }
            val built = mutableListOf<HomeShelf>()
            if (items.isNotEmpty()) built += HomeShelf(title = "Radio stations", items = items.distinctBy { it.videoId }.take(16))
            mixes.take(2).forEach { built += it }
            value = built
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
                    if (homeCommandCenter && selectedPill == "All") {
                        SpotifyHomeCommandCenter(
                            resume = podcastResumes.firstOrNull(),
                            onResume = { podcastResumes.firstOrNull()?.let(onPodcastResume) },
                            onQuickAction = { action ->
                                when (action) {
                                    "episodes" -> selectedPill = if ("Episodes" in pills) "Episodes" else if ("Podcasts" in pills) "Podcasts" else "All"
                                    "radio" -> selectedPill = if ("Radio" in pills) "Radio" else "All"
                                    "music" -> selectedPill = if ("Music" in pills) "Music" else "All"
                                    "downloads", "liked" -> selectedPill = "All"
                                }
                            },
                            timeOfDay = homeTimeOfDay,
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    if (homeImmersiveHeader) {
                        Text(
                            text = "Good evening",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 8.dp),
                        ) {
                            items(listOf("Episodes", "Short stories", "YouTube", "Live radio", "Chill", "Focus")) { chip ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(Color.White.copy(alpha = 0.12f))
                                        .clickable {
                                            when (chip) {
                                                "Episodes" -> selectedPill = if ("Episodes" in pills) "Episodes" else "Podcasts"
                                                "Short stories", "YouTube" -> selectedPill = if ("Episodes" in pills) "Episodes" else "Podcasts"
                                                "Live radio" -> selectedPill = if ("Radio" in pills) "Radio" else "All"
                                                else -> selectedPill = "All"
                                            }
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                ) { Text(chip, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium) }
                            }
                        }
                    }
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(pills) { pill ->
                            SpotifyHomePill(
                                label = pill,
                                selected = pill == selectedPill,
                                onClick = { selectedPill = pill },
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // 8-rectangle grid of recent playlists (2 per line, small).
                    // Only the All tab wears it — a focused tab (Music,
                    // Podcasts, Radio, Episodes) shows just its own shelves.
                    val recentItems = remember(shelves) {
                        shelves.flatMap { it.items }.take(8)
                    }
                    if (selectedPill == "All" && recentItems.isNotEmpty()) {
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

            if (selectedPill == "All") {
                item(key = "home-big-redesigns") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        if (homeImmersiveHero) {
                            SpotifyHomeImmersiveHeroBlock(
                                onQuickAction = { action ->
                                    when (action) {
                                        "episodes" -> selectedPill = if ("Episodes" in pills) "Episodes" else "Podcasts"
                                        "radio" -> selectedPill = if ("Radio" in pills) "Radio" else "All"
                                        "music" -> selectedPill = if ("Music" in pills) "Music" else "All"
                                        else -> selectedPill = "All"
                                    }
                                },
                            )
                        }
                        if (homeMoodLaunchpad) {
                            SpotifyHomeMoodLaunchpad(onMood = { selectedPill = if ("Radio" in pills) "Radio" else "All" })
                        }
                        if (homeBentoStoryRail) {
                            SpotifyHomeBentoStoryRail(shelves = shelves, onItemClick = { onItemClick(it, "") })
                        }
                        if (homeNowLaterExplore) {
                            SpotifyHomeNowLaterExplore(
                                resumes = podcastResumes,
                                downloadedCount = homeDownloadedCount,
                                onResume = { podcastResumes.firstOrNull()?.let(onPodcastResume) },
                                onSelect = { selectedPill = it },
                            )
                        }
                        if (homeDailyDigest) {
                            SpotifyHomeDailyDigest(
                                resume = podcastResumes.firstOrNull(),
                                downloadedCount = homeDownloadedCount,
                                onResume = { podcastResumes.firstOrNull()?.let(onPodcastResume) },
                                onOpenRadio = { selectedPill = if ("Radio" in pills) "Radio" else "All" },
                            )
                        }
                        if (homeOfflineMode) {
                            SpotifyHomeOfflineModeBanner(downloadedCount = homeDownloadedCount)
                        }
                        if (homeShowsHub && podcastResumes.isNotEmpty()) {
                            SpotifyHomeShowsHub(resumes = podcastResumes.take(6), onResume = onPodcastResume)
                        }
                        if (homeHighlightsRecap) {
                            SpotifyHomeHighlightsRecap(summary = homeHighlightsSummary)
                        }
                        if (homeCustomizableSections) {
                            SpotifyHomeCustomizableSectionsBlock()
                        }
                    }
                }
            }

            if (selectedPill == "All" && homeNowHub) {
                item(key = "home-now-hub") {
                    SpotifyHomeNowHub(
                        onPodcastResume = onPodcastResume,
                        onItemClick = onItemClick,
                    )
                }
            }

            // The Quick launch panel and the podcast "Jump back in" row sit
            // directly under the grid — the shortcut row and the half-heard
            // episodes Spotify puts right at the top of the home feed. The
            // shortcuts stay on every tab (the listener's own pins are always
            // relevant); the half-heard episodes follow the podcast tabs.
            item(key = "quick-launch") {
                if (homeQuickLaunchEnhanced) {
                    Column(Modifier.fillMaxWidth().padding(top = 2.dp)) {
                        Text(
                            "Quick launch · Dashboard",
                            color = SpotifyMuted(0.78f),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp),
                        )
                        SpotifyQuickLaunch(onPinTap = onPinTap)
                    }
                } else {
                    SpotifyQuickLaunch(onPinTap = onPinTap)
                }
            }
            if (homeTimeline && selectedPill == "All") {
                item(key = "home-timeline") {
                    SpotifyHomeTimeline(
                        onPodcastResume = onPodcastResume,
                        onItemClick = onItemClick,
                    )
                }
            }
            if (selectedPill == "All" || selectedPill == "Podcasts" || selectedPill == "Episodes") {
                item(key = "podcast-jump-back") {
                    SpotifyPodcastJumpBackIn(onResume = onPodcastResume, onRestart = onPodcastRestart)
                }
            }
            if (homeExploreBento && selectedPill == "All") {
                item(key = "home-explore-bento") {
                    SpotifyHomeExploreBento(onSelectPill = { selectedPill = it })
                }
            }

            // The "For you" shelf — most played songs, artists and albums —
            // belongs to the All tab only. Focused tabs skip it so their own
            // shelves sit right under the pill row.
            if (selectedPill == "All") {
                item(key = "jump-back-in") {
                    SpotifyJumpBackIn(onItemClick = onItemClick, onItemLongPress = onItemLongPress)
                }
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
                    val specialShelves = when (selectedPill) {
                        "Episodes" -> episodeShelves.value
                        "Radio" -> radioShelves.value
                        else -> null
                    }
                    val specialLoading = (selectedPill == "Episodes" && episodeShelves.value == null) ||
                        (selectedPill == "Radio" && radioShelves.value == null)
                    if (specialLoading) {
                        item(key = "special-loading") {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator(color = SpotifyGreen, strokeWidth = 2.dp)
                            }
                        }
                    } else if (specialShelves != null && specialShelves.isNotEmpty()) {
                        itemsIndexed(
                            specialShelves,
                            key = { index, shelf -> "special-$index-${shelf.title}" },
                        ) { index, shelf ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                if (homeEditorialLayout && index == 0 && shelf.items.isNotEmpty()) {
                                    SpotifyHomeEditorialSpotlight(
                                        shelf = shelf,
                                        onItemClick = { item -> onItemClick(item, shelf.title) },
                                    )
                                }
                                SpotifyHomeShelf(
                                    shelf = shelf,
                                    onItemClick = { item -> onItemClick(item, shelf.title) },
                                    onItemLongPress = onItemLongPress,
                                )
                            }
                        }
                    } else {
                     val podcastsToShow = (selectedPill == "Podcasts" || selectedPill == "Episodes") && filtered.isEmpty() &&
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
                        ) { index, shelf ->
                            val dynamicMod = if (homeDynamicSectionColors) {
                                val art = shelf.items.firstOrNull()?.thumbnailUrl
                                val wash = rememberArtworkPalette(art, dark = true).wash.copy(alpha = 0.22f)
                                Modifier.background(wash)
                            } else Modifier
                            Column(modifier = dynamicMod.fillMaxWidth()) {
                                if (homeEditorialLayout && index == 0 && shelf.items.isNotEmpty()) {
                                    SpotifyHomeEditorialSpotlight(
                                        shelf = shelf,
                                        onItemClick = { item -> onItemClick(item, shelf.title) },
                                    )
                                }
                                SpotifyHomeShelf(
                                    shelf = shelf,
                                    onItemClick = { item -> onItemClick(item, shelf.title) },
                                    onItemLongPress = onItemLongPress,
                                )
                            }
                        }
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
    val antiCircle by AppSettings.antiCircleCorners.collectAsStateWithLifecycle()
    val pinShape = if (antiCircle) RoundedCornerShape(8.dp) else CircleShape
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
                val pinArt = resolvedSongArtwork(pin.videoId, pin.thumbnailUrl)
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
                            .clip(pinShape)
                            .background(if (kisetsuRadio) Color(0xFFFA2D48) else SpotifyCard)
                            .border(1.dp, if (kisetsuRadio) Color.Transparent else SpotifyMuted(0.28f), pinShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        when {
                            kisetsuRadio -> androidx.compose.foundation.Image(
                                painter = painterResource(com.music.bitchord.R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = Modifier.size(38.dp),
                            )
                            pinArt != null -> AsyncImage(
                                model = pinArt.artworkAt(120),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
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
    onItemLongPress: ((ShelfItem) -> Unit)? = null,
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
                    val art = resolvedSongArtwork(song.videoId, song.thumbnailUrl)
                    val shelfItem = ShelfItem(song.title, song.artist, art, song.videoId, null)
                    JumpCard(
                        artwork = art,
                        title = song.title,
                        subtitle = song.artist,
                        circular = false,
                        kisetsuFallback = song.videoId.startsWith("src:"),
                        onClick = { onItemClick(shelfItem, "On repeat this month") },
                        onLongClick = onItemLongPress?.let { { it(shelfItem) } },
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
                onItemLongPress = onItemLongPress,
            )
        }
        if (artists.isNotEmpty()) {
            ArtistJumpRow(artists, onItemClick, showHeader = true, shelfLabel = "For you", onItemLongPress = onItemLongPress)
        }
        if (albums.isNotEmpty()) {
            AlbumJumpRow(albums, onItemClick, showHeader = true, shelfLabel = "For you", onItemLongPress = onItemLongPress)
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
    onItemLongPress: ((ShelfItem) -> Unit)? = null,
) {
    if (songs.isEmpty()) return
    if (showHeader) JumpJumpRowHeader(title)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(songs, key = { "${shelfLabel}_${it.song.videoId}" }) { ranked ->
            val song = ranked.song
            val art = resolvedSongArtwork(song.videoId, song.thumbnailUrl)
            val shelfItem = ShelfItem(song.title, song.artist, art, song.videoId, null)
            JumpCard(
                artwork = art,
                title = song.title,
                subtitle = song.artist,
                circular = false,
                kisetsuFallback = song.videoId.startsWith("src:"),
                onClick = { onItemClick(shelfItem, shelfLabel) },
                onLongClick = onItemLongPress?.let { { it(shelfItem) } },
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
    onItemLongPress: ((ShelfItem) -> Unit)? = null,
) {
    if (artists.isEmpty()) return
    if (showHeader) JumpJumpRowHeader("Most played artists")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(artists, key = { "a_${it.browseId ?: it.title}" }) { artist ->
            val shelfItem = ShelfItem(artist.title, artist.subtitle.orEmpty(), artist.artworkUrl, null, artist.browseId)
            JumpCard(
                artwork = artist.artworkUrl,
                title = artist.title,
                subtitle = artist.subtitle,
                circular = true,
                kisetsuFallback = artist.artworkUrl.isNullOrBlank(),
                onClick = { onItemClick(shelfItem, shelfLabel) },
                onLongClick = onItemLongPress?.let { { it(shelfItem) } },
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
    onItemLongPress: ((ShelfItem) -> Unit)? = null,
) {
    if (albums.isEmpty()) return
    if (showHeader) JumpJumpRowHeader("Most played albums")
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(albums, key = { "al_${it.browseId ?: it.title}" }) { album ->
            val shelfItem = ShelfItem(album.title, album.subtitle.orEmpty(), album.artworkUrl, null, album.browseId)
            JumpCard(
                artwork = album.artworkUrl,
                title = album.title,
                subtitle = album.subtitle,
                circular = false,
                onClick = { onItemClick(shelfItem, shelfLabel) },
                onLongClick = onItemLongPress?.let { { it(shelfItem) } },
            )
        }
    }
}

/** One card for Jump back in: square or circle, same text underneath. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun JumpCard(
    artwork: String?,
    title: String,
    subtitle: String?,
    circular: Boolean,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    kisetsuFallback: Boolean = false,
) {
    val shape = if (circular) CircleShape else RoundedCornerShape(8.dp)
    val useKisetsu = kisetsuFallback || artwork.isNullOrBlank()
    Column(
        modifier = Modifier
            .width(118.dp)
            .clip(RoundedCornerShape(8.dp))
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
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

/**
 * A track's artwork, filling in later what persistence didn't carry.
 *
 * The listening ledger and the Discover saves store a row's cover as it was
 * when the build recorded it — and a stretch of builds recorded rows without
 * one (see the thumbnail parsing fix). The row's title and id are right; only
 * the art is blank. When it is, the watch queue is asked once for the id, and
 * what it says is remembered for the process: every surface that draws the
 * same track sees the same answer, and nothing is written back to disk.
 *
 * `src:` radios and local files are not YouTube ids and are passed through —
 * their fallbacks (the Kisetsu tile, the note icon) are intentional.
 */
@Composable
internal fun resolvedSongArtwork(videoId: String?, known: String?): String? {
    if (!known.isNullOrBlank()) return known
    val lookup = com.music.bitchord.data.cover.BrowseThumbnails.lookupId(videoId) ?: return null
    val songArt by com.music.bitchord.data.cover.BrowseThumbnails.songs.collectAsStateWithLifecycle()
    val resolved = songArt[lookup]
    LaunchedEffect(lookup, resolved) {
        if (resolved.isNullOrBlank()) {
            val thumb = runCatching {
                com.music.bitchord.data.YtMusicRepository.trackLinks(lookup).getOrNull()?.thumbnailUrl
            }.getOrNull()
            if (!thumb.isNullOrBlank()) com.music.bitchord.data.cover.BrowseThumbnails.putSong(lookup, thumb)
        }
    }
    return resolved
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun resolvedShelfThumbnail(item: ShelfItem): String? {
    val browseCovers by com.music.bitchord.data.cover.BrowseThumbnails.covers.collectAsStateWithLifecycle()
    val browseId = item.browseId?.takeUnless { it.startsWith("local:") || it.startsWith("src:") }
    val resolved = browseId?.let { browseCovers[it] }
    LaunchedEffect(browseId, item.thumbnailUrl, resolved) {
        if (browseId != null && item.thumbnailUrl.isNullOrBlank() && resolved.isNullOrBlank()) {
            val thumb = com.music.bitchord.data.YtMusicRepository.browseSongs(browseId)
                .getOrNull()?.header?.thumbnailUrl
            if (!thumb.isNullOrBlank()) com.music.bitchord.data.cover.BrowseThumbnails.put(browseId, thumb)
        }
    }
    // A row with no page of its own — a track on a chart — has only its video
    // id to be asked about.
    val song = resolvedSongArtwork(item.videoId?.takeIf { browseId == null }, item.thumbnailUrl)
    return item.thumbnailUrl ?: resolved ?: song
}

@Composable
private fun SpotifyHomeCard(
    item: ShelfItem,
    onClick: () -> Unit,
    onLongPress: (() -> Unit)?,
) {
    val haptics = rememberHaptics()
    val thumb = resolvedShelfThumbnail(item)
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
        if (item.videoId?.startsWith("src:") == true && thumb.isNullOrBlank()) {
            KisetsuRadioFallback(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp)),
            )
        } else {
            AsyncImage(
                model = thumb?.artworkAt(420),
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
    val thumb = resolvedShelfThumbnail(item)
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
        if (item.videoId?.startsWith("src:") == true && thumb.isNullOrBlank()) {
            KisetsuRadioFallback(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
        } else {
            AsyncImage(
                model = thumb?.artworkAt(120),
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
    val thumb = resolvedShelfThumbnail(item)
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
            model = thumb?.artworkAt(420),
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
