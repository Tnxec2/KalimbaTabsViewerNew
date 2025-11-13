package com.kontranik.kalimbatabsviewer2

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kontranik.kalimbatabsviewer2.room.viewmodel.KtabRoomViewModel
import com.kontranik.kalimbatabsviewer2.room.viewmodel.PlaylistViewModel
import com.kontranik.kalimbatabsviewer2.ui.settings.SettingsViewModel
import com.kontranik.kalimbatabsviewer2.ui.song.KTabDetailsViewModel
import com.kontranik.kalimbatabsviewer2.ui.songlist.SyncViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {

        initializer {
            KtabRoomViewModel(
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
    }
}


/**
 * Extension function to queries for [KTabApplication] object and returns an instance of
 * [KTabApplication].
 */
fun CreationExtras.application(): KTabApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as KTabApplication)
