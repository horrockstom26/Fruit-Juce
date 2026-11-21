package com.music.bitchord.ui.screens

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.data.stats.ListeningStats
import com.music.bitchord.data.stats.ReplayPeriod
import com.music.bitchord.data.stats.ReplaySummary
import com.music.bitchord.ui.replay.ShareAction
import com.music.bitchord.ui.replay.cacheForSharing
import com.music.bitchord.ui.replay.saveToGallery
import com.music.bitchord.ui.replay.sendIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.roundToInt

data class PersonaResult(
    val name: String,
    val tagline: String,
    val traits: List<String>,
    val summary: ReplaySummary,
)

fun computePersona(summary: ReplaySummary): PersonaResult {
    val hours = summary.hourOfDay
    val night = hours.getOrElse(22) { 0L } + hours.getOrElse(23) { 0L } + hours.getOrElse(0) { 0L } + hours.getOrElse(1) { 0L }
    val morning = (5..10).sumOf { hours.getOrElse(it) { 0L } }
    val totalHoursMs = hours.sum().coerceAtLeast(1)
    val variety = if (summary.totalPlays > 0) summary.distinctSongs.toFloat() / summary.totalPlays else 0f
    val repeatRate = if (summary.distinctSongs > 0) summary.totalPlays.toFloat() / summary.distinctSongs else 0f
    val topArtistShare = if (summary.totalMs > 0 && summary.artists.isNotEmpty()) {
        summary.artists.first().ms.toFloat() / summary.totalMs
    } else 0f

    val persona = when {
        night > totalHoursMs * 0.35f -> "Night Owl" to "When the world goes quiet, your music gets loud."
        morning > totalHoursMs * 0.35f -> "Early Riser" to "You set the tone before the day does."
        repeatRate >= 3.0f -> "On-Repeat Soul" to "When you love a song, you really love a song."
        variety >= 0.7f -> "Deep Diver" to "Always hunting for the next sound."
        topArtistShare >= 0.45f -> "Loyal Listener" to "You know what you like - and you stay with it."
        summary.distinctAlbums >= 20 -> "Album Collector" to "You don't just hear tracks, you live in records."
        else -> "All-Rounder" to "A little bit of everything, all in good taste."
    }

    val traits = buildList {
        val peak = summary.peakHour
        if (peak != null) add("Peak hour: ${formatHour(peak)}")
        if (summary.distinctArtists > 0) add("${summary.distinctArtists} artists explored")
        if (summary.distinctSongs > 0) add("${summary.distinctSongs} different songs")
        add("${String.format(Locale.getDefault(), "%,d", summary.minutes)} minutes listened")
        if (topArtistShare >= 0.25f && summary.artists.isNotEmpty()) {
            add("Top artist: ${summary.artists.first().title}")
        } else if (variety >= 0.6f) {
            add("High variety taste")
        } else if (repeatRate >= 2.2f) {
            add("Comfort-repeat champion")
        }
    }.take(4)

    return PersonaResult(persona.first, persona.second, traits, summary)
}

private fun formatHour(hour: Int): String = when (hour) {
    0 -> "12 am"
    12 -> "12 pm"
    in 1..11 -> "$hour am"
    else -> "${hour - 12} pm"
}

