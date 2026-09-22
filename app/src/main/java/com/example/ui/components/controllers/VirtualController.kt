package com.example.ui.components.controllers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.AnalogStickType
import com.example.model.ConsoleSystem
import com.example.model.ControllerButton

@Composable
fun VirtualController(
    system: ConsoleSystem,
    isLandscape: Boolean,
    modifier: Modifier = Modifier,
    opacity: Float = 0.85f,
    scale: Float = 1.0f,
    hapticsEnabled: Boolean = true,
    onButtonChange: (ControllerButton, Boolean) -> Unit,
    onAnalogChange: (AnalogStickType, Float, Float) -> Unit
) {
    val dpadSize = (130 * scale).dp
    val buttonSize = (50 * scale).dp

    if (isLandscape) {
        // Landscape Layout: Controls flanked on left and right sides
        Box(modifier = modifier.fillMaxSize()) {
            // Top Shoulder Buttons (Left and Right)
            ShoulderBar(
                system = system,
                opacity = opacity,
                onButtonChange = onButtonChange,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 6.dp)
            )

            // Left Side: D-Pad / Analog Stick
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 24.dp)
            ) {
                when (system) {
                    ConsoleSystem.N64, ConsoleSystem.PSP -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AnalogStick(
                                stickType = AnalogStickType.LEFT,
                                size = (120 * scale).dp,
                                opacity = opacity,
                                onValueChange = onAnalogChange
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            DPad(
                                size = (100 * scale).dp,
                                opacity = opacity,
                                hapticsEnabled = hapticsEnabled,
                                onButtonChange = onButtonChange
                            )
                        }
                    }
                    ConsoleSystem.NINTENDO_3DS -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnalogStick(
                                stickType = AnalogStickType.CIRCLE_PAD,
                                size = (110 * scale).dp,
                                opacity = opacity,
                                onValueChange = onAnalogChange
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DPad(
                                size = (90 * scale).dp,
                                opacity = opacity,
                                hapticsEnabled = hapticsEnabled,
                                onButtonChange = onButtonChange
                            )
                        }
                    }
                    ConsoleSystem.PS1 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            DPad(
                                size = dpadSize,
                                opacity = opacity,
                                hapticsEnabled = hapticsEnabled,
                                onButtonChange = onButtonChange
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            AnalogStick(
                                stickType = AnalogStickType.LEFT,
                                size = (90 * scale).dp,
                                opacity = opacity,
                                onValueChange = onAnalogChange
                            )
                        }
                    }
                    else -> {
                        DPad(
                            size = dpadSize,
                            opacity = opacity,
                            hapticsEnabled = hapticsEnabled,
                            onButtonChange = onButtonChange
                        )
                    }
                }
            }

            // Bottom Center: Start, Select, System buttons
            SystemCenterControls(
                system = system,
                opacity = opacity,
                onButtonChange = onButtonChange,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )

            // Right Side: Action Buttons / C-buttons / Diamond
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
            ) {
                when (system) {
                    ConsoleSystem.GAME_BOY, ConsoleSystem.GAME_BOY_COLOR, ConsoleSystem.GAME_BOY_ADVANCE, ConsoleSystem.NES -> {
                        TwoButtonCluster(
                            button1 = ControllerButton.B,
                            button2 = ControllerButton.A,
                            opacity = opacity,
                            onButtonChange = onButtonChange
                        )
                    }
                    ConsoleSystem.SNES, ConsoleSystem.NDS, ConsoleSystem.NINTENDO_3DS -> {
                        NintendoDiamond(
                            size = (140 * scale).dp,
                            buttonSize = buttonSize,
                            opacity = opacity,
                            isSnesColors = system == ConsoleSystem.SNES,
                            onButtonChange = onButtonChange
                        )
                    }
                    ConsoleSystem.N64 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            N64CButtons(
                                size = (110 * scale).dp,
                                opacity = opacity,
                                onButtonChange = onButtonChange
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                ActionButton(
                                    button = ControllerButton.B,
                                    size = (44 * scale).dp,
                                    customColor = Color(0xFF10B981),
                                    opacity = opacity,
                                    onButtonChange = onButtonChange
                                )
                                ActionButton(
                                    button = ControllerButton.A,
                                    size = (44 * scale).dp,
                                    customColor = Color(0xFF3B82F6),
                                    opacity = opacity,
                                    onButtonChange = onButtonChange
                                )
                            }
                        }
                    }
                    ConsoleSystem.PS1 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PlayStationDiamond(
                                size = (130 * scale).dp,
                                buttonSize = buttonSize,
                                opacity = opacity,
                                onButtonChange = onButtonChange
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            AnalogStick(
                                stickType = AnalogStickType.RIGHT,
                                size = (90 * scale).dp,
                                opacity = opacity,
                                onValueChange = onAnalogChange
                            )
                        }
                    }
                    ConsoleSystem.PSP -> {
                        PlayStationDiamond(
                            size = (130 * scale).dp,
                            buttonSize = buttonSize,
                            opacity = opacity,
                            onButtonChange = onButtonChange
                        )
                    }
                    ConsoleSystem.ATARI -> {
                        ActionButton(
                            button = ControllerButton.ATARI_FIRE,
                            size = (64 * scale).dp,
                            customColor = Color(0xFFDC2626),
                            labelOverride = "FIRE",
                            opacity = opacity,
                            onButtonChange = onButtonChange
                        )
                    }
                    ConsoleSystem.GAME_GEAR -> {
                        TwoButtonCluster(
                            button1 = ControllerButton.GG_1,
                            button2 = ControllerButton.GG_2,
                            opacity = opacity,
                            onButtonChange = onButtonChange
                        )
                    }
                }
            }
        }
    } else {
        // Portrait Layout: Game Screen is on top; Controller fills lower region
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Shoulders on Top of Controller Area
            ShoulderBar(
                system = system,
                opacity = opacity,
                onButtonChange = onButtonChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Main Primary Controls Row (D-Pad on left, Buttons on right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: D-Pad / Stick
                when (system) {
                    ConsoleSystem.N64, ConsoleSystem.PSP -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnalogStick(
                                stickType = AnalogStickType.LEFT,
                                size = (100 * scale).dp,
                                opacity = opacity,
                                onValueChange = onAnalogChange
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DPad(
                                size = (90 * scale).dp,
                                opacity = opacity,
                                hapticsEnabled = hapticsEnabled,
                                onButtonChange = onButtonChange
                            )
                        }
                    }
                    ConsoleSystem.NINTENDO_3DS -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AnalogStick(
                                stickType = AnalogStickType.CIRCLE_PAD,
                                size = (100 * scale).dp,
                                opacity = opacity,
                                onValueChange = onAnalogChange
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            DPad(
                                size = (85 * scale).dp,
                                opacity = opacity,
                                hapticsEnabled = hapticsEnabled,
                                onButtonChange = onButtonChange
                            )
                        }
                    }
                    ConsoleSystem.PS1 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            DPad(
                                size = (110 * scale).dp,
                                opacity = opacity,
                                hapticsEnabled = hapticsEnabled,
                                onButtonChange = onButtonChange
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AnalogStick(
                                stickType = AnalogStickType.LEFT,
                                size = (80 * scale).dp,
                                opacity = opacity,
                                onValueChange = onAnalogChange
                            )
                        }
                    }
                    else -> {
                        DPad(
                            size = dpadSize,
                            opacity = opacity,
                            hapticsEnabled = hapticsEnabled,
                            onButtonChange = onButtonChange
                        )
                    }
                }

                // Right: Buttons
                when (system) {
                    ConsoleSystem.GAME_BOY, ConsoleSystem.GAME_BOY_COLOR, ConsoleSystem.GAME_BOY_ADVANCE, ConsoleSystem.NES -> {
                        TwoButtonCluster(
                            button1 = ControllerButton.B,
                            button2 = ControllerButton.A,
                            opacity = opacity,
                            onButtonChange = onButtonChange
                        )
                    }
                    ConsoleSystem.SNES, ConsoleSystem.NDS, ConsoleSystem.NINTENDO_3DS -> {
                        NintendoDiamond(
                            size = (140 * scale).dp,
                            buttonSize = buttonSize,
                            opacity = opacity,
                            isSnesColors = system == ConsoleSystem.SNES,
                            onButtonChange = onButtonChange
                        )
                    }
                    ConsoleSystem.N64 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            N64CButtons(
                                size = (105 * scale).dp,
                                opacity = opacity,
                                onButtonChange = onButtonChange
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                ActionButton(
                                    button = ControllerButton.B,
                                    size = (42 * scale).dp,
                                    customColor = Color(0xFF10B981),
                                    opacity = opacity,
                                    onButtonChange = onButtonChange
                                )
                                ActionButton(
                                    button = ControllerButton.A,
                                    size = (42 * scale).dp,
                                    customColor = Color(0xFF3B82F6),
                                    opacity = opacity,
                                    onButtonChange = onButtonChange
                                )
                            }
                        }
                    }
                    ConsoleSystem.PS1 -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PlayStationDiamond(
                                size = (120 * scale).dp,
                                buttonSize = (44 * scale).dp,
                                opacity = opacity,
                                onButtonChange = onButtonChange
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            AnalogStick(
                                stickType = AnalogStickType.RIGHT,
                                size = (80 * scale).dp,
                                opacity = opacity,
                                onValueChange = onAnalogChange
                            )
                        }
                    }
                    ConsoleSystem.PSP -> {
                        PlayStationDiamond(
                            size = (125 * scale).dp,
                            buttonSize = buttonSize,
                            opacity = opacity,
                            onButtonChange = onButtonChange
                        )
                    }
                    ConsoleSystem.ATARI -> {
                        ActionButton(
                            button = ControllerButton.ATARI_FIRE,
                            size = (68 * scale).dp,
                            customColor = Color(0xFFDC2626),
                            labelOverride = "FIRE",
                            opacity = opacity,
                            onButtonChange = onButtonChange
                        )
                    }
                    ConsoleSystem.GAME_GEAR -> {
                        TwoButtonCluster(
                            button1 = ControllerButton.GG_1,
                            button2 = ControllerButton.GG_2,
                            opacity = opacity,
                            onButtonChange = onButtonChange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom: Start / Select Controls
            SystemCenterControls(
                system = system,
                opacity = opacity,
                onButtonChange = onButtonChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ShoulderBar(
    system: ConsoleSystem,
    opacity: Float,
    onButtonChange: (ControllerButton, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    when (system) {
        ConsoleSystem.GAME_BOY_ADVANCE, ConsoleSystem.SNES, ConsoleSystem.NDS, ConsoleSystem.GAME_GEAR -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    button = ControllerButton.L,
                    size = 54.dp,
                    shape = RoundedCornerShape(12.dp),
                    opacity = opacity,
                    onButtonChange = onButtonChange
                )
                ActionButton(
                    button = ControllerButton.R,
                    size = 54.dp,
                    shape = RoundedCornerShape(12.dp),
                    opacity = opacity,
                    onButtonChange = onButtonChange
                )
            }
        }
        ConsoleSystem.N64 -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(
                        button = ControllerButton.L,
                        size = 46.dp,
                        shape = RoundedCornerShape(10.dp),
                        opacity = opacity,
                        onButtonChange = onButtonChange
                    )
                    ActionButton(
                        button = ControllerButton.Z,
                        size = 46.dp,
                        shape = RoundedCornerShape(10.dp),
                        opacity = opacity,
                        customColor = Color(0xFF94A3B8),
                        onButtonChange = onButtonChange
                    )
                }
                ActionButton(
                    button = ControllerButton.R,
                    size = 46.dp,
                    shape = RoundedCornerShape(10.dp),
                    opacity = opacity,
                    onButtonChange = onButtonChange
                )
            }
        }
        ConsoleSystem.NINTENDO_3DS -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(
                        button = ControllerButton.L,
                        size = 44.dp,
                        shape = RoundedCornerShape(10.dp),
                        opacity = opacity,
                        onButtonChange = onButtonChange
                    )
                    ActionButton(
                        button = ControllerButton.ZL,
                        size = 44.dp,
                        shape = RoundedCornerShape(10.dp),
                        opacity = opacity,
                        onButtonChange = onButtonChange
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(
                        button = ControllerButton.ZR,
                        size = 44.dp,
                        shape = RoundedCornerShape(10.dp),
                        opacity = opacity,
                        onButtonChange = onButtonChange
                    )
                    ActionButton(
                        button = ControllerButton.R,
                        size = 44.dp,
                        shape = RoundedCornerShape(10.dp),
                        opacity = opacity,
                        onButtonChange = onButtonChange
                    )
                }
            }
        }
        ConsoleSystem.PS1 -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(
                        button = ControllerButton.L1,
                        size = 44.dp,
                        shape = RoundedCornerShape(10.dp),
                        opacity = opacity,
                        onButtonChange = onButtonChange
                    )
                    ActionButton(
                        button = ControllerButton.L2,
                        size = 44.dp,
                        shape = RoundedCornerShape(10.dp),
                        opacity = opacity,
                        onButtonChange = onButtonChange
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ActionButton(
                        button = ControllerButton.R2,
                        size = 44.dp,
                        shape = RoundedCornerShape(10.dp),
                        opacity = opacity,
                        onButtonChange = onButtonChange
                    )
                    ActionButton(
                        button = ControllerButton.R1,
                        size = 44.dp,
                        shape = RoundedCornerShape(10.dp),
                        opacity = opacity,
                        onButtonChange = onButtonChange
                    )
                }
            }
        }
        ConsoleSystem.PSP -> {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ActionButton(
                    button = ControllerButton.L,
                    size = 52.dp,
                    shape = RoundedCornerShape(12.dp),
                    opacity = opacity,
                    onButtonChange = onButtonChange
                )
                ActionButton(
                    button = ControllerButton.R,
                    size = 52.dp,
                    shape = RoundedCornerShape(12.dp),
                    opacity = opacity,
                    onButtonChange = onButtonChange
                )
            }
        }
        else -> {
            // Systems without shoulder buttons (GB, GBC, NES, Atari)
        }
    }
}

@Composable
private fun SystemCenterControls(
    system: ConsoleSystem,
    opacity: Float,
    onButtonChange: (ControllerButton, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (system == ConsoleSystem.ATARI) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (system != ConsoleSystem.GAME_GEAR) {
            ActionButton(
                button = ControllerButton.SELECT,
                size = 46.dp,
                shape = RoundedCornerShape(16.dp),
                labelOverride = "SELECT",
                opacity = opacity,
                onButtonChange = onButtonChange
            )
            Spacer(modifier = Modifier.width(20.dp))
        }

        if (system == ConsoleSystem.PSP) {
            ActionButton(
                button = ControllerButton.HOME,
                size = 46.dp,
                shape = RoundedCornerShape(16.dp),
                labelOverride = "HOME",
                opacity = opacity,
                onButtonChange = onButtonChange
            )
            Spacer(modifier = Modifier.width(20.dp))
        }

        ActionButton(
            button = ControllerButton.START,
            size = 46.dp,
            shape = RoundedCornerShape(16.dp),
            labelOverride = "START",
            opacity = opacity,
            onButtonChange = onButtonChange
        )
    }
}
