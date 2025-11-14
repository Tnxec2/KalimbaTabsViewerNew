package com.kontranik.kalimbatabsviewer.room.dao


import android.util.Log
import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.kontranik.kalimbatabsviewer.room.model.KTabRoom
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
        val whereClauses = mutableListOf<String>()
        val whereObjects = mutableListOf<Any>()

        if (showOnlyBookmarked) whereClauses.add("bookmarked = 1")
        if (searchText != null && searchText.isNotEmpty()) {
            whereClauses.add("(LOWER(title) LIKE LOWER(:searchText) OR LOWER(interpreter) LIKE LOWER(:searchText))")
            whereObjects.add("%$searchText%")
        }
        val where = if (whereClauses.isNotEmpty()) whereClauses.joinToString(separator = " AND ", prefix = "WHERE ") else ""

        val sortOrder = if (sortAscending) "ASC" else "DESC"
        // Schutz vor SQL-Injection in Spaltennamen
        val safeSortColumn = when (sortColumn) {
            "title", "interpreter", "difficulty", "updated" -> sortColumn
            else -> "title" // Standard-Sortierung als Fallback
        }

        var sortQ = "ORDER BY LOWER($safeSortColumn) $sortOrder"
        if (safeSortColumn != "title") {
            sortQ += ", LOWER(title) ASC"
        }

        val statement = "SELECT * FROM ktabs_table $where $sortQ"
        val query = SimpleSQLiteQuery(statement, whereObjects.toTypedArray())

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
    fun getByIdFlow(id: String): Flow<KTabRoom?>

    @Query("INSERT INTO playlist_ktab_cross_ref (playlistId, kTabId) VALUES (:playlistId, :ktabId)")
    fun addKtabToPlaylist(ktabId: String, playlistId: Long)

    @Query("SELECT * FROM ktabs_table WHERE  " +
            " kTabId IN (SELECT kTabId FROM playlist_ktab_cross_ref WHERE playlistId = :playlistId) ORDER BY title")
    fun getSongsForPlaylistId(playlistId: Long): PagingSource<Int, KTabRoom>
}