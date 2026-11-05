package com.music.bitchord.ui.screens

import com.music.bitchord.data.event.KisetsuEvent
import com.music.bitchord.data.event.KisetsuGreeting
import androidx.compose.ui.text.font.FontWeight
import com.music.bitchord.R

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.model.ROW_ART_PX
import com.music.bitchord.data.studio.ScoredPick
import com.music.bitchord.data.studio.StudioMood
import com.music.bitchord.ui.components.KisetsuRadioBanner
import com.music.bitchord.ui.icons.BitChordIcons

/**
 * Fruit Juce v5.2: the Create tab.
 *
 * Four actions, each on its own tile — the tab replaced Explore, whose feed
 * of suggestions would keep being read as a front page. This is honest about
 * what the tab is: things you do, not things we think you should hear.
 *
 * View Playlists and Create Playlist are the account's own tools; Import
 * Spotify opens the long-standing importer; View My Stats opens Replay.
 */
@Composable
fun CreateHubScreen(
    onViewPlaylists: () -> Unit,
    onCreatePlaylist: () -> Unit,
    onImportSpotify: () -> Unit,
    onViewStats: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    /**
     * Kisetsu Radio's banner, at the head of the page. Null on callers that
     * have no Flow page to open.
     */
    onOpenRadio: (() -> Unit)? = null,
    /** The mix's own first cover, when there is one, behind the banner. */
    radioArtworkUrl: String? = null,
    // v9.0: the Music Studio. A null draft is its rest state; the mood chips
    // ask, the draft answers with accept / rebuild, and saving runs the
    // ordinary playlist-create path.
    studioMood: StudioMood = StudioMood.CHILL,
    studioDraft: List<ScoredPick>? = null,
    studioBuilding: Boolean = false,
    onBuildStudio: (StudioMood) -> Unit = {},
    onPlayStudioDraft: () -> Unit = {},
    onSaveStudioDraft: () -> Unit = {},
    onDiscardStudioDraft: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.create),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        // The time-of-day greeting that used to ride under the home heading
        // now lives on Create, where it sits beside the actions.
        Text(
            text = KisetsuGreeting.lineAt(seasonalEmoji = KisetsuEvent.emoji),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 2.dp, bottom = 4.dp),
        )
        Spacer(Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            HubCard(
                icon = BitChordIcons.TabLibrary,
                label = stringResource(R.string.view_playlists),
                onClick = onViewPlaylists,
                modifier = Modifier.weight(1f),
            )
            HubCard(
                icon = BitChordIcons.Plus,
                label = stringResource(R.string.create_playlist),
                onClick = onCreatePlaylist,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            HubCard(
                icon = BitChordIcons.MusicNote,
                label = stringResource(R.string.import_spotify),
                onClick = onImportSpotify,
                modifier = Modifier.weight(1f),
            )
            HubCard(
                icon = BitChordIcons.Clock,
                label = stringResource(R.string.view_my_stats),
                onClick = onViewStats,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

/**
 * One tile: a live-square card with the glyph at the top and the name under
 * it. Square-ish rather than flat rows, because four items in two rows read
 * as buttons — the point of this page — instead of as a list to scroll
 * past.
 */@Composable
private fun HubCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tone = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .aspectRatio(1.15f)
            .background(tone.surfaceContainerHigh, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp),
        contentAlignment = Alignment.TopStart,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tone.primary,
            modifier = Modifier.size(26.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = tone.onSurface,
            maxLines = 2,
            minLines = 1,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 4.dp),
        )
    }
}

/**
 * The Music Studio: the auto-recommendation system behind "the perfect
 * playlist based on flow".
 *
 * The work is a small state machine at the surface of a scoring engine:
 * pick a mood, the engine drafts, the listener accepts (plays it), saves it
 * as a playlist, or rejects the whole thing and asks again. The draft's
 * picks carry their reasons with them, so the page says why a track is
 * here instead of asking the listener to take it on faith.
 */
