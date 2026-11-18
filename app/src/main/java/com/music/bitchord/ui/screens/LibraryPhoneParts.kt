package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.bitchord.R
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.data.stats.ReplaySummary
import com.music.bitchord.download.Downloads
import com.music.bitchord.download.SavedCollection
import com.music.bitchord.sharedui.resources.Res
import com.music.bitchord.sharedui.resources.spotify_logo
import com.music.bitchord.ui.components.PAGE_GUTTER
import java.util.Locale

// The phone's halves of the shared Library page: its list of folders, what
// sits on its "On device" shelf, and the way in to Replay at its head.

/**
 * The phone's folders, drawn as a list under the Replay cards: Downloads and
 * Local Music, the song cache when Settings has it shown, and the two remote
 * libraries it can reach.
 */
@Composable
fun libraryLinks(): List<LibraryLink> {
    val webdavConfigured by AppSettings.webdavUrl.collectAsStateWithLifecycle()
    val smbHost by AppSettings.smbHost.collectAsStateWithLifecycle()
    val smbShare by AppSettings.smbShare.collectAsStateWithLifecycle()
    val showCacheFolder by AppSettings.showCacheFolder.collectAsStateWithLifecycle()
    val spotifyConnected by AppSettings.spotifySpdcToken.collectAsStateWithLifecycle()
    fun link(icon: ImageVector, title: String, subtitle: String, browseId: String) = LibraryLink(
        item = ShelfItem(
            title = title,
            subtitle = subtitle,
            thumbnailUrl = null,
            videoId = null,
            browseId = browseId,
        ),
        icon = icon,
    )
    return listOfNotNull(
        link(
            Icons.Rounded.Download,
            stringResource(R.string.downloads),
            stringResource(R.string.downloaded_songs),
            "local:downloads",
        ),
        link(
            Icons.Rounded.Folder,
            stringResource(R.string.local_music),
            stringResource(R.string.audio_files_on_device),
            "local:all",
        ),
        // Opt-in from Settings → Storage: what the song cache is holding from
        // YouTube and JioSaavn. See [com.music.bitchord.playback.AudioCache.cachedSongs].
        link(
            Icons.Rounded.Storage,
            stringResource(R.string.cached_songs),
            stringResource(R.string.cached_songs_subtitle),
            CACHE_FOLDER_BROWSE_ID,
        ).takeIf { showCacheFolder },
        // Only once set up in Sources; unconfigured, they'd just be dead ends.
        link(
            Icons.Rounded.Cloud,
            stringResource(R.string.webdav),
            stringResource(R.string.webdav_subtitle),
            com.music.bitchord.data.webdav.WebDavConfig.BROWSE_ID,
        ).takeIf { webdavConfigured.isNotBlank() },
        link(
            Icons.Rounded.Lan,
            stringResource(R.string.smb),
            stringResource(R.string.smb_subtitle),
            com.music.bitchord.data.smb.SmbConfig.BROWSE_ID,
        ).takeIf { smbHost.isNotBlank() && smbShare.isNotBlank() },
        // Only while signed in to Spotify in Settings → Accounts; it opens that
        // account's playlists rather than a browse page.
        link(
            Icons.Rounded.Cloud,
            stringResource(R.string.spotify),
            stringResource(R.string.spotify_library_subtitle),
            SPOTIFY_BROWSE_ID,
        ).takeIf { spotifyConnected.isNotBlank() }?.copy(logo = Res.drawable.spotify_logo),
    )
}

/**
 * The phone's "On device" shelf: the playlists and albums downloaded whole —
 * the same promise the folders above it make, here, now, without a network.
 * Nothing is truncated: the shelf is a row that scrolls, so "all of them"
 * costs nothing.
 */
@Composable
fun libraryDeviceItems(downloadedReleases: List<SavedCollection>): List<ShelfItem> {
    val downloadedPlaylist = stringResource(R.string.downloaded_playlist)
    return downloadedReleases.map { release ->
        ShelfItem(
            title = release.title,
            // The credit the release was downloaded with, because this is also
            // what the page it opens bills itself by — see `headerLines`, which
            // reads the kind and the owner back out of it. Saying "Downloaded
            // playlist" here instead would make that header read "Downloaded
            // playlist" over "PLAYLIST • 12 SONGS", and the shelf this card is on
            // already says where it lives.
            subtitle = release.subtitle.ifBlank { if (release.playlist) downloadedPlaylist else "" },
            thumbnailUrl = release.thumbnailUrl,
            videoId = null,
            browseId = Downloads.pageIdFor(release.id),
        )
    }
}

/** The Library row that opens the connected Spotify account; handled by the app, not a browse page. */
const val SPOTIFY_BROWSE_ID = "app:spotify"

/** The Cached songs folder's page id — one of the `local:` device folders. */
const val CACHE_FOLDER_BROWSE_ID = "local:cache"

/**
 * The phone's way in to Replay: its row of headline cards, placeholders for
 * them while the history is read, and nothing at all while there is not yet
 * enough listening to deal them — Settings still opens Replay either way.
 */
@Composable
fun LibraryReplayEntry(
    summary: ReplaySummary?,
    loading: Boolean,
    onViewFullStats: () -> Unit,
) {
    if (loading) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = PAGE_GUTTER, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(Modifier.fillMaxWidth(0.4f).height(18.dp).clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)))
            repeat(4) {
                Box(Modifier.fillMaxWidth(if (it == 2) 0.55f else 0.85f).height(14.dp).clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)))
            }
        }
        return
    }
    val data = summary?.takeUnless { it.isEmpty } ?: return

    val topSong = data.songs.firstOrNull()?.let { "${it.song.title} · ${it.song.artist}" } ?: "—"
    val topArtist = data.artists.firstOrNull()?.title ?: "—"
    val minutes = String.format(Locale.getDefault(), "%,d min", data.minutes)
    val topAlbum = data.albums.firstOrNull()?.let {
        if (it.subtitle.isNullOrBlank()) it.title else "${it.title} · ${it.subtitle}"
    } ?: "—"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PAGE_GUTTER, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Rounded.BarChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = "Your Replay",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "View full stats",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onViewFullStats)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            )
        }

        ReplayStatRow(label = "Top song", value = topSong)
        ReplayStatRow(label = "Top artist", value = topArtist)
        ReplayStatRow(label = "Minutes listened", value = minutes)
        ReplayStatRow(label = "Top album", value = topAlbum)
    }
}

@Composable
private fun ReplayStatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.42f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(0.58f),
        )
    }
}
