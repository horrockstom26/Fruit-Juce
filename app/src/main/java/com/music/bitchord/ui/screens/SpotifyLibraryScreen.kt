package com.music.bitchord.ui.screens

import android.webkit.CookieManager
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import org.jetbrains.compose.resources.painterResource
import com.music.bitchord.sharedui.resources.spotify_logo
import com.music.bitchord.sharedui.resources.Res
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.music.bitchord.R
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.data.spotify.SpotifyLibrary
import com.music.bitchord.data.spotify.SpotifyPlaylist
import com.music.bitchord.data.spotify.SpotifyTrack

/**
 * The Spotify section shown on the Home and Library screens: when signed in,
 * a rail of the account's playlists that opens the full Spotify library; when
 * not, a connect card that opens the sign-in window directly — Spotify was
 * previously only reachable from the account settings, and nobody found it.
 *
 * Self-contained by design: it loads its own playlists and knows how to sign
 * in, so the screens that host it need one line and two callbacks.
 */
@Composable
fun SpotifyHomeSection(
    onOpenSpotify: () -> Unit,
    onOpenPlaylist: (SpotifyPlaylist) -> Unit,
    modifier: Modifier = Modifier,
    /** Matches the forced-black Netflix surfaces, where theme colours don't apply. */
    dark: Boolean = false,
) {
    val cookie by AppSettings.spotifySpdcToken.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    val playlists by produceState<List<SpotifyPlaylist>?>(initialValue = null, cookie) {
        value = if (cookie.isBlank()) {
            null
        } else {
            runCatching { SpotifyLibrary.playlists() }.getOrNull()
        }
    }
    val connected = cookie.isNotBlank()
    val titleColor = if (dark) Color.White else MaterialTheme.colorScheme.onBackground
    val accentColor = if (dark) Color(0xFF1DB954) else MaterialTheme.colorScheme.primary

    if (!connected) {
        // The way in: a card that looks like part of the page, not a settings
        // entry. Tapping it opens the sign-in window straight away.
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(listOf(Color(0xFF1DB954), Color(0xFF0B5A2A))),
                )
                .clickable {
                    runCatching {
                        context.startActivity(
                            android.content.Intent(
                                context,
                                com.music.bitchord.auth.SpotifyLoginActivity::class.java,
                            ),
                        )
                    }
                }
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.spotify_logo),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(38.dp),
            )
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.spotify),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Connect to see your playlists, liked songs and more",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        return
    }

    Column(modifier = modifier.fillMaxWidth().padding(top = 10.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onOpenSpotify)
                .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(
                painter = painterResource(Res.drawable.spotify_logo),
                contentDescription = null,
                tint = titleColor,
                modifier = Modifier.size(26.dp),
            )
            Text(
                text = stringResource(R.string.spotify),
                color = titleColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(R.string.show_all),
                color = accentColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        when {
            playlists == null -> {}
            playlists.orEmpty().isEmpty() -> {}
            else -> {
                androidx.compose.foundation.lazy.LazyRow(
                    contentPadding = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    items(playlists.orEmpty().take(10), key = { it.id }) { playlist ->
                        Column(
                            modifier = Modifier
                                .width(132.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onOpenPlaylist(playlist) },
                        ) {
                            AsyncImage(
                                model = playlist.imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(132.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = playlist.name,
                                color = titleColor,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * The signed-in Spotify account's playlists. A tap opens one as an ordinary
 * playlist page ([onOpenPlaylist]) — this screen is only the way in.
 */
@Composable
fun SpotifyLibraryScreen(
    onOpenPlaylist: (SpotifyPlaylist) -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val cookie by AppSettings.spotifySpdcToken.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current
    var playlists by remember { mutableStateOf<List<SpotifyPlaylist>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val connect: () -> Unit = {
        runCatching {
            context.startActivity(android.content.Intent(context, com.music.bitchord.auth.SpotifyLoginActivity::class.java))
        }
    }

    LaunchedEffect(cookie) {
        if (cookie.isBlank()) {
            playlists = emptyList()
            error = null
            return@LaunchedEffect
        }
        loading = true
        error = null
        runCatching { SpotifyLibrary.playlists() }
            .onSuccess { playlists = it }
            .onFailure { error = it.message }
        loading = false
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 14.dp),
            ) {
                Icon(
                    painter = painterResource(Res.drawable.spotify_logo),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(38.dp),
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.spotify),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
        if (cookie.isBlank()) {
            item {
                Column(Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = stringResource(R.string.spotify_connect_subtitle),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = connect) {
                        Text(stringResource(R.string.spotify_sign_in))
                    }
                }
            }
        } else {
            if (loading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
            error?.let { message ->
                item {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    )
                }
            }
            items(playlists, key = { it.id }) { item ->
                LibraryRow(
                    title = item.name,
                    subtitle = item.owner.orEmpty(),
                    imageUrl = item.imageUrl,
                    onClick = { onOpenPlaylist(item) },
                )
            }
        }
    }
}

@Composable
private fun LibraryRow(
    title: String,
    subtitle: String,
    imageUrl: String?,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(8.dp)),
        )
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private val SPOTIFY_COOKIE_URLS = listOf(
    "https://open.spotify.com",
    "https://accounts.spotify.com",
    "https://www.spotify.com",
    "https://spotify.com",
)

/**
 * Signs the login WebView out of Spotify. Disconnecting only forgot the
 * stored cookie, and the WebView's own jar would have signed the next
 * "Sign in" straight back in. Only Spotify's cookies go — the same jar holds
 * the YouTube Music sign-in.
 */
fun clearSpotifyWebSession() {
    val cookies = CookieManager.getInstance()
    for (url in SPOTIFY_COOKIE_URLS) {
        val names = cookies.getCookie(url)?.split(";")
            ?.map { it.substringBefore("=").trim() }
            ?.filter { it.isNotEmpty() }
            .orEmpty()
        for (name in names) {
            val expired = "$name=; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; Path=/"
            cookies.setCookie(url, expired)
            cookies.setCookie(url, "$expired; Domain=.spotify.com")
        }
    }
    cookies.flush()
}

