package com.kontranik.kalimbatabsviewer.ui.common

import android.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.kontranik.kalimbatabsviewer.ui.theme.paddingMedium
import com.kontranik.kalimbatabsviewer.ui.theme.paddingSmall


@Composable
fun EditDialog(
    originalInput: String,
    label: String,
    onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var result by rememberSaveable { mutableStateOf(originalInput) }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .padding(paddingSmall)
        ) {
            Column(Modifier.padding(paddingSmall)) {
                OutlinedTextField(
                    value = result,
                    onValueChange = { result = it },
                    label = { Text(label) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    modifier = Modifier.focusRequester(focusRequester)
                )
                Row(
                    Modifier.padding(top = paddingMedium).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = onDismiss
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    OutlinedButton(
                        onClick = {
                            onConfirm(result)
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
fun EditDialogPreview() {
    EditDialog(
        originalInput = "Test",
        label = "Test",
        onDismiss = {},
        onConfirm = {}
    )
}