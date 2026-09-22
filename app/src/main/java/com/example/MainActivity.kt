package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameEntity
import com.example.ui.GameViewModel
import com.example.ui.components.GameDetailsSheet
import com.example.ui.components.ImportRomDialog
import com.example.ui.screens.EmulatorScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.RecentScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ZenAccent
import com.example.ui.theme.ZenGlassBackground
import com.example.ui.theme.ZenGlassBorder
import com.example.ui.theme.ZenOnyx
import com.example.ui.theme.ZenSurfaceElevated
import com.example.ui.theme.ZenTextPrimary
import com.example.ui.theme.ZenTextSecondary

enum class EmuZenTab(val label: String, val icon: ImageVector) {
    LIBRARY("Library", Icons.Default.VideogameAsset),
    RECENT("Recent", Icons.Default.History),
    SETTINGS("Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                EmuZenApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun EmuZenApp(viewModel: GameViewModel) {
    var currentTab by remember { mutableStateOf(EmuZenTab.LIBRARY) }
    var selectedGameForDetails by remember { mutableStateOf<GameEntity?>(null) }

    val allGames by viewModel.allGames.collectAsState()
    val recentGames by viewModel.recentGames.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val activeGame by viewModel.activeGame.collectAsState()
    val detectedRom by viewModel.detectedRom.collectAsState()
    val activeSaveStates by viewModel.activeSaveStates.collectAsState()

    // File picker launcher for ROM files
    val romPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.handleRomPicked(uri)
        }
    }

    // Active Game Session vs Main Launcher Navigation
    if (activeGame != null) {
        EmulatorScreen(
            game = activeGame!!,
            coreManager = viewModel.coreManager,
            preferences = preferences,
            existingSaveStates = activeSaveStates,
            onSaveState = { slot, data -> viewModel.saveState(slot, data) },
            onDeleteSaveState = { slot -> viewModel.deleteSaveState(slot) },
            onExitGame = { viewModel.exitActiveGame() },
            onOpenSettings = { currentTab = EmuZenTab.SETTINGS; viewModel.exitActiveGame() }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = ZenOnyx,
            bottomBar = {
                // Sleek Apple-style Bottom Navigation Bar
                NavigationBar(
                    containerColor = ZenGlassBackground.copy(alpha = 0.95f),
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ZenGlassBorder, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .testTag("bottom_nav_bar")
                ) {
                    EmuZenTab.entries.forEach { tab ->
                        val isSelected = currentTab == tab
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = tab },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.label,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = tab.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ZenAccent,
                                selectedTextColor = ZenAccent,
                                indicatorColor = ZenAccent.copy(alpha = 0.15f),
                                unselectedIconColor = ZenTextSecondary,
                                unselectedTextColor = ZenTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tab_transition"
                ) { tab ->
                    when (tab) {
                        EmuZenTab.LIBRARY -> LibraryScreen(
                            games = allGames,
                            onAddRomClick = { romPickerLauncher.launch("*/*") },
                            onTryDemoClick = { viewModel.tryDemoGame() },
                            onLaunchGame = { game -> viewModel.launchGame(game) },
                            onGameDetails = { game -> selectedGameForDetails = game },
                            onOpenSettings = { currentTab = EmuZenTab.SETTINGS }
                        )
                        EmuZenTab.RECENT -> RecentScreen(
                            recentGames = recentGames,
                            onLaunchGame = { game -> viewModel.launchGame(game) }
                        )
                        EmuZenTab.SETTINGS -> SettingsScreen(
                            currentPreferences = preferences,
                            onUpdatePreferences = { viewModel.updatePreferences(it) },
                            onBackClick = { currentTab = EmuZenTab.LIBRARY }
                        )
                    }
                }

                // Import ROM Confirmation Dialog
                detectedRom?.let { detected ->
                    ImportRomDialog(
                        detected = detected,
                        onConfirmImport = { system, title, playNow ->
                            viewModel.confirmImport(system, title, playNow)
                        },
                        onDismiss = { viewModel.dismissImport() }
                    )
                }

                // Game Details Sheet / Dialog
                selectedGameForDetails?.let { game ->
                    GameDetailsSheet(
                        game = game,
                        onPlayClick = {
                            selectedGameForDetails = null
                            viewModel.launchGame(game)
                        },
                        onDeleteClick = {
                            viewModel.deleteGame(game)
                            selectedGameForDetails = null
                        },
                        onDismiss = { selectedGameForDetails = null }
                    )
                }
            }
        }
    }
}
