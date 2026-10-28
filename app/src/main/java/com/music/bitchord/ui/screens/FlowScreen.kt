package com.music.bitchord.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AllInclusive
import androidx.compose.material.icons.rounded.Block
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.R
import com.music.bitchord.data.flow.FLOW_TUNER_GENRES
import com.music.bitchord.data.flow.FlowMood
import com.music.bitchord.data.flow.FlowStatus
import com.music.bitchord.data.flow.FlowTuner
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.UiState
import com.music.bitchord.data.model.isSameTrackAs
import com.music.bitchord.ui.components.MessageState
import com.music.bitchord.ui.components.PAGE_GUTTER
import com.music.bitchord.ui.components.PlayingAccent
import com.music.bitchord.ui.components.ROW_DIVIDER_INSET
import com.music.bitchord.ui.components.SongRow
import com.music.bitchord.ui.components.songListSkeleton
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Flow, per the approved proposal: dark gradient hero, left-aligned title
 * ("Flow ∞" + mood subtitle), white Play pill with Shuffle / Like / Tune
 * glass circles, mood pill strip, tuner card, mix list.
 *
 * Two rules keep it honest:
 * - On the dark gradient, content is always white — never a theme color
 *   that can go light-on-light.
 * - Everywhere else uses stock Material controls in theme colors. No
 *   hand-rolled accent fills, which is what went white-on-white before.
 *
 * Tuner edits are drafts until Apply: mood, sliders and genre chips only
 * touch local state, and one tap commits everything with a single rebuild —
 * dragging a slider never replays the whole mix under your thumb.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FlowScreen(
    tracksState: UiState<List<Song>>,
    mood: FlowMood,
    tuner: FlowTuner,
    status: FlowStatus,
    bannedIds: Set<String>,
    isSaved: Boolean,
    isGrowing: Boolean,
    rebuilding: Boolean,
    listState: LazyListState,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    onSave: () -> Unit,
    onNewMix: () -> Unit,
    onToggleLike: () -> Unit,
    currentLiked: Boolean,
    onApply: (FlowMood, Float, Float, Set<String>) -> Unit,
    onUnban: (String) -> Unit,
    onBan: (Song) -> Unit,
    onSongClick: (List<Song>, Int) -> Unit,
    onSongLongPress: (Song) -> Unit,
    onRetry: () -> Unit,
    contentPadding: PaddingValues,
    currentSong: Song? = null,
    isPlaying: Boolean = false,
    modifier: Modifier = Modifier,
) {
    // Drafts reset whenever the committed config changes (i.e. after Apply).
    var draftMood by remember(mood) { mutableStateOf(mood) }
    var draftDiscovery by remember(tuner.discovery) { mutableFloatStateOf(tuner.discovery) }
    var draftMemory by remember(tuner.memory) { mutableFloatStateOf(tuner.memory) }
    var draftExcluded by remember(tuner.excludedGenres) { mutableStateOf(tuner.excludedGenres) }
    var showTuner by remember { mutableStateOf(false) }
    val dirty = draftMood != mood || draftDiscovery != tuner.discovery ||
        draftMemory != tuner.memory || draftExcluded != tuner.excludedGenres
    val songs = (tracksState as? UiState.Success)?.data.orEmpty()

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        item(key = "flow:hero") {
            FlowHero(
                mood = draftMood,
                trackCount = songs.size,
                isSaved = isSaved,
                isGrowing = isGrowing,
                rebuilding = rebuilding,
                onAir = isPlaying && songs.isNotEmpty(),
                tunerDirty = dirty,
                tunerOpen = showTuner,
                currentLiked = currentLiked,
                onPlay = onPlay,
                onShuffle = onShuffle,
                onSave = onSave,
                onToggleLike = onToggleLike,
                onTune = { showTuner = !showTuner },
            )
        }
        item(key = "flow:moods") {
            FlowMoodStrip(
                mood = draftMood,
                onMood = { draftMood = it },
            )
        }
        if (showTuner) {
            item(key = "flow:tuner") {
                FlowTunerCard(
                    tuner = tuner.copy(
                        discovery = draftDiscovery,
                        memory = draftMemory,
                        excludedGenres = draftExcluded,
                    ),
                    onDiscovery = { draftDiscovery = it },
                    onMemory = { draftMemory = it },
                    onToggleGenre = { genre, enabled ->
                        draftExcluded = if (enabled) draftExcluded - genre.lowercase()
                        else draftExcluded + genre.lowercase()
                    },
                    dirty = dirty,
                    rebuilding = rebuilding,
                    onApply = { onApply(draftMood, draftDiscovery, draftMemory, draftExcluded) },
                )
            }
        }
        if (songs.isNotEmpty()) {
            item(key = "flow:tracks-header") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = PAGE_GUTTER, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(R.string.flow_mix_title, songs.size),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(onClick = onNewMix) {
                        Icon(Icons.Rounded.Refresh, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(R.string.flow_new_mix))
                    }
                }
            }
        }
        when (tracksState) {
            is UiState.Loading -> songListSkeleton(count = 8, keyPrefix = "skeleton:flow")
            is UiState.Error -> item(key = "flow:message") {
                MessageState(
                    message = tracksState.message,
                    actionLabel = stringResource(R.string.try_again),
                    onAction = onRetry,
                )
            }
            is UiState.Success -> {
                if (songs.isEmpty()) {
                    item(key = "flow:empty") {
                        FlowLockedCard(status = status)
                    }
                } else {
                    items(
                        count = songs.size,
                        key = { "flow:${songs[it].videoId}:$it" },
                    ) { index ->
                        val song = songs[index]
                        SongRow(
                            song = song,
                            onClick = { onSongClick(songs, index) },
                            onLongPress = { onSongLongPress(song) },
                            onSwipeToQueue = { onBan(song) },
                            isCurrent = song.isSameTrackAs(currentSong),
                            isPlaying = song.isSameTrackAs(currentSong) && isPlaying,
                            searchPlayingStyle = true,
                            activeTint = PlayingAccent,
                        )
                        if (index < songs.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(start = ROW_DIVIDER_INSET),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            )
                        }
                    }
                }
            }
        }
        if (bannedIds.isNotEmpty()) {
            item(key = "flow:banned") {
                FlowBans(bannedIds = bannedIds, onUnban = onUnban)
            }
        }
        item(key = "flow:spacer") { Spacer(Modifier.height(24.dp)) }
    }
}

