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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.music.bitchord.data.model.HEADER_ART_PX
import com.music.bitchord.data.model.artworkAt
import com.music.bitchord.ui.icons.BitChordIcons

/**
 * "Your Personal Station" card, at the head of the picks row.
 *
 * Clones the hero card's exact shape and size (260×325 rectangle, artwork
 * top, gradient text panel bottom) so it sits in line with the ordinary
 * Top Picks for You cards — never a square, never a different shape.
 *
 * v4.2: the station card now mirrors the hero card treatment, drops the play
 * button, and names the station "Your Personal Station".
 */
@Composable
fun TomsStationCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    ownerName: String = "",
    artworkUrl: String? = null,
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .width(260.dp)
            .height(325.dp)
            .shadow(12.dp, shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
    ) {
        // Cover of the top song behind the copy, so the card is a shelf
        // entry rather than a flat slab.
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
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.75f),
                            ),
                        ),
                    ),
            )
        }
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
                text = "YOUR PERSONAL STATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp,
                ),
                color = Color.White.copy(alpha = 0.78f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "A Mix That Keeps Itself Endless",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
