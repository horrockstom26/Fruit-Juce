package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.R
import com.music.bitchord.ui.components.BottomFadeScrim
import dev.chrisbanes.haze.HazeState
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

@Composable
fun PremiumScreen(
    contentPadding: PaddingValues,
    hazeState: HazeState? = null,
    withMiniPlayer: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val regionPrice = remember {
        runCatching {
            val locale = Locale.getDefault()
            val currency = Currency.getInstance(locale)
            val fmt = NumberFormat.getCurrencyInstance(locale)
            fmt.maximumFractionDigits = 0
            "${fmt.format(1)} / month after trial"
        }.getOrElse { "$1 / month after trial" }
    }

    val goldTop = Color(0xFF6B4E00)
    val goldMid = Color(0xFFB8860B)
    val goldGlow = Color(0xFFFFD54A)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to goldTop,
                        0.22f to Color(0xFF3A2A00),
                        0.55f to Color(0xFF14110A),
                        1f to Color.Black,
                    )
                )
                .padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item {
                Spacer(Modifier.height(28.dp))
                Icon(
                    painter = painterResource(R.drawable.ic_premium_note),
                    contentDescription = null,
                    tint = goldGlow,
                    modifier = Modifier.size(54.dp),
                )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Premium",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = goldGlow,
                letterSpacing = 0.5.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Listen without limits. You're Premium.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Your plan includes complimentary Premium access with unlimited listening features. Premium features are provided as part of your Kisetsu experience; availability may vary by region, device, catalog, and network conditions. This is not a paid subscription and no payment is required.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 22.dp),
            )
            Spacer(Modifier.height(26.dp))
        }

        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp)) {
                Text(
                    text = "Available plans",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFF2A2A2A))
                        .border(1.dp, goldMid.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                        .padding(18.dp),
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.CheckCircle, contentDescription = null, tint = Color(0xFF1DB954), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "You are on Free Premium",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1DB954),
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Free for 100 years",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = regionPrice,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.7f),
                        )
                        Spacer(Modifier.height(14.dp))
                        val features = listOf(
                            "Unlimited skips and on-demand playback",
                            "Unlimited songs, albums, artists, and playlists",
                            "No audio ads interrupting your music",
                            "High-quality streaming options where available",
                            "Background play and seamless queue control",
                            "Offline downloads and library access features",
                            "Podcasts and radio in Discover",
                            "Personalized mixes, stats, and Kisetsu Wrapped insights",
                            "Cross-device listening experience and premium-style controls",
                            "Priority access to new Kisetsu features as they roll out",
                        )
                        features.forEach { f ->
                            Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                                Text("•", color = goldGlow, fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 10.dp))
                                Text(f, style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.9f), lineHeight = 20.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(28.dp))
            }
            }
        }

        // Always-visible progressive fade (works even if haze blur is unavailable)
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    Brush.verticalGradient(
                        0f to Color(0xFF6B4E00).copy(alpha = 0.92f),
                        0.35f to Color(0xFF6B4E00).copy(alpha = 0.55f),
                        0.7f to Color(0xFF3A2A00).copy(alpha = 0.18f),
                        1f to Color.Transparent,
                    )
                )
        )
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(if (withMiniPlayer) 220.dp else 170.dp)
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.35f to Color.Black.copy(alpha = 0.22f),
                        0.7f to Color.Black.copy(alpha = 0.62f),
                        1f to Color.Black.copy(alpha = 0.96f),
                    )
                )
        )
        BottomFadeScrim(
            pageColor = Color.Transparent,
            hazeState = hazeState,
            topEdge = true,
            blurAtTop = true,
            heightOverride = 170.dp,
            forceBlur = true,
            blurTintPeak = 0f,
            modifier = Modifier.align(Alignment.TopCenter),
        )
        BottomFadeScrim(
            withMiniPlayer = withMiniPlayer,
            pageColor = Color.Transparent,
            heightOverride = if (withMiniPlayer) 280.dp else 210.dp,
            forceBlur = true,
            blurTintPeak = 0f,
            hazeState = hazeState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
