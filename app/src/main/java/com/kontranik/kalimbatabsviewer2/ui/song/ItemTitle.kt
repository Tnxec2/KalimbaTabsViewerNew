package com.kontranik.kalimbatabsviewer2.ui.song

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer2.ui.theme.paddingSmall

@Composable
fun ItemTitle(kTabRoom: KTabRoom, modifier: Modifier = Modifier.padding(paddingSmall)) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(kTabRoom.title, style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f))
        }
        if (kTabRoom.interpreter.isNotEmpty())
            Row() {
                Text(
                    kTabRoom.interpreter,
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            }
    }
}

val mocupKtabRoomState = mutableStateOf(
    KTabRoom(
            kTabId = "1",
            difficulty = 1,
            bookmarked = false,
            title = "Jingle Bells",
            interpreter = "Christmas Songs",
            source = "https://tnxec2.github.io/kalimbatabs-web/",
            youtube = "https://www.youtube.com/watch?v=CzlzGqn2VOw",
            text =   "3   3   3     3   3   3      3  5    1  2   3\n" +
                    ">Jin-gle bells jin-gle bells jin-gle all the way\n" +
                    "4  4    4   4  4  3   3   3  3 3   2     2 3   2      5\n" +
                    ">Oh what fun it is to ride in a one horse o-pen sleigh HEY!\n" +
                    "3   3   3     3   3   3      3  5    1  2   3\n" +
                    ">Jin-gle bells jin-gle bells jin-gle all the way\n" +
                    "4   4   4   4  4   3  3   3  3 5    5    4 3     1\n" +
                    ">Oh what fun it is to ride in a one horse o-pen sleigh"
        )
    )