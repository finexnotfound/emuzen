package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EmulatorPreferences
import com.example.ui.components.GlassCard
import com.example.ui.theme.ZenAccent
import com.example.ui.theme.ZenGlassBorder
import com.example.ui.theme.ZenOnyx
import com.example.ui.theme.ZenSurfaceElevated
import com.example.ui.theme.ZenTextPrimary
import com.example.ui.theme.ZenTextSecondary
import com.example.ui.theme.ZenTextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentPreferences: EmulatorPreferences,
    onUpdatePreferences: (EmulatorPreferences) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var preferences by remember { mutableStateOf(currentPreferences) }
    var showLegalDialog by remember { mutableStateOf(false) }

    fun update(newPrefs: EmulatorPreferences) {
        preferences = newPrefs
        onUpdatePreferences(newPrefs)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ZenOnyx)
            .testTag("settings_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = ZenTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Section: General
                SettingsSectionHeader(title = "GENERAL")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsOptionSelector(
                            title = "Default Orientation",
                            options = listOf("Auto", "Portrait", "Landscape"),
                            selected = preferences.defaultOrientation,
                            onSelect = { update(preferences.copy(defaultOrientation = it)) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = ZenGlassBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        SettingsSwitchRow(
                            title = "Confirm Before Exit",
                            subtitle = "Show warning dialog when closing active game",
                            checked = preferences.confirmBeforeExit,
                            onCheckedChange = { update(preferences.copy(confirmBeforeExit = it)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Emulator
                SettingsSectionHeader(title = "EMULATOR ENGINE")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsOptionSelector(
                            title = "Fast Forward Speed",
                            options = listOf("1.5×", "2.0×", "3.0×", "4.0×", "8.0×"),
                            selected = "${preferences.fastForwardSpeed}×",
                            onSelect = {
                                val spd = it.removeSuffix("×").toFloatOrNull() ?: 2.0f
                                update(preferences.copy(fastForwardSpeed = spd))
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = ZenGlassBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        SettingsSwitchRow(
                            title = "Haptic Feedback",
                            subtitle = "Vibrate subtly on virtual button tap",
                            checked = preferences.hapticFeedbackEnabled,
                            onCheckedChange = { update(preferences.copy(hapticFeedbackEnabled = it)) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = ZenGlassBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        SettingsSwitchRow(
                            title = "Show FPS Counter",
                            subtitle = "Display live emulation frame rate",
                            checked = preferences.showFps,
                            onCheckedChange = { update(preferences.copy(showFps = it)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Controls Customization
                SettingsSectionHeader(title = "VIRTUAL CONTROLS")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Controller Opacity: ${(preferences.controllerOpacity * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = ZenTextPrimary
                        )
                        Slider(
                            value = preferences.controllerOpacity,
                            onValueChange = { update(preferences.copy(controllerOpacity = it)) },
                            valueRange = 0.2f..1.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = ZenAccent,
                                activeTrackColor = ZenAccent,
                                inactiveTrackColor = ZenSurfaceElevated
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Button Scale: ${String.format("%.1f", preferences.buttonScale)}×",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = ZenTextPrimary
                        )
                        Slider(
                            value = preferences.buttonScale,
                            onValueChange = { update(preferences.copy(buttonScale = it)) },
                            valueRange = 0.8f..1.4f,
                            colors = SliderDefaults.colors(
                                thumbColor = ZenAccent,
                                activeTrackColor = ZenAccent,
                                inactiveTrackColor = ZenSurfaceElevated
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    update(preferences.copy(controllerOpacity = 0.85f, buttonScale = 1.0f))
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = ZenAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Reset to Default Layout",
                                style = MaterialTheme.typography.labelMedium,
                                color = ZenAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: Graphics
                SettingsSectionHeader(title = "GRAPHICS & DISPLAY")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsOptionSelector(
                            title = "Aspect Ratio",
                            options = listOf("Original", "4:3", "16:9", "Stretch"),
                            selected = preferences.aspectRatioMode,
                            onSelect = { update(preferences.copy(aspectRatioMode = it)) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = ZenGlassBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        SettingsSwitchRow(
                            title = "Bilinear Filtering",
                            subtitle = "Smooth pixel edges (disable for crisp retro pixels)",
                            checked = preferences.bilinearFilter,
                            onCheckedChange = { update(preferences.copy(bilinearFilter = it)) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = ZenGlassBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        SettingsSwitchRow(
                            title = "Integer Scaling",
                            subtitle = "Snap display to exact integer pixel multiples",
                            checked = preferences.integerScaling,
                            onCheckedChange = { update(preferences.copy(integerScaling = it)) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Section: About & Legal
                SettingsSectionHeader(title = "ABOUT EMUZEN")
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Version",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ZenTextPrimary
                            )
                            Text(
                                text = "1.0.0 (Zen Engine)",
                                style = MaterialTheme.typography.bodySmall,
                                color = ZenTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = ZenGlassBorder)
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLegalDialog = true },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = ZenAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Legal Disclaimer & Architecture",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ZenTextPrimary
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = ZenTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        if (showLegalDialog) {
            BasicAlertDialog(onDismissRequest = { showLegalDialog = false }) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(22.dp)) {
                        Text(
                            text = "Legal & Architecture Notice",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ZenTextPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "EmuZen is an independent open-source emulator frontend. No copyrighted ROMs, BIOS images, or proprietary assets are bundled.\n\nEmuZen features a strictly modular core design: frontend UI is completely decoupled from low-level emulation engines, ensuring transparent and legally compliant operation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZenTextSecondary,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ZenAccent)
                                .clickable { showLegalDialog = false }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = "Got it",
                                color = ZenOnyx,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = ZenTextTertiary,
        letterSpacing = 1.2.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = ZenTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = ZenTextSecondary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ZenOnyx,
                checkedTrackColor = ZenAccent,
                uncheckedThumbColor = ZenTextSecondary,
                uncheckedTrackColor = ZenSurfaceElevated
            )
        )
    }
}

@Composable
private fun SettingsOptionSelector(
    title: String,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = ZenTextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { opt ->
                val isSelected = opt == selected
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) ZenAccent.copy(alpha = 0.2f) else ZenSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) ZenAccent.copy(alpha = 0.6f) else ZenGlassBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelect(opt) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = opt,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) ZenAccent else ZenTextSecondary
                    )
                }
            }
        }
    }
}
