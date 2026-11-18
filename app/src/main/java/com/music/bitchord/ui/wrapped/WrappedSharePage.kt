package com.music.bitchord.ui.wrapped

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.data.stats.ListeningStats
import com.music.bitchord.data.stats.ReplaySummary
import com.music.bitchord.ui.replay.ShareAction
import com.music.bitchord.ui.replay.cacheForSharing
import com.music.bitchord.ui.replay.saveToGallery
import com.music.bitchord.ui.replay.sendIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The Wrapped share page: one advertisement-shaped card with the four facts
 * people actually post — the artist the year opened with, the hours, the top
 * artist and the top song — and the two things to do with it: send it, or keep
 * it. Drawn with a canvas rather than screenshotted so the picture is the same
 * 9:16 on every phone (see [com.music.bitchord.ui.replay.renderReplayPoster]).
 */
@Composable
fun WrappedSharePage(
    summary: ReplaySummary,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var poster by remember { mutableStateOf<Bitmap?>(null) }
    var failed by remember { mutableStateOf(false) }
    var saved by remember { mutableStateOf(false) }

    val firstArtist by produceState<String?>(initialValue = null, summary) {
        value = runCatching { ListeningStats.firstArtist(summary.period) }.getOrNull()
    }

    LaunchedEffect(summary, firstArtist) {
        poster = runCatching { renderWrappedAd(context, summary, firstArtist) }
            .onFailure { failed = true }
            .getOrNull()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0B0614)),
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "SHARE YOUR WRAPPED",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                    )
                    Text(
                        "Post your year",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                val image = poster
                when {
                    image != null -> Image(
                        bitmap = image.asImageBitmap(),
                        contentDescription = "Your Kisetsu Wrapped card",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(9f / 16f)
                            .clip(RoundedCornerShape(16.dp)),
                    )
                    failed -> Text(
                        "Couldn't draw the card.",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    else -> CircularProgressIndicator(color = Color.White)
                }
            }

            val ready = poster != null
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ShareAction(
                    label = if (saved) "Saved" else "Download",
                    icon = Icons.Rounded.Download,
                    accent = false,
                    enabled = ready && !saved,
                    modifier = Modifier.weight(1f),
                ) {
                    val image = poster ?: return@ShareAction
                    scope.launch { saved = saveToGallery(context, image, summary.label, "kisetsu-wrapped") }
                }
                ShareAction(
                    label = "Share",
                    icon = Icons.Rounded.IosShare,
                    accent = true,
                    enabled = ready,
                    modifier = Modifier.weight(1f),
                ) {
                    val image = poster ?: return@ShareAction
                    scope.launch {
                        val uri = cacheForSharing(context, image, "wrapped.png") ?: return@launch
                        context.startActivity(
                            Intent.createChooser(sendIntent(uri), "Share your Kisetsu Wrapped"),
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
        }
    }
}

/**
 * Draws the advertisement: a saturated 1080 × 1920 card, the year huge at the
 * top, then four ruled stat blocks. Deliberately text-only apart from the
 * headline — the facts are the poster.
 */
private suspend fun renderWrappedAd(
    context: Context,
    summary: ReplaySummary,
    firstArtist: String?,
): Bitmap = withContext(Dispatchers.Default) {
    val w = 1080
    val h = 1920
    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val backdrop = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(
            0f, 0f, w.toFloat(), h.toFloat(),
            intArrayOf(0xFFE1339B.toInt(), 0xFF7A2AE6.toInt(), 0xFF1B1040.toInt()),
            null,
            Shader.TileMode.CLAMP,
        )
    }
    canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), backdrop)

    val bold = Typeface.create("sans-serif-black", Typeface.BOLD)
    val regular = Typeface.create("sans-serif", Typeface.NORMAL)

    val kicker = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xE6FFFFFF.toInt()
        textSize = 34f
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        letterSpacing = 0.14f
    }
    val title = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        textSize = 150f
        typeface = bold
    }
    val label = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xB3FFFFFF.toInt()
        textSize = 30f
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        letterSpacing = 0.10f
    }
    val value = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFFFF.toInt()
        textSize = 62f
        typeface = bold
    }
    val small = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xCCFFFFFF.toInt()
        textSize = 38f
        typeface = regular
    }

    fun fit(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var cut = text
        while (cut.isNotEmpty() && paint.measureText("$cut…") > maxWidth) {
            cut = cut.dropLast(1)
        }
        return "$cut…"
    }

    var y = 190f
    canvas.drawText("KISETSU WRAPPED", 80f, y, kicker)
    y += 175f
    canvas.drawText("'${summary.label.takeLast(2)}", 80f, y, title)
    y += 70f
    canvas.drawText("your year in music", 84f, y, small)

    y += 150f

    fun stat(heading: String, text: String) {
        canvas.drawText(heading, 80f, y, label)
        y += 78f
        canvas.drawText(fit(text, value, 920f), 80f, y, value)
        y += 60f
        canvas.drawRect(80f, y, 1000f, y + 3f, Paint().apply { color = 0x33FFFFFF })
        y += 90f
    }

    val topSong = summary.songs.firstOrNull()
    stat("FIRST ARTIST", firstArtist ?: summary.artists.firstOrNull()?.title ?: "—")
    stat("HOURS LISTENED", "${summary.hours} hours · ${summary.minutes} min")
    stat("TOP ARTIST", summary.artists.firstOrNull()?.title ?: "—")
    stat(
        "TOP SONG",
        topSong?.let { "${it.song.title} · ${it.song.artist}" } ?: "—",
    )

    val footer = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xCCFFFFFF.toInt()
        textSize = 34f
        typeface = Typeface.create("sans-serif", Typeface.BOLD)
        letterSpacing = 0.12f
    }
    canvas.drawText("KISETSU  ·  YOUR YEAR IN MUSIC", 80f, h - 110f, footer)

    bitmap
}
