package com.kontranik.kalimbatabsviewer2.room.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.asLiveData
import com.kontranik.kalimbatabsviewer2.room.KTabRoomDatabase
import com.kontranik.kalimbatabsviewer2.room.dao.PlaylistDao
import com.kontranik.kalimbatabsviewer2.room.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistRepository(private val playlistDao: PlaylistDao) {

    val getAllPlaylists = playlistDao.getAllPlaylists()

    @WorkerThread
    suspend fun insert(playlist: Playlist): Long? {
        return playlistDao.insert(playlist)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun upsert(playlist: Playlist) {
        playlistDao.upsert(playlist)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(playlistId: Long): Int? {
        return playlistDao.deleteById(playlistId)
    }


    fun getFlowById(playlistId: Long): Flow<Playlist> {
        return playlistDao.getFlowById(playlistId)
    }

    fun deleteKTabFromPlaylist(ktabId: String, playlistId: Long) {
        return playlistDao.deleteKTabFromPlaylist(ktabId, playlistId)

    }

}