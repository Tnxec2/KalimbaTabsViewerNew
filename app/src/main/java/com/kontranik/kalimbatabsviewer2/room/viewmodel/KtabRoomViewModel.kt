package com.kontranik.kalimbatabsviewer2.room.viewmodel

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer2.room.repository.KTabsRepository
import com.kontranik.kalimbatabsviewer2.ui.dialogs.SortParams
import com.kontranik.kalimbatabsviewer2.ui.settings.SettingsHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class KtabRoomViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: KTabsRepository,
) : ViewModel() {

    private val playlistId: Long? = savedStateHandle["playlistId"]
    var playlistIdState = MutableStateFlow<Long?>(null)

    private val songTextFilter = MutableLiveData<SongTextFilter>(SongTextFilter("", "updated", false, bookmarked = false))

    val showBookmarked = songTextFilter.switchMap { filter ->
        liveData {
            emit(filter.bookmarked)
        }
    }.asFlow()

    val searchText = songTextFilter.switchMap { filter ->
        liveData {
            emit(filter.text)
        }
    }.asFlow()

    init {
        playlistId?.let {
            playlistIdState.value = it
        }
    }

    val songsPageByFilter = songTextFilter.switchMap {
        filter -> getPageByFilter(filter)
    }.asFlow().cachedIn(viewModelScope)


    private fun getPageByFilter(songTextFilter: SongTextFilter) = Pager(PagingConfig(pageSize = 15)) {
        repository.pageKtabs(songTextFilter.bookmarked, songTextFilter.text, songTextFilter.sort, songTextFilter.ascending) }
        .liveData
        .cachedIn(viewModelScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val songsForPlaylistPage = playlistIdState
        .filterNotNull()
        .flatMapLatest { playlistId ->
            Pager(
                config = PagingConfig(
                    pageSize = 15,
                    enablePlaceholders = false,
                    prefetchDistance = 3
                )
            ) {
                repository.getSongsForPlaylistId(playlistId)
            }.flow
        }.cachedIn(viewModelScope)

    fun changeSearchText(context: Context, text: String?) {
        Log.d("KtabRoomViewModel", "changeSearchText: $text")
        val altVal = this.songTextFilter.value
        if ( altVal == null || altVal.text != text) {
            val defSortParams = SettingsHelper.getDefaultSort(context, true)
            this.songTextFilter.postValue(SongTextFilter(
                text = text,
                sort = altVal?.sort ?: defSortParams.column,
                ascending = altVal?.ascending ?: defSortParams.ascending,
                bookmarked = altVal?.bookmarked ?: false
            ))
        }
    }

    fun changeSortColumn(column: String, ascending: Boolean) {
        val altVal = this.songTextFilter.value
        if ( altVal == null || altVal.sort != column || altVal.ascending != ascending) {
            this.songTextFilter.postValue(
                SongTextFilter(
                    text = altVal?.text,
                    sort = column,
                    ascending = ascending,
                    bookmarked = altVal?.bookmarked ?: false
                )
            )
        }
    }

    private fun getSearchText(): String? {
        return songTextFilter.value?.text
    }

    private fun getSortParams(context: Context): SortParams {
        val defSortParams = SettingsHelper.getDefaultSort(context, true)
        return SortParams(songTextFilter.value?.sort ?: defSortParams.column, songTextFilter.value?.ascending ?: defSortParams.ascending)
    }

    private fun getById(kTabId: String): KTabRoom? {
        return repository.getById(kTabId)
    }

    fun insert(kTabRoom: KTabRoom) = viewModelScope.launch {
        repository.insert(kTabRoom)
    }

    fun update(kTabRoom: KTabRoom) = viewModelScope.launch {
        repository.update(kTabRoom)
    }

    fun delete(kTabId: String) = viewModelScope.launch {
        repository.delete(kTabId)
    }

    fun updateOrInsert(lastDocument: KTabRoom) {

        viewModelScope.launch {
            val ktabInDatabase = getById(lastDocument.kTabId)

            if (ktabInDatabase != null && ktabInDatabase.kTabId == lastDocument.kTabId) {

                ktabInDatabase.title = lastDocument.title
                ktabInDatabase.interpreter = lastDocument.interpreter
                ktabInDatabase.difficulty = lastDocument.difficulty
                ktabInDatabase.source = lastDocument.source
                ktabInDatabase.youtube = lastDocument.youtube
                ktabInDatabase.text = lastDocument.text
                ktabInDatabase.updated = lastDocument.updated

                update(ktabInDatabase)
            } else {
                insert(lastDocument)
            }
        }

    }


//    fun openSortDialog(context: Context) {
//        val sortListener = object : SortDialog.DialogListener {
//            override fun ready(sortParams: SortDialog.SortParams) {
//                changeSortColumn(sortParams.column, sortParams.ascending)
//            }
//            override fun cancelled() {
//            }
//        }
//
//        val mSortDialog = SortDialog(getSortParams(context), context, sortListener )
//        mSortDialog.show()
//    }

    fun toggleSearchSong(searchView: View, editText: EditText) {
        if (searchView.isVisible) {
            searchView.visibility = View.GONE
        } else {
            searchView.visibility = View.VISIBLE
        }
        if (getSearchText() != null) {
            editText.text = null
            changeSearchText(editText.context, null)
        }
    }

    fun toggleShowBookmarked() {
        songTextFilter.value?.let {
            songTextFilter.value = it.copy(bookmarked = !it.bookmarked)
        }
    }


//    fun openDeleteSongDialog(adapter: PagingSongListAdapter, position: Int, kTabRoom: KTabRoom, context: Context) {
//        AlertDialog.Builder(context)
//            .setTitle(context.getString(R.string.song_delete_dialog_title))
//            .setMessage(context.getString(R.string.song_delete_dialog_message)) // Specifying a listener allows you to take an action before dismissing the dialog.
//            // The dialog is automatically dismissed when a dialog button is clicked.
//            .setPositiveButton(
//                R.string.ok
//            ) { dialog, which ->
//                delete(kTabRoom.kTabId)
//                adapter.deleteItem(position)
//            } // A null listener allows the button to dismiss the dialog and take no further action.
//            .setNegativeButton(R.string.cancel) { dialogInterface, i ->
//                adapter.cancelDeletion(position)
//            }
//            .setIcon(R.drawable.ic_dialog_alert)
//            .show()
//    }


    data class SongTextFilter(
        var text: String?,
        var sort: String,
        var ascending: Boolean,
        var bookmarked: Boolean = false
    )
}