@Composable
private fun MusicStudioPanel(
    mood: StudioMood,
    draft: List<ScoredPick>?,
    building: Boolean,
    onBuild: (StudioMood) -> Unit,
    onPlayDraft: () -> Unit,
    onSaveDraft: () -> Unit,
    onDiscardDraft: () -> Unit,
) {
    val tone = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(listOf(tone.surfaceContainerHigh, tone.surfaceContainer)),
            )
            .padding(16.dp),
    ) {
        Text(
            text = "MUSIC STUDIO",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.4.sp,
            ),
            color = tone.primary,
        )
        Text(
            text = "The perfect playlist, on flow",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = tone.onSurface,
            modifier = Modifier.padding(top = 2.dp),
        )
        Text(
            text = "Pick a mood. Kisetsu drafts the rest — your library, your recents, and a few new colours to finish.",
            style = MaterialTheme.typography.bodyMedium,
            color = tone.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )

        // The mood chips — the studio's one control. Each press is a fresh
        // question to the engine; the last one asked stays lit.
        FlowChips(
            selected = mood,
            enabled = !building,
            onSelect = onBuild,
        )

        when {
            building -> {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "Reading your library and recents…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = tone.onSurfaceVariant,
                )
            }
            draft != null -> FlowDraft(
                draft = draft,
                mood = mood,
                onPlayDraft = onPlayDraft,
                onSaveDraft = onSaveDraft,
                onRebuild = onDiscardDraft,
            )
            else -> {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "No draft yet — the chiprow above is the door.",
                    style = MaterialTheme.typography.bodySmall,
                    color = tone.onSurfaceVariant,
                )
            }
        }
    }
}

/** The mood chips, one per [StudioMood], selected on the last request. */
@Composable
private fun FlowChips(
    selected: StudioMood,
    enabled: Boolean,
    onSelect: (StudioMood) -> Unit,
) {
    val tone = MaterialTheme.colorScheme
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 2.dp),
    ) {
        items(StudioMood.entries) { entry ->
            val selected = entry == selected
            Text(
                text = entry.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) tone.primary else tone.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (selected) tone.primary.copy(alpha = 0.16f) else tone.surfaceContainerHigh)
                    .border(
                        width = 1.dp,
                        color = if (selected) tone.primary else tone.outlineVariant,
                        shape = RoundedCornerShape(50),
                    )
                    .clickable(enabled = enabled) { onSelect(entry) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
            )
        }
    }
}

/** The draft once it exists: the picks with their reasons, and the verdict. */
@Composable
private fun FlowDraft(
    draft: List<ScoredPick>,
    mood: StudioMood,
    onPlayDraft: () -> Unit,
    onSaveDraft: () -> Unit,
    onRebuild: () -> Unit,
) {
    val tone = MaterialTheme.colorScheme
    Spacer(Modifier.height(14.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Play pill
        Row(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
                .clickable { onPlayDraft() }
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = BitChordIcons.Play,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Play ${mood.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MaterialTheme.typography.titleSmall,
                color = Color.Black,
                maxLines = 1,
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(50))
                .background(tone.surfaceContainerHigh)
                .clickable { onSaveDraft() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = BitChordIcons.Plus,
                contentDescription = "Save as playlist",
                tint = tone.onSurface,
                modifier = Modifier.size(20.dp),
            )
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(50))
                .background(tone.surfaceContainerHigh)
                .clickable { onRebuild() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Cached,
                contentDescription = "Rebuild",
                tint = tone.onSurface,
                modifier = Modifier.size(20.dp),
            )
        }
    }

    Spacer(Modifier.height(12.dp))
    draft.take(DRAFT_PREVIEW_ROWS).forEach { pick ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = pick.song.thumbnailUrl.artworkAt(ROW_ART_PX),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp),
            ) {
                Text(
                    text = pick.song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = tone.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = pick.reasons.firstOrNull() ?: "",
                    style = MaterialTheme.typography.bodySmall,
                    color = tone.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
    val remaining = draft.size - DRAFT_PREVIEW_ROWS
    if (remaining > 0) {
        Text(
            text = "and $remaining more…",
            style = MaterialTheme.typography.bodySmall,
            color = tone.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

/** How many of the draft are previewed inline; the pill row starts the rest. */
private const val DRAFT_PREVIEW_ROWS = 5
