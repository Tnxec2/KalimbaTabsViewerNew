package com.kontranik.kalimbatabsviewer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kontranik.kalimbatabsviewer.room.viewmodel.KtabRoomViewModel
import com.kontranik.kalimbatabsviewer.room.viewmodel.PlaylistViewModel
import com.kontranik.kalimbatabsviewer.room.viewmodel.ToggleFavoritesViewModel
import com.kontranik.kalimbatabsviewer.ui.settings.SettingsViewModel
import com.kontranik.kalimbatabsviewer.ui.song.KTabDetailsViewModel
import com.kontranik.kalimbatabsviewer.ui.songlist.SyncViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {

        initializer {
            KtabRoomViewModel(
                application().applicationContext,
                this.createSavedStateHandle(),
                application().container.kTabsRepository,
            )
        }

        initializer {
            PlaylistViewModel(
                this.createSavedStateHandle(),
                application().container.playlistRepository,
            )
        }

        initializer {
            SyncViewModel(
                application().container.kTabsRepository,
            )
        }
        initializer {
            SettingsViewModel(
                application().applicationContext
            )
        }

        initializer {
            KTabDetailsViewModel(
                this.createSavedStateHandle(),
                application().container.kTabsRepository,

            )
        }


        initializer {
            ToggleFavoritesViewModel(
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
