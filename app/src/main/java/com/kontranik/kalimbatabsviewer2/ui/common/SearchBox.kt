package com.kontranik.kalimbatabsviewer2.ui.common

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.stringResource
import com.kontranik.kalimbatabsviewer2.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

@Composable
fun SearchBox(
    queryState: MutableStateFlow<String?>,
    minLength: Int = 2,
    onChangeSearchQuery: (query: String?) -> Unit
    ){
    val coroutineScope = rememberCoroutineScope()
    var query by rememberSaveable {
        mutableStateOf(queryState.value ?: "")
    }
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                coroutineScope.launch {
                    onChangeSearchQuery(query)
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
                    query = ""
                    onChangeSearchQuery(query)
                }
            }) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = stringResource(R.string.search)
            )
        }
    }

}