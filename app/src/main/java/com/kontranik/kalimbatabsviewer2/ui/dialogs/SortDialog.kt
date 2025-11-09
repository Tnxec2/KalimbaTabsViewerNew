package com.kontranik.kalimbatabsviewer2.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kontranik.kalimbatabsviewer2.R

// These can be moved to a more central location if used elsewhere
const val SORT_COLUMN_TITLE = "title"
const val SORT_COLUMN_INTERPRETER = "interpreter"
const val SORT_COLUMN_DIFFICULTY = "difficulty"
const val SORT_COLUMN_UPDATED = "updated"

data class SortParams(
    val column: String = SORT_COLUMN_TITLE,
    val ascending: Boolean = true
)

// A list of all available sorting options for the UI
private val sortOptions = listOf(
    SortParams(SORT_COLUMN_TITLE, true) to R.string.sort_title_asc,
    SortParams(SORT_COLUMN_TITLE, false) to R.string.sort_title_desc,
    SortParams(SORT_COLUMN_INTERPRETER, true) to R.string.sort_interpreter,
    SortParams(SORT_COLUMN_INTERPRETER, false) to R.string.sort_interpreter_desc,
    SortParams(SORT_COLUMN_DIFFICULTY, true) to R.string.sort_difficulty_asc,
    SortParams(SORT_COLUMN_DIFFICULTY, false) to R.string.sort_difficulty_desc,
    SortParams(SORT_COLUMN_UPDATED, false) to R.string.sort_update_time_new_first,
)

@Composable
fun SortDialog(
    currentSortParams: SortParams,
    onDismissRequest: () -> Unit,
    onConfirm: (SortParams) -> Unit
) {
    // Internal state to manage the user's selection within the dialog
    var selectedOption by remember { mutableStateOf(currentSortParams) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(R.string.sort)) },
        text = {
            Column {
                sortOptions.forEach { (params, stringResId) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (params == selectedOption),
                                onClick = { selectedOption = params }
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (params == selectedOption),
                            onClick = { selectedOption = params }
                        )
                        Spacer(modifier = Modifier.padding(start = 16.dp))
                        Text(text = stringResource(id = stringResId))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(selectedOption)
                    onDismissRequest()
                }
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
