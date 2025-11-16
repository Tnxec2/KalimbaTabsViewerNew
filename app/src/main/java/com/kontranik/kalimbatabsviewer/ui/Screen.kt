package com.kontranik.kalimbatabsviewer.ui

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen(val route: String) {
    data object KTabList : Screen("ktab_list")
    data object Favorites : Screen("favorites")
    data object Playlist : Screen("playlist")
    data object PlaylistKtabList : Screen("playlist_ktab_list/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist_ktab_list/$playlistId"
    }
    data object Settings : Screen("settings")

    data object KTabDetails : Screen("ktab_details/{ktabid}") {
        fun createRoute(ktabid: String) = "ktab_details/$ktabid"
    }
}