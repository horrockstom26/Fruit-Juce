package com.music.bitchord.ui.components

import android.net.Uri
import coil3.intercept.Interceptor
import coil3.request.ImageResult
import com.music.bitchord.R
import com.music.bitchord.data.settings.AppSettings
import java.io.File

/**
 * When "Welcome to Switzerland" is enabled, replace album/artwork image loads
 * with the Swiss flag cover so artwork everywhere becomes the flag.
 */
class SwissFlagCoilInterceptor : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult {
        val request = chain.request
        val switzerland = runCatching { AppSettings.welcomeToSwitzerland.value }.getOrDefault(false)
        if (!switzerland) return chain.proceed()
        val data = request.data
        if (data == R.drawable.swiss_flag_cover) return chain.proceed()
        val replace = when (data) {
            is String -> data.isNotBlank() && (
                data.startsWith("http://") ||
                    data.startsWith("https://") ||
                    data.startsWith("content://") ||
                    data.startsWith("file://") ||
                    data.startsWith("smb:") ||
                    data.startsWith("android.resource://") ||
                    data.startsWith("bitchord://swiss-flag")
                )
            is Uri -> true
            is File -> true
            else -> false
        }
        if (!replace) return chain.proceed()
        return chain.withRequest(
            request.newBuilder().data(R.drawable.swiss_flag_cover).build(),
        ).proceed()
    }
}
