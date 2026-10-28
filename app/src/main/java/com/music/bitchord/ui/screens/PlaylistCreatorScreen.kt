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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.PlaylistPrivacy
import com.music.bitchord.data.model.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

/**
 * Fruit Juce v5.2: the Create tab's playlist builder.
 *
 * One page does the whole job: name the playlist, search YouTube Music for
 * the songs right here, tap a result to drop it into the list, and create.
 * Nothing about the flow leaves the page — no detour to the picker, no
 * "add songs later" dead end.
 *
 * The list is a *draft* until the button is pressed: tapping a result adds a
 * copy locally, and only Create speaks to the account. Signed out, the same
 * page still builds the playlist — it lands on this device instead (see
 * the create call's own branch).
 */
@Composable
fun PlaylistCreatorScreen(
    signedIn: Boolean,
    searchResults: StateFlow<List<Song>>,
    searching: StateFlow<Boolean>,
    draft: StateFlow<List<Song>>,
    onSearch: (String) -> Unit,
    onAddDraft: (Song) -> Unit,
    onRemoveDraft: (Song) -> Unit,
    onCreate: (String, PlaylistPrivacy) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    var name by rememberSaveable { mutableStateOf("") }
    var query by rememberSaveable { mutableStateOf("") }

    // One search per settled keystroke: the key change cancels the delay and
    // restarts it, so a fast typist fires once, on their last word.
    LaunchedEffect(query) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            onSearch("")
            return@LaunchedEffect
        }
        delay(450)
        onSearch(trimmed)
    }

    val results by searchResults.collectAsStateWithLifecycle()
    val searchingNow by searching.collectAsStateWithLifecycle()
    val draftSongs by draft.collectAsStateWithLifecycle()
    val tone = MaterialTheme.colorScheme

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.playlist_creator_title),
            style = MaterialTheme.typography.headlineMedium,
            color = tone.onBackground,
            modifier = Modifier.padding(top = 6.dp),
        )
        Spacer(Modifier.height(14.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            placeholder = { Text(stringResource(R.string.playlist_name_hint)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            placeholder = { Text(stringResource(R.string.search_songs_hint)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(12.dp))

        if (!signedIn) {
            Text(
                text = stringResource(R.string.playlist_creator_local_hint),
                style = MaterialTheme.typography.bodySmall,
                color = tone.onSurfaceVariant,
            )
            Spacer(Modifier.height(10.dp))
        }

        // In transit between queries: a quiet line, not a spinner jumping a
        // row of results around it.
        if (searchingNow) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                Text(
                    text = stringResource(R.string.playlist_creator_searching),
                    style = MaterialTheme.typography.bodySmall,
                    color = tone.onSurfaceVariant,
                    modifier = Modifier.padding(start = 10.dp),
                )
            }
            Spacer(Modifier.height(10.dp))
        }

        if (results.isNotEmpty()) {
            Text(
                text = stringResource(R.string.playlist_creator_results),
                style = MaterialTheme.typography.titleSmall,
                color = tone.onSurfaceVariant,
            )
            Spacer(Modifier.height(6.dp))
            results.forEachIndexed { index, song ->
                CreatorResultRow(
                    song = song,
                    onAdd = { onAddDraft(song) },
                )
                if (index < results.lastIndex) Spacer(Modifier.height(6.dp))
            }
            Spacer(Modifier.height(14.dp))
        }

        if (draftSongs.isNotEmpty()) {
            Text(
                text = stringResource(R.string.playlist_creator_in_playlist),
                style = MaterialTheme.typography.titleSmall,
                color = tone.onSurfaceVariant,
            )
            Spacer(Modifier.height(6.dp))
            draftSongs.forEachIndexed { index, song ->
                CreatorDraftRow(
                    song = song,
                    onRemove = { onRemoveDraft(song) },
                )
                if (index < draftSongs.lastIndex) Spacer(Modifier.height(6.dp))
            }
        } else {
            Text(
                text = stringResource(R.string.playlist_creator_draft_hint),
                style = MaterialTheme.typography.bodySmall,
                color = tone.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 10.dp),
            )
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onCreate(name, PlaylistPrivacy.PRIVATE) },
            enabled = name.isNotBlank() && draftSongs.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
        ) {
            Text(
                text = stringResource(R.string.playlist_creator_create),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Spacer(Modifier.height(24.dp))
    }
}

/** A search hit with a quiet add button — one tap, straight to the draft. */
@Composable
private fun CreatorResultRow(
    song: Song,
    onAdd: () -> Unit,
) {
    val tone = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tone.surfaceContainerHigh, RoundedCornerShape(14.dp))
            .clickable { onAdd() }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = song.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tone.surfaceContainerHighest),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                color = tone.onSurface,
                maxLines = 1,
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = tone.onSurfaceVariant,
                maxLines = 1,
            )
        }
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(tone.primary, CircleShape)
                .clickable { onAdd() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.titleMedium,
                color = tone.onPrimary,
            )
        }
    }
}

/** A drafted song; the trailing minus takes it back out. */
@Composable
private fun CreatorDraftRow(
    song: Song,
    onRemove: () -> Unit,
) {
    val tone = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tone.surfaceContainerHigh, RoundedCornerShape(14.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = song.thumbnailUrl,
            contentDescription = null,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tone.surfaceContainerHighest),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp),
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                color = tone.onSurface,
                maxLines = 1,
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = tone.onSurfaceVariant,
                maxLines = 1,
            )
        }
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(tone.surfaceContainerHighest, CircleShape)
                .clickable { onRemove() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\u2013",
                style = MaterialTheme.typography.titleMedium,
                color = tone.onSurface,
            )
        }
    }
}
