package com.kontranik.kalimbatabsviewer2.room.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer2.room.repository.KTabsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToggleFavoritesViewModel(
    private val kTabsRepository: KTabsRepository
) : ViewModel() {


    fun toggleFavorite(kTabRoom: KTabRoom) {
        viewModelScope.launch(Dispatchers.IO) {
            kTabsRepository.update(kTabRoom.copy(bookmarked = kTabRoom.bookmarked.not()))
        }
    }
}