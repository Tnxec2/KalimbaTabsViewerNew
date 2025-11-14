package com.kontranik.kalimbatabsviewer.room.repository

import androidx.annotation.WorkerThread
import androidx.paging.PagingSource
import com.kontranik.kalimbatabsviewer.room.dao.PlaylistKtabDao
import com.kontranik.kalimbatabsviewer.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer.room.model.PlaylistKtabCrossRef
import kotlinx.coroutines.flow.Flow

class PlaylistKtabRepository(private val playlistKtabDao: PlaylistKtabDao) {

    fun getAllKTabsToPlaylistId(playlistId: Long): Flow<List<KTabRoom>> {
        return playlistKtabDao.getAllKTabsToPlaylistId(playlistId)
    }

    fun getPageKTabsToPlaylistIdFiltered(
        playlistId: Long, searchText: String?, sort: String, ascending: Boolean): PagingSource<Int, KTabRoom> {
        return playlistKtabDao.getPageKTabsToPlaylistIdFiltered(playlistId, searchText, sort, ascending)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteKtabFromPlaylist(playlistId: Long, kTabId: String): Int {
        return playlistKtabDao.deleteKtabFromPlaylist(playlistId, kTabId)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(playlistKtabCrossRef: PlaylistKtabCrossRef): Long {
        return playlistKtabDao.insertPlaylistKtabCrossRef(playlistKtabCrossRef)
    }
}