package com.kontranik.kalimbatabsviewer2.room.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class PlaylistWithKtabs(
    @Embedded
    val playlist: Playlist,
    @Relation(
        entity = KTabRoom::class,
        parentColumn = "playlistId",
        entityColumn = "kTabId",
        associateBy = Junction(
            PlaylistKtabCrossRef::class,
            parentColumn = "kTabId",
            entityColumn = "playlistId"
        )
    )
    val ktabs: List<KTabRoom>
)