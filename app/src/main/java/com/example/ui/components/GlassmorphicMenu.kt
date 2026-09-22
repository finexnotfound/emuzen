package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ZenAccent
import com.example.ui.theme.ZenAccentRed
import com.example.ui.theme.ZenGlassBackground
import com.example.ui.theme.ZenGlassBorder
import com.example.ui.theme.ZenGlassBorderStrong
import com.example.ui.theme.ZenOnyx
import com.example.ui.theme.ZenTextPrimary
import com.example.ui.theme.ZenTextSecondary

@Composable
fun GlassmorphicMenu(
    isOpen: Boolean,
    currentSpeed: Float,
    currentOrientation: String,
    onResume: () -> Unit,
    onSaveState: () -> Unit,
    onLoadState: () -> Unit,
    onSpeedSelect: (Float) -> Unit,
    onOrientationToggle: () -> Unit,
    onControlsCustomize: () -> Unit,
    onSettings: () -> Unit,
    onExitGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSpeedSubmenuOpen by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn() + slideInVertically { it / 4 },
        exit = fadeOut() + slideOutVertically { it / 4 },
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 340.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(ZenGlassBackground)
                .border(1.dp, ZenGlassBorderStrong, RoundedCornerShape(26.dp))
                .padding(18.dp)
        ) {
            Column {
                // Header: EmuZen title + close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(ZenAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Z",
                                color = ZenAccent,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "EmuZen",
                                style = MaterialTheme.typography.titleMedium,
                                color = ZenTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "In-Game Menu",
                                style = MaterialTheme.typography.labelSmall,
                                color = ZenTextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onResume,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Resume Game",
                            tint = ZenTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = ZenGlassBorder)
                Spacer(modifier = Modifier.height(8.dp))

                if (isSpeedSubmenuOpen) {
                    // Speed selection submenu
                    Text(
                        text = "Emulation Speed",
                        style = MaterialTheme.typography.labelMedium,
                        color = ZenTextSecondary,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                    val speeds = listOf(1.0f, 1.5f, 2.0f, 3.0f, 4.0f, 8.0f)
                    speeds.forEach { spd ->
                        val label = if (spd >= 8.0f) "Maximum / Unlimited" else "${spd}×"
                        MenuItemRow(
                            icon = Icons.Default.FastForward,
                            title = label,
                            isSelected = currentSpeed == spd,
                            onClick = {
                                onSpeedSelect(spd)
                                isSpeedSubmenuOpen = false
                            }
                        )
                    }
                    MenuItemRow(
                        icon = Icons.Default.Close,
                        title = "Back",
                        onClick = { isSpeedSubmenuOpen = false }
                    )
                } else {
                    // Primary menu actions
                    MenuItemRow(
                        icon = Icons.Default.PlayArrow,
                        title = "Resume",
                        onClick = onResume
                    )
                    MenuItemRow(
                        icon = Icons.Default.FileUpload,
                        title = "Save State",
                        onClick = onSaveState
                    )
                    MenuItemRow(
                        icon = Icons.Default.FileDownload,
                        title = "Load State",
                        onClick = onLoadState
                    )
                    MenuItemRow(
                        icon = Icons.Default.FastForward,
                        title = "Speed",
                        subtitle = "${currentSpeed}×",
                        onClick = { isSpeedSubmenuOpen = true }
                    )
                    MenuItemRow(
                        icon = Icons.Default.VideogameAsset,
                        title = "Controls",
                        subtitle = "Customize",
                        onClick = onControlsCustomize
                    )
                    MenuItemRow(
                        icon = Icons.Default.ScreenRotation,
                        title = "Orientation",
                        subtitle = currentOrientation,
                        onClick = onOrientationToggle
                    )
                    MenuItemRow(
                        icon = Icons.Default.Settings,
                        title = "Settings",
                        onClick = onSettings
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = ZenGlassBorder)
                    Spacer(modifier = Modifier.height(4.dp))
                    MenuItemRow(
                        icon = Icons.Default.ExitToApp,
                        title = "Exit Game",
                        tintColor = ZenAccentRed,
                        onClick = onExitGame
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuItemRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isSelected: Boolean = false,
    tintColor: Color? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tintColor ?: if (isSelected) ZenAccent else ZenTextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = tintColor ?: if (isSelected) ZenTextPrimary else ZenTextPrimary
            )
        }

        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = ZenTextSecondary
            )
        }
    }
}
