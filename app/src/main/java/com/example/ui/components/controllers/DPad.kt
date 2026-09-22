package com.example.ui.components.controllers

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.ControllerButton
import kotlin.math.atan2
import kotlin.math.hypot

@Composable
fun DPad(
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    opacity: Float = 0.85f,
    hapticsEnabled: Boolean = true,
    onButtonChange: (ControllerButton, Boolean) -> Unit
) {
    var activeDirection by remember { mutableStateOf<ControllerButton?>(null) }

    fun updateDirection(offset: Offset, center: Offset, radius: Float) {
        val dx = offset.x - center.x
        val dy = offset.y - center.y
        val dist = hypot(dx, dy)

        val newDir = if (dist < radius * 0.2f || dist > radius * 1.2f) {
            null
        } else {
            val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
            when {
                angle in -135f..-45f -> ControllerButton.DPAD_UP
                angle in -45f..45f -> ControllerButton.DPAD_RIGHT
                angle in 45f..135f -> ControllerButton.DPAD_DOWN
                else -> ControllerButton.DPAD_LEFT
            }
        }

        if (newDir != activeDirection) {
            activeDirection?.let { onButtonChange(it, false) }
            newDir?.let { onButtonChange(it, true) }
            activeDirection = newDir
        }
    }

    fun release() {
        activeDirection?.let { onButtonChange(it, false) }
        activeDirection = null
    }

    Box(
        modifier = modifier
            .size(size)
            .testTag("dpad_control")
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                        val radius = size.toPx() / 2f
                        updateDirection(offset, center, radius)
                        tryAwaitRelease()
                        release()
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                        val radius = size.toPx() / 2f
                        updateDirection(offset, center, radius)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val center = Offset(size.toPx() / 2f, size.toPx() / 2f)
                        val radius = size.toPx() / 2f
                        updateDirection(change.position, center, radius)
                    },
                    onDragEnd = { release() },
                    onDragCancel = { release() }
                )
            }
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val armWidth = w * 0.34f
            val cornerRadius = 14f

            val baseColor = Color.White.copy(alpha = 0.12f * opacity)
            val strokeColor = Color.White.copy(alpha = 0.28f * opacity)
            val activeColor = Color.White.copy(alpha = 0.40f * opacity)

            // D-Pad Cross Path
            val dpadPath = Path().apply {
                // Horizontal Arm
                addRoundRect(
                    RoundRect(
                        rect = Rect(0f, (h - armWidth) / 2f, w, (h + armWidth) / 2f),
                        radiusX = cornerRadius,
                        radiusY = cornerRadius
                    )
                )
                // Vertical Arm
                addRoundRect(
                    RoundRect(
                        rect = Rect((w - armWidth) / 2f, 0f, (w + armWidth) / 2f, h),
                        radiusX = cornerRadius,
                        radiusY = cornerRadius
                    )
                )
            }

            drawPath(dpadPath, baseColor)
            drawPath(dpadPath, strokeColor, style = Stroke(width = 2.dp.toPx()))

            // Highlight active direction
            activeDirection?.let { dir ->
                val highlightRect = when (dir) {
                    ControllerButton.DPAD_UP -> Rect((w - armWidth) / 2f, 0f, (w + armWidth) / 2f, (h - armWidth) / 2f + 8f)
                    ControllerButton.DPAD_DOWN -> Rect((w - armWidth) / 2f, (h + armWidth) / 2f - 8f, (w + armWidth) / 2f, h)
                    ControllerButton.DPAD_LEFT -> Rect(0f, (h - armWidth) / 2f, (w - armWidth) / 2f + 8f, (h + armWidth) / 2f)
                    ControllerButton.DPAD_RIGHT -> Rect((w + armWidth) / 2f - 8f, (h - armWidth) / 2f, w, (h + armWidth) / 2f)
                    else -> null
                }
                highlightRect?.let { rect ->
                    drawRoundRect(
                        color = activeColor,
                        topLeft = Offset(rect.left, rect.top),
                        size = androidx.compose.ui.geometry.Size(rect.width, rect.height),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius, cornerRadius)
                    )
                }
            }

            // Center pivot subtle circle
            drawCircle(
                color = Color.White.copy(alpha = 0.08f * opacity),
                radius = armWidth * 0.35f,
                center = Offset(w / 2f, h / 2f)
            )
        }
    }
}
