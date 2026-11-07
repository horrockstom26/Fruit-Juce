package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.BrowseItem
import com.music.bitchord.data.model.SearchFilter
import com.music.bitchord.data.model.SearchResult
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.UiState

@Composable
fun SimpleSearchContent(
    state: UiState<List<SearchResult>>?,
    filter: SearchFilter,
    onFilterChange: (SearchFilter) -> Unit,
    onPlaySong: (Song) -> Unit,
    onSongMenu: (Song) -> Unit,
    onOpenBrowse: (BrowseItem) -> Unit,
    onBrowseMenu: (BrowseItem) -> Unit,
    contentPadding: PaddingValues,
    topPadding: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
) {
    val filters = listOf(
        SearchFilter.ALL,
        SearchFilter.SONGS,
        SearchFilter.ARTISTS,
        SearchFilter.ALBUMS,
        SearchFilter.PLAYLISTS,
        SearchFilter.VIDEOS,
    )
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = topPadding),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            filters.forEach { f ->
                val selected = f == filter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (selected) MaterialTheme.colorScheme.onBackground
                            else MaterialTheme.colorScheme.surfaceVariant,
                        )
                        .clickable { onFilterChange(f) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(
                        text = f.label,
                        color = if (selected) MaterialTheme.colorScheme.background
                        else MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                    )
                }
            }
        }

        when (state) {
            null -> {
                Box(Modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
                    Text(
                        "Search for songs, artists, albums, and playlists",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                }
            }
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Error -> {
                Box(Modifier.fillMaxSize().padding(contentPadding), contentAlignment = Alignment.Center) {
                    Text(
                        state.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                }
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 0.dp,
                        end = 0.dp,
                        top = 4.dp,
                        bottom = contentPadding.calculateBottomPadding() + 24.dp,
                    ),
                ) {
                    itemsIndexed(state.data, key = { index, item ->
                        when (item) {
                            is SearchResult.Track -> "t:${item.song.videoId}:$index"
                            is SearchResult.TopTrack -> "top:${item.song.videoId}:$index"
                            is SearchResult.Browse -> "b:${item.item.browseId}:${item.item.type}:$index"
                        }
                    }) { _, item ->
                        when (item) {
                            is SearchResult.Track -> SimpleSongRow(
                                song = item.song,
                                onClick = { onPlaySong(item.song) },
                                onLongClick = { onSongMenu(item.song) },
                            )
                            is SearchResult.TopTrack -> SimpleSongRow(
                                song = item.song,
                                onClick = { onPlaySong(item.song) },
                                onLongClick = { onSongMenu(item.song) },
                            )
                            is SearchResult.Browse -> SimpleBrowseRow(
                                item = item.item,
                                onClick = { onOpenBrowse(item.item) },
                                onLongClick = { onBrowseMenu(item.item) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleSongRow(
    song: Song,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = song.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = listOfNotNull(song.artist.takeIf { it.isNotBlank() }, song.durationText).joinToString(" • "),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SimpleBrowseRow(
    item: BrowseItem,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    val circle = item.type.name == "ARTIST"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = item.thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(52.dp)
                .clip(if (circle) CircleShape else RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.subtitle.ifBlank { item.type.name.lowercase().replaceFirstChar { it.uppercase() } },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
