package com.kontranik.kalimbatabsviewer.ui.settings

import com.kontranik.kalimbatabsviewer.helper.SortHelper
import com.kontranik.kalimbatabsviewer.helper.TransposeTypes
import com.kontranik.kalimbatabsviewer.ui.settings.SettingsViewModel.Companion.INTERFACE_THEME_SYSTEM


const val HISTORY_SIZE = 10

data class Settings(
    val lineBreak: Boolean = true,
    val fontSize: Float = 12f,
    val interfaceTheme: String = INTERFACE_THEME_SYSTEM,
    val tune: String = TransposeTypes.NUMBER,
    val hideText: Boolean = false,
    val sortAllSongs: SortHelper.SortParams = SortHelper.DEFAULT_SORT_ALL_SONGS,
    val sortPlaylists: SortHelper.SortParams = SortHelper.DEFAULT_SORT_PLAYLISTS
)


