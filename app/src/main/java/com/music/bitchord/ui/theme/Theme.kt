package com.music.bitchord.ui.theme

import android.content.Context
import android.graphics.Typeface
import java.io.File
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.bitchord.R
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.ui.LocalDynamicAccent
import com.music.bitchord.ui.SpotifyGreen
import androidx.compose.ui.graphics.luminance

// Apple Music's signature red. No longer the primary accent, but kept for the
// spots (Replay's rank badge) that want that specific red regardless of theme.
val AccentRed = Color(0xFFFA2D48)

/** Kisetsu's own cherry-blossom pink — the app's default accent from v7.2. */
val KisetsuBlossom = Color(0xFFF06292)

/** A deeper blossom for light mode, where the pastel pink needs more weight. */
val KisetsuBlossomDeep = Color(0xFFD81B60)

/** Fruit Juce's old orange, kept as a preset. */
val FruitJuceOrange = Color(0xFFFF9500)

/** The accent a preset name resolves to; unknown names land on blossom. */
fun accentForPreset(name: String, customArgb: Int): Color = when (name) {
    "spotify" -> SpotifyGreen
    "orange" -> FruitJuceOrange
    "apple" -> AccentRed
    "custom" -> Color(customArgb)
    else -> KisetsuBlossom
}

private val DarkColors = darkColorScheme(
    primary = KisetsuBlossom,
    onPrimary = Color.Black,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color(0xFF0D0D0F),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1C1C1E),
    onSurfaceVariant = Color(0xFF8E8E93),
    outline = Color(0xFF2C2C2E),
)

private val LightColors = lightColorScheme(
    primary = KisetsuBlossomDeep,
    onPrimary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF7F7F9),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFF2F2F7),
    onSurfaceVariant = Color(0xFF6E6E73),
    outline = Color(0xFFE5E5EA),
)

/**
 * The Kisetsu Apple-look geometry.
 *
 * v7.4 squared it off. v5's round-everything pass had drifted a few degrees
 * past the reference: Apple Music's album tiles and cards are distinctly
 * squarer than the pills and discs that sit on them, and that contrast is a
 * large part of why its grids read as tidy. These are the same scale, pulled
 * back — and the artwork radii below came down with it.
 */
private val JuceShapes = Shapes(
    extraSmall = RoundedCornerShape(5.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(11.dp),
    large = RoundedCornerShape(15.dp),
    extraLarge = RoundedCornerShape(20.dp),
)

/**
 * Kisetsu v7.2: Inter Tight for headings and Inter for text — the pair behind
 * the app's new typography, replacing SF Pro. Both ship regular + bold; the
 * intermediate weights Compose asks for resolve to the nearest of the two.
 */
val InterFamily = FontFamily(
    Font(R.font.inter_regular, FontWeight.W400),
    Font(R.font.inter_bold, FontWeight.W700),
)

val InterTightFamily = FontFamily(
    Font(R.font.inter_tight_regular, FontWeight.W400),
    Font(R.font.inter_tight_bold, FontWeight.W700),
)

/**
 * Spotify Mix, the preset font the user supplied. Three real weights now:
 * regular for body text, medium for grid titles and metadata, bold for song
 * and album names — the shape Spotify's own typography takes.
 */
val SpotifyMixFamily = FontFamily(
    Font(R.font.spotify_mix_regular, FontWeight.W400),
    Font(R.font.spotify_mix_medium, FontWeight.W500),
    Font(R.font.spotify_mix_medium, FontWeight.W600),
    Font(R.font.spotify_mix_bold, FontWeight.W700),
    Font(R.font.spotify_mix_bold, FontWeight.W800),
)

/**
 * Spotify Mix Extrabold — the old single heavy face. No longer the default:
 * turned back on by the "Bold text" toggle, which sets every weight to it.
 */
val SpotifyMixExtraBoldFamily = FontFamily(
    Font(R.font.spotify_mix_extrabold, FontWeight.W400),
    Font(R.font.spotify_mix_extrabold, FontWeight.W500),
    Font(R.font.spotify_mix_extrabold, FontWeight.W600),
    Font(R.font.spotify_mix_extrabold, FontWeight.W700),
    Font(R.font.spotify_mix_extrabold, FontWeight.W800),
)

/**
 * SF Pro Display, the face Apple Music itself is set in — kept for the Replay
 * poster renderer, which paints its own share artwork outside the theme.
 */
val SFProDisplay = FontFamily(
    Font(R.font.sf_pro_display_regular, FontWeight.W400),
    Font(R.font.sf_pro_display_medium, FontWeight.W500),
    Font(R.font.sf_pro_display_semibold, FontWeight.W600),
    Font(R.font.sf_pro_display_bold, FontWeight.W700),
    Font(R.font.sf_pro_display_heavy, FontWeight.W800),
)

// Heavy, tight typography — the backbone of the Apple Music look. Kisetsu v7.3
// rides the platform sans (the same face the Spotify wardrobe uses) rather than
// a bundled family: one font everywhere, no extra APK weight, and system
// language coverage for free.
private val BitChordTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.W800, fontSize = 34.sp, letterSpacing = (-0.8).sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.W800, fontSize = 30.sp, letterSpacing = (-0.7).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.W700, fontSize = 22.sp, letterSpacing = (-0.4).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.W700, fontSize = 20.sp, letterSpacing = (-0.3).sp),
    titleMedium = TextStyle(fontWeight = FontWeight.W600, fontSize = 16.sp, letterSpacing = (-0.2).sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.W400, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.W400, fontSize = 14.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.W600, fontSize = 12.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.W600, fontSize = 11.sp),
)

