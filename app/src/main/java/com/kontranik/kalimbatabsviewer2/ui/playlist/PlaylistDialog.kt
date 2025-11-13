package com.kontranik.kalimbatabsviewer2.ui.playlist

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.kalimbatabsviewer2.AppViewModelProvider
import com.kontranik.kalimbatabsviewer2.R
import com.kontranik.kalimbatabsviewer2.room.model.Playlist
import com.kontranik.kalimbatabsviewer2.room.viewmodel.PlaylistViewModel
import com.kontranik.kalimbatabsviewer2.ui.theme.paddingMedium
import com.kontranik.kalimbatabsviewer2.ui.theme.paddingSmall
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PlaylistSelectDialog(
    onDismiss: () -> Unit,
    onConfirm: (playlist: Playlist) -> Unit,
    playlistViewModel: PlaylistViewModel = viewModel(factory = AppViewModelProvider .Factory),
    ) {

    val coroutineScope = rememberCoroutineScope()

    var playlists = playlistViewModel.playlists.collectAsState(emptyList())

    PlaylistSelectDialogContent(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        onAddPlaylist = { name ->
            coroutineScope.launch {
                if (name.isNotEmpty() && playlists.value.find { it.playlistName == name  } == null) {
                        playlistViewModel.addPlaylist(name)
                }
            }
        },
        playlists = playlists
    )
}

@Composable
fun PlaylistSelectDialogContent(
    playlists: State<List<Playlist>>,
    onDismiss: () -> Unit,
    onConfirm: (playlist: Playlist) -> Unit,
    onAddPlaylist: (name: String) -> Unit = {},
    showInput: Boolean = false,
) {
    var result = rememberSaveable { mutableStateOf<Playlist?>(null) }
    var showAddPlaylistDialog by rememberSaveable { mutableStateOf(showInput) }

    var playlistName by rememberSaveable { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier.padding(paddingSmall)
        ) {
            Column(Modifier.padding(paddingSmall)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.select_playlist),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(paddingSmall).weight(1f)
                    )
                    IconButton(
                        onClick = { showAddPlaylistDialog = showAddPlaylistDialog.not() },
                    ) {
                        Icon(
                            imageVector = if (showAddPlaylistDialog) Icons.Default.KeyboardArrowUp
                                else Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_playlist)
                        )
                    }
                }
                if (showAddPlaylistDialog) {
                    val focusRequester = remember { FocusRequester() }
                    LaunchedEffect(Unit) {
                        focusRequester.requestFocus()
                    }
                    Row(Modifier.fillMaxWidth().padding(bottom = paddingMedium),
                        verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = playlistName,
                            label = { Text(stringResource(R.string.playlist_name)) },
                            onValueChange = { playlistName = it},
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                        )
                        IconButton (
                            onClick = {
                                onAddPlaylist(playlistName)
                                showAddPlaylistDialog = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = stringResource(R.string.add_playlist)
                            )
                        }
                    }
                }
                LazyColumn {
                    items(items = playlists.value,
                        key = { item -> item.playlistId!! }) {
                        Row(
                            Modifier
                                .padding(bottom = paddingSmall)
                                .background(
                                    color = if (it.playlistId == result.value?.playlistId) {
                                        MaterialTheme.colorScheme.inversePrimary
                                    } else {
                                        Color.Transparent
                                    }
                                )
                        ) {
                            Text(
                                text = it.playlistName,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(paddingSmall)
                                    .clickable {
                                        result.value = it
                                    },
                            )
                        }
                    }
                }
                Row(
                    Modifier
                        .padding(top = paddingMedium)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            result.value?.let { onConfirm(it) }
                        },
                    ) {
                        Text(stringResource(R.string.ok))
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun PlaylistSelectDialogContentPreview() {
    PlaylistSelectDialogContent(
        playlists = remember { mutableStateOf(listOf(
            Playlist(1, "Playlist 1"),
            Playlist(2, "Playlist 2"),
            Playlist(3, "Playlist 3"),
        )) },
        onDismiss = {},
        onConfirm = {},
    )
}

@Composable
@Preview
fun PlaylistSelectDialogContentPreviewWithInput() {
    PlaylistSelectDialogContent(
        playlists = remember { mutableStateOf(listOf(
            Playlist(1, "Playlist 1"),
            Playlist(2, "Playlist 2"),
            Playlist(3, "Playlist 3"),
        )) },
        onDismiss = {},
        onConfirm = {},
        showInput = true
    )
}