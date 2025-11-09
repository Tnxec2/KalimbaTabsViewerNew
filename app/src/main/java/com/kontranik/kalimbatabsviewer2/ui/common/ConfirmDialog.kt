package com.kontranik.kalimbatabsviewer2.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import java.io.Serializable

@Composable
fun ConfirmDialog(
    data: ConfirmDialogData = ConfirmDialogData(),
) {
    if (data.show) {
        AlertDialog(
            onDismissRequest = { data.onDismiss() },
            title = { Text(text = data.title) },
            text = { Text(text = data.text) },
            confirmButton = {
                TextButton(
                    onClick = {
                        data.onConfirm()
                    }
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { data.onDismiss() }
                ) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            }
        )
    }
}

data class ConfirmDialogData(
    val show: Boolean = false,
    val title: String = "Confirm",
    val text: String = "Are you sure?",
    val onConfirm: () -> Unit = {},
    val onDismiss: () -> Unit = {},
) : Serializable