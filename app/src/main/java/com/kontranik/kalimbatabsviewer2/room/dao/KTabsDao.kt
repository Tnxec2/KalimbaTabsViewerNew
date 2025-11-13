package com.kontranik.kalimbatabsviewer2.room.dao


import android.util.Log
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import kotlinx.coroutines.flow.Flow


@Dao
interface KTabsDao {

    @RawQuery(observedEntities = [KTabRoom::class])
    fun getPagedSongsViaQuery(query: SupportSQLiteQuery): PagingSource<Int, KTabRoom>

    fun getPage(
        showOnlyBookmarked: Boolean,
        searchText: String?,
        sortColumn: String,
        sortAscending: Boolean): PagingSource<Int, KTabRoom> {
        val where = StringBuilder("")
        if (showOnlyBookmarked) where.append(" WHERE bookmarked = 1 ")
        if (searchText != null && searchText.isNotEmpty()) {
            if (where.isBlank()) where.append(" WHERE ")
            else where.append(" AND ")
            where.append(" LOWER(title) LIKE LOWER('%$searchText%') OR LOWER(interpreter) LIKE LOWER('%$searchText%') ")
        }
        var sortQ = "ORDER BY LOWER($sortColumn) ${if (sortAscending) "ASC" else "DESC "}"
        if (sortColumn != "title") {
            sortQ += ", title ASC "
        }
        val statement = "SELECT * FROM ktabs_table $where $sortQ"
        val query = SimpleSQLiteQuery(statement)
        Log.d("query", query.sql)
        return getPagedSongsViaQuery(query)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(ktab: KTabRoom)

    @Query("DELETE FROM ktabs_table WHERE kTabId = :id")
    fun deleteById(id: String): Int


    @Query("SELECT * FROM ktabs_table ORDER BY updated DESC LIMIT 1")
    fun getLastUpdated(): KTabRoom?

    @Upsert
    suspend fun update(ktab: KTabRoom): Long

    @Upsert
    suspend fun updateAll(ktabs: List<KTabRoom>): List<Long>

    @Query("SELECT * FROm ktabs_table WHERE kTabId = :id")
    fun getById(id: String): KTabRoom?

    @Query("SELECT * FROm ktabs_table WHERE kTabId = :id")
    fun getFlowById(id: String): Flow<KTabRoom?>

    @Query("INSERT INTO playlist_ktab_cross_ref (playlistId, kTabId) VALUES (:playlistId, :ktabId)")
    fun addKtabToPlaylist(ktabId: String, playlistId: Long)

    @Query("SELECT * FROM ktabs_table WHERE  " +
            " kTabId IN (SELECT kTabId FROM playlist_ktab_cross_ref WHERE playlistId = :playlistId) ORDER BY title")
    fun getSongsForPlaylistId(playlistId: Long): PagingSource<Int, KTabRoom>
}