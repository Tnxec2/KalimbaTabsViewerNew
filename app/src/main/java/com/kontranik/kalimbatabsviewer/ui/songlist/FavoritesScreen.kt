package com.kontranik.kalimbatabsviewer.ui.songlist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.kontranik.kalimbatabsviewer.AppViewModelProvider
import com.kontranik.kalimbatabsviewer.R
import com.kontranik.kalimbatabsviewer.room.viewmodel.KtabRoomViewModel
import com.kontranik.kalimbatabsviewer.room.viewmodel.ToggleFavoritesViewModel
import com.kontranik.kalimbatabsviewer.ui.appbar.AppBar
import com.kontranik.kalimbatabsviewer.ui.appbar.AppBarAction
import com.kontranik.kalimbatabsviewer.ui.common.SearchBox
import com.kontranik.kalimbatabsviewer.ui.dialogs.SortDialog
import com.kontranik.kalimbatabsviewer.ui.theme.paddingSmall
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FavoritesScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    openSong: (id: String) -> Unit,
    viewModel: KtabRoomViewModel = viewModel(factory = AppViewModelProvider.Factory ),
    syncViewModel: SyncViewModel = viewModel(factory = AppViewModelProvider.Factory),
    toggleFavoritesViewModel: ToggleFavoritesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()

    val snackBarHostState = remember { SnackbarHostState() }

    val searchQuery = viewModel.searchText.collectAsState("")
    val listState = rememberLazyListState()

    var showSortDialog by rememberSaveable { mutableStateOf(false) }
    val currentSort = viewModel.currentSort.collectAsState(null)

    val list = viewModel.favoritesSongsPageByFilter.collectAsLazyPagingItems()

    var expandedMenu by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        topBar = {
            AppBar (
                titleString = stringResource(id = R.string.favorites),
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(id = R.string.menu_all_songs)
                        )
                    }
                },
                appBarActions = listOf{
                    AppBarAction(
                        AppBarAction(
                        vector = Icons.AutoMirrored.Default.Sort,
                        description = R.string.sort,
                        onClick = {
                            showSortDialog = true
                        }
                    ))

                    AppBarAction(
                        AppBarAction(
                            vector = Icons.Filled.MoreVert,
                            description = R.string.menu,
                            onClick = { expandedMenu = true }
                        )
                    )
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        SyncAppBarAction( onSyncCompleted = {
                            coroutineScope.launch {
                                listState.scrollToItem(0)
                                expandedMenu = false
                            }
                        }, syncViewModel, isAppBarAction = false)
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Column(Modifier.padding(padding)) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = paddingSmall, vertical = paddingSmall),
                shape = MaterialTheme.shapes.small, // Abgerundete Ecken
                shadowElevation = 3.dp // Leichter Schatten
            ) {
                SearchBox(
                    queryState = searchQuery.value ?: "",
                    focus = false,
                    onChangeSearchQuery = {
                        coroutineScope.launch {
                            viewModel.changeSearchText(it)
                            listState.scrollToItem(0)
                        }
                    }
                )
            }

            PageSongs(
                listState,
                list,
                openSong,
                onToggleFavorite = {
                    coroutineScope.launch {
                        toggleFavoritesViewModel.toggleFavorite(it)
                    }
                }
            )

            if (showSortDialog && currentSort.value != null) SortDialog(
                currentSortParams = currentSort.value!!,
                onDismissRequest = { showSortDialog = false},
                onConfirm = { viewModel.changeSortColumn(it)}
            )
        }
    }
}
