package com.example.ui.components.controllers

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.ControllerButton

@Composable
fun PlayStationDiamond(
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    buttonSize: Dp = 50.dp,
    opacity: Float = 0.85f,
    onButtonChange: (ControllerButton, Boolean) -> Unit
) {
    // PlayStation iconic colors
    val triangleGreen = Color(0xFF10B981)
    val circleRed = Color(0xFFEF4444)
    val crossBlue = Color(0xFF3B82F6)
    val squarePink = Color(0xFFEC4899)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Triangle (Top)
        ActionButton(
            button = ControllerButton.PS_TRIANGLE,
            size = buttonSize,
            customColor = triangleGreen,
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        // Circle (Right)
        ActionButton(
            button = ControllerButton.PS_CIRCLE,
            size = buttonSize,
            customColor = circleRed,
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        // Cross (Bottom)
        ActionButton(
            button = ControllerButton.PS_CROSS,
            size = buttonSize,
            customColor = crossBlue,
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        // Square (Left)
        ActionButton(
            button = ControllerButton.PS_SQUARE,
            size = buttonSize,
            customColor = squarePink,
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}
