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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
 * The limited-edition skins strip that tops the Home feed.
 *
 * Three coloured tab buttons and a line of small text — deliberately not a big
 * banner. Picking a hero repaints the whole app (see
 * [com.music.bitchord.ui.theme.BitChordTheme]); the tabs wear the suit colours
 * so they double as the preview. The X only puts this strip away: it never
 * touches the selected skin.
 */
@Composable
fun InvincibleThemeBanner(modifier: Modifier = Modifier) {
    val active by AppSettings.invincibleTheme.collectAsStateWithLifecycle()
    val minimised by AppSettings.invincibleBannerMinimised.collectAsStateWithLifecycle()
    if (minimised) return
    val accent = invincibleAccent(active) ?: InvincibleYellow
    val secondary = if (invincibleAccent(active) != null) invincibleSecondary(active) else InvincibleBlue

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "LIMITED EDITION SKINS",
                color = accent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick = { AppSettings.setInvincibleBannerMinimised(true) },
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = "Hide the skins strip",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            SkinTab("invincible", "Invincible", InvincibleYellow, Color(0xFF16130A), active, Modifier.weight(1f))
            SkinTab("atom_eve", "Atom Eve", AtomEvePink, Color.White, active, Modifier.weight(1f))
            SkinTab("omni_man", "Omni-Man", OmniManRed, Color.White, active, Modifier.weight(1f))
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (invincibleAccent(active) != null) {
                "${skinName(active)} is on — every colour follows the suit."
            } else {
                "Small text, big colour: tap a hero to repaint the app."
            },
            color = if (invincibleAccent(active) != null) accent else secondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
        )
    }
}

@Composable
private fun SkinTab(
    key: String,
    label: String,
    color: Color,
    textColor: Color,
    active: String,
    modifier: Modifier = Modifier,
) {
    val selected = active == key
    Text(
        text = label,
        color = textColor,
        fontSize = 13.sp,
        fontWeight = FontWeight.Black,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) Color.White else Color.Transparent,
                shape = RoundedCornerShape(12.dp),
            )
            .clickable { AppSettings.setInvincibleTheme(key) }
            .padding(vertical = 11.dp),
    )
}

private fun skinName(key: String): String = when (key) {
    "invincible" -> "Invincible"
    "atom_eve" -> "Atom Eve"
    "omni_man" -> "Omni-Man"
    else -> ""
}
