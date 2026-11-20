package com.music.bitchord.ui.components

import coil3.intercept.Interceptor
import coil3.request.ImageResult

/**
 * Legacy interceptor kept for compatibility. Album covers are now normal; this
 * no longer replaces artwork loads.
 */
class SwissFlagCoilInterceptor : Interceptor {
    override suspend fun intercept(chain: Interceptor.Chain): ImageResult = chain.proceed()
}
