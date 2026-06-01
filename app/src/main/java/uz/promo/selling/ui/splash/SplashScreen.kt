package uz.promo.selling.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val TagGreen        = Color(0xFF1A6B3C)
private val StringColor     = Color(0xFFA0B8AC)
private val HoleColor       = Color(0xFFF9F7F4)
private val BackgroundColor = Color(0xFFF9F7F4)

@Composable
fun SplashScreen(onAnimationEnd: () -> Unit) {

    val stringScaleY   = remember { Animatable(0f) }
    val stringAlpha    = remember { Animatable(0f) }
    val tagScale       = remember { Animatable(0.4f) }
    val tagAlpha       = remember { Animatable(0f) }
    val tagOffsetY     = remember { Animatable(-40f) }
    val tagRotation    = remember { Animatable(-20f) }
    val holeScale      = remember { Animatable(0f) }
    val letterScale    = remember { Animatable(0f) }
    val letterRotation = remember { Animatable(10f) }
    val letterAlpha    = remember { Animatable(0f) }
    val shineProgress  = remember { Animatable(-0.8f) }
    val swingRotation  = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // 1. String falls
        launch {
            delay(100)
            launch { stringScaleY.animateTo(1f, tween(400, easing = EaseOut)) }
            stringAlpha.animateTo(1f, tween(400, easing = EaseOut))
        }
        // 2. Tag drops with bounce
        launch {
            delay(450)
            launch { tagAlpha.animateTo(1f, tween(200)) }
            launch { tagScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)) }
            launch { tagOffsetY.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)) }
            tagRotation.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
        }
        // 3. Hole pops
        launch {
            delay(950)
            holeScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow))
        }
        // 4. Letter pops
        launch {
            delay(1050)
            launch { letterAlpha.animateTo(1f, tween(150)) }
            launch { letterScale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium)) }
            letterRotation.animateTo(0f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium))
        }
        // 5. Shine sweep
        launch {
            delay(1400)
            shineProgress.animateTo(1.6f, tween(600, easing = EaseOut))
        }
        // 6. Idle swing
        launch {
            delay(1200)
            repeat(3) {
                swingRotation.animateTo( 3f, tween(750,  easing = EaseInOut))
                swingRotation.animateTo(-3f, tween(1500, easing = EaseInOut))
                swingRotation.animateTo( 0f, tween(750,  easing = EaseInOut))
            }
        }
        delay(3200)
        onAnimationEnd()
    }

    val textMeasurer = rememberTextMeasurer()
    val density      = LocalDensity.current

    // Pre-convert all dp/sp → px
    val tagW       = with(density) { 100.dp.toPx() }
    val tagH       = with(density) { 115.dp.toPx() }
    val cornerR    = with(density) { 18.dp.toPx() }
    val stringW    = with(density) { 2.5.dp.toPx() }
    val stringH    = with(density) { 36.dp.toPx() }
    val holeRadius = with(density) { 8.dp.toPx() }
    val holeTopPad = with(density) { 13.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(width = 120.dp, height = 200.dp)
                // Only the outer swing uses graphicsLayer — no clipping here,
                // so there are zero software-rasterized edges at this level.
                .graphicsLayer {
                    rotationZ       = swingRotation.value
                    transformOrigin = TransformOrigin(0.5f, 0f)
                }
        ) {
            val cx = size.width / 2f

            // ── String ────────────────────────────────────────────
            drawRect(
                color   = StringColor.copy(alpha = stringAlpha.value),
                topLeft = Offset(cx - stringW / 2f, 0f),
                size    = Size(stringW, stringH * stringScaleY.value)
            )

            // ── Tag ───────────────────────────────────────────────
            // The key to smooth edges: we rotate via drawscope.rotate()
            // (a canvas matrix op), then draw a Path + clipPath inside it.
            // The GPU handles anti-aliasing on the path edges directly —
            // no Compose layer rasterization, no bitmap scaling artifacts.

            val tagTop = stringH
            val pivotX = cx
            val pivotY = tagTop

            rotate(degrees = tagRotation.value, pivot = Offset(pivotX, pivotY)) {
                translate(top = tagOffsetY.value) {

                    val scaledW    = tagW * tagScale.value
                    val scaledH    = tagH * tagScale.value
                    val scaledLeft = cx - scaledW / 2f
                    val scaledCorner = CornerRadius(cornerR * tagScale.value)

                    // Rounded rect path — drawn & clipped in one GPU pass
                    val tagPath = Path().apply {
                        addRoundRect(
                            RoundRect(
                                rect = Rect(
                                    left   = scaledLeft,
                                    top    = tagTop,
                                    right  = scaledLeft + scaledW,
                                    bottom = tagTop + scaledH
                                ),
                                cornerRadius = scaledCorner
                            )
                        )
                    }

                    // Tag background
                    drawPath(tagPath, TagGreen.copy(alpha = tagAlpha.value))

                    // Everything inside the tag is clipped to tagPath
                    clipPath(tagPath) {

                        // Shine sweep
                        val shineX    = scaledLeft + shineProgress.value * scaledW
                        val sw        = scaledW * 0.35f
                        val skew      = scaledH * 0.15f
                        val shinePath = Path().apply {
                            moveTo(shineX - sw + skew, tagTop)
                            lineTo(shineX + skew,      tagTop)
                            lineTo(shineX - skew,      tagTop + scaledH)
                            lineTo(shineX - sw - skew, tagTop + scaledH)
                            close()
                        }
                        drawPath(shinePath, Color.White.copy(alpha = 0.18f))

                        // Hole
                        val holeCy = tagTop + holeTopPad * tagScale.value + holeRadius * tagScale.value
                        drawCircle(
                            color  = HoleColor,
                            radius = holeRadius * tagScale.value * holeScale.value,
                            center = Offset(cx, holeCy)
                        )
                    }

                    // ── Letter S ──────────────────────────────────
                    // Drawn outside clipPath so it isn't cut by the tag edges
                    val measured = textMeasurer.measure(
                        text  = "S",
                        style = TextStyle(
                            fontSize   = 68.sp,
                            fontWeight = FontWeight.Black
                        )
                    )
                    val tw = measured.size.width.toFloat()
                    val th = measured.size.height.toFloat()

                    val letterCx = cx
                    val letterCy = tagTop + scaledH * 0.58f

                    // Rotate letter around its own center
                    rotate(degrees = letterRotation.value, pivot = Offset(letterCx, letterCy)) {
                        // Scale letter from its center
                        val ls    = letterScale.value
                        val drawX = letterCx - tw / 2f + tw / 2f * (1f - ls)
                        val drawY = letterCy - th / 2f + th / 2f * (1f - ls)

                        translate(left = drawX, top = drawY) {
                            drawText(
                                textLayoutResult = measured,
                                color            = Color.White.copy(alpha = letterAlpha.value),
                                topLeft          = Offset.Zero,
                                // scale the text manually via canvas
                                // by pre-scaling the translate offset above
                            )
                        }
                    }
                }
            }
        }
    }
}