/**
 * The station masthead.
 *
 * v7.4 turns this from a page header into the front of a radio: one dark
 * tuner plate carrying the call sign, an ON AIR lamp, the mood dial, and a
 * round transport knob, with the station's own controls along the bottom.
 * The plate is deliberately dark in both themes — a station face is a panel
 * of glass and backlighting, not a page — so everything on it is white and
 * nothing here reads a theme colour except the accent for the needle.
 */
@Composable
private fun FlowHero(
    mood: FlowMood,
    trackCount: Int,
    isSaved: Boolean,
    isGrowing: Boolean,
    rebuilding: Boolean,
    onAir: Boolean,
    tunerDirty: Boolean,
    tunerOpen: Boolean,
    currentLiked: Boolean,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    onSave: () -> Unit,
    onToggleLike: () -> Unit,
    onTune: () -> Unit,
) {
    val ink = Color.White
    val dim = Color.White.copy(alpha = 0.6f)
    val plate = RoundedCornerShape(14.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 14.dp)
            .padding(horizontal = PAGE_GUTTER)
            .clip(plate)
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF2A1A2E), Color(0xFF19111C), Color(0xFF100C13)),
                ),
            )
            .border(0.5.dp, Color.White.copy(alpha = 0.14f), plate)
            .padding(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.flow).uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.4.sp),
                color = dim,
                modifier = Modifier.weight(1f),
            )
            OnAirLamp(live = onAir)
        }
        Spacer(Modifier.height(14.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.flow),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    lineHeight = 38.sp,
                    color = ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (mood == FlowMood.FLOW) {
                        stringResource(R.string.flow_subtitle)
                    } else {
                        stringResource(R.string.radio_tuned_to, mood.label)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = dim,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.width(12.dp))
            // The dial's knob: one round transport, the way a station's own
            // app leads with a single big play.
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable(onClick = onPlay),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Rounded.PlayArrow,
                    stringResource(R.string.play),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(30.dp),
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        StationDial(mood = mood, ink = ink)
        Spacer(Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = when {
                    isSaved -> stringResource(R.string.saved)
                    trackCount > 0 -> stringResource(R.string.flow_temp_meta, trackCount)
                    else -> stringResource(R.string.flow_temp_playlist)
                },
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.7.sp),
                color = dim,
            )
            if (isGrowing && !isSaved && trackCount > 0) {
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(4.dp).clip(CircleShape).background(dim))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.flow_growing),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.7.sp),
                    color = dim,
                )
            }
            if (rebuilding) {
                Spacer(Modifier.width(8.dp))
                CircularProgressIndicator(
                    Modifier.size(12.dp),
                    strokeWidth = 1.5.dp,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FlowGlassCircle(
                icon = Icons.Rounded.Shuffle,
                description = stringResource(R.string.shuffle),
                onClick = onShuffle,
                onDark = true,
            )
            FlowGlassCircle(
                icon = if (currentLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                description = stringResource(R.string.like),
                onClick = onToggleLike,
                filled = currentLiked,
                onDark = true,
            )
            Box(contentAlignment = Alignment.TopEnd) {
                FlowGlassCircle(
                    icon = Icons.Rounded.Tune,
                    description = stringResource(R.string.flow_tuner),
                    onClick = onTune,
                    selected = tunerOpen,
                    onDark = true,
                )
                if (tunerDirty) {
                    Box(
                        Modifier
                            .padding(top = 1.dp, end = 1.dp)
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(1.5.dp, Color(0xFF100C13), CircleShape),
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            if (!isSaved) {
                TextButton(
                    onClick = onSave,
                    colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                        contentColor = ink.copy(alpha = 0.85f),
                    ),
                ) {
                    Icon(Icons.Rounded.Save, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.flow_save), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

/**
 * The ON AIR lamp: filled red with a soft pulse while the station is playing,
 * a dead grey pill when it is not. The pulse is the only persistent animation
 * on this screen and it stops with the music.
 */
@Composable
private fun OnAirLamp(live: Boolean) {
    val pulse = rememberInfiniteTransition(label = "onAir")
    val glow by pulse.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "onAirGlow",
    )
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (live) Color(0xFFE23B4E) else Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = if (live) glow else 0.45f)),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = stringResource(if (live) R.string.radio_on_air else R.string.radio_off_air).uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp),
            color = if (live) Color.White else Color.White.copy(alpha = 0.55f),
        )
    }
}

/**
 * The tuning scale under the masthead.
 *
 * A band of FM ticks with the needle parked on the current mood's frequency,
 * so switching stations moves the needle along the dial rather than only
 * swapping a chip's fill. The numbers are decorative — the mood next to them
 * is the real setting — but they are what makes the plate read as a radio
 * instead of a gradient card.
 */
@Composable
private fun StationDial(mood: FlowMood, ink: Color) {
    val stations = FlowMood.entries
    val index = stations.indexOf(mood).coerceAtLeast(0)
    val fraction = if (stations.size <= 1) 0f else index.toFloat() / (stations.size - 1)
    val dim = ink.copy(alpha = 0.55f)
    val accent = MaterialTheme.colorScheme.primary
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = String.format(Locale.US, "%.1f", 88.1f + fraction * 19.8f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = ink,
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = stringResource(R.string.radio_fm),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.4.sp),
                color = dim,
                modifier = Modifier.padding(bottom = 2.dp),
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = mood.label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.6.sp),
                color = accent,
            )
        }
        Spacer(Modifier.height(5.dp))
        Canvas(Modifier.fillMaxWidth().height(20.dp)) {
            val ticks = 41
            val step = size.width / (ticks - 1)
            for (i in 0 until ticks) {
                val x = i * step
                val major = i % 5 == 0
                val h = if (major) size.height * 0.8f else size.height * 0.42f
                drawLine(
                    color = ink.copy(alpha = if (major) 0.4f else 0.16f),
                    start = Offset(x, size.height - h),
                    end = Offset(x, size.height),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            val needle = size.width * fraction
            drawLine(
                color = accent,
                start = Offset(needle, 0f),
                end = Offset(needle, size.height),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}


/** Tonal circle: surface wash, hairline rim, theme glyph. Red heart when loved. */
@Composable
private fun FlowGlassCircle(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    selected: Boolean = false,
    filled: Boolean = false,
    size: Dp = 46.dp,
    /** Sits on the station plate, where the chrome has to be white-on-dark. */
    onDark: Boolean = false,
) {
    val glyph = if (onDark) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                when {
                    selected -> MaterialTheme.colorScheme.primary.copy(alpha = if (onDark) 0.32f else 0.18f)
                    onDark -> Color.White.copy(alpha = 0.1f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                },
            )
            .border(0.5.dp, glyph.copy(alpha = if (onDark) 0.22f else 0.3f), CircleShape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon, description,
            tint = if (filled) Color(0xFFFF5B6E) else glyph.copy(alpha = if (enabled) 1f else 0.4f),
            modifier = Modifier.size(20.dp),
        )
    }
}

/** Moods as a horizontal pill strip — one swipe, no page taken. */
@Composable
private fun FlowMoodStrip(
    mood: FlowMood,
    onMood: (FlowMood) -> Unit,
) {
    Column(Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 2.dp)) {
        Text(
            text = stringResource(R.string.flow_moods),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = PAGE_GUTTER),
        )
        Spacer(Modifier.height(8.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = PAGE_GUTTER),
        ) {
            items(FlowMood.entries.toList()) { m ->
                FilterChip(
                    selected = m == mood,
                    onClick = { onMood(m) },
                    label = { Text(m.label) },
                )
            }
        }
    }
}

