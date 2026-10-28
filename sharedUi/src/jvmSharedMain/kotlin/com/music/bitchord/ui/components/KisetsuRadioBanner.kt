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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
 * Kisetsu Radio, as a banner rather than a feed card.
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
                text = "KISETSU RADIO",
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
