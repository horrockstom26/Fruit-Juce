package com.music.bitchord.ui.screens

import android.content.Intent
import com.music.bitchord.R

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.music.bitchord.data.cover.PlaylistCovers
import com.music.bitchord.data.model.UserPlaylist
import com.music.bitchord.data.playlist.PlaylistCoverStore
import kotlinx.coroutines.flow.StateFlow

/**
 * Fruit Juce v5.2: "View Playlists" from the Create tab — the account's own
 * playlists as a plain list, with the playlist builder one tap away above
 * them.
 *
 * The picker sheet already lists these for "add to playlist"; this page is
 * the same list standing on its own, because the Create tab promised it and
 * a page is where you browse — a sheet is where you choose.
 */
@Composable
fun PlaylistsBrowserScreen(
    signedIn: Boolean,
    playlists: StateFlow<List<UserPlaylist>>,
    loading: StateFlow<Boolean>,
    listState: LazyListState,
    onPlaylistClick: (UserPlaylist) -> Unit,
    onCreateNew: () -> Unit,
    onRetry: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val playlistList by playlists.collectAsStateWithLifecycle()
    val loadingNow by loading.collectAsStateWithLifecycle()
    val covers by PlaylistCovers.covers.collectAsStateWithLifecycle()
    val context = LocalContext.current
    // One picker serves the whole list. Which row asked is held here while the
    // system picker is up, because the callback arrives long after that row has
    // scrolled away or recomposed.
    val coverTarget = remember { mutableStateOf<String?>(null) }
    val coverPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        val target = coverTarget.value
        if (uri != null && target != null) {
            // The cover is drawn on every later launch, so the grant has to
            // outlive this process.
            runCatching {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION,
                )
            }
            PlaylistCoverStore.set(target, uri.toString())
        }
        coverTarget.value = null
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(key = "playlists_header") {
            Text(
                text = stringResource(R.string.my_playlists),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 6.dp, bottom = 4.dp),
            )
        }
        item(key = "playlists_create") {
            NewPlaylistTile(onClick = onCreateNew)
        }
        if (!signedIn) {
            item(key = "playlists_signed_out") {
                Text(
                    text = stringResource(R.string.playlists_sign_in_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else if (loadingNow && playlistList.isEmpty()) {
            item(key = "playlists_loading") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 22.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(26.dp))
                }
            }
        } else if (playlistList.isEmpty() && !loadingNow) {
            item(key = "playlists_empty") {
                Text(
                    text = stringResource(R.string.no_playlists_yet),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            }
        } else {
            items(playlistList, key = { it.playlistId }) { playlist ->
                PlaylistRow(
                    playlist = playlist,
                    coverUri = covers[playlist.browseId],
                    onClick = { onPlaylistClick(playlist) },
                    onPickCover = {
                        coverTarget.value = playlist.browseId
                        coverPicker.launch(arrayOf("image/*"))
                    },
                )
            }
        }
    }
}

@Composable
private fun NewPlaylistTile(onClick: () -> Unit) {
    val tone = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(tone.surfaceContainerHigh, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = stringResource(R.string.new_playlist),
            style = MaterialTheme.typography.titleMedium,
            color = tone.primary,
        )
    }
}

@Composable
private fun PlaylistRow(
    playlist: UserPlaylist,
    coverUri: String?,
    onClick: () -> Unit,
    onPickCover: () -> Unit,
) {
    val tone = MaterialTheme.colorScheme
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tone.surfaceContainerHigh, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            // Kisetsu v7.2: the listener's own photo, where they have picked
            // one, in place of the cover YouTube generated.
            model = coverUri ?: playlist.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tone.surfaceContainerHighest),
        )
        Column(
            modifier = Modifier
                .padding(start = 14.dp)
                .weight(1f),
        ) {
            Text(
                text = playlist.title,
                style = MaterialTheme.typography.titleMedium,
                color = tone.onSurface,
                maxLines = 1,
            )
            Text(
                text = playlist.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = tone.onSurfaceVariant,
                maxLines = 1,
            )
        }
        IconButton(onClick = onPickCover) {
            Icon(
                imageVector = Icons.Rounded.Image,
                contentDescription = stringResource(R.string.change_cover),
                tint = tone.onSurfaceVariant,
            )
        }
    }
}
