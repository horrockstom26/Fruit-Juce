package com.music.bitchord.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.bitchord.R
import com.music.bitchord.data.settings.AppSettings
import kotlin.math.roundToInt

/**
 * Apple Music Sing, restyled for iOS 18.
 *
 * One engine, no mode selector: the band-limited vocal cut that spares bass, air
 * and drum transients. The slider runs from Full Vocals (100%) down to No Vocals
 * (0%); the read-out and the helper line are Apple's, verbatim in shape.
 */
@Composable
fun SingSheet(onDismiss: () -> Unit) {
    val level by AppSettings.singLevel.collectAsStateWithLifecycle()
    // The unified engine is the voice gate - the safest on a dense modern mix.
    // Set it once on open so a listener who never touched the mode selector
    // still gets the best remover rather than whatever was last persisted.
    androidx.compose.runtime.LaunchedEffect(Unit) {
        AppSettings.setSingMethod(com.music.bitchord.playback.SingMethod.VOICE_GATE)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1C1C1E).copy(alpha = 0.95f))
            .padding(horizontal = 24.dp)
            .padding(top = 12.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Drag handle.
        Box(
            modifier = Modifier
                .size(width = 36.dp, height = 5.dp)
                .clip(RoundedCornerShape(2.5.dp))
                .background(Color.White.copy(alpha = 0.3f)),
        )
        Spacer(Modifier.height(18.dp))
        Icon(
            imageVector = if (level > 0.001f) Icons.Rounded.Mic else Icons.Rounded.MicOff,
            contentDescription = null,
            tint = Color(0xFFFA2D48),
            modifier = Modifier.size(40.dp),
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Vocals at ${(level * 100).roundToInt()}%",
            style = androidx.compose.ui.text.TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFA2D48),
            ),
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Turn the lead vocal down and sing over the track.",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFEBEBF5).copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Slider(
            value = level,
            onValueChange = { AppSettings.setSingLevel(it) },
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = Color.White,
                activeTrackColor = Color(0xFFFA2D48),
                inactiveTrackColor = Color.White.copy(alpha = 0.2f),
            ),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Full Vocals",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFEBEBF5).copy(alpha = 0.6f),
            )
            Text(
                text = "No Vocals",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFEBEBF5).copy(alpha = 0.6f),
            )
        }
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = {
            AppSettings.setSingLevel(0f)
            onDismiss()
        }) {
            Text(stringResource(R.string.sing_turn_off))
        }
    }
}
