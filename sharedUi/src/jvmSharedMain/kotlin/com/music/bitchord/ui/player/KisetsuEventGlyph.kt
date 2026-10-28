package com.music.bitchord.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.music.bitchord.ui.haptics.Haptic
import com.music.bitchord.ui.haptics.rememberHaptics

/**
 * The like button wearing the season's face: a little jack-o'-lantern through
 * October, the heart again from 1 November.
 *
 * Drawn as text rather than a vector so it keeps the platform's own emoji
 * artwork — the same pumpkin the keyboard offers — instead of a hand-drawn
 * one. The tap target, haptic and content description are the heart's, so
 * only the picture changes.
 */
@Composable
fun PumpkinLikeButton(
    label: String,
    onClick: () -> Unit,
    active: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    fontSize: TextUnit = 22.sp,
) {
    val haptics = rememberHaptics()
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable {
                haptics.play(if (active) Haptic.ToggleOff else Haptic.ToggleOn)
                onClick()
            }
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "🎃", fontSize = fontSize)
    }
}
