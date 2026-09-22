package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.model.ConsoleSystem
import com.example.model.DetectedROM
import com.example.ui.theme.ZenAccent
import com.example.ui.theme.ZenGlassBackground
import com.example.ui.theme.ZenGlassBorder
import com.example.ui.theme.ZenGlassBorderStrong
import com.example.ui.theme.ZenOnyx
import com.example.ui.theme.ZenSurfaceElevated
import com.example.ui.theme.ZenTextPrimary
import com.example.ui.theme.ZenTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportRomDialog(
    detected: DetectedROM,
    onConfirmImport: (system: ConsoleSystem, title: String, playImmediately: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSystem by remember { mutableStateOf(detected.system) }
    var gameTitle by remember { mutableStateOf(detected.title) }
    var isDropdownOpen by remember { mutableStateOf(false) }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(ZenGlassBackground)
                .border(1.dp, ZenGlassBorderStrong, RoundedCornerShape(26.dp))
                .padding(22.dp)
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(selectedSystem.accentColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideogameAsset,
                                contentDescription = null,
                                tint = selectedSystem.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Import Game",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ZenTextPrimary
                            )
                            Text(
                                text = detected.formatDescription.ifBlank { "ROM Detected" },
                                style = MaterialTheme.typography.bodySmall,
                                color = ZenTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = ZenTextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Title Input
                Text(
                    text = "Game Title",
                    style = MaterialTheme.typography.labelSmall,
                    color = ZenTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = gameTitle,
                    onValueChange = { gameTitle = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("import_title_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ZenAccent,
                        unfocusedBorderColor = ZenGlassBorder,
                        focusedTextColor = ZenTextPrimary,
                        unfocusedTextColor = ZenTextPrimary,
                        focusedContainerColor = ZenSurfaceElevated.copy(alpha = 0.5f),
                        unfocusedContainerColor = ZenSurfaceElevated.copy(alpha = 0.5f)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // System Selector
                Text(
                    text = "Console System",
                    style = MaterialTheme.typography.labelSmall,
                    color = ZenTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ZenSurfaceElevated.copy(alpha = 0.5f))
                            .border(1.dp, ZenGlassBorder, RoundedCornerShape(14.dp))
                            .clickable { isDropdownOpen = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${selectedSystem.fullName} (${selectedSystem.shortName})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ZenTextPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select System",
                            tint = ZenTextSecondary
                        )
                    }

                    DropdownMenu(
                        expanded = isDropdownOpen,
                        onDismissRequest = { isDropdownOpen = false },
                        modifier = Modifier.background(ZenOnyx)
                    ) {
                        ConsoleSystem.entries.forEach { sys ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = sys.fullName,
                                        color = if (sys == selectedSystem) ZenAccent else ZenTextPrimary
                                    )
                                },
                                onClick = {
                                    selectedSystem = sys
                                    isDropdownOpen = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Add to Library only
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ZenSurfaceElevated)
                            .border(1.dp, ZenGlassBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                onConfirmImport(selectedSystem, gameTitle, false)
                            }
                            .padding(vertical = 12.dp)
                            .testTag("btn_add_to_library"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Add to Library",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ZenTextPrimary
                        )
                    }

                    // Play Immediately
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(ZenAccent)
                            .clickable {
                                onConfirmImport(selectedSystem, gameTitle, true)
                            }
                            .padding(vertical = 12.dp)
                            .testTag("btn_play_immediately"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = ZenOnyx,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Play Now",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = ZenOnyx
                            )
                        }
                    }
                }
            }
        }
    }
}
