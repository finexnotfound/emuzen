package com.example.ui.components.controllers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ControllerButton

@Composable
fun ActionButton(
    button: ControllerButton,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    shape: Shape = CircleShape,
    customColor: Color? = null,
    labelOverride: String? = null,
    opacity: Float = 0.85f,
    onButtonChange: (ControllerButton, Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1.0f, label = "pressScale")

    val baseColor = customColor ?: Color.White
    val bgAlpha = if (isPressed) 0.40f * opacity else 0.15f * opacity
    val borderAlpha = if (isPressed) 0.65f * opacity else 0.30f * opacity

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .clip(shape)
            .background(baseColor.copy(alpha = bgAlpha))
            .border(1.5.dp, baseColor.copy(alpha = borderAlpha), shape)
            .testTag("action_button_${button.name}")
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onButtonChange(button, true)
                        tryAwaitRelease()
                        isPressed = false
                        onButtonChange(button, false)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = labelOverride ?: button.label,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = (if (isPressed) 1.0f else 0.85f) * opacity),
            fontSize = if (button.label.length > 2) 11.sp else 16.sp
        )
    }
}
