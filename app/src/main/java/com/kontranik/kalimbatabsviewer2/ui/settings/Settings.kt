package com.kontranik.kalimbatabsviewer2.ui.settings

import com.kontranik.kalimbatabsviewer2.ui.settings.SettingsViewModel.Companion.INTERFACE_THEME_SYSTEM


const val HISTORY_SIZE = 10

data class Settings(
    val lineBreak: Boolean = true,
    val fontSize: Float = 12f,
    val interfaceTheme: String = INTERFACE_THEME_SYSTEM,
    val hideText: Boolean = false,
)


