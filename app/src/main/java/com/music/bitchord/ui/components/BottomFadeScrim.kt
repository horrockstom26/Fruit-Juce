package com.music.bitchord.ui.components

import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.music.bitchord.data.settings.AppSettings
import dev.chrisbanes.haze.ExperimentalHazeApi
import dev.chrisbanes.haze.HazeInputScale
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials

/**
 * The strip the tab bar alone needs, above the gesture inset. Generous on
 * purpose: the ramp below spends most of its run at an alpha too low to see,
 * and that long invisible lead-in is what hides where the layer begins.
 */
private val FADE_HEIGHT = 180.dp

/**
 * Taller once the mini player is stacked on top of the tab bar — by the 56dp
 * the pill-shaped bar now stands, plus the 8dp gap above it and the run the
 * ramp wants over both.
 */
private val FADE_HEIGHT_WITH_MINI_PLAYER = 254.dp

/**
 * How many colour stops the ramp is cut into.
 *
 * A two-stop gradient is interpolated linearly in the shader, which is both the
 * wrong curve and — across a strip this tall in a near-flat colour — enough of
 * a straight line through the low alphas to band visibly on an 8-bit display.
 * Sampling the curve at intervals hands the shader short spans to interpolate
 * across instead, and the banding goes with them.
 */
private const val STOPS = 16

/**
 * The floor the floating bars sit on: fades page content out toward the edge.
 *
 * By default this is a flat colour gradient (cheap, always on). When
 * [progressiveBlur] is enabled in settings and [hazeState] is provided, it
 * renders a high-quality smooth progressive Haze blur (stronger at the edge,
 * fading to clear) with a soft color blend for readability — iOS-style.
 */
@OptIn(ExperimentalHazeApi::class, ExperimentalHazeMaterialsApi::class)
@Composable
fun BottomFadeScrim(
    modifier: Modifier = Modifier,
    withMiniPlayer: Boolean = false,
    /**
     * The colour to fade/blend toward — whatever the page is painting at the
     * foot of the screen.
     */
    pageColor: Color = MaterialTheme.colorScheme.background,
    /** When provided (and progressive blur is enabled), the strip blurs sampled content. */
    hazeState: HazeState? = null,
    /** Top-edge variant (typically rotated 180° by caller): sized for status/top bar, no nav insets. */
    topEdge: Boolean = false,
) {
    val reduceDynamicBlur by AppSettings.reduceDynamicBlur.collectAsStateWithLifecycle()
    val progressivePageBlur by AppSettings.progressivePageBlur.collectAsStateWithLifecycle()
    val useBlur = progressivePageBlur && !reduceDynamicBlur && hazeState != null

    val target = if (topEdge) {
        topBarHeight() + 56.dp
    } else {
        val inset = floatingBarInsets.asPaddingValues().calculateBottomPadding()
        inset + if (withMiniPlayer) FADE_HEIGHT_WITH_MINI_PLAYER else FADE_HEIGHT
    }
    val height by animateDpAsState(
        targetValue = target,
        animationSpec = tween(220),
        label = "bottomScrimHeight",
    )

    val scrimBrush = remember(pageColor, useBlur) {
        val peak = if (useBlur) 0.55f else 1f
        Brush.verticalGradient(
            colorStops = Array(STOPS) { i ->
                val t = i / (STOPS - 1f)
                t to pageColor.copy(alpha = peak * EaseInCubic.transform(t))
            },
        )
    }

    if (useBlur) {
        val haze = hazeState!!
        // Mask the blur layer so its top edge dissolves smoothly into the page.
        val topMask = remember {
            Brush.verticalGradient(
                0f to Color.Transparent,
                0.22f to Color.Black,
                1f to Color.Black,
            )
        }
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height),
        ) {
            Box(
                Modifier
                    .matchParentSize()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawRect(topMask, blendMode = BlendMode.DstIn)
                    }
                    .hazeEffect(state = haze, style = HazeMaterials.ultraThin(pageColor)) {
                        noiseFactor = 0f
                        inputScale = HazeInputScale.Fixed(0.33f)
                        // Clear at top of strip, strongest at bottom edge (near bars).
                        progressive = HazeProgressive.verticalGradient(
                            easing = EaseInOut,
                            startIntensity = 0f,
                            endIntensity = 0.85f,
                        )
                    },
            )
            Box(Modifier.matchParentSize().background(scrimBrush))
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(height)
                .background(scrimBrush),
        )
    }
}

/**
 * How far above the floating bars the page stops taking touches. A tap that
 * just misses the top of the mini player belongs to it, not to whatever row
 * the feed happens to have scrolled under there.
 */
private val TAP_GUARD_MARGIN = 8.dp

/**
 * A dead strip across the foot of the screen, as tall as the floating bars
 * plus [TAP_GUARD_MARGIN], so a thumb that misses a tab or the mini player
 * lands on nothing rather than on a song or an album passing under the bars —
 * which played it, or opened it, out of a tap meant for the bar.
 *
 * Drawn after the page and before the bars, so the bars still take their own
 * touches and the page gets none here. Full width, gaps included: the space
 * beside the pill and between the bars is where a near miss lands. Scrolls
 * started on the strip are swallowed too — the page scrolls from anywhere
 * above it — which is how a tab bar behaves on iOS as well.
 *
 * Only the bottom: the fade at the top, over the status bar, guards nothing.
 *
 * [barsHeight] is read in layout only. The glass bar changes height on every
 * frame of its fold, and read in composition that would recompose the whole
 * app root along with it.
 */
@Composable
fun FloatingBarsTapGuard(
    barsHeight: () -> Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .layout { measurable, constraints ->
                val bars = barsHeight()
                val height = if (bars <= 0) 0 else (bars + TAP_GUARD_MARGIN.roundToPx())
                val placeable = measurable.measure(
                    Constraints.fixed(
                        width = constraints.maxWidth,
                        height = height.coerceAtMost(constraints.maxHeight),
                    ),
                )
                layout(placeable.width, placeable.height) { placeable.place(0, 0) }
            }
            .pointerInput(Unit) {
                // Being hit at all is what keeps the page underneath from
                // being hit. Consumed as well, so nothing above it in the tree
                // reads a tap out of it either.
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            },
    )
}
