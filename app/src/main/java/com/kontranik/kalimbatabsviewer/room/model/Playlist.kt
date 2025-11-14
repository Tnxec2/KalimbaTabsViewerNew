package com.kontranik.kalimbatabsviewer.room.model


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Playlist(
    @PrimaryKey(autoGenerate = true) val playlistId: Long? = null,
    var playlistName: String
)