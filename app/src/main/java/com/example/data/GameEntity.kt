package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val fileName: String,
    val filePath: String,
    val consoleSystem: String, // from ConsoleSystem.id
    val coverArtUri: String? = null,
    val playTimeSeconds: Long = 0L,
    val lastPlayedTimestamp: Long? = null,
    val dateAddedTimestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val fileSizeBytes: Long = 0L,
    val customNotes: String = ""
)

@Entity(tableName = "save_states")
data class SaveStateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val gameId: Long,
    val slotIndex: Int, // 1..5
    val timestamp: Long = System.currentTimeMillis(),
    val title: String = "Save Slot",
    val screenshotPath: String? = null,
    val stateSizeBytes: Long = 0L,
    val stateJson: String = "{}"
)

@Entity(tableName = "controller_settings")
data class ControllerSettingEntity(
    @PrimaryKey
    val consoleId: String,
    val opacity: Float = 0.85f,
    val buttonScale: Float = 1.0f,
    val hapticsEnabled: Boolean = true,
    val hideButtonsCsv: String = ""
)
