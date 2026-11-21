package com.music.bitchord.ui.wrapped

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.Song
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.data.stats.ListeningStats
import com.music.bitchord.data.stats.ReplaySummary
import com.music.bitchord.ui.replay.ShareAction
import com.music.bitchord.ui.replay.cacheForSharing
import com.music.bitchord.ui.replay.saveToGallery
import com.music.bitchord.ui.replay.sendIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.YearMonth
import java.util.Locale

private const val SLIDE_MS = 6_500L

@Composable
fun MonthlyRecapEntry(
    month: YearMonth,
    onOpen: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = month.month.name.lowercase(Locale.ROOT).replaceFirstChar { it.uppercase(Locale.ROOT) } + " ${month.year}"
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .clickable { onOpen(month) }
            .padding(18.dp),
    ) {
        Column(Modifier.align(Alignment.BottomStart)) {
            Text("MONTHLY RECAP", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            Text(label, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("Tap to relive your month", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp)
        }
    }
}

@Composable
fun MonthlyRecapStories(
    month: YearMonth,
    onClose: () -> Unit,
    onPlaySong: (Song, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val summaryState by produceState<ReplaySummary?>(initialValue = null, month) {
        value = runCatching { ListeningStats.summaryForMonth(month) }.getOrNull()
    }
    val summary = summaryState
    if (summary == null) {
        Box(modifier.fillMaxSize().background(Color(0xFF061018)), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color.White)
        }
        return
    }
    if (summary.isEmpty) {
        Box(modifier.fillMaxSize().background(Color(0xFF061018)), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No listening in ${summary.label}", color = Color.White)
                Spacer(Modifier.height(12.dp))
                Text("Close", color = Color.White.copy(alpha = 0.8f), modifier = Modifier.clickable { onClose() })
            }
        }
        return
    }

    val slides = remember(summary) { buildMonthlySlides(summary) }
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()
    var paused by remember { mutableStateOf(false) }
    var sharing by remember { mutableStateOf(false) }
    val progress = remember(pagerState.currentPage) { Animatable(0f) }

    LaunchedEffect(pagerState.currentPage, paused) {
        if (paused) return@LaunchedEffect
        val remaining = ((1f - progress.value) * SLIDE_MS).toLong().coerceAtLeast(1)
        progress.animateTo(1f, tween(remaining.toInt(), easing = LinearEasing))
        val next = pagerState.currentPage + 1
        if (next < slides.size && !pagerState.isScrollInProgress) {
            scope.launch { pagerState.animateScrollToPage(next, animationSpec = tween(450)) }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(pagerState) {
                detectTapGestures(
                    onPress = { paused = true; try { tryAwaitRelease() } finally { paused = false } },
                    onTap = { pos ->
                        scope.launch {
                            if (pos.x > size.width * 0.62f) {
                                val next = pagerState.currentPage + 1
                                if (next < slides.size) pagerState.animateScrollToPage(next)
                            } else if (pagerState.currentPage > 0) {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }
                    },
                )
            },
    ) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            MonthlySlide(slide = slides[page], summary = summary, onPlaySong = onPlaySong, onShare = { sharing = true })
        }
        // progress + close
        Column(Modifier.fillMaxWidth().statusBarsPadding().padding(top = 8.dp, start = 10.dp, end = 10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                slides.indices.forEach { i ->
                    val frac = when {
                        i < pagerState.currentPage -> 1f
                        i > pagerState.currentPage -> 0f
                        else -> progress.value
                    }
                    Box(Modifier.weight(1f).height(3.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.25f))) {
                        Box(Modifier.fillMaxWidth(frac.coerceIn(0f, 1f)).height(3.dp).clip(CircleShape).background(Color.White))
                    }
                }
            }
        }
        IconButton(onClick = onClose, modifier = Modifier.align(Alignment.TopEnd).statusBarsPadding().padding(top = 14.dp, end = 4.dp)) {
            Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.White)
        }
        if (sharing) {
            MonthlyRecapSharePage(summary = summary, onDismiss = { sharing = false })
        }
    }
}

