package com.kontranik.kalimbatabsviewer2.ui.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import com.kontranik.kalimbatabsviewer2.ui.dialogs.SORT_COLUMN_DIFFICULTY
import com.kontranik.kalimbatabsviewer2.ui.dialogs.SORT_COLUMN_INTERPRETER
import com.kontranik.kalimbatabsviewer2.ui.dialogs.SORT_COLUMN_TITLE
import com.kontranik.kalimbatabsviewer2.ui.dialogs.SORT_COLUMN_UPDATED
import com.kontranik.kalimbatabsviewer2.ui.dialogs.SortParams

class SettingsHelper {
    companion object {
        fun getDefaultSort(context: Context, allSongs: Boolean = false): SortParams {
            val sharedPreferences: SharedPreferences =
                context.getSharedPreferences(preferenceFileName, 0)
            val defSortColumn = sharedPreferences.getString(
                if (allSongs)
                    DEFAULT_SORT_COLUMN_ALL_SONGS
                else
                    DEFAULT_SORT_COLUMN_PLAYLISTS,
                sort_data.first().column
            )
            val defSortAsc = sharedPreferences.getBoolean(
                if (allSongs)
                    DEFAULT_SORT_ASSCENDING_ALL_SONGS
                else
                    DEFAULT_SORT_ASSCENDING_PLAYLISTS,
                sort_data.first().ascending
            )
            return SortParams(defSortColumn!!, defSortAsc)
        }

        const val preferenceFileName = "TNX_KALIMBA_TABS_PREFS"
        const val DEFAULT_TUNE = "DEFAULT_TUNE"
        const val DEFAULT_SORT_COLUMN_ALL_SONGS = "DEFAULT_SORT_COLUMN_ALL_SONGS"
        const val DEFAULT_SORT_ASSCENDING_ALL_SONGS = "DEFAULT_SORT_ASSCENDING_ALL_SONGS"
        const val DEFAULT_SORT_COLUMN_PLAYLISTS = "DEFAULT_SORT_COLUMN_PLAYLISTS"
        const val DEFAULT_SORT_ASSCENDING_PLAYLISTS = "DEFAULT_SORT_ASSCENDING_PLAYLISTS"

        val tune_data = arrayListOf(
            "number",
            "c tune",
            "g gune"
        )

        val sort_data = arrayListOf(
            SortParams(SORT_COLUMN_TITLE, true),
            SortParams(SORT_COLUMN_TITLE, false),
            SortParams(SORT_COLUMN_INTERPRETER, true),
            SortParams(SORT_COLUMN_INTERPRETER, false),
            SortParams(SORT_COLUMN_DIFFICULTY, true),
            SortParams(SORT_COLUMN_DIFFICULTY, false),
            SortParams(SORT_COLUMN_UPDATED, false)
        )
    }
}