/** Applies [family] to every style in the scale, so nothing is left on Roboto. */
private fun Typography.withFamily(family: FontFamily) = Typography(
    displayLarge = displayLarge.copy(fontFamily = family),
    displayMedium = displayMedium.copy(fontFamily = family),
    displaySmall = displaySmall.copy(fontFamily = family),
    headlineLarge = headlineLarge.copy(fontFamily = family),
    headlineMedium = headlineMedium.copy(fontFamily = family),
    headlineSmall = headlineSmall.copy(fontFamily = family),
    titleLarge = titleLarge.copy(fontFamily = family),
    titleMedium = titleMedium.copy(fontFamily = family),
    titleSmall = titleSmall.copy(fontFamily = family),
    bodyLarge = bodyLarge.copy(fontFamily = family),
    bodyMedium = bodyMedium.copy(fontFamily = family),
    bodySmall = bodySmall.copy(fontFamily = family),
    labelLarge = labelLarge.copy(fontFamily = family),
    labelMedium = labelMedium.copy(fontFamily = family),
    labelSmall = labelSmall.copy(fontFamily = family),
)

private const val UPLOADED_FONT_PREFIX = "uploaded:"
private const val UPLOADED_FONT_DIR = "fonts"

/**
 * Resolves the listener's font choice to a [FontFamily]. Null means "keep the
 * bundled platform sans" — the system default.
 */
private fun appFontFamily(context: Context, choice: String): FontFamily? = when {
    choice == "system" -> null
    choice == "spotify_mix" -> SpotifyMixFamily
    choice.startsWith(UPLOADED_FONT_PREFIX) -> {
        val name = choice.removePrefix(UPLOADED_FONT_PREFIX)
        val file = File(context.filesDir, "$UPLOADED_FONT_DIR/$name")
        if (!file.exists()) null else runCatching {
            FontFamily(
                Font(file, FontWeight.W400),
                Font(file, FontWeight.W500),
                Font(file, FontWeight.W600),
                Font(file, FontWeight.W700),
                Font(file, FontWeight.W800),
            )
        }.getOrNull()
    }
    else -> null
}

@Composable
fun BitChordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // The wardrobe is read here rather than passed in by the host, so every
    // screen, sheet and dialog underneath changes clothes at once — including
    // ones composed outside MainActivity's own tree. Switching skins is a
    // state change, not a rebuild: the same navigation stack survives it.
    val appFontChoice by AppSettings.appFont.collectAsStateWithLifecycle()
    val boldText by AppSettings.boldText.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val appFontFamily = remember(appFontChoice, boldText) {
        // "Bold text": everything in the old extrabold face, one toggle.
        if (boldText && appFontChoice == "spotify_mix") {
            SpotifyMixExtraBoldFamily
        } else {
            appFontFamily(context, appFontChoice)
        }
    }
    val fontSizeScale by AppSettings.fontSizeScale.collectAsStateWithLifecycle()
    val fontScaleFactor = when (fontSizeScale) {
        "small" -> 0.85f
        "big" -> 1.3f
        else -> 1f
    }
    // Juce Droplet: when a cover has been sampled, the app's accent IS the
    // cover's colour. The Spotify nav bar and player are their own dark
    // surfaces, so they never read this.
    val juceDroplet by AppSettings.juceDroplet.collectAsStateWithLifecycle()
    val dropletColor by DynamicAccent.color.collectAsStateWithLifecycle()
    val accentPreset by AppSettings.accentPreset.collectAsStateWithLifecycle()
    val customAccentArgb by AppSettings.customAccentArgb.collectAsStateWithLifecycle()
    val baseColors = if (darkTheme) DarkColors else LightColors
    // Kisetsu v7.2: the accent is the cover's colour while dynamic accent is
    // on, otherwise the chosen preset — blossom pink unless the listener
    // picked otherwise.
    val presetAccent = accentForPreset(accentPreset, customAccentArgb)
    val effectiveAccent = if (juceDroplet && dropletColor != null) dropletColor!! else presetAccent
    val onAccent = if (effectiveAccent.luminance() > 0.55f) Color.Black else Color.White
    val colorScheme = baseColors.copy(
        primary = effectiveAccent,
        onPrimary = onAccent,
        secondary = effectiveAccent,
        onSecondary = onAccent,
    )
    val baseDensity = LocalDensity.current
    val scaledDensity = Density(baseDensity.density, baseDensity.fontScale * fontScaleFactor)
    CompositionLocalProvider(
        // Broadcast the accent that is actually being worn, so shared modules
        // that used to reach for Spotify's green reach for Kisetsu's accent.
        LocalDynamicAccent provides effectiveAccent,
        LocalDensity provides scaledDensity,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = BitChordTypography.let { base ->
                appFontFamily?.let { base.withFamily(it) } ?: base
            },
            shapes = JuceShapes,
            content = content,
        )
    }
}

/**
 * Draws the status and navigation bar glyphs dark or light.
 *
 * `enableEdgeToEdge()` decides this from the *system* dark-mode setting, which
 * is the wrong input the moment the in-app theme disagrees with it: Light theme
 * on a phone in dark mode left white icons on a white bar, invisible. The bars
 * have to follow the content the app is actually painting. The player supplies
 * its own stable light-icon value and paints contrast behind it; every other
 * surface follows the theme. Hence a parameter rather than reading it here.
 */
@Composable
fun SystemBarIcons(dark: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val window = findWindow(view) ?: return
    SideEffect {
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = dark
            isAppearanceLightNavigationBars = dark
        }
    }
}

