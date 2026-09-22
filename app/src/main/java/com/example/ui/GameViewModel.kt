package com.example.ui

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.EmulatorPreferences
import com.example.data.GameEntity
import com.example.data.GameRepository
import com.example.data.PreferencesManager
import com.example.data.SaveStateEntity
import com.example.emulator.CoreManager
import com.example.model.ConsoleSystem
import com.example.model.DetectedROM
import com.example.model.ROMDetector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val repository = GameRepository(application, database.gameDao())
    val preferencesManager = PreferencesManager(application)
    val coreManager = CoreManager(application)

    val allGames: StateFlow<List<GameEntity>> = repository.allGames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentGames: StateFlow<List<GameEntity>> = repository.recentGames
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val preferences: StateFlow<EmulatorPreferences> = preferencesManager.settings

    private val _activeGame = MutableStateFlow<GameEntity?>(null)
    val activeGame: StateFlow<GameEntity?> = _activeGame.asStateFlow()

    private var sessionStartTime = 0L

    private val _pendingImportUri = MutableStateFlow<Uri?>(null)
    private val _detectedRom = MutableStateFlow<DetectedROM?>(null)
    val detectedRom: StateFlow<DetectedROM?> = _detectedRom.asStateFlow()

    private val _activeSaveStates = MutableStateFlow<List<SaveStateEntity>>(emptyList())
    val activeSaveStates: StateFlow<List<SaveStateEntity>> = _activeSaveStates.asStateFlow()

    fun launchGame(game: GameEntity) {
        _activeGame.value = game
        sessionStartTime = System.currentTimeMillis()
        loadSaveStates(game.id)
    }

    fun exitActiveGame() {
        val game = _activeGame.value
        if (game != null && sessionStartTime > 0L) {
            val elapsedSecs = (System.currentTimeMillis() - sessionStartTime) / 1000L
            if (elapsedSecs > 0) {
                viewModelScope.launch {
                    repository.recordPlaySession(game.id, elapsedSecs)
                }
            }
        }
        coreManager.stop()
        _activeGame.value = null
        sessionStartTime = 0L
    }

    private fun loadSaveStates(gameId: Long) {
        viewModelScope.launch {
            repository.getSaveStates(gameId).collect { states ->
                _activeSaveStates.value = states
            }
        }
    }

    fun handleRomPicked(uri: Uri) {
        _pendingImportUri.value = uri
        viewModelScope.launch {
            var fileName = "unknown_rom.bin"
            var fileSize = 0L
            val context = getApplication<Application>()
            try {
                context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    val sizeIdx = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (cursor.moveToFirst()) {
                        if (nameIdx >= 0) fileName = cursor.getString(nameIdx) ?: fileName
                        if (sizeIdx >= 0) fileSize = cursor.getLong(sizeIdx)
                    }
                }
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    val detected = ROMDetector.detect(fileName, stream, fileSize)
                    _detectedRom.value = detected
                }
            } catch (_: Exception) {
                _detectedRom.value = DetectedROM(
                    title = fileName.substringBeforeLast('.'),
                    system = ConsoleSystem.GAME_BOY,
                    confidence = 0.5f,
                    sizeBytes = fileSize
                )
            }
        }
    }

    fun confirmImport(system: ConsoleSystem, title: String, playImmediately: Boolean) {
        val uri = _pendingImportUri.value ?: return
        viewModelScope.launch {
            val importedGame = repository.importRomFromUri(
                uri = uri,
                systemOverride = system,
                titleOverride = title
            )
            _pendingImportUri.value = null
            _detectedRom.value = null

            if (playImmediately) {
                launchGame(importedGame)
            }
        }
    }

    fun dismissImport() {
        _pendingImportUri.value = null
        _detectedRom.value = null
    }

    fun tryDemoGame() {
        viewModelScope.launch {
            val demo = repository.createHomebrewDemo()
            launchGame(demo)
        }
    }

    fun deleteGame(game: GameEntity) {
        viewModelScope.launch {
            repository.deleteGame(game)
            if (_activeGame.value?.id == game.id) {
                exitActiveGame()
            }
        }
    }

    fun saveState(slot: Int, data: ByteArray) {
        val game = _activeGame.value ?: return
        viewModelScope.launch {
            val entity = SaveStateEntity(
                gameId = game.id,
                slotIndex = slot,
                timestamp = System.currentTimeMillis(),
                title = "Slot $slot",
                stateSizeBytes = data.size.toLong(),
                stateJson = String(data)
            )
            repository.saveState(entity)
        }
    }

    fun deleteSaveState(slot: Int) {
        val game = _activeGame.value ?: return
        viewModelScope.launch {
            repository.deleteSaveState(game.id, slot)
        }
    }

    fun updatePreferences(newPreferences: EmulatorPreferences) {
        preferencesManager.updateSettings(newPreferences)
    }

    override fun onCleared() {
        super.onCleared()
        coreManager.stop()
    }
}
