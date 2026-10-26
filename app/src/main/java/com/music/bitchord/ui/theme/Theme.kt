package com.music.bitchord.ui.theme

import android.app.Activity
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.bitchord.R
import com.music.bitchord.data.settings.AppSettings
import com.music.bitchord.data.settings.AppUi
import com.music.bitchord.ui.LocalSpotifySkin
import com.music.bitchord.ui.SpotifyGreen

// Apple Music's signature red. No longer the primary accent, but kept for the
// spots (Replay's rank badge) that want that specific red regardless of theme.
val AccentRed = Color(0xFFFA2D48)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFFF9500),
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
    primary = Color(0xFFE26600),
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
 * Spotify's green, the one fixed point of the skin. A dark-mode-first palette:
 * Spotify's canvas is pure black rather than the near-black Apple skin uses,
 * with its cards a shade above it and text either white or the familiar warm
 * grey. Light mode keeps the green but moves to the paper-white base Spotify
 * uses on its web and desktop light theme.
 */
private val SpotifyDarkColors = darkColorScheme(
    primary = SpotifyGreen,
    onPrimary = Color.Black,
    secondary = Color(0xFF1ED760),
    onSecondary = Color.Black,
    tertiary = Color(0xFF14833B),
    onTertiary = Color.White,
    background = Color(0xFF000000),
    onBackground = Color.White,
    surface = Color(0xFF121212),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1E1E1E),
    onSurfaceVariant = Color(0xFFB3B3B3),
    outline = Color(0xFF2A2A2A),
)

private val SpotifyLightColors = lightColorScheme(
    primary = Color(0xFF158A3E),
    onPrimary = Color.White,
    secondary = SpotifyGreen,
    onSecondary = Color.Black,
    tertiary = Color(0xFF14833B),
    onTertiary = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color(0xFFF7F7F7),
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFEFEFEF),
    onSurfaceVariant = Color(0xFF5A5A5A),
    outline = Color(0xFFD9D9D9),
)

/**
 * Spotify's geometry: cards are a touch squarer than Apple's, and its pills
 * stay fully rounded because their shape is local to the component rather
 * than global. Only the Spotify wardrobe reads this scale.
 */
private val SpotifyShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

/**
 * SF Pro Display, the face Apple Music itself is set in. Only the weights the
 * type scale actually asks for are bundled; Compose synthesises nothing, so a
 * missing weight would silently fall back to the nearest one shipped.
 */
val SFProDisplay = FontFamily(
    Font(R.font.sf_pro_display_regular, FontWeight.W400),
    Font(R.font.sf_pro_display_medium, FontWeight.W500),
    Font(R.font.sf_pro_display_semibold, FontWeight.W600),
    Font(R.font.sf_pro_display_bold, FontWeight.W700),
    Font(R.font.sf_pro_display_heavy, FontWeight.W800),
)

// Heavy, tight typography — the backbone of the Apple Music look.
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
).withFamily(SFProDisplay)

/**
 * The Spotify wardrobe's type: the platform sans, not SF Pro, because the
 * skin should read as a different product the moment it is switched on.
 * Spotify's own face is Circular; Roboto is the closest system-owned cousin,
 * and the scale leans heavier with less negative tracking than Apple's.
 */
private val SpotifyTypography = Typography(
    displayLarge = TextStyle(fontWeight = FontWeight.W800, fontSize = 30.sp, letterSpacing = (-0.6).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.W800, fontSize = 26.sp, letterSpacing = (-0.4).sp),
    displaySmall = TextStyle(fontWeight = FontWeight.W700, fontSize = 24.sp, letterSpacing = (-0.2).sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.W800, fontSize = 26.sp, letterSpacing = (-0.4).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.W700, fontSize = 22.sp, letterSpacing = (-0.2).sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.W700, fontSize = 20.sp),
    titleLarge = TextStyle(fontWeight = FontWeight.W700, fontSize = 20.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.W700, fontSize = 16.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.W600, fontSize = 14.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.W400, fontSize = 16.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.W400, fontSize = 14.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.W400, fontSize = 12.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.W700, fontSize = 14.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.W700, fontSize = 12.sp, letterSpacing = 0.2.sp),
    labelSmall = TextStyle(fontWeight = FontWeight.W700, fontSize = 11.sp, letterSpacing = 0.2.sp),
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

@Composable
fun BitChordTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // The wardrobe is read here rather than passed in by the host, so every
    // screen, sheet and dialog underneath changes clothes at once — including
    // ones composed outside MainActivity's own tree. Switching skins is a
    // state change, not a rebuild: the same navigation stack survives it.
    val appUi by AppSettings.appUi.collectAsStateWithLifecycle()
    val spotifySkin = appUi == AppUi.SPOTIFY
    CompositionLocalProvider(LocalSpotifySkin provides spotifySkin) {
        MaterialTheme(
            colorScheme = when {
                spotifySkin && darkTheme -> SpotifyDarkColors
                spotifySkin -> SpotifyLightColors
                darkTheme -> DarkColors
                else -> LightColors
            },
            typography = if (spotifySkin) SpotifyTypography else BitChordTypography,
            shapes = if (spotifySkin) SpotifyShapes else Shapes(),
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

