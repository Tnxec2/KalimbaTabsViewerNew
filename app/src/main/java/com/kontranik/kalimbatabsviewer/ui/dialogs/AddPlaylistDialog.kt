package com.kontranik.kalimbatabsviewer.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kontranik.kalimbatabsviewer.R

/**
 * A Jetpack Compose dialog for adding or editing a playlist name.
 *
 * @param initialPlaylistName The initial name to display in the text field. Can be empty for a new playlist.
 * @param onDismissRequest Lambda to be invoked when the user wants to dismiss the dialog (e.g., by clicking outside or pressing the back button).
 * @param onConfirm Lambda to be invoked when the user clicks the 'Save' button. It provides the new playlist name.
 */
@Composable
fun AddPlaylistDialog(
    initialPlaylistName: String,
    onDismissRequest: () -> Unit,
    onConfirm: (playlistName: String) -> Unit
) {
    // Internal state to hold the text field's current value.
    var text by remember { mutableStateOf(initialPlaylistName) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            // The title can be dynamic based on whether it's a new or existing playlist.
            val titleText = if (initialPlaylistName.isEmpty()) {
                stringResource(R.string.add_playlist)
            } else {
                stringResource(R.string.playlist_name, initialPlaylistName)
            }
            Text(text = titleText)
        },
        text = {
            Column {
                Text(text = stringResource(R.string.playlist_name))
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    label = { Text(stringResource(R.string.playlist_name)) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(text)
                    onDismissRequest() // Close the dialog after confirming.
                },
                // The confirm button is disabled if the playlist name is blank.
                enabled = text.isNotBlank()
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
