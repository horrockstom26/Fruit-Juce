package com.music.bitchord.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.music.bitchord.R
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.data.spotify.SpotifyLibrary
import com.music.bitchord.data.spotify.SpotifyPlaylist
import com.music.bitchord.data.spotify.SpotifyTrack

private const val LOGIN_URL =
    "https://accounts.spotify.com/login?continue=https%3A%2F%2Fopen.spotify.com%2F"

// A phone user agent, so Spotify serves its mobile sign-in page — the desktop
// page is laid out for a wide window and fights the WebView's narrow viewport,
// which is what made the login look like it was flickering and tearing itself
// apart. The sp_dc cookie is written the same way on either page.
private const val LOGIN_USER_AGENT =
    "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36"

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
    var showLogin by remember { mutableStateOf(false) }
    var playlists by remember { mutableStateOf<List<SpotifyPlaylist>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    BackHandler(enabled = showLogin) { showLogin = false }

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

    if (showLogin) {
        SpotifyLogin(
            modifier = modifier.padding(contentPadding),
            onConnected = { token ->
                AppSettings.setSpotifySpdcToken(token)
                showLogin = false
            },
        )
        return
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
                    Button(onClick = { showLogin = true }) {
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

private val LOGIN_HOST_SUFFIXES = listOf(
    "spotify.com", "scdn.co", "google.com", "gstatic.com", "facebook.com", "apple.com",
)

private fun hostMatches(uri: Uri?, suffixes: List<String>): Boolean {
    if (uri?.scheme != "https") return false
    val host = uri.host?.lowercase() ?: return false
    return suffixes.any { host == it || host.endsWith(".$it") }
}

private fun isLoginHost(uri: Uri?) = hostMatches(uri, LOGIN_HOST_SUFFIXES)

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

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun SpotifyLogin(
    onConnected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.userAgentString = LOGIN_USER_AGENT
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.builtInZoomControls = false
            settings.displayZoomControls = false
            if (Build.VERSION.SDK_INT >= 33) {
                settings.isAlgorithmicDarkeningAllowed = false
            } else if (Build.VERSION.SDK_INT >= 29) {
                @Suppress("DEPRECATION")
                settings.forceDark = WebSettings.FORCE_DARK_OFF
            }
            setBackgroundColor(0xFF121212.toInt())
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest,
                ): Boolean = !isLoginHost(request.url)

                // Nothing is injected into Spotify's pages on purpose: the
                // sign-in screen is a hydrated React layout, and the CSS
                // "layout fix" that used to run here fought its own styles —
                // that injection was what made the page flicker and the
                // sign-in buttons vanish. The page is left entirely alone.
            }
            loadUrl(LOGIN_URL)
        }
    }

    // The sp_dc cookie is written after the sign-in redirect lands on
    // open.spotify.com, and a single-page-app redirect does not always fire
    // another onPageFinished — so the cookie jar is polled instead of hooked
    // on page events. A user who is already signed in is picked up on the
    // first tick and the login closes straight away.
    var captured by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val cookies = CookieManager.getInstance()
        while (true) {
            if (!captured) {
                val token = cookies.getCookie("https://open.spotify.com")
                    ?.split(";")
                    ?.map { it.trim() }
                    ?.firstOrNull { it.startsWith("sp_dc=") }
                    ?.substringAfter("=")
                    ?.takeIf { it.isNotBlank() }
                if (token != null) {
                    captured = true
                    onConnected(token)
                    return@LaunchedEffect
                }
            }
            kotlinx.coroutines.delay(400)
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { webView },
        onRelease = { it.destroy() },
    )
}
