package com.kontranik.kalimbatabsviewer.ui.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kontranik.kalimbatabsviewer.R
import com.kontranik.kalimbatabsviewer.helper.SortHelper
import com.kontranik.kalimbatabsviewer.helper.SortHelper.Companion.sortOptions


@Composable
fun SortDialog(
    currentSortParams: SortHelper.SortParams,
    onDismissRequest: () -> Unit,
    onConfirm: (SortHelper.SortParams) -> Unit
) {
    var selectedOption by remember { mutableStateOf(currentSortParams) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.sort)) },
        text = {
            LazyColumn {
                items(sortOptions) { params ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (params.column == selectedOption.column && params.ascending == selectedOption.ascending),
                                onClick = { selectedOption = params }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (params.column == selectedOption.column && params.ascending == selectedOption.ascending),
                            onClick = { selectedOption = params }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(text = stringResource(id = params.idRes))
                    }
                }
            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    onConfirm(selectedOption)
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            OutlinedButton (
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true, name = "SearchBox Empty")
@Composable
fun SortDialogPreviewEmpty() {
    SortDialog (
        currentSortParams = sortOptions[3],
        onConfirm = {},
        onDismissRequest = {}
    )
}
