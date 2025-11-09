package com.kontranik.kalimbatabsviewer2.room.repository

import androidx.annotation.WorkerThread
import androidx.paging.PagingSource
import com.kontranik.kalimbatabsviewer2.room.dao.KTabsDao
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import kotlinx.coroutines.flow.Flow


class KTabsRepository(private val kTabsDao: KTabsDao) {

    fun pageKtabs(bookmarked: Boolean?, searchText: String?, sort: String, ascending: Boolean): PagingSource<Int, KTabRoom> {
        return kTabsDao.getPage(bookmarked, searchText, sort, ascending)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getLastUpdated(): KTabRoom? {
        return kTabsDao.getLastUpdated()
    }

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(kTabRoom: KTabRoom) {
        kTabsDao.insert(kTabRoom)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(kTabId: String): Int {
        return kTabsDao.deleteById(kTabId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(kTabRoom: KTabRoom) {
        kTabsDao.update(kTabRoom)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateAll(kTabs: List<KTabRoom>): List<Long> {
        return kTabsDao.updateAll(kTabs)
    }

    @WorkerThread
    fun getById(kTabId: String): KTabRoom? {
        return kTabsDao.getById(kTabId)
    }
}