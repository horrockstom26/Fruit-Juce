package com.music.bitchord.ui.screens

import com.music.bitchord.R

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.create),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
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
    }
}

/**
 * One tile: a live-square card with the glyph at the top and the name under
 * it. Square-ish rather than flat rows, because four items in two rows read
 * as buttons — the point of this page — instead of as a list to scroll
 * past.
 */
@Composable
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
            .background(tone.surfaceContainerHigh, RoundedCornerShape(20.dp))
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
