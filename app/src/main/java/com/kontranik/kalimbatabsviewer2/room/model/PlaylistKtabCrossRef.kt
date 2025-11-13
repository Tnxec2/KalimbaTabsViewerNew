package com.kontranik.kalimbatabsviewer2.room.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "playlist_ktab_cross_ref",
    primaryKeys = ["playlistId", "kTabId"],
)
data class PlaylistKtabCrossRef(
    val playlistId: Long,
    val kTabId: String
)