package com.music.bitchord.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.HEADER_ART_PX
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.ui.icons.BitChordIcons

/**
 * "Tom's Station", as a card at the head of the picks row.
 *
 * v9.0 rebrand: Kisetsu Radio is the station, and the station belongs to the
 * listener — the name carries their own first name. The gradient is this
 * app's own red-to-orange wash, warm enough to read as a hand-picked shelf
 * without borrowing any other service's marks. Sized like the hero card so
 * it leads the picks row in line, which is how a "made for you" shelf opens.
 */
@Composable
fun TomsStationCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    ownerName: String = "Tom",
    artworkUrl: String? = null,
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .width(260.dp)
            .height(260.dp)
            .shadow(12.dp, shape)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFA2D48),
                        Color(0xFFFF6D3A),
                    ),
                ),
            )
            .clickable(onClick = onClick),
    ) {
        // Cover of the top song behind the copy, so the card is a shelf
        // entry rather than a flat orange slab.
        if (artworkUrl != null) {
            AsyncImage(
                model = artworkUrl.artworkAt(HEADER_ART_PX),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.55f),
                            ),
                        ),
                    ),
            )
        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(18.dp),
        ) {
            Text(
                text = ownerName.uppercase() + "'S STATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp,
                ),
                color = Color.White.copy(alpha = 0.78f),
            )
            Text(
                text = "Made for You",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
            Text(
                text = "A mix that keeps itself · endless",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 1,
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(18.dp)
                .size(46.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = BitChordIcons.Play,
                contentDescription = null,
                tint = Color(0xFFFA2D48),
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/**
 * Kisetsu Radio, rebranded in v9.0 as "Tom's Station" — the personal station
 * keeps its home at the top of Create and of Library as a full-width plate.
 *
 * The station used to open the home screen as the biggest thing on it, which
 * made the front page a door to one feature. It now lives where things are
 * made and kept — the top of Create and of Library — as a full-width plate
 * with the mix's own first cover behind it, so the page can still play it in
 * one tap without the home feed leading with it.
 */
@Composable
fun KisetsuRadioBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    artworkUrl: String? = null,
) {
    val shape = RoundedCornerShape(12.dp)
    val tone = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .width(260.dp)
            .height(325.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
    ) {
        // Full-bleed artwork as the base layer.
        AsyncImage(
            model = artworkUrl.artworkAt(HEADER_ART_PX),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        // Bottom text panel: smooth vertical gradient sampled from the cover,
        // with the song name (bold) and artist name (smaller) beneath.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.35f),
                            Color.Black.copy(alpha = 0.75f),
                        ),
                    ),
                )
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "TOM'S STATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp,
                ),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "Your personal station",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "A mix that keeps itself · endless",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
                .size(32.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = BitChordIcons.Play,
                contentDescription = null,
                tint = Color(0xFFFA2D48),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
