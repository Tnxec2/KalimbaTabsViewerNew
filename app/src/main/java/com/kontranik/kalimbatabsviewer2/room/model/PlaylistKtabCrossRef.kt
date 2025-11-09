package com.kontranik.kalimbatabsviewer2.room.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    primaryKeys = ["playlistId", "kTabId"],
)
data class PlaylistKtabCrossRef(
    val playlistId: Long,
    val kTabId: String
)