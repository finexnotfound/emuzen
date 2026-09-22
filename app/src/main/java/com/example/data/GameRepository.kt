package com.example.data

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.model.ConsoleSystem
import com.example.model.ROMDetector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class GameRepository(
    private val context: Context,
    private val gameDao: GameDao
) {
    val allGames: Flow<List<GameEntity>> = gameDao.getAllGames()
    val recentGames: Flow<List<GameEntity>> = gameDao.getRecentGames()

    suspend fun getGameById(id: Long): GameEntity? = withContext(Dispatchers.IO) {
        gameDao.getGameById(id)
    }

    suspend fun insertGame(game: GameEntity): Long = withContext(Dispatchers.IO) {
        gameDao.insertGame(game)
    }

    suspend fun updateGame(game: GameEntity) = withContext(Dispatchers.IO) {
        gameDao.updateGame(game)
    }

    suspend fun deleteGame(game: GameEntity) = withContext(Dispatchers.IO) {
        // Also delete file if inside app private storage
        try {
            val file = File(game.filePath)
            if (file.exists() && file.startsWith(context.filesDir)) {
                file.delete()
            }
        } catch (_: Exception) {}
        gameDao.deleteGame(game)
    }

    suspend fun recordPlaySession(gameId: Long, deltaSeconds: Long) = withContext(Dispatchers.IO) {
        gameDao.recordPlaySession(gameId, deltaSeconds)
    }

    fun getSaveStates(gameId: Long): Flow<List<SaveStateEntity>> = gameDao.getSaveStatesForGame(gameId)

    suspend fun getSaveState(gameId: Long, slotIndex: Int): SaveStateEntity? = withContext(Dispatchers.IO) {
        gameDao.getSaveState(gameId, slotIndex)
    }

    suspend fun saveState(saveState: SaveStateEntity): Long = withContext(Dispatchers.IO) {
        gameDao.saveState(saveState)
    }

    suspend fun deleteSaveState(gameId: Long, slotIndex: Int) = withContext(Dispatchers.IO) {
        gameDao.deleteSaveState(gameId, slotIndex)
    }

    suspend fun getControllerSetting(consoleId: String): ControllerSettingEntity = withContext(Dispatchers.IO) {
        gameDao.getControllerSetting(consoleId) ?: ControllerSettingEntity(consoleId = consoleId)
    }

    suspend fun saveControllerSetting(setting: ControllerSettingEntity) = withContext(Dispatchers.IO) {
        gameDao.saveControllerSetting(setting)
    }

    suspend fun importRomFromUri(
        uri: Uri,
        systemOverride: ConsoleSystem? = null,
        titleOverride: String? = null
    ): GameEntity = withContext(Dispatchers.IO) {
        var fileName = "imported_rom.bin"
        var fileSize = 0L

        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                if (nameIndex >= 0) fileName = cursor.getString(nameIndex) ?: fileName
                if (sizeIndex >= 0) fileSize = cursor.getLong(sizeIndex)
            }
        }

        // Copy file to internal roms directory
        val romsDir = File(context.filesDir, "roms").apply { mkdirs() }
        val safeFileName = "${System.currentTimeMillis()}_${fileName.replace("[^a-zA-Z0-9._-]".toRegex(), "_")}"
        val targetFile = File(romsDir, safeFileName)

        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }

        val detected = targetFile.inputStream().use { stream ->
            ROMDetector.detect(fileName, stream, targetFile.length())
        }

        val resolvedSystem = systemOverride ?: detected.system
        val resolvedTitle = titleOverride?.takeIf { it.isNotBlank() } ?: detected.title

        val newGame = GameEntity(
            title = resolvedTitle,
            fileName = fileName,
            filePath = targetFile.absolutePath,
            consoleSystem = resolvedSystem.id,
            coverArtUri = null,
            playTimeSeconds = 0L,
            lastPlayedTimestamp = null,
            dateAddedTimestamp = System.currentTimeMillis(),
            isFavorite = false,
            fileSizeBytes = targetFile.length()
        )

        val id = gameDao.insertGame(newGame)
        newGame.copy(id = id)
    }

    suspend fun createHomebrewDemo(): GameEntity = withContext(Dispatchers.IO) {
        val romsDir = File(context.filesDir, "roms").apply { mkdirs() }
        val demoFile = File(romsDir, "zen_quest_demo.gb")
        if (!demoFile.exists()) {
            demoFile.writeBytes(ByteArray(32768) { i ->
                if (i in 0x0134..0x0142) {
                    val title = "ZEN QUEST"
                    val charIdx = i - 0x0134
                    if (charIdx < title.length) title[charIdx].code.toByte() else 0
                } else (i % 256).toByte()
            })
        }

        val demoGame = GameEntity(
            title = "Zen Quest (Homebrew Demo)",
            fileName = "zen_quest_demo.gb",
            filePath = demoFile.absolutePath,
            consoleSystem = ConsoleSystem.GAME_BOY.id,
            coverArtUri = null,
            playTimeSeconds = 0L,
            lastPlayedTimestamp = null,
            dateAddedTimestamp = System.currentTimeMillis(),
            isFavorite = true,
            fileSizeBytes = demoFile.length()
        )
        val id = gameDao.insertGame(demoGame)
        demoGame.copy(id = id)
    }
}
