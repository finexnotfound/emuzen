package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.*

enum class ConsoleSystem(
    val id: String,
    val fullName: String,
    val shortName: String,
    val manufacturer: String,
    val extensions: List<String>,
    val aspectRatio: Float,
    val nativeWidth: Int,
    val nativeHeight: Int,
    val hasDualScreen: Boolean = false,
    val hasTouchScreen: Boolean = false,
    val accentColor: Color = ZenAccent
) {
    GAME_BOY(
        id = "gb",
        fullName = "Game Boy",
        shortName = "GB",
        manufacturer = "Nintendo",
        extensions = listOf("gb"),
        aspectRatio = 160f / 144f,
        nativeWidth = 160,
        nativeHeight = 144,
        accentColor = ColorGB
    ),
    GAME_BOY_COLOR(
        id = "gbc",
        fullName = "Game Boy Color",
        shortName = "GBC",
        manufacturer = "Nintendo",
        extensions = listOf("gbc"),
        aspectRatio = 160f / 144f,
        nativeWidth = 160,
        nativeHeight = 144,
        accentColor = ColorGBC
    ),
    GAME_BOY_ADVANCE(
        id = "gba",
        fullName = "Game Boy Advance",
        shortName = "GBA",
        manufacturer = "Nintendo",
        extensions = listOf("gba"),
        aspectRatio = 240f / 160f,
        nativeWidth = 240,
        nativeHeight = 160,
        accentColor = ColorGBA
    ),
    NES(
        id = "nes",
        fullName = "Nintendo Entertainment System",
        shortName = "NES",
        manufacturer = "Nintendo",
        extensions = listOf("nes", "unf", "fds"),
        aspectRatio = 4f / 3f,
        nativeWidth = 256,
        nativeHeight = 240,
        accentColor = ColorNES
    ),
    SNES(
        id = "snes",
        fullName = "Super Nintendo",
        shortName = "SNES",
        manufacturer = "Nintendo",
        extensions = listOf("smc", "sfc", "fig"),
        aspectRatio = 4f / 3f,
        nativeWidth = 256,
        nativeHeight = 224,
        accentColor = ColorSNES
    ),
    N64(
        id = "n64",
        fullName = "Nintendo 64",
        shortName = "N64",
        manufacturer = "Nintendo",
        extensions = listOf("n64", "z64", "v64"),
        aspectRatio = 4f / 3f,
        nativeWidth = 320,
        nativeHeight = 240,
        accentColor = ColorN64
    ),
    NDS(
        id = "nds",
        fullName = "Nintendo DS",
        shortName = "NDS",
        manufacturer = "Nintendo",
        extensions = listOf("nds"),
        aspectRatio = 4f / 3f,
        nativeWidth = 256,
        nativeHeight = 192,
        hasDualScreen = true,
        hasTouchScreen = true,
        accentColor = ColorNDS
    ),
    NINTENDO_3DS(
        id = "3ds",
        fullName = "Nintendo 3DS",
        shortName = "3DS",
        manufacturer = "Nintendo",
        extensions = listOf("3ds", "cia", "cxi"),
        aspectRatio = 5f / 3f,
        nativeWidth = 400,
        nativeHeight = 240,
        hasDualScreen = true,
        hasTouchScreen = true,
        accentColor = Color3DS
    ),
    PS1(
        id = "ps1",
        fullName = "PlayStation 1",
        shortName = "PS1",
        manufacturer = "Sony",
        extensions = listOf("iso", "bin", "cue", "pbp", "chd", "img"),
        aspectRatio = 4f / 3f,
        nativeWidth = 320,
        nativeHeight = 240,
        accentColor = ColorPS1
    ),
    PSP(
        id = "psp",
        fullName = "PlayStation Portable",
        shortName = "PSP",
        manufacturer = "Sony",
        extensions = listOf("iso", "cso", "pbp"),
        aspectRatio = 16f / 9f,
        nativeWidth = 480,
        nativeHeight = 272,
        accentColor = ColorPSP
    ),
    ATARI(
        id = "atari",
        fullName = "Atari 2600",
        shortName = "Atari",
        manufacturer = "Atari",
        extensions = listOf("a26", "bin"),
        aspectRatio = 4f / 3f,
        nativeWidth = 160,
        nativeHeight = 192,
        accentColor = ColorAtari
    ),
    GAME_GEAR(
        id = "gg",
        fullName = "Sega Game Gear",
        shortName = "Game Gear",
        manufacturer = "Sega",
        extensions = listOf("gg"),
        aspectRatio = 4f / 3f,
        nativeWidth = 160,
        nativeHeight = 144,
        accentColor = ColorGameGear
    );

    companion object {
        fun fromId(id: String): ConsoleSystem {
            return entries.find { it.id.equals(id, ignoreCase = true) || it.shortName.equals(id, ignoreCase = true) } ?: GAME_BOY
        }

        fun fromExtension(ext: String): ConsoleSystem? {
            val cleanExt = ext.lowercase().removePrefix(".")
            // Special case for .bin: might be PS1 or Atari, prefer PS1 or allow user override
            return entries.find { system ->
                system.extensions.any { it.equals(cleanExt, ignoreCase = true) }
            }
        }
    }
}