@Composable
private fun FlowTunerCard(
    tuner: FlowTuner,
    onDiscovery: (Float) -> Unit,
    onMemory: (Float) -> Unit,
    onToggleGenre: (String, Boolean) -> Unit,
    dirty: Boolean,
    rebuilding: Boolean,
    onApply: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = PAGE_GUTTER, vertical = 8.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(
                stringResource(R.string.flow_tuner),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(R.string.flow_draft_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    stringResource(R.string.flow_personal),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    stringResource(R.string.flow_adventurous),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
            Slider(value = tuner.discovery, onValueChange = onDiscovery, modifier = Modifier.fillMaxWidth())
            Text(
                stringResource(R.string.flow_discovery_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    stringResource(R.string.flow_alltime),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    stringResource(R.string.flow_recent),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
            }
            Slider(value = tuner.memory, onValueChange = onMemory, modifier = Modifier.fillMaxWidth())
            Text(
                stringResource(R.string.flow_memory_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.flow_genres),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            @OptIn(ExperimentalLayoutApi::class)
            androidx.compose.foundation.layout.FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FLOW_TUNER_GENRES.forEach { genre ->
                    FilterChip(
                        selected = tuner.isGenreEnabled(genre),
                        onClick = { onToggleGenre(genre, !tuner.isGenreEnabled(genre)) },
                        label = { Text(genre) },
                    )
                }
            }
            if (dirty) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onApply,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !rebuilding,
                ) {
                    if (rebuilding) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                    }
                    Text(stringResource(R.string.flow_apply))
                }
            }
        }
    }
}

@Composable
private fun FlowLockedCard(status: FlowStatus) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = PAGE_GUTTER, vertical = 8.dp),
    ) {
        Column(
            Modifier.fillMaxWidth().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Rounded.AllInclusive, null, modifier = Modifier.size(28.dp))
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.flow_locked_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.flow_locked_subtitle, status.neededMore),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun FlowBans(
    bannedIds: Set<String>,
    onUnban: (String) -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = PAGE_GUTTER, vertical = 8.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Block, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(
                    stringResource(R.string.flow_banned),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(8.dp))
            bannedIds.take(20).forEach { id ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(id.take(11), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    AssistChip(onClick = { onUnban(id) }, label = { Text(stringResource(R.string.remove)) })
                }
            }
        }
    }
}

/** Name prompt for saving the temp mix — no more "Flow — Flow". */
@Composable
fun FlowSaveDialog(
    initial: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var name by remember(initial) { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.flow_save_title)) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text(stringResource(R.string.playlist_name)) },
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name.trim()) },
                enabled = name.isNotBlank(),
            ) { Text(stringResource(R.string.save)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        },
    )
}
