package com.kontranik.kalimbatabsviewer2.room.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import com.kontranik.kalimbatabsviewer2.room.dao.PlaylistDao
import com.kontranik.kalimbatabsviewer2.room.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val playlistDao: PlaylistDao) {

    val getAllPlaylists = playlistDao.getAllPlaylists().asLiveData()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(playlist: Playlist): Long? {
        return playlistDao.insert(playlist)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(playlistId: Long): Int? {
        return playlistDao.deleteById(playlistId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(playlist: Playlist): Int? {
        return playlistDao.update(playlist)
    }

    fun getById(playlistId: Long): Flow<Playlist> {
        return playlistDao.getById(playlistId)
    }
}