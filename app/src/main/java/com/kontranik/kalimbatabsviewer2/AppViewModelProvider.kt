package com.kontranik.kalimbatabsviewer2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kontranik.kalimbatabsviewer2.room.viewmodel.AllSongsViewModel
import com.kontranik.kalimbatabsviewer2.ui.songlist.SyncViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {

        initializer {
            AllSongsViewModel(
                this.createSavedStateHandle(),
                application().container.kTabsRepository,
            )
        }

        initializer {
            SyncViewModel(
                application().container.kTabsRepository,
            )
        }
    }
}


/**
 * Extension function to queries for [KTabApplication] object and returns an instance of
 * [KTabApplication].
 */
fun CreationExtras.application(): KTabApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as KTabApplication)
