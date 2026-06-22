package uz.promo.selling.ui.main.add

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import uz.promo.selling.R

// Gemini-ish accent palette used across the AI surfaces.
private val aiBlue = Color(0xFF4285F4)
private val aiPurple = Color(0xFF9B72F2)
private val aiPink = Color(0xFFEA4C89)

/** Prominent gradient CTA that kicks off photo analysis. */
@Composable
fun AiAutoFillButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    idleText: String = "✨ " + stringResource(R.string.ai_autofill_from_photos),
    loadingText: String = stringResource(R.string.ai_analyzing_photos)
) {
    val background: Brush =
        if (enabled) Brush.horizontalGradient(listOf(aiBlue, aiPurple, aiPink))
        else SolidColor(Color(0xFFBDBDBD))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .clickable(enabled = enabled && !isLoading) { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(loadingText, color = Color.White, fontWeight = FontWeight.SemiBold)
            } else {
                Text(idleText, color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Animated "something magic is happening" overlay. A coloured gradient sweeps
 * across while the AI works; it also swallows taps so photos can't be edited
 * mid-analysis. Size it to match the area you want to cover (e.g. matchParentSize).
 */
@Composable
fun AiAnalyzingOverlay(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "ai_sweep")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ai_shift"
    )
    val sweep = Brush.linearGradient(
        colors = listOf(aiBlue, aiPurple, aiPink, aiPurple, aiBlue),
        start = Offset(shift - 1000f, 0f),
        end = Offset(shift, 0f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(sweep)
            // Consume taps so the photo controls underneath are locked while analyzing.
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {},
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.dp,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text("✨ " + stringResource(R.string.ai_analyzing_photos), color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * Full-screen "AI is working" overlay (Gemini-style): dims the whole screen and
 * shows a rotating multi-colour gradient ring with a pulsing sparkle in the
 * centre, so the user feels something magic is happening behind the scenes.
 */
@Composable
fun AiGeneratingOverlay(message: String = stringResource(R.string.ai_creating_listing)) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        val transition = rememberInfiniteTransition(label = "ai_gen")
        val rotation by transition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "rotation"
        )
        val pulse by transition.animateFloat(
            initialValue = 0.82f,
            targetValue = 1.18f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 850, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
        val dots by transition.animateFloat(
            initialValue = 0f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "dots"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.62f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer { rotationZ = rotation }
                    ) {
                        drawArc(
                            brush = Brush.sweepGradient(listOf(aiBlue, aiPurple, aiPink, aiBlue)),
                            startAngle = 0f,
                            sweepAngle = 300f,
                            useCenter = false,
                            style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                    Text(
                        text = "✨",
                        fontSize = 40.sp,
                        modifier = Modifier.graphicsLayer { scaleX = pulse; scaleY = pulse }
                    )
                }
                Spacer(Modifier.height(28.dp))
                Text(
                    text = message + ".".repeat((dots.toInt() % 3) + 1),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
