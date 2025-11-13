package com.kontranik.kalimbatabsviewer2.room.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.kalimbatabsviewer2.room.model.Playlist
import com.kontranik.kalimbatabsviewer2.room.repository.PlaylistRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch


class PlaylistViewModel(
    savedStateHandle: SavedStateHandle,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    val playlists = playlistRepository.getAllPlaylists

    private val playlistIdState: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val playlistId: Long? = savedStateHandle["playlistId"]

    init {
        playlistId?.let {
            playlistIdState.value = it
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val playlistState = playlistIdState
        .filterNotNull()
        .flatMapLatest { id ->
            playlistRepository.getFlowById(id)
    }

    fun insert(playlist: Playlist) {
        viewModelScope.launch {
            playlistRepository.insert(playlist)
        }
    }

    fun update(playlist: Playlist) {
        viewModelScope.launch {
            playlistRepository.upsert(playlist)
        }
    }

    fun updatePlaylistName(playlist: Playlist, name: String) {
        viewModelScope.launch {
            playlistRepository.upsert(playlist.copy(playlistName = name))
        }
    }

    fun deletePlaylist(playlistIdn: Long?) {
        playlistIdn?.let {
            viewModelScope.launch {
                playlistRepository.delete(it)
            }
        }
    }

    fun addPlaylist(name: String) {
        viewModelScope.launch {
            playlistRepository.insert(Playlist(playlistName = name))
        }
    }

    fun deleteKtabFromPlaylist(ktabId: String, playlistId: Long) {
        viewModelScope.launch {
            playlistRepository.deleteKTabFromPlaylist(ktabId, playlistId)
        }
    }
}

