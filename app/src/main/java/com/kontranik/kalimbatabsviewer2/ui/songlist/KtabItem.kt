package com.kontranik.kalimbatabsviewer2.ui.songlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kontranik.kalimbatabsviewer2.R
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import java.util.Date


@Composable
fun KtabItem(
    contextMenu: List<@Composable () -> Unit>? = null,
    ktab: KTabRoom,
    showUpdateTime: Boolean = false,
    onOpenKtab: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    var showMenu by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (ktab.bookmarked) Icons.Filled.Star else Icons.Default.StarBorder,
                contentDescription = "Favorite Icon",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 16.dp)
                    .clickable { onToggleFavorite() }
            )
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clickable { onOpenKtab() }
            ) {
                if (showUpdateTime)
                    Text(
                        text = stringResource(
                            R.string.updated_date,
                            ktab.updated.toLocaleString()
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.End)
                    )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            text = ktab.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Row(Modifier.fillMaxWidth()) {
                            Text(
                                text = ktab.interpreter,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }


            if (contextMenu != null ) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = stringResource(R.string.menu_song))
                    }
                    if (showMenu) {
                        DropdownMenu(
                            expanded = true,
                            onDismissRequest = { showMenu = false },
                        ) {
                            contextMenu.map {
                                it()
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
fun PreviewSongWithAuthorItem() {

    val ktab = KTabRoom(
            kTabId = "1",
            title = "Preview Song",
            interpreter = "Interpreter",
            difficulty = 3,
            bookmarked = false,
            updated = Date(),
        )

    KtabItem(
        ktab = ktab,
        showUpdateTime = true,
        onOpenKtab = { },
        onToggleFavorite = {  }
    )
}

