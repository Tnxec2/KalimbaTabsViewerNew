package com.kontranik.kalimbatabsviewer2.ui.song

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer2.room.model.Playlist
import com.kontranik.kalimbatabsviewer2.room.repository.KTabsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class KTabDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val kTabsRepository: KTabsRepository
) : ViewModel() {

    var ktabUiState = MutableStateFlow(KTabRoom(""))

    private val ktabid: String? = savedStateHandle["ktabid"]

    init {
        viewModelScope.launch {
            ktabUiState.value = if (ktabid != null) {
                kTabsRepository.getFlowById(ktabid)
                    .filterNotNull()
//                    .onEach { song ->
//                        historyRepository.upsert(
//                            History(
//                                songId = ktabid,
//                                date = System.currentTimeMillis(),
//                                isUserSong = false
//                            )
//                        )
//                    }
                    .first()
            } else {
                KTabRoom("")
            }
        }
    }

    fun toggleFavorite() {
        ktabUiState.value = ktabUiState.value.copy(bookmarked = !ktabUiState.value.bookmarked)
    }


    fun addToPlaylist(songId: String, playlist: Playlist) {
        if (playlist.playlistId != null)
            viewModelScope.launch(Dispatchers.IO) {
                kTabsRepository.addKtabToPlaylist(songId, playlist.playlistId)
            }
    }



}

