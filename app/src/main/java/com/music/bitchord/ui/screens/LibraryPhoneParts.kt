package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.Lan
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.bitchord.R
import com.music.bitchord.sharedui.resources.Res
import com.music.bitchord.sharedui.resources.spotify_logo
import com.music.bitchord.data.model.ShelfItem
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.download.Downloads
import com.music.bitchord.download.SavedCollection
import com.music.bitchord.ui.components.PAGE_GUTTER
import com.music.bitchord.ui.components.ReplayCardRowSkeleton
import com.music.bitchord.ui.replay.ReplayCardRow
import com.music.bitchord.ui.replay.ReplayCreditCard
import com.music.bitchord.ui.replay.ReplayHeroCard
import com.music.bitchord.ui.replay.ReplayStoryPage

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
    cards: List<ReplayHeroCard>,
    loading: Boolean,
    holder: String,
    memberSince: String?,
    onOpenReplay: (ReplayStoryPage) -> Unit,
) {
    val showUserCount by AppSettings.showUserCount.collectAsStateWithLifecycle()
    val userCount by AppSettings.cachedUserCount.collectAsStateWithLifecycle()
    val marked by AppSettings.userPresenceMarked.collectAsStateWithLifecycle()
    val showCommunityCard = showUserCount && (marked || userCount > 0)

    if (loading) {
        ReplayCardRowSkeleton()
        return
    }

    if (cards.isEmpty() && !showCommunityCard) return

    LazyRow(
        modifier = Modifier.padding(vertical = 6.dp),
        contentPadding = PaddingValues(horizontal = PAGE_GUTTER),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(cards, key = { index, card -> "replay-credit:$index:${card.label}" }) { _, card ->
            ReplayCreditCard(
                label = card.label,
                value = card.value,
                detail = card.detail,
                artworkUrl = card.artworkUrl,
                holder = holder,
                memberSince = memberSince,
                onClick = { onOpenReplay(card.page) },
                modifier = Modifier.width(300.dp),
            )
        }
        if (showCommunityCard) {
            item(key = "community-users") {
                Box(
                    modifier = Modifier
                        .width(300.dp)
                        .aspectRatio(0.68f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (userCount > 0) userCount.toString() else "—",
                            color = Color.White,
                            fontSize = 64.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 68.sp,
                        )
                        Text(
                            text = "Number of users",
                            color = Color.White.copy(alpha = 0.78f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}
