package com.kontranik.kalimbatabsviewer.ui.songlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.kontranik.kalimbatabsviewer.R
import com.kontranik.kalimbatabsviewer.ui.appbar.AppBarAction
import com.kontranik.kalimbatabsviewer.ui.common.ConfirmDialog
import com.kontranik.kalimbatabsviewer.ui.common.ConfirmDialogData
import com.kontranik.kalimbatabsviewer.ui.theme.paddingBig
import com.kontranik.kalimbatabsviewer.ui.theme.paddingMedium

import kotlinx.coroutines.launch


@Composable
fun SyncAppBarAction(
    onSyncCompleted: () -> Unit = {},
    syncViewModel: SyncViewModel,
    isAppBarAction: Boolean = true
) {
    val syncMessage = syncViewModel.syncMessage.collectAsState(null)
    val syncState = syncViewModel.syncState.collectAsState()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var confirmDialogData by remember { mutableStateOf(ConfirmDialogData()) }
    if (isAppBarAction)
        AppBarAction(
            appBarAction = AppBarAction(
                vector = Icons.Default.Sync,
                description = R.string.sync,
                onClick = { confirmDialogData = ConfirmDialogData(
                    show = true,
                    title = context.getString(R.string.sync_songs),
                    text = context.getString(R.string.do_you_want_to_sync_songs),
                    onConfirm = {
                        confirmDialogData = ConfirmDialogData()
                        coroutineScope.launch {
                            syncViewModel.syncSongs()
                            onSyncCompleted()
                        }
                    },
                    onDismiss = {
                        confirmDialogData = ConfirmDialogData()
                    }
                )
                }
            )
        )
    else
        DropdownMenuItem(text = {
            Text(stringResource(R.string.sync))
        },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = stringResource(R.string.sync)
                )
            },
            onClick = {
                confirmDialogData = ConfirmDialogData(
                    show = true,
                    title = context.getString(R.string.sync_songs),
                    text = context.getString(R.string.do_you_want_to_sync_songs),
                    onConfirm = {
                        confirmDialogData = ConfirmDialogData()
                        coroutineScope.launch {
                            syncViewModel.syncSongs()
                            onSyncCompleted()
                        }
                    },
                    onDismiss = {
                        confirmDialogData = ConfirmDialogData()
                    }
                )
            }
        )

    ConfirmDialog(
        data = confirmDialogData
    )

    SyncMessageDialog(
        syncMessage,
        syncState,
        onDismiss = {
            syncViewModel.syncState.value = SyncState()
        }
    )
}

@Composable
fun SyncMessageDialog(
    syncMessage: State<String?>,
    syncState: State<SyncState>,
    onDismiss: () -> Unit = {}
) {
    syncMessage.value?.let { message ->
        Dialog(onDismissRequest = {
            if (syncState.value.isFinished) {
                onDismiss()
            }
        }) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(paddingMedium)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = if (syncState.value.isFinished.not()) stringResource(R.string.syncing) else stringResource(
                            R.string.sync_finished
                        ),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = paddingMedium)
                    )
                    Text(
                        text = stringResource(R.string.do_not_close_the_app),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = paddingMedium)
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (syncState.value.error!=null) Color.Red else Color.Unspecified,
                    )
                    if (syncState.value.isFinished) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = paddingBig),
                            horizontalArrangement = Arrangement.End
                        ) {
                            OutlinedButton(onClick = { onDismiss() }) {
                                Text(
                                    text = stringResource(R.string.ok),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSyncMessageDialog() {
    val s = stringResource(R.string.sync_completed_successfully)
    val syncState = remember { mutableStateOf(SyncState(state = SyncStateType.FINISHED)) }
    val syncMessage = remember { mutableStateOf(s) }

    SyncMessageDialog(
        syncMessage = syncMessage,
        syncState = syncState,
        onDismiss = {}
    )
}