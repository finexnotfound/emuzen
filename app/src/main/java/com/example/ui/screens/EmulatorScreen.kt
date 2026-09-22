package com.example.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.EmulatorPreferences
import com.example.data.GameEntity
import com.example.data.SaveStateEntity
import com.example.emulator.CoreManager
import com.example.model.ConsoleSystem
import com.example.ui.components.GlassmorphicMenu
import com.example.ui.components.SaveStateDialog
import com.example.ui.components.controllers.VirtualController
import com.example.ui.theme.ZenAccent
import com.example.ui.theme.ZenAccentAmber
import com.example.ui.theme.ZenGlassBackground
import com.example.ui.theme.ZenGlassBorder
import com.example.ui.theme.ZenOnyx
import com.example.ui.theme.ZenSurface
import com.example.ui.theme.ZenTextPrimary
import com.example.ui.theme.ZenTextSecondary
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmulatorScreen(
    game: GameEntity,
    coreManager: CoreManager,
    preferences: EmulatorPreferences,
    existingSaveStates: List<SaveStateEntity>,
    onSaveState: (slot: Int, data: ByteArray) -> Unit,
    onDeleteSaveState: (slot: Int) -> Unit,
    onExitGame: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val context = LocalContext.current
    val system = ConsoleSystem.fromId(game.consoleSystem)

    var isMenuOpen by remember { mutableStateOf(false) }
    var saveStateDialogMode by remember { mutableStateOf<Boolean?>(null) } // true for save, false for load, null for closed
    var showExitConfirm by remember { mutableStateOf(false) }
    var showTechInfo by remember { mutableStateOf(false) }

    val currentFps by coreManager.currentFps.collectAsState()
    val isFastForwarding by coreManager.isFastForwarding.collectAsState()
    val speedMultiplier by coreManager.speedMultiplier.collectAsState()
    val frameCounter by coreManager.frameCounter.collectAsState()

    var activeOrientation by remember { mutableStateOf(preferences.defaultOrientation) }

    // Start ROM session
    LaunchedEffect(game) {
        val file = File(game.filePath)
        coreManager.loadAndStart(system, file, speed = 1.0f)
    }

    // Manage screen orientation
    DisposableEffect(activeOrientation) {
        val activity = context as? Activity
        val originalOrientation = activity?.requestedOrientation
        when (activeOrientation) {
            "Landscape" -> activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
            "Portrait" -> activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            else -> activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
        onDispose {
            originalOrientation?.let { activity?.requestedOrientation = it }
            coreManager.stop()
        }
    }

    BackHandler {
        if (isMenuOpen) {
            isMenuOpen = false
            coreManager.resume()
        } else if (preferences.confirmBeforeExit) {
            showExitConfirm = true
            coreManager.pause()
        } else {
            onExitGame()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(ZenOnyx)
            .testTag("emulator_screen")
    ) {
        val isLandscape = maxWidth > maxHeight

        Column(modifier = Modifier.fillMaxSize()) {
            // Subtle In-Game Top Bar
            InGameTopBar(
                title = game.title,
                system = system,
                fps = currentFps,
                showFps = preferences.showFps,
                isFastForwarding = isFastForwarding,
                speedMultiplier = speedMultiplier,
                onFastForwardToggle = { coreManager.toggleFastForward(preferences.fastForwardSpeed) },
                onMenuClick = {
                    isMenuOpen = true
                    coreManager.pause()
                },
                onInfoClick = { showTechInfo = true }
            )

            // Primary Content Area: Display Screen(s) + Virtual Controller
            if (isLandscape) {
                // Landscape: Centered Game Screen with overlays for controller
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    GameDisplayScreens(
                        system = system,
                        primaryBitmap = coreManager.primaryBitmap,
                        secondaryBitmap = coreManager.secondaryBitmap,
                        frameCounter = frameCounter,
                        onTouchScreen = { x, y, isDown -> coreManager.onTouchScreen(x, y, isDown) },
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 4.dp)
                    )

                    // Virtual Controller Overlaid
                    VirtualController(
                        system = system,
                        isLandscape = true,
                        opacity = preferences.controllerOpacity,
                        scale = preferences.buttonScale,
                        hapticsEnabled = preferences.hapticFeedbackEnabled,
                        onButtonChange = { btn, pressed ->
                            if (pressed) coreManager.onButtonDown(btn, preferences.hapticFeedbackEnabled)
                            else coreManager.onButtonUp(btn)
                        },
                        onAnalogChange = { stick, x, y -> coreManager.onAnalogStick(stick, x, y) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            } else {
                // Portrait: Top half is Game Display; bottom half is Controller
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.05f)
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GameDisplayScreens(
                            system = system,
                            primaryBitmap = coreManager.primaryBitmap,
                            secondaryBitmap = coreManager.secondaryBitmap,
                            frameCounter = frameCounter,
                            onTouchScreen = { x, y, isDown -> coreManager.onTouchScreen(x, y, isDown) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // Lower half: Virtual Controller
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.2f),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        VirtualController(
                            system = system,
                            isLandscape = false,
                            opacity = preferences.controllerOpacity,
                            scale = preferences.buttonScale,
                            hapticsEnabled = preferences.hapticFeedbackEnabled,
                            onButtonChange = { btn, pressed ->
                                if (pressed) coreManager.onButtonDown(btn, preferences.hapticFeedbackEnabled)
                                else coreManager.onButtonUp(btn)
                            },
                            onAnalogChange = { stick, x, y -> coreManager.onAnalogStick(stick, x, y) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // In-Game Glassmorphic Menu Overlay
        if (isMenuOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        isMenuOpen = false
                        coreManager.resume()
                    },
                contentAlignment = Alignment.Center
            ) {
                GlassmorphicMenu(
                    isOpen = isMenuOpen,
                    currentSpeed = speedMultiplier,
                    currentOrientation = activeOrientation,
                    onResume = {
                        isMenuOpen = false
                        coreManager.resume()
                    },
                    onSaveState = {
                        saveStateDialogMode = true
                    },
                    onLoadState = {
                        saveStateDialogMode = false
                    },
                    onSpeedSelect = { spd ->
                        coreManager.setSpeed(spd)
                    },
                    onOrientationToggle = {
                        activeOrientation = when (activeOrientation) {
                            "Auto" -> "Landscape"
                            "Landscape" -> "Portrait"
                            else -> "Auto"
                        }
                    },
                    onControlsCustomize = {
                        isMenuOpen = false
                        onOpenSettings()
                    },
                    onSettings = {
                        isMenuOpen = false
                        onOpenSettings()
                    },
                    onExitGame = {
                        isMenuOpen = false
                        if (preferences.confirmBeforeExit) {
                            showExitConfirm = true
                        } else {
                            onExitGame()
                        }
                    }
                )
            }
        }

        // Save State & Load State Dialog
        saveStateDialogMode?.let { isSaveMode ->
            SaveStateDialog(
                isSaveMode = isSaveMode,
                existingStates = existingSaveStates,
                onSlotSelected = { slot ->
                    if (isSaveMode) {
                        val stateData = coreManager.saveState(slot)
                        if (stateData != null) {
                            onSaveState(slot, stateData)
                        }
                    } else {
                        val targetState = existingSaveStates.find { it.slotIndex == slot }
                        if (targetState != null) {
                            coreManager.loadState(slot, targetState.stateJson.toByteArray())
                        }
                    }
                    saveStateDialogMode = null
                    isMenuOpen = false
                    coreManager.resume()
                },
                onDeleteSlot = { slot ->
                    onDeleteSaveState(slot)
                },
                onDismiss = {
                    saveStateDialogMode = null
                }
            )
        }

        // Exit Game Confirmation Dialog
        if (showExitConfirm) {
            BasicAlertDialog(onDismissRequest = {
                showExitConfirm = false
                coreManager.resume()
            }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(ZenGlassBackground)
                        .border(1.dp, ZenGlassBorder, RoundedCornerShape(22.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Exit to Library?",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ZenTextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Unsaved progress will be lost unless you saved state.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ZenTextSecondary
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Cancel",
                                color = ZenTextSecondary,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        showExitConfirm = false
                                        coreManager.resume()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Exit",
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        showExitConfirm = false
                                        onExitGame()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Technical Architecture Info Dialog
        if (showTechInfo) {
            val core = coreManager.getActiveCore()
            BasicAlertDialog(onDismissRequest = { showTechInfo = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(ZenGlassBackground)
                        .border(1.dp, ZenGlassBorder, RoundedCornerShape(22.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = core?.name ?: "EmuZen Modular Core",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ZenTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = core?.status?.description ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = ZenAccent
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = core?.status?.technicalDetails ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZenTextSecondary,
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Box(
                            modifier = Modifier
                                .align(Alignment.End)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ZenSurface)
                                .clickable { showTechInfo = false }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(text = "Close", color = ZenTextPrimary, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InGameTopBar(
    title: String,
    system: ConsoleSystem,
    fps: Float,
    showFps: Boolean,
    isFastForwarding: Boolean,
    speedMultiplier: Float,
    onFastForwardToggle: () -> Unit,
    onMenuClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // System and title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onInfoClick)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(system.accentColor.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = system.shortName,
                    style = MaterialTheme.typography.labelSmall,
                    color = system.accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = ZenTextSecondary,
                maxLines = 1
            )
        }

        // Stats & Controls
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showFps) {
                Text(
                    text = "${fps.toInt()} FPS",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (fps > 55f) ZenTextSecondary else ZenAccentAmber,
                    modifier = Modifier.padding(end = 8.dp)
                )
            }

            // Quick Fast Forward button
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isFastForwarding) ZenAccent else Color.White.copy(alpha = 0.1f))
                    .clickable(onClick = onFastForwardToggle)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Fast Forward",
                        tint = if (isFastForwarding) ZenOnyx else ZenTextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    if (isFastForwarding) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${speedMultiplier}×",
                            style = MaterialTheme.typography.labelSmall,
                            color = ZenOnyx,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Menu icon
            IconButton(onClick = onMenuClick, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "In-Game Menu",
                    tint = ZenTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun GameDisplayScreens(
    system: ConsoleSystem,
    primaryBitmap: Bitmap?,
    secondaryBitmap: Bitmap?,
    frameCounter: Long,
    onTouchScreen: (Float, Float, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (system.hasDualScreen && secondaryBitmap != null) {
        // Dual Screen (NDS / 3DS) Layout: Top Screen & Bottom Touch Screen
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Screen
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(system.aspectRatio)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (primaryBitmap != null) {
                    Image(
                        bitmap = primaryBitmap.asImageBitmap(),
                        contentDescription = "Top Screen",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Bottom Touchscreen
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(system.aspectRatio)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, ZenAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                val normX = (offset.x / size.width).coerceIn(0f, 1f)
                                val normY = (offset.y / size.height).coerceIn(0f, 1f)
                                onTouchScreen(normX, normY, true)
                                tryAwaitRelease()
                                onTouchScreen(normX, normY, false)
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, _ ->
                                change.consume()
                                val normX = (change.position.x / size.width).coerceIn(0f, 1f)
                                val normY = (change.position.y / size.height).coerceIn(0f, 1f)
                                onTouchScreen(normX, normY, true)
                            },
                            onDragEnd = { onTouchScreen(0f, 0f, false) },
                            onDragCancel = { onTouchScreen(0f, 0f, false) }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = secondaryBitmap.asImageBitmap(),
                    contentDescription = "Touch Screen",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    } else {
        // Single Screen System
        Box(
            modifier = modifier
                .aspectRatio(system.aspectRatio)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (primaryBitmap != null) {
                Image(
                    bitmap = primaryBitmap.asImageBitmap(),
                    contentDescription = "Game Screen",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
