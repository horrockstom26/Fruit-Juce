package com.music.bitchord.ui.screens

import com.music.bitchord.R

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.music.bitchord.data.AppUpdateChecker
import com.music.bitchord.data.model.Account
import com.music.bitchord.ui.icons.BitChordIcons

/**
 * Fruit Juce v5.2: the Account tab, a fullscreen page replacing the
 * top-right avatar's dropdown.
 *
 * Everything about the session lives here: who is signed in (or the
 * invitation to be), the account's own pages, app updates — including the
 * manual check the old top-bar badge used to cover — and the escape hatch
 * that puts every setting back the way the app shipped.
 */
@Composable
fun AccountHubScreen(
    signedIn: Boolean,
    account: Account?,
    update: AppUpdateChecker.UpdateInfo?,
    version: String,
    onManageAccount: () -> Unit,
    onSettings: () -> Unit,
    onViewAccount: () -> Unit,
    onCheckForUpdate: () -> Unit,
    onUpdateClick: () -> Unit,
    onResetSettings: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val tone = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.account),
            style = MaterialTheme.typography.headlineMedium,
            color = tone.onBackground,
            modifier = Modifier.padding(top = 6.dp),
        )
        Spacer(Modifier.height(16.dp))

        // The account card mirrors the old avatar: the photo, the name, and —
        // below it now — everything that used to be hidden under the dropdown.
        AccountCard(signedIn = signedIn, account = account)

        Spacer(Modifier.height(14.dp))

        HubRow(
            label = stringResource(R.string.manage_account),
            onClick = onManageAccount,
        )
        Spacer(Modifier.height(8.dp))
        HubRow(
            label = stringResource(R.string.settings),
            onClick = onSettings,
        )
        Spacer(Modifier.height(8.dp))
        HubRow(
            label = stringResource(R.string.view_account),
            onClick = onViewAccount,
        )

        Spacer(Modifier.height(20.dp))
        AppUpdatesCard(
            update = update,
            version = version,
            onCheckForUpdate = onCheckForUpdate,
            onUpdateClick = onUpdateClick,
        )

        Spacer(Modifier.height(16.dp))
        ResetCard(onResetSettings)
    }
}

@Composable
private fun AccountCard(signedIn: Boolean, account: Account?) {
    val tone = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tone.surfaceContainerHigh, RoundedCornerShape(12.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (signedIn && account?.thumbnailUrl != null) {
            AsyncImage(
                model = account.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape),
            )
        } else {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(tone.primary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = BitChordIcons.TabAccount,
                    contentDescription = null,
                    tint = tone.onPrimary,
                    modifier = Modifier.size(26.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(
                text = when {
                    signedIn && account != null -> account.name
                    else -> stringResource(R.string.account_signed_out)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = tone.onSurface,
                maxLines = 1,
            )
            Text(
                text = when {
                    signedIn && account != null -> account.email
                    else -> stringResource(R.string.account_sign_in_hint)
                },
                style = MaterialTheme.typography.bodySmall,
                color = tone.onSurfaceVariant,
                maxLines = 1,
            )
        }
    }
}

/** One menu row of the old dropdown, restyled to a full-width pill. */
@Composable
private fun HubRow(
    label: String,
    onClick: () -> Unit,
) {
    val tone = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tone.surfaceContainerHigh, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = tone.onSurface,
        )
        Icon(
            imageVector = BitChordIcons.ChevronRight,
            contentDescription = null,
            tint = tone.onSurfaceVariant,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun AppUpdatesCard(
    update: AppUpdateChecker.UpdateInfo?,
    version: String,
    onCheckForUpdate: () -> Unit,
    onUpdateClick: () -> Unit,
) {
    val tone = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(tone.surfaceContainerHigh, RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.app_updates),
                style = MaterialTheme.typography.titleMedium,
                color = tone.onSurface,
            )
            // v7.5: no version number here any more. The build's own number is
            // a developer's fact; the line under the card's title is the one
            // place in the app selling where it came from, so it says so.
            Text(
                text = "Made in England 🌸",
                style = MaterialTheme.typography.bodySmall,
                color = tone.onSurfaceVariant,
            )
        }
        Spacer(Modifier.height(8.dp))
        when (update) {
            null -> {
                // No news is the good kind: the app is current and a manual
                // check remains available for the moments you doubt it.
                Text(
                    text = stringResource(R.string.up_to_date),
                    style = MaterialTheme.typography.bodySmall,
                    color = tone.onSurfaceVariant,
                )
            }
            else -> {
                Text(
                    text = stringResource(
                        R.string.update_available,
                        update.version,
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = tone.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onUpdateClick() },
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.check_for_update),
            style = MaterialTheme.typography.titleSmall,
            color = tone.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onCheckForUpdate() },
        )
    }
}

@Composable
private fun ResetCard(onReset: () -> Unit) {
    val tone = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(tone.surfaceContainerHigh, RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Text(
            text = stringResource(R.string.reset_all_settings),
            style = MaterialTheme.typography.titleMedium,
            color = tone.error,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onReset() },
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.reset_all_settings_summary),
            style = MaterialTheme.typography.bodySmall,
            color = tone.onSurfaceVariant,
        )
    }
}