@Composable
fun ListeningPersonaScreen(
    contentPadding: PaddingValues = PaddingValues(),
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val personaState by produceState<PersonaResult?>(initialValue = null) {
        val summary = runCatching { ListeningStats.summary(ReplayPeriod.THIS_YEAR) }.getOrNull()
            ?.takeUnless { it.isEmpty }
            ?: runCatching { ListeningStats.summary(ReplayPeriod.ALL_TIME) }.getOrNull()
        value = summary?.takeUnless { it.isEmpty }?.let { computePersona(it) }
    }
    var poster by remember { mutableStateOf<Bitmap?>(null) }
    var saved by remember { mutableStateOf(false) }

    val persona = personaState
    LaunchedEffect(persona) {
        poster = persona?.let { runCatching { renderPersonaPoster(context, it) }.getOrNull() }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(Color(0xFF0B0614)),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 12.dp,
            bottom = contentPadding.calculateBottomPadding() + 32.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Rounded.AutoAwesome, contentDescription = null, tint = Color(0xFFE7B3FF))
                Spacer(Modifier.size(8.dp))
                Text("Listening Persona", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        when {
            personaState == null && persona == null -> item {
                // loading until produceState resolves; distinguish by checking again below with delay-like: produceState sets value (possibly null)
            }
        }

        if (persona == null) {
            item {
                val stillLoading = personaState == null
                if (stillLoading) {
                    Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                } else {
                    Column(Modifier.padding(16.dp).clip(RoundedCornerShape(20.dp)).background(Color(0xFF1A1026)).padding(20.dp)) {
                        Text("Not enough listening yet", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("Play some music and your persona will show up here.", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    }
                }
            }
        } else {
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF7A2AE6), Color(0xFFE1339B), Color(0xFFFF8A5B))))
                        .padding(20.dp),
                ) {
                    Text("YOU ARE", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(persona.name, color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(6.dp))
                    Text(persona.tagline, color = Color.White.copy(alpha = 0.92f), fontSize = 15.sp)
                }
            }
            item {
                Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    persona.traits.forEach { trait ->
                        Box(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color(0xFF1A1026)).padding(horizontal = 14.dp, vertical = 12.dp),
                        ) {
                            Text(trait, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
            item {
                val image = poster
                if (image != null) {
                    Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Share your persona", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Image(
                            bitmap = image.asImageBitmap(),
                            contentDescription = "Persona card",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxWidth().aspectRatio(9f / 16f).clip(RoundedCornerShape(16.dp)),
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ShareAction(
                                label = if (saved) "Saved" else "Download",
                                icon = Icons.Rounded.Download,
                                accent = false,
                                enabled = !saved,
                                modifier = Modifier.weight(1f),
                            ) {
                                scope.launch { saved = saveToGallery(context, image, "Persona", "kisetsu-persona") }
                            }
                            ShareAction(
                                label = "Share",
                                icon = Icons.Rounded.IosShare,
                                accent = true,
                                enabled = true,
                                modifier = Modifier.weight(1f),
                            ) {
                                scope.launch {
                                    val uri = cacheForSharing(context, image, "persona.png") ?: return@launch
                                    context.startActivity(Intent.createChooser(sendIntent(uri), "Share your persona"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun renderPersonaPoster(context: Context, persona: PersonaResult): Bitmap =
    withContext(Dispatchers.Default) {
        val w = 1080
        val h = 1920
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), intArrayOf(0xFF7A2AE6.toInt(), 0xFFE1339B.toInt(), 0xFFFF8A5B.toInt()), null, Shader.TileMode.CLAMP)
        }
        canvas.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
        val bold = Typeface.create("sans-serif-black", Typeface.BOLD)
        val reg = Typeface.create("sans-serif", Typeface.NORMAL)

        fun text(t: String, x: Float, y: Float, size: Float, color: Int, tf: Typeface = bold) {
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                this.color = color
                textSize = size
                typeface = tf
            }
            canvas.drawText(t, x, y, p)
        }
        fun fit(t: String, size: Float, maxW: Float, tf: Typeface = bold): String {
            val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = size; typeface = tf }
            if (p.measureText(t) <= maxW) return t
            var s = t
            while (s.isNotEmpty() && p.measureText("$s.") > maxW) s = s.dropLast(1)
            return "$s."
        }

        text("KISETSU LISTENING PERSONA", 80f, 170f, 34f, 0xE6FFFFFF.toInt(), Typeface.create("sans-serif", Typeface.BOLD))
        text("You are", 80f, 360f, 54f, 0xF2FFFFFF.toInt(), reg)
        text(fit(persona.name, 130f, 920f), 80f, 520f, 130f, 0xFFFFFFFF.toInt())
        text(fit(persona.tagline, 40f, 920f, reg), 80f, 610f, 40f, 0xEEFFFFFF.toInt(), reg)

        var y = 760f
        persona.traits.forEach { trait ->
            val bg = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0x33FFFFFF }
            canvas.drawRoundRect(80f, y - 58f, 1000f, y + 22f, 28f, 28f, bg)
            text(fit(trait, 40f, 860f, reg), 120f, y, 40f, 0xFFFFFFFF.toInt(), reg)
            y += 110f
        }
        text("KISETSU  .  YOUR SOUND", 80f, h - 110f, 34f, 0xCCFFFFFF.toInt(), Typeface.create("sans-serif", Typeface.BOLD))
        bitmap
    }
