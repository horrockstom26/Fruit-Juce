package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.data.stats.ListeningStats
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@Composable
fun KisetsuMomentsStrip(
    onOpenChronicle: () -> Unit,
    onOpenOnThisDay: () -> Unit,
    onOpenPersona: () -> Unit,
    onOpenDiscoveryCrate: () -> Unit,
    onOpenEras: () -> Unit,
    onOpenMonthlyRecap: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val recapMonth by produceState<YearMonth?>(initialValue = null) {
        value = runCatching {
            val today = LocalDate.now()
            val candidates = listOf(YearMonth.from(today).minusMonths(1), YearMonth.from(today))
            candidates.firstOrNull { ym ->
                val s = ListeningStats.summaryForMonth(ym)
                !s.isEmpty
            }
        }.getOrNull()
    }
    val hasOnThisDay by produceState(initialValue = false) {
        value = runCatching { ListeningStats.onThisDay().isNotEmpty() }.getOrDefault(false)
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
        Text(
            "Moments",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                MomentChip(
                    title = "Chronicle",
                    subtitle = "Your story",
                    icon = Icons.Rounded.AutoStories,
                    onClick = onOpenChronicle,
                )
            }
            if (hasOnThisDay) {
                item {
                    MomentChip(
                        title = "On This Day",
                        subtitle = "Your memories",
                        icon = Icons.Rounded.History,
                        onClick = onOpenOnThisDay,
                    )
                }
            }
            recapMonth?.let { ym ->
                item {
                    val label = ym.month.name.lowercase(Locale.ROOT).replaceFirstChar { it.uppercase(Locale.ROOT) }
                    MomentChip(
                        title = "Monthly Recap",
                        subtitle = label,
                        icon = Icons.Rounded.CalendarMonth,
                        onClick = { onOpenMonthlyRecap(ym) },
                    )
                }
            }
            item {
                MomentChip(
                    title = "Persona",
                    subtitle = "Your sound",
                    icon = Icons.Rounded.AutoAwesome,
                    onClick = onOpenPersona,
                )
            }
            item {
                MomentChip(
                    title = "Discovery Crate",
                    subtitle = "New finds",
                    icon = Icons.Rounded.Inventory2,
                    onClick = onOpenDiscoveryCrate,
                )
            }
            item {
                MomentChip(
                    title = "Your Eras",
                    subtitle = "Chapters",
                    icon = Icons.Rounded.AutoStories,
                    onClick = onOpenEras,
                )
            }
        }
    }
}

@Composable
private fun MomentChip(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(10.dp))
        Column {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
        }
    }
}
