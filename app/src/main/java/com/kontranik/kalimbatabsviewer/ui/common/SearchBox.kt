package com.kontranik.kalimbatabsviewer.ui.common

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.kontranik.kalimbatabsviewer.R
import com.kontranik.kalimbatabsviewer.ui.theme.paddingSmall
import kotlinx.coroutines.launch

@Composable
fun SearchBox(
    queryState: String,
    minLength: Int = 2,
    focus: Boolean = true,
    onChangeSearchQuery: (query: String?) -> Unit
    ){
    val coroutineScope = rememberCoroutineScope()
    val query = remember(queryState) {
        mutableStateOf(queryState)
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        if (focus) focusRequester.requestFocus()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = paddingSmall, bottom = paddingSmall)
    ) {
        OutlinedTextField(
            value = query.value,
            onValueChange = {
                coroutineScope.launch {
                    query.value = it
                    onChangeSearchQuery(query.value)
                }
            },
            label = {
                Text(text = stringResource(R.string.search))
            },

            modifier = Modifier
                .weight(1f)
                .focusable(true)
                .focusRequester(focusRequester)
        )
        IconButton(
            onClick = {
                coroutineScope.launch {
                    query.value = ""
                    onChangeSearchQuery("")
                }
            }) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = stringResource(R.string.search)
            )
        }
    }
}

@Preview(showBackground = true, name = "SearchBox Empty")
@Composable
fun SearchBoxPreviewEmpty() {
    SearchBox(
        queryState = "", // Leerer Anfangszustand
        onChangeSearchQuery = {
            // In der Vorschau passiert bei einer Änderung nichts.
            // Das Lambda kann leer bleiben.
        }
    )
}

@Preview(showBackground = true, name = "SearchBox With Text")
@Composable
fun SearchBoxPreviewWithText() {
    SearchBox(
        queryState = "Für Elise", // Anfangszustand mit Text
        onChangeSearchQuery = {
            // Auch hier bleibt das Lambda für die Vorschau leer.
        }
    )
}