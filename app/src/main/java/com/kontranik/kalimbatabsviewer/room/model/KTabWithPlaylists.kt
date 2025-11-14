package com.kontranik.kalimbatabsviewer.room.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class KTabWithPlaylists(
    @Embedded val ktab: KTabRoom,
    @Relation(
        entity = Playlist::class,
        parentColumn = "kTabId",
        entityColumn = "playlistId",
        associateBy = Junction(PlaylistKtabCrossRef::class)
    )
    val playlists: List<Playlist>
)