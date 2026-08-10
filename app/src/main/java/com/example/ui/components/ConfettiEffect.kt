package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

data class Particle(
    val x: Float,
    val y: Float,
    val angle: Double,
    val speed: Float,
    val radius: Float,
    val color: Color
)

@Composable
fun ConfettiEffect(
    trigger: Boolean,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    if (!trigger) return

    val progress = remember { Animatable(0f) }

    val particles = remember {
        val colors = listOf(
            Color(0xFF818CF8),
            Color(0xFF2DD4BF),
            Color(0xFFF472B6),
            Color(0xFFF59E0B),
            Color(0xFF10B981)
        )
        List(40) {
            Particle(
                x = 0.5f,
                y = 0.5f,
                angle = Random.nextDouble(0.0, 2 * Math.PI),
                speed = Random.nextFloat() * 400f + 200f,
                radius = Random.nextFloat() * 10f + 6f,
                color = colors.random()
            )
        }
    }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val currentProgress = progress.value
        val alpha = (1f - currentProgress).coerceIn(0f, 1f)

        particles.forEach { p ->
            val dist = p.speed * currentProgress
            val px = centerX + (dist * cos(p.angle)).toFloat()
            val py = centerY + (dist * sin(p.angle)).toFloat() + (currentProgress * currentProgress * 200f) // gravity drop

            drawCircle(
                color = p.color.copy(alpha = alpha),
                radius = p.radius * (1f - currentProgress * 0.5f),
                center = Offset(px, py)
            )
        }
    }
}
