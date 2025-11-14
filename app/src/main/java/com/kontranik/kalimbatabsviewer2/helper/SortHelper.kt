package com.kontranik.kalimbatabsviewer2.helper

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.res.stringResource
import com.kontranik.kalimbatabsviewer2.R
import com.kontranik.kalimbatabsviewer2.ui.settings.SettingsViewModel.Companion.PREFS_NAME

class SortHelper {
    data class SortParams(
        val column: String = SORT_COLUMN_TITLE,
        val ascending: Boolean = true,
        val idRes: Int = R.string.sort_title_asc
    ) {

        fun fromString(sortString: String): SortParams? {
            return sortOptions.firstOrNull { sortOption ->
                sortOption.toString() == sortString
            }
        }

        override fun toString(): String {
            return column + (if (ascending) "_asc" else "_desc")
        }
    }

    companion object {
        fun getDefaultSort(context: Context, allSongs: Boolean = false): SortParams {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(PREFS_NAME, 0)
            val sort = if (allSongs) SortHelper.SortParams(
                sharedPreferences.getString(KEY_DEFAULT_SORT_COLUMN_ALL_SONGS, DEFAULT_SORT_ALL_SONGS.column) ?: DEFAULT_SORT_ALL_SONGS.column,
                sharedPreferences.getBoolean(KEY_DEFAULT_SORT_ASSCENDING_ALL_SONGS, DEFAULT_SORT_ALL_SONGS.ascending))
            else SortHelper.SortParams(
                sharedPreferences.getString(KEY_DEFAULT_SORT_COLUMN_PLAYLISTS, DEFAULT_SORT_PLAYLISTS.column) ?: DEFAULT_SORT_PLAYLISTS.column,
                sharedPreferences.getBoolean(KEY_DEFAULT_SORT_ASSCENDING_PLAYLISTS, DEFAULT_SORT_PLAYLISTS.ascending))
            return sort
        }

        const val KEY_DEFAULT_SORT_COLUMN_ALL_SONGS = "DEFAULT_SORT_COLUMN_ALL_SONGS"
        const val KEY_DEFAULT_SORT_ASSCENDING_ALL_SONGS = "DEFAULT_SORT_ASSCENDING_ALL_SONGS"
        const val KEY_DEFAULT_SORT_COLUMN_PLAYLISTS = "DEFAULT_SORT_COLUMN_PLAYLISTS"
        const val KEY_DEFAULT_SORT_ASSCENDING_PLAYLISTS = "DEFAULT_SORT_ASSCENDING_PLAYLISTS"

        val DEFAULT_SORT_ALL_SONGS = SortParams(SORT_COLUMN_UPDATED, false)
        val DEFAULT_SORT_PLAYLISTS = SortParams(SORT_COLUMN_TITLE, true)

        // These can be moved to a more central location if used elsewhere
        const val SORT_COLUMN_TITLE = "title"
        const val SORT_COLUMN_INTERPRETER = "interpreter"
        const val SORT_COLUMN_DIFFICULTY = "difficulty"
        const val SORT_COLUMN_UPDATED = "updated"

        // A list of all available sorting options for the UI
        val sortOptions = listOf(
            SortParams(SORT_COLUMN_TITLE, true, R.string.sort_title_asc),
            SortParams(SORT_COLUMN_TITLE, false, R.string.sort_title_desc),
            SortParams(SORT_COLUMN_INTERPRETER, true,  R.string.sort_interpreter),
            SortParams(SORT_COLUMN_INTERPRETER, false, R.string.sort_interpreter_desc),
            SortParams(SORT_COLUMN_DIFFICULTY, true, R.string.sort_difficulty_asc),
            SortParams(SORT_COLUMN_DIFFICULTY, false,R.string.sort_difficulty_desc),
            SortParams(SORT_COLUMN_UPDATED, false, R.string.sort_update_time_new_first),
        )

        val sortSettingsEntries = sortOptions.map { pair -> pair.toString() }
        val sortSettingsValues = sortOptions.map { pair -> pair.idRes }
    }
}