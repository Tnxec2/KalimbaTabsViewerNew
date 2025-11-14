package com.kontranik.kalimbatabsviewer.room.model

import androidx.room.Entity

@Entity(
    tableName = "playlist_ktab_cross_ref",
    primaryKeys = ["playlistId", "kTabId"],
)
data class PlaylistKtabCrossRef(
    val playlistId: Long,
    val kTabId: String
)