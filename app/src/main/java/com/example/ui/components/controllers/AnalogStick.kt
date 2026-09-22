package com.example.ui.components.controllers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.AnalogStickType
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun AnalogStick(
    stickType: AnalogStickType,
    modifier: Modifier = Modifier,
    size: Dp = 130.dp,
    opacity: Float = 0.85f,
    onValueChange: (AnalogStickType, Float, Float) -> Unit
) {
    var thumbOffset by remember { mutableStateOf(Offset.Zero) }

    fun processOffset(dragOffset: Offset, center: Offset, maxRadius: Float) {
        val dx = dragOffset.x - center.x
        val dy = dragOffset.y - center.y
        val dist = hypot(dx, dy)

        if (dist <= maxRadius) {
            thumbOffset = Offset(dx, dy)
            onValueChange(stickType, dx / maxRadius, dy / maxRadius)
        } else {
            val angle = atan2(dy.toDouble(), dx.toDouble())
            val clampedX = (cos(angle) * maxRadius).toFloat()
            val clampedY = (sin(angle) * maxRadius).toFloat()
            thumbOffset = Offset(clampedX, clampedY)
            onValueChange(stickType, clampedX / maxRadius, clampedY / maxRadius)
        }
    }

    fun release() {
        thumbOffset = Offset.Zero
        onValueChange(stickType, 0f, 0f)
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag("analog_stick_${stickType.name}")
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                        val maxRadius = size.toPx() * 0.38f
                        processOffset(offset, center, maxRadius)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                        val maxRadius = size.toPx() * 0.38f
                        processOffset(change.position, center, maxRadius)
                    },
                    onDragEnd = { release() },
                    onDragCancel = { release() }
                )
            }
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val center = Offset(w / 2f, h / 2f)
            val outerRadius = w * 0.46f
            val thumbRadius = w * 0.22f

            // Outer Base Ring
            drawCircle(
                color = Color.White.copy(alpha = 0.08f * opacity),
                radius = outerRadius,
                center = center
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.25f * opacity),
                radius = outerRadius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Inner stick head
            val thumbPos = center + thumbOffset
            drawCircle(
                color = Color.White.copy(alpha = 0.25f * opacity),
                radius = thumbRadius,
                center = thumbPos
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.45f * opacity),
                radius = thumbRadius,
                center = thumbPos,
                style = Stroke(width = 2.dp.toPx())
            )

            // Subtle center grip indicator
            drawCircle(
                color = Color.White.copy(alpha = 0.40f * opacity),
                radius = thumbRadius * 0.35f,
                center = thumbPos
            )
        }
    }
}
