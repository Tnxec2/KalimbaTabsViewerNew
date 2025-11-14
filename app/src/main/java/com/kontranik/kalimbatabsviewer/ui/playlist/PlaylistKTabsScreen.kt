package com.kontranik.kalimbatabsviewer.ui.playlist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kontranik.kalimbatabsviewer.AppViewModelProvider
import com.kontranik.kalimbatabsviewer.R
import com.kontranik.kalimbatabsviewer.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer.room.model.Playlist
import com.kontranik.kalimbatabsviewer.room.viewmodel.KtabRoomViewModel
import com.kontranik.kalimbatabsviewer.room.viewmodel.PlaylistViewModel
import com.kontranik.kalimbatabsviewer.room.viewmodel.ToggleFavoritesViewModel
import com.kontranik.kalimbatabsviewer.ui.appbar.AppBar
import com.kontranik.kalimbatabsviewer.ui.appbar.AppBarAction
import com.kontranik.kalimbatabsviewer.ui.common.ConfirmDialog
import com.kontranik.kalimbatabsviewer.ui.common.ConfirmDialogData
import com.kontranik.kalimbatabsviewer.ui.common.EditDialog
import com.kontranik.kalimbatabsviewer.ui.dialogs.SortDialog
import com.kontranik.kalimbatabsviewer.ui.songlist.KtabItem
import com.kontranik.kalimbatabsviewer.ui.theme.paddingMedium
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlaylistKtabsScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    openKtab: (ktabid: String) -> Unit,
    playlistViewModel: PlaylistViewModel = viewModel(factory = AppViewModelProvider.Factory),
    ktabRoomViewModel: KtabRoomViewModel = viewModel(factory = AppViewModelProvider.Factory),
    toggleFavoritesViewModel: ToggleFavoritesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val playlist by playlistViewModel.playlistState.collectAsStateWithLifecycle(Playlist(playlistName = "untitled"))
    val songs = ktabRoomViewModel.songsForPlaylistPage.collectAsLazyPagingItems()

    var showDelete by rememberSaveable { mutableStateOf(false) }
    var showEdit by rememberSaveable { mutableStateOf(false) }

    var showSortDialog by rememberSaveable { mutableStateOf(false) }
    val currentSort = ktabRoomViewModel.currentSort.collectAsState(null)

    var expandedMenu by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppBar (
                titleString = stringResource(
                    R.string.playlist_songs_title,
                    playlist.playlistName
                ),
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                appBarActions = listOf{
                    AppBarAction(appBarAction = AppBarAction(
                        vector = Icons.AutoMirrored.Default.Sort,
                        description = R.string.sort,
                        onClick = {
                            showSortDialog = true
                        }
                    ))
                    AppBarAction(
                        appBarAction = AppBarAction(
                            vector = Icons.Filled.MoreVert,
                            description = R.string.menu,
                            onClick = { expandedMenu = true }
                        )
                    )
                    DropdownMenu(
                        expanded = expandedMenu,
                        onDismissRequest = { expandedMenu = false }
                    ) {
                        DropdownMenuItem(text = {
                            Text(stringResource(R.string.edit))
                        },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.edit)
                                )
                            },
                            onClick = { showEdit = true }
                        )
                        DropdownMenuItem(text = {
                            Text(stringResource(R.string.delete_playlist))
                        },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete_playlist)
                                )
                            },
                            onClick = { showDelete = true }
                        )
                    }
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Column(Modifier.padding(padding)) {
            PlaylistSongs(ktabs = songs,
                openKtab = { openKtab(it) },
                onToggleFavorite = {
                    coroutineScope.launch {
                        toggleFavoritesViewModel.toggleFavorite(it)
                    }
                },
                onDelete = { songId ->
                    coroutineScope.launch {
                        playlist.playlistId?.let { playlistViewModel.deleteKtabFromPlaylist(songId, it) }
                    }
                })
        }

        if (showDelete) {
            ConfirmDialog(
                data = ConfirmDialogData(
                    show = true,
                    title = stringResource(R.string.title_delete_playlist),
                    text = stringResource(R.string.delete_this_playlist_message),
                    onDismiss = { showDelete = false },
                    onConfirm = {
                        coroutineScope.launch {
                            showDelete = false
                            playlistViewModel.deletePlaylist(playlist.playlistId)
                            snackbarHostState.showSnackbar(context.getString(R.string.playlist_deleted))
                            navigateBack()
                        }
                    }
                )
            )
        }

        if (showEdit) {
            EditDialog(
                originalInput = playlist.playlistName,
                label = stringResource(R.string.playlist_name),
                onDismiss = { showEdit = false },
                onConfirm = { name: String ->
                    coroutineScope.launch {
                        playlistViewModel.updatePlaylistName(playlist, name)
                    }
                    showEdit = false
                }
            )

        }

        if (showSortDialog && currentSort.value != null) SortDialog(
            currentSortParams = currentSort.value!!,
            onDismissRequest = { showSortDialog = false},
            onConfirm = { ktabRoomViewModel.changeSortColumn(it)}
        )
    }

}

@Composable
private fun PlaylistSongs(
    openKtab: (String) -> Unit,
    onToggleFavorite: (KTabRoom) -> Unit,
    onDelete: (String) -> Unit,
    ktabs: LazyPagingItems<KTabRoom>
) {
    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            items(
                count = ktabs.itemCount,
                key = ktabs.itemKey { item -> item.kTabId }
            ) { index ->
                ktabs[index]?.let { ktab ->
                    KtabItem(
                        ktab = ktab,
                        onOpenKtab = { openKtab(ktab.kTabId) },
                        onToggleFavorite = { onToggleFavorite(ktab) },
                        contextMenu = listOf {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(R.string.remove_from_playlist))
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(R.string.remove_from_playlist)
                                    )
                                },
                                onClick = {
                                    onDelete(ktab.kTabId)
                                }
                            )
                        }
                    )
                }
            }
        }

        val showButton by remember {
            derivedStateOf {
                // Zeige den Button, wenn mehr als das erste Element gescrollt wurde
                listState.firstVisibleItemIndex > 0
            }
        }

        if (showButton) {
            FloatingActionButton (
                onClick = { coroutineScope.launch { listState.scrollToItem(0) } },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(paddingMedium)
            ) {
                Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Up")
            }
        }
    }
}


