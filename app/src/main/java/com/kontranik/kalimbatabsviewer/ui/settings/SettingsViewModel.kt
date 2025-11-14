package com.kontranik.kalimbatabsviewer.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.kalimbatabsviewer.helper.SortHelper
import com.kontranik.kalimbatabsviewer.helper.SortHelper.Companion.DEFAULT_SORT_ALL_SONGS
import com.kontranik.kalimbatabsviewer.helper.SortHelper.Companion.DEFAULT_SORT_PLAYLISTS
import com.kontranik.kalimbatabsviewer.helper.TransposeTypes
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class SettingsViewModel(
    context: Context
) : ViewModel() {


    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settingsState = MutableStateFlow(Settings())

    val settingsState: StateFlow<Settings> = _settingsState

    @OptIn(ExperimentalCoroutinesApi::class)
    val interfaceTheme = _settingsState.flatMapLatest { m -> flowOf(m.interfaceTheme) }

    init {
        viewModelScope.launch {
            _settingsState.value = loadSettings()
        }
    }

    fun updateLineBreak(wrapText: Boolean) {
        _settingsState.value = _settingsState.value.copy(lineBreak = wrapText,)
        sharedPreferences.edit {
            putBoolean(LINE_BREAK, wrapText)
            apply()
        }
    }

    fun updateHideText(hideText: Boolean) {
        _settingsState.value = _settingsState.value.copy(hideText = hideText,)
        sharedPreferences.edit {
            putBoolean(HIDE_TEXT, hideText)
            apply()
        }
    }

    fun updateFontSize(fontSize: Float) {
        _settingsState.value = _settingsState.value.copy(fontSize = fontSize,)
        sharedPreferences.edit {
            putFloat(FONT_SIZE, fontSize)
            apply()
        }
    }

    fun loadSettings(): Settings {
        return Settings(
            lineBreak = sharedPreferences.getBoolean(LINE_BREAK, true),
            fontSize = sharedPreferences.getFloat(FONT_SIZE, 16f),
            interfaceTheme = sharedPreferences.getString(INTERFACE_THEME, INTERFACE_THEME_SYSTEM) ?: INTERFACE_THEME_SYSTEM,
            tune = sharedPreferences.getString(TRANSPOSE_TUNE, TransposeTypes.NUMBER) ?: TransposeTypes.NUMBER,
            sortAllSongs = SortHelper.SortParams(
                sharedPreferences.getString(SortHelper.KEY_DEFAULT_SORT_COLUMN_ALL_SONGS, DEFAULT_SORT_ALL_SONGS.column) ?: DEFAULT_SORT_ALL_SONGS.column,
                sharedPreferences.getBoolean(SortHelper.KEY_DEFAULT_SORT_ASSCENDING_ALL_SONGS, DEFAULT_SORT_ALL_SONGS.ascending)),
            sortPlaylists = SortHelper.SortParams(
                sharedPreferences.getString(SortHelper.KEY_DEFAULT_SORT_COLUMN_PLAYLISTS, DEFAULT_SORT_PLAYLISTS.column) ?: DEFAULT_SORT_PLAYLISTS.column,
                sharedPreferences.getBoolean(SortHelper.KEY_DEFAULT_SORT_ASSCENDING_PLAYLISTS, DEFAULT_SORT_PLAYLISTS.ascending)),
            hideText = sharedPreferences.getBoolean(HIDE_TEXT, false),
        )
    }

    fun toggleLineBreak() {
        updateLineBreak(_settingsState.value.lineBreak.not())
    }


    fun decreaseFontSize() {
        val newFontSize = _settingsState.value.fontSize - 1f
        if (newFontSize >= 8f) {
            updateFontSize(newFontSize)
        }
    }

    fun increaseFontSize() {
        val newFontSize = _settingsState.value.fontSize + 1f
        updateFontSize(newFontSize)
    }

    fun changeInterfaceTheme(interfaceTheme: String) {
        _settingsState.value = _settingsState.value.copy(interfaceTheme = interfaceTheme,)

        sharedPreferences.edit {
            putString(INTERFACE_THEME, interfaceTheme)
            apply()
        }
    }

    fun changeDefaultSortAllSongs(s: String) {
        Log.d("C", s)
        val sort = SortHelper.SortParams().fromString(s)

        if (sort != null) {
            Log.d("C", "$s | $sort")
            _settingsState.value =
                _settingsState.value.copy(
                    sortAllSongs = SortHelper.SortParams(sort.column, sort.ascending)
                )

            sharedPreferences.edit {
                putString(SortHelper.KEY_DEFAULT_SORT_COLUMN_ALL_SONGS, sort.column)
                putBoolean(SortHelper.KEY_DEFAULT_SORT_ASSCENDING_ALL_SONGS, sort.ascending)
                apply()
            }
        }
    }

    fun changeDefaultSortPlaylists(s: String) {
        val sort = SortHelper.SortParams().fromString(s)
        if (sort != null) {
            _settingsState.value =
                _settingsState.value.copy(
                    sortPlaylists = SortHelper.SortParams(sort.column, sort.ascending)
                )

            sharedPreferences.edit {
                putString(SortHelper.KEY_DEFAULT_SORT_COLUMN_PLAYLISTS, sort.column)
                putBoolean(SortHelper.KEY_DEFAULT_SORT_ASSCENDING_PLAYLISTS, sort.ascending)
                apply()
            }
        }
    }

    fun changeTransposeTune(transposeTune: String) {
        _settingsState.value = _settingsState.value.copy(tune = transposeTune,)

        sharedPreferences.edit {
            putString(TRANSPOSE_TUNE, transposeTune)
            apply()
        }
    }

    fun changeFontSize(fontSize: Float) {
        updateFontSize(fontSize)
    }

    fun toggleHideText() {
        updateHideText(_settingsState.value.hideText.not())
    }

    companion object {
        const val PREFS_NAME = "global_settings_prefs"

        const val LINE_BREAK = "line_break"
        const val HIDE_TEXT = "hide_text"
        const val TRANSPOSE_TUNE = "transpose_tune"
        const val SYNC_BATCH_SIZE = "sync_batch_size"
        const val SYNC_BATCH_SIZE_DEFAULT = 10
        const val FONT_SIZE = "font_size"

        const val INTERFACE_THEME = "interface_theme"
        const val INTERFACE_THEME_LIGHT = "Light"
        const val INTERFACE_THEME_DARK = "Dark"
        const val INTERFACE_THEME_SYSTEM = "System"

        val INTERFACE_ENTRIES = listOf(
            INTERFACE_THEME_LIGHT,
            INTERFACE_THEME_DARK,
            INTERFACE_THEME_SYSTEM
        )
    }
}