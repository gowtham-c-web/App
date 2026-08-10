package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedOrb(
    isRunning: Boolean,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 220.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "OrbPulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = if (isRunning) 1.25f else 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isRunning) 3500 else 2500,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RotateAngle"
    )

    val coreColors = listOf(
        Color(0xFF818CF8),
        Color(0xFF2DD4BF),
        Color(0xFFF472B6)
    )

    Canvas(modifier = modifier.size(sizeDp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f

        // Outer pulsing aura rings
        val outerRadius = maxRadius * 0.85f * pulseScale
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x66818CF8),
                    Color(0x222DD4BF),
                    Color.Transparent
                ),
                center = center,
                radius = outerRadius
            ),
            center = center,
            radius = outerRadius
        )

        // Middle ring
        drawCircle(
            color = Color(0x882DD4BF),
            radius = maxRadius * 0.65f * pulseScale,
            center = center,
            style = Stroke(width = 3.dp.toPx())
        )

        // Inner core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF818CF8),
                    Color(0xFFC084FC),
                    Color(0xFF06B6D4)
                ),
                center = center,
                radius = maxRadius * 0.45f * pulseScale
            ),
            center = center,
            radius = maxRadius * 0.45f * pulseScale
        )
    }
}
