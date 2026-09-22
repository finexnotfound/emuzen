package com.example.model

import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.Locale

data class DetectedROM(
    val title: String,
    val system: ConsoleSystem,
    val confidence: Float,
    val internalTitle: String? = null,
    val internalCode: String? = null,
    val sizeBytes: Long = 0L,
    val formatDescription: String = ""
)

object ROMDetector {

    fun detect(fileName: String, inputStream: InputStream?, fileSizeBytes: Long): DetectedROM {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        val headerBytes = ByteArray(512)
        var bytesRead = 0
        try {
            if (inputStream != null) {
                bytesRead = inputStream.read(headerBytes, 0, headerBytes.size)
            }
        } catch (_: Exception) {}

        // 1. Check Magic Bytes / Header
        // NES Check: "NES\x1A"
        if (bytesRead >= 4 &&
            headerBytes[0] == 'N'.code.toByte() &&
            headerBytes[1] == 'E'.code.toByte() &&
            headerBytes[2] == 'S'.code.toByte() &&
            headerBytes[3] == 0x1A.toByte()
        ) {
            val title = cleanTitleFromFilename(fileName)
            return DetectedROM(
                title = title,
                system = ConsoleSystem.NES,
                confidence = 0.99f,
                sizeBytes = fileSizeBytes,
                formatDescription = "iNES Format Header"
            )
        }

        // N64 Check: 0x80 0x37 0x12 0x40 (z64) or 0x37 0x80 0x40 0x12 (v64)
        if (bytesRead >= 4) {
            val b0 = headerBytes[0].toInt() and 0xFF
            val b1 = headerBytes[1].toInt() and 0xFF
            val b2 = headerBytes[2].toInt() and 0xFF
            val b3 = headerBytes[3].toInt() and 0xFF
            if ((b0 == 0x80 && b1 == 0x37 && b2 == 0x12 && b3 == 0x40) ||
                (b0 == 0x37 && b1 == 0x80 && b2 == 0x40 && b3 == 0x12) ||
                (b0 == 0x40 && b1 == 0x12 && b2 == 0x37 && b3 == 0x80)
            ) {
                val title = cleanTitleFromFilename(fileName)
                return DetectedROM(
                    title = title,
                    system = ConsoleSystem.N64,
                    confidence = 0.98f,
                    sizeBytes = fileSizeBytes,
                    formatDescription = "N64 ROM Image"
                )
            }
        }

        // Game Boy / GBC Header: Title at 0x0134..0x0142, CGB flag at 0x0143
        if (bytesRead >= 0x0144) {
            val cgbFlag = headerBytes[0x0143].toInt() and 0xFF
            val rawTitle = extractAscii(headerBytes, 0x0134, 15)
            if (rawTitle.isNotEmpty() && rawTitle.all { it.isLetterOrDigit() || it.isWhitespace() || it == '-' || it == '_' }) {
                val isGBC = cgbFlag == 0x80 || cgbFlag == 0xC0 || extension == "gbc"
                val system = if (isGBC) ConsoleSystem.GAME_BOY_COLOR else ConsoleSystem.GAME_BOY
                val title = rawTitle.ifBlank { cleanTitleFromFilename(fileName) }
                return DetectedROM(
                    title = title,
                    system = system,
                    confidence = 0.95f,
                    internalTitle = rawTitle,
                    sizeBytes = fileSizeBytes,
                    formatDescription = if (isGBC) "Game Boy Color Cartridge" else "Game Boy Cartridge"
                )
            }
        }

        // GBA Header: Title at 0x00A0..0x00AB (12 bytes), Game code at 0x00AC..0x00AF (4 bytes)
        if (bytesRead >= 0x00B0 && extension == "gba") {
            val rawTitle = extractAscii(headerBytes, 0x00A0, 12)
            val code = extractAscii(headerBytes, 0x00AC, 4)
            val title = if (rawTitle.isNotBlank() && rawTitle.all { it.isLetterOrDigit() || it.isWhitespace() }) rawTitle else cleanTitleFromFilename(fileName)
            return DetectedROM(
                title = title,
                system = ConsoleSystem.GAME_BOY_ADVANCE,
                confidence = 0.95f,
                internalTitle = rawTitle,
                internalCode = code,
                sizeBytes = fileSizeBytes,
                formatDescription = "GBA Cartridge"
            )
        }

        // NDS Header: Title at 0x0000..0x000B (12 bytes), Game code at 0x000C..0x000F
        if (bytesRead >= 0x0010 && extension == "nds") {
            val rawTitle = extractAscii(headerBytes, 0x0000, 12)
            val code = extractAscii(headerBytes, 0x000C, 4)
            val title = if (rawTitle.isNotBlank() && rawTitle.all { it.isLetterOrDigit() || it.isWhitespace() }) rawTitle else cleanTitleFromFilename(fileName)
            return DetectedROM(
                title = title,
                system = ConsoleSystem.NDS,
                confidence = 0.95f,
                internalTitle = rawTitle,
                internalCode = code,
                sizeBytes = fileSizeBytes,
                formatDescription = "Nintendo DS Card"
            )
        }

        // 2. Extension Match Fallback
        val matchedSystem = when (extension) {
            "gb" -> ConsoleSystem.GAME_BOY
            "gbc" -> ConsoleSystem.GAME_BOY_COLOR
            "gba" -> ConsoleSystem.GAME_BOY_ADVANCE
            "nes", "unf", "fds" -> ConsoleSystem.NES
            "smc", "sfc", "fig" -> ConsoleSystem.SNES
            "n64", "z64", "v64" -> ConsoleSystem.N64
            "nds" -> ConsoleSystem.NDS
            "3ds", "cia", "cxi" -> ConsoleSystem.NINTENDO_3DS
            "cue", "chd", "pbp" -> ConsoleSystem.PS1
            "iso" -> if (fileSizeBytes > 800 * 1024 * 1024L) ConsoleSystem.PS1 else ConsoleSystem.PSP
            "cso" -> ConsoleSystem.PSP
            "a26" -> ConsoleSystem.ATARI
            "gg" -> ConsoleSystem.GAME_GEAR
            "bin" -> if (fileSizeBytes < 128 * 1024) ConsoleSystem.ATARI else ConsoleSystem.PS1
            else -> null
        }

        val cleanedTitle = cleanTitleFromFilename(fileName)
        return if (matchedSystem != null) {
            DetectedROM(
                title = cleanedTitle,
                system = matchedSystem,
                confidence = 0.85f,
                sizeBytes = fileSizeBytes,
                formatDescription = "${matchedSystem.fullName} Image"
            )
        } else {
            // Default or unrecognized
            DetectedROM(
                title = cleanedTitle,
                system = ConsoleSystem.GAME_BOY,
                confidence = 0.30f,
                sizeBytes = fileSizeBytes,
                formatDescription = "Unrecognized Format"
            )
        }
    }

    private fun extractAscii(bytes: ByteArray, offset: Int, length: Int): String {
        val end = minOf(offset + length, bytes.size)
        val sub = bytes.copyOfRange(offset, end)
        val nullIdx = sub.indexOf(0.toByte())
        val actualBytes = if (nullIdx >= 0) sub.copyOfRange(0, nullIdx) else sub
        return String(actualBytes, StandardCharsets.US_ASCII).trim()
    }

    fun cleanTitleFromFilename(fileName: String): String {
        var name = fileName.substringBeforeLast('.')
        // Remove content in brackets/parentheses e.g. (USA), (En,Fr,De), [!], (v1.1), (Rev 1)
        name = name.replace(Regex("\\([^)]*\\)"), " ")
        name = name.replace(Regex("\\[[^\\]]*\\]"), " ")
        // Replace underscores and extra hyphens with spaces
        name = name.replace('_', ' ')
        name = name.replace(Regex("\\s+"), " ").trim()
        if (name.isEmpty()) return fileName

        // Capitalize words cleanly
        return name.split(" ").joinToString(" ") { word ->
            if (word.length <= 1) word.uppercase(Locale.ROOT)
            else word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        }
    }
}
