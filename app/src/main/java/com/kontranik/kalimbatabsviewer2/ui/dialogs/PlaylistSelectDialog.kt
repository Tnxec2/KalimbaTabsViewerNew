package com.kontranik.kalimbatabsviewer2.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kontranik.kalimbatabsviewer2.R
import com.kontranik.kalimbatabsviewer2.room.model.Playlist

/**
 * A Jetpack Compose dialog to select an existing playlist or create a new one.
 *
 * @param playlists The list of existing playlists to choose from.
 * @param onDismissRequest Lambda invoked when the user wants to dismiss the dialog.
 * @param onSelectExisting Lambda invoked when the user selects an existing playlist and confirms. Passes the selected Playlist.
 * @param onCreateNew Lambda invoked when the user enters a name for a new playlist and confirms. Passes the new name.
 */
@Composable
fun PlaylistSelectDialog(
    playlists: List<Playlist>,
    onDismissRequest: () -> Unit,
    onSelectExisting: (Playlist) -> Unit,
    onCreateNew: (String) -> Unit
) {
    // State to track whether the user is creating a new playlist or selecting an existing one.
    var isCreatingNew by remember { mutableStateOf(false) }

    // State for the name of the new playlist.
    var newPlaylistName by remember { mutableStateOf("") }

    // State for which existing playlist is currently selected.
    var selectedPlaylist by remember { mutableStateOf<Playlist?>(null) }

    val canConfirm = (isCreatingNew && newPlaylistName.isNotBlank()) || (!isCreatingNew && selectedPlaylist != null)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.add_playlist)) },
        text = {
            Column {
                // Radio button to select an existing playlist
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isCreatingNew = false },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = !isCreatingNew,
                        onClick = { isCreatingNew = false }
                    )
                    Text(text = stringResource(R.string.add_playlist))
                }

                // Dropdown menu for existing playlists
                ExposedDropdownMenu(
                    enabled = !isCreatingNew && playlists.isNotEmpty(),
                    selectedPlaylist = selectedPlaylist,
                    playlists = playlists,
                    onPlaylistSelected = { selectedPlaylist = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Radio button to create a new playlist
                Row(
                    modifier = Modifier
                            .fillMaxWidth()
                        .clickable { isCreatingNew = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isCreatingNew,
                        onClick = { isCreatingNew = true }
                    )
                    Text(text = stringResource(R.string.create_new_playlist))
                }

                // Text field for new playlist name
                OutlinedTextField(
                    value = newPlaylistName,
                    onValueChange = { newPlaylistName = it },
                    label = { Text(text = stringResource(R.string.playlist_name)) },
                    singleLine = true,
                    enabled = isCreatingNew,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (isCreatingNew) {
                        onCreateNew(newPlaylistName)
                    } else {
                        selectedPlaylist?.let { onSelectExisting(it) }
                    }
                    onDismissRequest() // Close dialog after action
                },
                enabled = canConfirm
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenu(
    enabled: Boolean,
    selectedPlaylist: Playlist?,
    playlists: List<Playlist>,
    onPlaylistSelected: (Playlist) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) {
                expanded = !expanded
            }
        },
        modifier = Modifier.padding(start = 16.dp)
    ) {
        OutlinedTextField(
            value = selectedPlaylist?.playlistName ?: stringResource(R.string.select_playlist),
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            enabled = enabled,
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            playlists.forEach { playlist ->
                DropdownMenuItem(
                    text = { Text(playlist.playlistName) },
                    onClick = {
                        onPlaylistSelected(playlist)
                        expanded = false
                    }
                )
            }
        }
    }
}
