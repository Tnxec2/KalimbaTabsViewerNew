package com.kontranik.kalimbatabsviewer2.ui.songlist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kontranik.kalimbatabsviewer2.AppViewModelProvider
import com.kontranik.kalimbatabsviewer2.R
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer2.room.viewmodel.KtabRoomViewModel
import com.kontranik.kalimbatabsviewer2.room.viewmodel.ToggleFavoritesViewModel
import com.kontranik.kalimbatabsviewer2.ui.appbar.AppBar
import com.kontranik.kalimbatabsviewer2.ui.appbar.AppBarAction
import com.kontranik.kalimbatabsviewer2.ui.common.SearchBox
import com.kontranik.kalimbatabsviewer2.ui.theme.paddingSmall
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllKTabsListScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    openSong: (id: String) -> Unit,
    viewModel: KtabRoomViewModel = viewModel(factory = AppViewModelProvider.Factory ),
    syncViewModel: SyncViewModel = viewModel(factory = AppViewModelProvider.Factory),
    toggleFavoritesViewModel: ToggleFavoritesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val snackbarHostState = remember { SnackbarHostState() }

    var showSearch by rememberSaveable { mutableStateOf(false) }
    val listState = rememberLazyListState()

    val list = viewModel.songsPageByFilter.collectAsLazyPagingItems()
    val showBookmarked = viewModel.showBookmarked.collectAsState(false)

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppBar (
                titleString = "Kalimba Tabs"
                ,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(id = R.string.menu_all_songs)
                        )
                    }
                },
                appBarActions = listOf{
                    AppBarAction(appBarAction =  AppBarAction(
                        vector = if (showSearch) Icons.Default.SearchOff else Icons.Default.Search,
                        description = R.string.search,
                        onClick = {
                            coroutineScope.launch {
                                showSearch = showSearch.not()
                                if (!showSearch) viewModel.changeSearchText(context,"")
                            }
                        }
                    ))
                    AppBarAction(appBarAction = AppBarAction(
                        vector = if (showBookmarked.value) Icons.Filled.Star else Icons.Default.StarBorder,
                        description = R.string.favorites,
                        onClick = {
                            coroutineScope.launch {
                                viewModel.toggleShowBookmarked()
                            }
                        }
                    ))
                    SyncAppBarAction( onSyncCompleted = {
                        coroutineScope.launch {
                            listState.scrollToItem(0)
                        }
                    }, syncViewModel)
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Column(Modifier.padding(padding)) {
            if (showSearch) {
                val searchQuery = viewModel.searchText.collectAsState("")
                SearchBox(
                    queryState = searchQuery.value ?: "",
                    onChangeSearchQuery = {
                        coroutineScope.launch {
                            viewModel.changeSearchText(context,  it)
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
        }
    }
}

@Composable
fun PageSongs(
    listState: LazyListState,
    ktabs: LazyPagingItems<KTabRoom>,
    openSong: (id: String) -> Unit,
    onToggleFavorite: (ktabRoom: KTabRoom) -> Unit,
) {


    LazyColumn(
        state = listState,
        modifier = Modifier.padding(horizontal = paddingSmall)
    ) {
        item {
            if (ktabs.loadState.append is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }
        items(
            count = ktabs.itemCount,
            key = ktabs.itemKey { item -> item.kTabId }
        ) { index ->
            ktabs[index]?.let { ktabRoom ->
                KtabItem(ktab = ktabRoom, onOpenKtab = {
                    openSong(ktabRoom.kTabId)
                }, onToggleFavorite = {
                    onToggleFavorite(ktabRoom)
                })
            }
        }
    }
}