private sealed interface MSlide {
    data class Intro(val label: String, val minutes: Long) : MSlide
    data class TopSong(val song: Song, val plays: Int) : MSlide
    data class TopArtist(val name: String, val art: String?, val plays: Int) : MSlide
    data class Minutes(val minutes: Long, val plays: Int, val artists: Int) : MSlide
    data class Outro(val label: String, val topSong: Song?) : MSlide
}

private fun buildMonthlySlides(summary: ReplaySummary): List<MSlide> = buildList {
    add(MSlide.Intro(summary.label, summary.minutes))
    add(MSlide.Minutes(summary.minutes, summary.totalPlays, summary.distinctArtists))
    summary.songs.firstOrNull()?.let { add(MSlide.TopSong(it.song, it.plays)) }
    summary.artists.firstOrNull()?.let { add(MSlide.TopArtist(it.title, it.artworkUrl, it.plays)) }
    add(MSlide.Outro(summary.label, summary.songs.firstOrNull()?.song))
}

@Composable
private fun MonthlySlide(slide: MSlide, summary: ReplaySummary, onPlaySong: (Song, String) -> Unit, onShare: () -> Unit) {
    val bg = when (slide) {
        is MSlide.Intro -> Brush.linearGradient(listOf(Color(0xFF0EA5E9), Color(0xFF1E3A8A)))
        is MSlide.TopSong -> Brush.linearGradient(listOf(Color(0xFF7C3AED), Color(0xFF312E81)))
        is MSlide.TopArtist -> Brush.linearGradient(listOf(Color(0xFF059669), Color(0xFF064E3B)))
        is MSlide.Minutes -> Brush.linearGradient(listOf(Color(0xFFEA580C), Color(0xFF7C2D12)))
        is MSlide.Outro -> Brush.linearGradient(listOf(Color(0xFFDB2777), Color(0xFF4C1D95)))
    }
    Box(Modifier.fillMaxSize().background(bg)) {
        Column(
            Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 24.dp, vertical = 56.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            when (slide) {
                is MSlide.Intro -> {
                    Text("MONTHLY RECAP", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(slide.label, color = Color.White, fontSize = 44.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(8.dp))
                    Text("${String.format(Locale.getDefault(), "%,d", slide.minutes)} minutes of music", color = Color.White.copy(alpha = 0.9f), fontSize = 18.sp)
                }
                is MSlide.Minutes -> {
                    Text("YOU LISTENED", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("${String.format(Locale.getDefault(), "%,d", slide.minutes)}", color = Color.White, fontSize = 64.sp, fontWeight = FontWeight.Black)
                    Text("minutes", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))
                    Text("${slide.plays} plays . ${slide.artists} artists", color = Color.White.copy(alpha = 0.9f), fontSize = 18.sp)
                }
                is MSlide.TopSong -> {
                    Text("TOP SONG", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    AsyncImage(
                        model = slide.song.thumbnailUrl?.artworkAt(600),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(220.dp).clip(RoundedCornerShape(20.dp)).background(Color.White.copy(alpha = 0.1f)),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(slide.song.title, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(slide.song.artist, color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.height(8.dp))
                    Text("${slide.plays} plays", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.clip(CircleShape).background(Color.White).clickable { onPlaySong(slide.song, "Monthly Recap") }.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.Black)
                        Spacer(Modifier.size(6.dp))
                        Text("Play it", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                is MSlide.TopArtist -> {
                    Text("TOP ARTIST", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(16.dp))
                    AsyncImage(
                        model = slide.art?.artworkAt(600),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(220.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)),
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(slide.name, color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text("${slide.plays} plays this month", color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
                }
                is MSlide.Outro -> {
                    Text("THAT WAS ${slide.label.uppercase(Locale.ROOT)}", color = Color.White.copy(alpha = 0.85f), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Same time next month?", color = Color.White, fontSize = 34.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(18.dp))
                    if (slide.topSong != null) {
                        Row(
                            modifier = Modifier.clip(CircleShape).background(Color.White).clickable { onPlaySong(slide.topSong, "Monthly Recap") }.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(Icons.Rounded.PlayArrow, contentDescription = null, tint = Color.Black)
                            Spacer(Modifier.size(6.dp))
                            Text("Play your top song", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                    Row(
                        modifier = Modifier.clip(CircleShape).background(Color.White.copy(alpha = 0.18f)).clickable(onClick = onShare).padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.IosShare, contentDescription = null, tint = Color.White)
                        Spacer(Modifier.size(6.dp))
                        Text("Share this recap", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlyRecapSharePage(summary: ReplaySummary, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var poster by remember { mutableStateOf<Bitmap?>(null) }
    var saved by remember { mutableStateOf(false) }
    LaunchedEffect(summary) {
        poster = runCatching { renderMonthlyPoster(summary) }.getOrNull()
    }
    Box(Modifier.fillMaxSize().background(Color(0xFF061018))) {
        Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Share your recap", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                IconButton(onClick = onDismiss) { Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Color.White) }
            }
            Spacer(Modifier.height(12.dp))
            Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                val image = poster
                if (image != null) {
                    Image(bitmap = image.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier.fillMaxWidth().aspectRatio(9f / 16f).clip(RoundedCornerShape(16.dp)))
                } else {
                    CircularProgressIndicator(color = Color.White)
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
                ) { val image = poster ?: return@ShareAction; scope.launch { saved = saveToGallery(context, image, summary.label, "kisetsu-recap") } }
                ShareAction(
                    label = "Share",
                    icon = Icons.Rounded.IosShare,
                    accent = true,
                    enabled = ready,
                    modifier = Modifier.weight(1f),
                ) {
                    val image = poster ?: return@ShareAction
                    scope.launch {
                        val uri = cacheForSharing(context, image, "recap.png") ?: return@launch
                        context.startActivity(Intent.createChooser(sendIntent(uri), "Share your recap"))
                    }
                }
            }
        }
    }
}

private suspend fun renderMonthlyPoster(summary: ReplaySummary): Bitmap = withContext(Dispatchers.Default) {
    val w = 1080; val h = 1920
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val c = Canvas(bmp)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = LinearGradient(0f, 0f, w.toFloat(), h.toFloat(), intArrayOf(0xFF2563EB.toInt(), 0xFF06B6D4.toInt(), 0xFF14B8A6.toInt()), null, Shader.TileMode.CLAMP)
    }
    c.drawRect(0f, 0f, w.toFloat(), h.toFloat(), paint)
    val bold = Typeface.create("sans-serif-black", Typeface.BOLD)
    val reg = Typeface.create("sans-serif", Typeface.NORMAL)
    fun draw(t: String, x: Float, y: Float, size: Float, color: Int, tf: Typeface = bold) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color; textSize = size; typeface = tf }
        c.drawText(t, x, y, p)
    }
    fun fit(t: String, size: Float, maxW: Float, tf: Typeface = bold): String {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { textSize = size; typeface = tf }
        if (p.measureText(t) <= maxW) return t
        var s = t
        while (s.isNotEmpty() && p.measureText("$s.") > maxW) s = s.dropLast(1)
        return "$s."
    }
    draw("KISETSU MONTHLY RECAP", 80f, 170f, 34f, 0xE6FFFFFF.toInt(), Typeface.create("sans-serif", Typeface.BOLD))
    draw(fit(summary.label, 120f, 920f), 80f, 360f, 120f, 0xFFFFFFFF.toInt())
    var y = 520f
    fun stat(label: String, value: String) {
        draw(label.uppercase(Locale.ROOT), 80f, y, 28f, 0xCCFFFFFF.toInt(), Typeface.create("sans-serif", Typeface.BOLD))
        y += 70f
        draw(fit(value, 58f, 920f), 80f, y, 58f, 0xFFFFFFFF.toInt())
        y += 90f
        c.drawRect(80f, y - 40f, 1000f, y - 37f, Paint().apply { color = 0x33FFFFFF })
    }
    stat("Minutes listened", "${String.format(Locale.getDefault(), "%,d", summary.minutes)} min")
    stat("Top artist", summary.artists.firstOrNull()?.title ?: "-")
    stat("Top song", summary.songs.firstOrNull()?.let { "${it.song.title} . ${it.song.artist}" } ?: "-")
    stat("Different songs", "${summary.distinctSongs}")
    draw("KISETSU  .  ${summary.label.uppercase(Locale.ROOT)}", 80f, h - 110f, 30f, 0xCCFFFFFF.toInt(), Typeface.create("sans-serif", Typeface.BOLD))
    bmp
}
