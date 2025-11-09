package com.kontranik.kalimbatabsviewer2.room.dao

import androidx.room.*
import com.kontranik.kalimbatabsviewer2.room.model.Playlist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM Playlist ORDER BY playlistName")
    fun getAllPlaylists(): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(playlist: Playlist): Long

    @Query("DELETE FROM playlist WHERE playlistId = :playlistid")
    fun deleteById(playlistid: Long): Int

    @Update
    suspend fun update(playlist: Playlist): Int

    @Query("SELECT * FROM Playlist WHERE playlistId = :id")
    fun getById(id: Long): Flow<Playlist>
}