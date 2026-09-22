package com.example.ui.components.controllers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.ControllerButton

@Composable
fun N64CButtons(
    modifier: Modifier = Modifier,
    size: Dp = 120.dp,
    opacity: Float = 0.85f,
    onButtonChange: (ControllerButton, Boolean) -> Unit
) {
    val yellowC = Color(0xFFFBBF24)
    val buttonSize = 36.dp

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.35f * opacity))
            .border(1.dp, yellowC.copy(alpha = 0.25f * opacity), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // C-UP
        ActionButton(
            button = ControllerButton.C_UP,
            size = buttonSize,
            customColor = yellowC,
            labelOverride = "▲",
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 6.dp)
        )
        // C-DOWN
        ActionButton(
            button = ControllerButton.C_DOWN,
            size = buttonSize,
            customColor = yellowC,
            labelOverride = "▼",
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-6).dp)
        )
        // C-LEFT
        ActionButton(
            button = ControllerButton.C_LEFT,
            size = buttonSize,
            customColor = yellowC,
            labelOverride = "◀",
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 6.dp)
        )
        // C-RIGHT
        ActionButton(
            button = ControllerButton.C_RIGHT,
            size = buttonSize,
            customColor = yellowC,
            labelOverride = "▶",
            opacity = opacity,
            onButtonChange = onButtonChange,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(x = (-6).dp)
        )
    }
}
