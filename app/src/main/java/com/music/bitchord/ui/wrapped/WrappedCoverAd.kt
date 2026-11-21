package com.music.bitchord.ui.wrapped

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Whether this track's player cover should be the Wrapped advert. One in five,
 * keyed on the video id so a track keeps its answer while it is paused, seeked
 * or recomposed - a random roll per frame would flicker.
 */
fun showsWrappedCoverAd(videoId: String): Boolean {
    if (videoId.isBlank()) return false
    return (videoId.hashCode().toLong() and 0xFFFFFFFFL) % 5L == 0L
}

/**
 * The Kisetsu Wrapped advert that takes the playlist cover's place in the
 * player every fifth track. Tapping it opens Wrapped - which is what the ad is
 * for - and it sits inside the sleeve's own square and corners, so it reads as
 * artwork that happens to be an invitation.
 */
@Composable
fun WrappedCoverAd(
    onOpen: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(4.dp))
            .clickable(onClick = onOpen),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "KISETSU",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "WRAPPED",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.5).sp,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Your year in music",
                color = Color.White.copy(alpha = 0.86f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Tap to play your Wrapped",
                color = Color.Black,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .background(Color.White, RoundedCornerShape(50))
                    .padding(horizontal = 16.dp, vertical = 9.dp),
            )
        }
    }
}
