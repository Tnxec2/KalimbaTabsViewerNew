package com.kontranik.kalimbatabsviewer2.room.dao

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer2.room.model.Playlist
import com.kontranik.kalimbatabsviewer2.room.model.PlaylistKtabCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistKtabDao {

    @RawQuery(observedEntities = [KTabRoom::class])
    fun getPageKtabsViaQuery(query: SupportSQLiteQuery): PagingSource<Int, KTabRoom>

    fun getPageKTabsToPlaylistIdFiltered(
        playlistid: Long,
        searchText: String?,
        sortColumn: String,
        sortAscending: Boolean): PagingSource<Int, KTabRoom> {
        val where = StringBuilder("")
        if (searchText != null) {
            where.append(" AND LOWER(title) LIKE LOWER('%$searchText%') OR LOWER(interpreter) LIKE LOWER('%$searchText%') ")
        }
        var sortQ = " ORDER BY LOWER($sortColumn) ${if (sortAscending) "ASC" else "DESC "}"
        if (sortColumn != "title") {
            sortQ += ", title ASC "
        }
        val statement = "SELECT * FROM ktabs_table WHERE kTabId in (SELECT kTabId FROM PlaylistKtabCrossRef WHERE playlistId = $playlistid) $where $sortQ"
        val query = SimpleSQLiteQuery(statement)
        return getPageKtabsViaQuery(query)
    }

    @Query("SELECT * FROM ktabs_table WHERE kTabId in (SELECT kTabId FROM playlist_ktab_cross_ref WHERE playlistId = :id)")
    fun getAllKTabsToPlaylistId(id: Long): Flow<List<KTabRoom>>

    @Query("SELECT * FROM playlist WHERE playlistId in (SELECT playlistId FROM playlist_ktab_cross_ref WHERE kTabId = :ktabId)")
    fun getAllPlaylistsForKtabId(ktabId: String): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistKtabCrossRef(playlistKtabCrossRef: PlaylistKtabCrossRef): Long

    // delete return number of deleted rows
    @Query("DELETE FROM playlist_ktab_cross_ref WHERE playlistId = :playlistid AND kTabId = :ktabId")
    fun deleteKtabFromPlaylist(playlistid: Long, ktabId: String): Int
}
