package com.music.bitchord.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.music.bitchord.data.settings.AppSettings

/**
 * Spotify sign-in in its own full-screen window, on a bare [WebView] with no
 * Compose around it.
 *
 * The old sign-in lived inside the app's main animated screen - under blur
 * layers, transition animations and overlay surfaces - and every attempt to
 * stabilise it there still left the page flickering and its buttons glitching.
 * This is the rebuilt holder: a plain window whose only child is the WebView,
 * the way a browser window is laid out. Nothing samples or animates it, so the
 * page renders exactly as it does on the web.
 *
 * No script is ever injected into Spotify's page - that CSS "layout fix" was
 * fighting the page's own styles and was the original source of the glitching.
 * Sign-in completion is detected by polling the cookie jar for `sp_dc` (the
 * single-page-app redirect to open.spotify.com does not reliably re-fire page
 * events), and the captured cookie is stored the moment it appears, closing
 * the window; the Spotify screen behind it reloads the library off the new
 * session automatically.
 */
class SpotifyLoginActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())
    private var captured = false
    private var webView: WebView? = null

    /** Sign-in hosts the page may navigate within; anything else opens outside. */
    private val loginHosts = listOf(
        "spotify.com", "scdn.co", "google.com", "gstatic.com",
        "facebook.com", "apple.com", "accounts.youtube.com",
    )

    private fun allowedHost(uri: Uri?): Boolean {
        if (uri?.scheme !in listOf("https", "http")) return false
        val host = uri?.host?.lowercase() ?: return false
        return loginHosts.any { host == it || host.endsWith(".$it") }
    }

    private val poll = object : Runnable {
        override fun run() {
            if (captured) return
            val token = CookieManager.getInstance()
                .getCookie("https://open.spotify.com")
                ?.split(";")
                ?.map { it.trim() }
                ?.firstOrNull { it.startsWith("sp_dc=") }
                ?.substringAfter("=")
                ?.takeIf { it.isNotBlank() }
            if (token != null) {
                captured = true
                AppSettings.setSpotifySpdcToken(token)
                finish()
                return
            }
            handler.postDelayed(this, 400)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setBackgroundColor(Color.BLACK)

        val view = WebView(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            // Phone user agent so Spotify serves its mobile sign-in layout,
            // which fits this window instead of fighting the viewport.
            settings.userAgentString =
                "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) " +
                    "Chrome/131.0.0.0 Mobile Safari/537.36"
            settings.useWideViewPort = true
            settings.loadWithOverviewMode = true
            settings.mediaPlaybackRequiresUserGesture = true
            if (android.os.Build.VERSION.SDK_INT >= 33) {
                settings.isAlgorithmicDarkeningAllowed = false
            } else if (android.os.Build.VERSION.SDK_INT >= 29) {
                @Suppress("DEPRECATION")
                settings.forceDark = WebSettings.FORCE_DARK_OFF
            }
            setBackgroundColor(Color.BLACK)
            CookieManager.getInstance().setAcceptCookie(true)
            webViewClient = object : WebViewClient() {
                // Anything outside the sign-in flow (an app install page, a
                // Help link) opens in the real browser instead of leaving the
                // window for a page it cannot come back from.
                override fun shouldOverrideUrlLoading(
                    view: WebView,
                    request: WebResourceRequest,
                ): Boolean {
                    if (allowedHost(request.url)) return false
                    runCatching {
                        startActivity(Intent(Intent.ACTION_VIEW, request.url))
                    }
                    return true
                }
            }
            webChromeClient = object : WebChromeClient() {
                // Spotify's sign-in offers Google, Facebook and Apple, and
                // each opens its own page in a popup window. A WebView without
                // an onCreateWindow silently swallows that tap - the button
                // appears to do nothing or vanish. The popup is instead
                // captured and routed into this window itself.
                override fun onCreateWindow(
                    view: WebView,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message,
                ): Boolean {
                    if (!isUserGesture) return false
                    val popup = WebView(view.context).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.userAgentString =
                            view.settings.userAgentString
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                target: WebView,
                                request: WebResourceRequest,
                            ): Boolean {
                                val url = request.url
                                if (allowedHost(url)) {
                                    webView?.loadUrl(url.toString())
                                } else {
                                    runCatching {
                                        startActivity(Intent(Intent.ACTION_VIEW, url))
                                    }
                                }
                                return true
                            }
                        }
                    }
                    (resultMsg.obj as WebView.WebViewTransport).setWebView(popup)
                    resultMsg.sendToTarget()
                    return true
                }
            }
            loadUrl(LOGIN_URL)
        }
        CookieManager.getInstance().setAcceptThirdPartyCookies(view, true)
        webView = view
        setContentView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
        handler.post(poll)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val view = webView
        if (view != null && view.canGoBack()) view.goBack() else finish()
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        webView?.destroy()
        webView = null
        super.onDestroy()
    }

    private companion object {
        const val LOGIN_URL =
            "https://accounts.spotify.com/login?continue=https%3A%2F%2Fopen.spotify.com%2F"
    }
}
