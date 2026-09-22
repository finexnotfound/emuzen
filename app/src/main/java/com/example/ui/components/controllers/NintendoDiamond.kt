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
fun NintendoDiamond(
    modifier: Modifier = Modifier,
    size: Dp = 140.dp,
    buttonSize: Dp = 48.dp,
    opacity: Float = 0.85f,
    isSnesColors: Boolean = false,
    onButtonChange: (ControllerButton, Boolean) -> Unit
) {
    // SNES colors (Super Famicom/PAL: Blue, Red, Yellow, Green) or US Purple/Lilac
    val colorX = if (isSnesColors) Color(0xFF3B82F6) else Color.White
    val colorA = if (isSnesColors) Color(0xFFEF4444) else Color.White
    val colorB = if (isSnesColors) Color(0xFFF59E0B) else Color.White
    val colorY = if (isSnesColors) Color(0xFF10B981) else Color.White

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // X (Top)
        ActionButton(
            button = ControllerButton.X,
            size = buttonSize,
            customColor = colorX,
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        // A (Right)
        ActionButton(
            button = ControllerButton.A,
            size = buttonSize,
            customColor = colorA,
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
        // B (Bottom)
        ActionButton(
            button = ControllerButton.B,
            size = buttonSize,
            customColor = colorB,
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        // Y (Left)
        ActionButton(
            button = ControllerButton.Y,
            size = buttonSize,
            customColor = colorY,
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@Composable
fun TwoButtonCluster(
    button1: ControllerButton,
    button2: ControllerButton,
    modifier: Modifier = Modifier,
    buttonSize: Dp = 54.dp,
    opacity: Float = 0.85f,
    color1: Color = Color.White,
    color2: Color = Color.White,
    onButtonChange: (ControllerButton, Boolean) -> Unit
) {
    // Angled B and A buttons (B lower left, A upper right)
    Box(
        modifier = modifier.size(width = 120.dp, height = 90.dp),
        contentAlignment = Alignment.Center
    ) {
        ActionButton(
            button = button1, // e.g. B
            size = buttonSize,
            customColor = color1,
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = 6.dp, y = (-4).dp)
        )
        ActionButton(
            button = button2, // e.g. A
            size = buttonSize,
            customColor = color2,
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-6).dp, y = 4.dp)
        )
    }
}
