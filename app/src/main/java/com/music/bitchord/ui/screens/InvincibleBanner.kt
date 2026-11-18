package com.music.bitchord.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.ui.theme.AtomEvePink
import com.music.bitchord.ui.theme.InvincibleBlue
import com.music.bitchord.ui.theme.InvincibleYellow
import com.music.bitchord.ui.theme.OmniManRed
import com.music.bitchord.ui.theme.invincibleAccent
import com.music.bitchord.ui.theme.invincibleSecondary

/**
 * The limited-edition Invincible banner that tops the Home feed.
 *
 * Picking a character repaints the whole app — accent, page tint and the
 * lettering — see [com.music.bitchord.ui.theme.BitChordTheme]. The banner
 * itself always wears the *selected* skin's suit colours, so it doubles as the
 * preview. Hidden once a skin is on and the listener has dismissed nothing —
 * it stays, with an Off chip, because there is no other way back.
 */
@Composable
fun InvincibleThemeBanner(modifier: Modifier = Modifier) {
    val active by AppSettings.invincibleTheme.collectAsStateWithLifecycle()
    val accent = invincibleAccent(active) ?: InvincibleYellow
    val secondary = if (invincibleAccent(active) != null) invincibleSecondary(active) else InvincibleBlue
    val onAccent = if (accent.luminance() > 0.6f) Color.Black else Color.White

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    listOf(accent, secondary),
                ),
            )
            .border(2.dp, Color.Black.copy(alpha = 0.55f), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Text(
            text = "LIMITED EDITION",
            color = Color.Black.copy(alpha = 0.62f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Invincible theme",
            color = onAccent,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp,
            lineHeight = 28.sp,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = if (invincibleAccent(active) != null) {
                "Wearing ${skinName(active)}. Every colour and the lettering follow the suit."
            } else {
                "Make every colour and letter in the app match the suit. Pick your hero."
            },
            color = onAccent.copy(alpha = 0.82f),
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(Modifier.height(12.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SkinChip("invincible", "Invincible", InvincibleYellow, Color(0xFF1B1B1F), active)
            SkinChip("atom_eve", "Atom Eve", AtomEvePink, Color.White, active)
            SkinChip("omni_man", "Omni-Man", OmniManRed, Color.White, active)
            if (invincibleAccent(active) != null) {
                Spacer(Modifier.width(2.dp))
                BoxedOffChip()
            }
        }
    }
}

@Composable
private fun SkinChip(
    key: String,
    label: String,
    color: Color,
    textColor: Color,
    active: String,
) {
    val selected = active == key
    Text(
        text = label,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) Color.White else Color.Black.copy(alpha = 0.35f),
                shape = RoundedCornerShape(50),
            )
            .clickable { AppSettings.setInvincibleTheme(key) }
            .padding(horizontal = 12.dp, vertical = 7.dp),
    )
}

@Composable
private fun BoxedOffChip() {
    Text(
        text = "Off",
        color = Color.White,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { AppSettings.setInvincibleTheme("off") }
            .padding(horizontal = 10.dp, vertical = 7.dp),
    )
}

private fun skinName(key: String): String = when (key) {
    "invincible" -> "Invincible"
    "atom_eve" -> "Atom Eve"
    "omni_man" -> "Omni-Man"
    else -> ""
}
