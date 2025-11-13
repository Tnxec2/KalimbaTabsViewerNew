package com.kontranik.kalimbatabsviewer2.ui.playlist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.kalimbatabsviewer2.AppViewModelProvider

import com.kontranik.kalimbatabsviewer2.R
import com.kontranik.kalimbatabsviewer2.room.model.Playlist
import com.kontranik.kalimbatabsviewer2.room.viewmodel.PlaylistViewModel
import com.kontranik.kalimbatabsviewer2.ui.appbar.AppBar
import com.kontranik.kalimbatabsviewer2.ui.appbar.AppBarAction
import com.kontranik.kalimbatabsviewer2.ui.common.EditDialog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlaylistListScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    openPlaylist: (playlistId: Long) -> Unit,
    playlistViewModel: PlaylistViewModel = viewModel(factory = AppViewModelProvider .Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val playlists = playlistViewModel.playlists.collectAsStateWithLifecycle(emptyList())

    var showAdd by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            AppBar (
                titleString = stringResource(R.string.playlists),
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                        Icon(imageVector = Icons.Filled.Menu,
                            contentDescription = stringResource(id = R.string.menu)
                        )
                    }
                },
                appBarActions = listOf{
                    AppBarAction(appBarAction =  AppBarAction(
                        vector = Icons.Default.Add,
                        description = R.string.add_playlist,
                        onClick = { showAdd = true }
                    ))
                }
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Column(Modifier.padding(padding)) {
            AllPlaylists(
                openPlaylist = {openPlaylist(it)},
                state = playlists
            )
        }
        if (showAdd) {
            EditDialog(
                originalInput = "",
                label = stringResource(id = R.string.add_playlist),
                onDismiss = { showAdd = false },
                onConfirm = { name ->
                    if (name.isNotEmpty() && playlists.value.find { it.playlistName == name } == null) {
                        coroutineScope.launch {
                            playlistViewModel.addPlaylist(name)
                            showAdd = false
                        }
                    }
                }
            )
        }
    }
}


@Composable
private fun AllPlaylists(
    openPlaylist: (Long) -> Unit,
    state: State<List<Playlist>>
) {
    val listState = rememberLazyListState()

    LazyColumn(state = listState, modifier = Modifier.padding(horizontal = 16.dp)) {
        items(
            items = state.value,
            key =  { item -> item.playlistId!! }
        ) { item ->
            PlaylistItem(
                playlist = item,
                onClick = { openPlaylist(item.playlistId!!) },
            )
        }
    }
}

@Composable
@Preview
fun AllPlaylistPreview() {
    val playlists = listOf(
        Playlist(
            playlistId = 1,
            playlistName = "My Playlist",
        ),
        Playlist(
            playlistId = 2,
            playlistName = "My Playlist 2",
        ),
        Playlist(
            playlistId = 3,
            playlistName = "My Playlist 3",
        )
    )

    AllPlaylists(openPlaylist = {}, state = MutableStateFlow(playlists).collectAsState())
}

