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
            .height(325.dp)
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
        // The mix's own first cover, if it is playing — dimmed to stay under
        // the copy, exactly as the banner plate does it.
        if (artworkUrl != null) {
            AsyncImage(
                model = artworkUrl.artworkAt(HEADER_ART_PX),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.35f,
                modifier = Modifier.fillMaxSize(),
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
    val shape = RoundedCornerShape(18.dp)
    val tone = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(148.dp)
            .clip(shape)
            .background(Brush.linearGradient(listOf(tone.primary, tone.tertiary)))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = artworkUrl.artworkAt(HEADER_ART_PX),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        // The scrim is horizontal rather than vertical: the copy sits bottom
        // left and the play button right, so the art has to fall away towards
        // the text without disappearing under the button.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.74f),
                            Color.Black.copy(alpha = 0.28f),
                            Color.Black.copy(alpha = 0.12f),
                        ),
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, end = 84.dp, bottom = 16.dp),
        ) {
            Text(
                text = "TOM'S STATION",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.4.sp,
                ),
                color = Color.White.copy(alpha = 0.72f),
            )
            Text(
                text = "Your personal station",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
            Text(
                text = "A mix that keeps itself · endless",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.78f),
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
                .size(46.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = BitChordIcons.Play,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}
