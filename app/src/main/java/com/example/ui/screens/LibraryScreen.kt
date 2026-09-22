package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.VideogameAsset
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GameEntity
import com.example.model.ConsoleSystem
import com.example.ui.components.GameCard
import com.example.ui.components.GlassCard
import com.example.ui.theme.ZenAccent
import com.example.ui.theme.ZenGlassBackground
import com.example.ui.theme.ZenGlassBorder
import com.example.ui.theme.ZenOnyx
import com.example.ui.theme.ZenSurface
import com.example.ui.theme.ZenSurfaceElevated
import com.example.ui.theme.ZenTextPrimary
import com.example.ui.theme.ZenTextSecondary
import com.example.ui.theme.ZenTextTertiary

enum class SortOption(val label: String) {
    RECENTLY_PLAYED("Recently Played"),
    RECENTLY_ADDED("Recently Added"),
    NAME("Alphabetical"),
    CONSOLE("By Console")
}

@Composable
fun LibraryScreen(
    games: List<GameEntity>,
    onAddRomClick: () -> Unit,
    onTryDemoClick: () -> Unit,
    onLaunchGame: (GameEntity) -> Unit,
    onGameDetails: (GameEntity) -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedConsoleFilter by remember { mutableStateOf<String?>("ALL") }
    var sortOption by remember { mutableStateOf(SortOption.RECENTLY_PLAYED) }
    var isGridView by remember { mutableStateOf(true) }
    var isSortMenuOpen by remember { mutableStateOf(false) }

    val filteredGames by remember(games, searchQuery, selectedConsoleFilter, sortOption) {
        derivedStateOf {
            var list = games

            // Filter by search query
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.trim().lowercase()
                list = list.filter {
                    it.title.lowercase().contains(query) ||
                            it.consoleSystem.lowercase().contains(query)
                }
            }

            // Filter by console
            if (selectedConsoleFilter != "ALL" && selectedConsoleFilter != null) {
                list = list.filter { it.consoleSystem == selectedConsoleFilter }
            }

            // Sorting
            when (sortOption) {
                SortOption.NAME -> list.sortedBy { it.title.lowercase() }
                SortOption.RECENTLY_PLAYED -> list.sortedByDescending { it.lastPlayedTimestamp ?: 0L }
                SortOption.RECENTLY_ADDED -> list.sortedByDescending { it.dateAddedTimestamp }
                SortOption.CONSOLE -> list.sortedBy { it.consoleSystem }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ZenOnyx)
            .testTag("library_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar
            LibraryHeaderBar(
                onOpenSettings = onOpenSettings
            )

            if (games.isEmpty()) {
                // Empty Library State
                EmptyLibraryView(
                    onAddRomClick = onAddRomClick,
                    onTryDemoClick = onTryDemoClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                )
            } else {
                // Search & Filter Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp)
                ) {
                    // Minimal Spotlight-style Search Input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("library_search_input"),
                        placeholder = {
                            Text(
                                text = "Search games...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = ZenTextTertiary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = ZenTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = ZenTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ZenAccent,
                            unfocusedBorderColor = ZenGlassBorder,
                            focusedContainerColor = ZenSurfaceElevated.copy(alpha = 0.6f),
                            unfocusedContainerColor = ZenSurfaceElevated.copy(alpha = 0.6f),
                            focusedTextColor = ZenTextPrimary,
                            unfocusedTextColor = ZenTextPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Console Filter Chips (Scrollable row)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChipPill(
                            label = "All (${games.size})",
                            isSelected = selectedConsoleFilter == "ALL",
                            onClick = { selectedConsoleFilter = "ALL" }
                        )

                        ConsoleSystem.entries.forEach { console ->
                            val count = games.count { it.consoleSystem == console.id }
                            if (count > 0 || games.size <= 2) {
                                FilterChipPill(
                                    label = "${console.shortName} ($count)",
                                    isSelected = selectedConsoleFilter == console.id,
                                    accentColor = console.accentColor,
                                    onClick = { selectedConsoleFilter = console.id }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // View Mode & Sort Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${filteredGames.size} ${if (filteredGames.size == 1) "game" else "games"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = ZenTextSecondary
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Sort Dropdown
                            Box {
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { isSortMenuOpen = true }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Sort,
                                        contentDescription = "Sort Options",
                                        tint = ZenTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = sortOption.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = ZenTextSecondary
                                    )
                                }

                                DropdownMenu(
                                    expanded = isSortMenuOpen,
                                    onDismissRequest = { isSortMenuOpen = false },
                                    modifier = Modifier.background(ZenSurfaceElevated)
                                ) {
                                    SortOption.entries.forEach { opt ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = opt.label,
                                                    color = if (opt == sortOption) ZenAccent else ZenTextPrimary
                                                )
                                            },
                                            onClick = {
                                                sortOption = opt
                                                isSortMenuOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Grid / List View Toggle
                            IconButton(
                                onClick = { isGridView = !isGridView },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (isGridView) Icons.Default.ViewList else Icons.Default.GridView,
                                    contentDescription = "Toggle Grid or List View",
                                    tint = ZenTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Game Items List / Grid
                if (filteredGames.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No matching games found.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ZenTextSecondary
                        )
                    }
                } else if (isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 170.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredGames, key = { it.id }) { game ->
                            GameCard(
                                game = game,
                                onPlayClick = { onLaunchGame(game) },
                                onCardClick = { onGameDetails(game) }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(filteredGames, key = { it.id }) { game ->
                            GameCard(
                                game = game,
                                onPlayClick = { onLaunchGame(game) },
                                onCardClick = { onGameDetails(game) }
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add ROM
        if (games.isNotEmpty()) {
            FloatingActionButton(
                onClick = onAddRomClick,
                shape = CircleShape,
                containerColor = ZenAccent,
                contentColor = ZenOnyx,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(24.dp)
                    .testTag("fab_add_rom")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add ROM",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun LibraryHeaderBar(
    onOpenSettings: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // EmuZen Monogram Logo
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ZenAccent.copy(alpha = 0.15f))
                    .border(1.dp, ZenAccent.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Z",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = ZenAccent
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "EmuZen",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "Retro gaming, simplified",
                    style = MaterialTheme.typography.labelSmall,
                    color = ZenTextSecondary
                )
            }
        }

        IconButton(
            onClick = onOpenSettings,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(ZenSurfaceElevated.copy(alpha = 0.5f))
                .testTag("settings_icon_button")
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = ZenTextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun EmptyLibraryView(
    onAddRomClick: () -> Unit,
    onTryDemoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(26.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(ZenAccent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VideogameAsset,
                        contentDescription = null,
                        tint = ZenAccent,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Add your games",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = ZenTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Play your own ROMs across classic consoles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = ZenTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(26.dp))

                // Large rounded ＋ Add ROM button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ZenAccent)
                        .clickable(onClick = onAddRomClick)
                        .padding(vertical = 14.dp)
                        .testTag("add_rom_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "＋ Add ROM",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = ZenOnyx
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Try Homebrew Demo button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ZenSurfaceElevated)
                        .border(1.dp, ZenGlassBorder, RoundedCornerShape(16.dp))
                        .clickable(onClick = onTryDemoClick)
                        .padding(vertical = 12.dp)
                        .testTag("try_demo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Try Built-in Zen Quest Demo",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = ZenTextPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Supported Systems Badges
        Text(
            text = "SUPPORTED SYSTEMS",
            style = MaterialTheme.typography.labelSmall,
            color = ZenTextTertiary,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ConsoleSystem.entries.forEach { console ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ZenSurfaceElevated.copy(alpha = 0.5f))
                        .border(1.dp, ZenGlassBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = console.shortName,
                        style = MaterialTheme.typography.labelSmall,
                        color = console.accentColor.copy(alpha = 0.9f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterChipPill(
    label: String,
    isSelected: Boolean,
    accentColor: Color = ZenAccent,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) accentColor.copy(alpha = 0.18f) else ZenSurfaceElevated.copy(alpha = 0.4f))
            .border(
                1.dp,
                if (isSelected) accentColor.copy(alpha = 0.55f) else ZenGlassBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) accentColor else ZenTextSecondary
        )
    }
}
