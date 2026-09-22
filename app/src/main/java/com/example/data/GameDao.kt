package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM games ORDER BY dateAddedTimestamp DESC")
    fun getAllGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE lastPlayedTimestamp IS NOT NULL ORDER BY lastPlayedTimestamp DESC")
    fun getRecentGames(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getGameById(id: Long): GameEntity?

    @Query("SELECT * FROM games WHERE consoleSystem = :consoleId ORDER BY title ASC")
    fun getGamesByConsole(consoleId: String): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity): Long

    @Update
    suspend fun updateGame(game: GameEntity)

    @Delete
    suspend fun deleteGame(game: GameEntity)

    @Query("UPDATE games SET playTimeSeconds = playTimeSeconds + :deltaSeconds, lastPlayedTimestamp = :timestamp WHERE id = :gameId")
    suspend fun recordPlaySession(gameId: Long, deltaSeconds: Long, timestamp: Long = System.currentTimeMillis())

    // Save States
    @Query("SELECT * FROM save_states WHERE gameId = :gameId ORDER BY slotIndex ASC")
    fun getSaveStatesForGame(gameId: Long): Flow<List<SaveStateEntity>>

    @Query("SELECT * FROM save_states WHERE gameId = :gameId AND slotIndex = :slotIndex LIMIT 1")
    suspend fun getSaveState(gameId: Long, slotIndex: Int): SaveStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveState(saveState: SaveStateEntity): Long

    @Query("DELETE FROM save_states WHERE gameId = :gameId AND slotIndex = :slotIndex")
    suspend fun deleteSaveState(gameId: Long, slotIndex: Int)

    // Controller Layout Settings
    @Query("SELECT * FROM controller_settings WHERE consoleId = :consoleId")
    suspend fun getControllerSetting(consoleId: String): ControllerSettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveControllerSetting(setting: ControllerSettingEntity)
}
