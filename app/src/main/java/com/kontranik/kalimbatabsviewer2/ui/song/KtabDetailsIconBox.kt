package com.kontranik.kalimbatabsviewer2.ui.song

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.WrapText
import androidx.compose.material.icons.filled.FormatAlignJustify
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.HideSource
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.TextDecrease
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentDataType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import java.util.Date

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KtabDetailsIconBox(
    uiState: State<KTabRoom>,
    lineBreak: Boolean,
    hideText: Boolean,
    onToggleFavorite: () -> Unit,
    onToggleHideText: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onIncreaseFontSize: () -> Unit,
    onToggleLineBreak: () -> Unit,
    hasUrl: Boolean,
    showUrls: Boolean = false,
) {

    var showUrlsMenu by rememberSaveable { mutableStateOf(showUrls) }


    FlowRow(
        Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        IconButton(onClick = { onToggleFavorite() }) {
            Icon(
                imageVector = if (uiState.value.bookmarked)
                    Icons.Filled.Star
                else
                    Icons.Default.StarBorder,
                contentDescription = "Favorites"
            )
        }

        Row {
            IconButton(onClick = { onDecreaseFontSize() }) {
                Icon(
                    imageVector = Icons.Default.TextDecrease,
                    contentDescription = "Decrease font size"
                )
            }
            IconButton(onClick = { onIncreaseFontSize() }) {
                Icon(
                    imageVector = Icons.Default.TextIncrease,
                    contentDescription = "Increase font size"
                )
            }
        }


        //Spacer(Modifier.weight(1f))
        IconButton(onClick = { onToggleHideText() }) {
            Icon(
                imageVector = if (!hideText) Icons.Default.HideSource else Icons.Default.TextFields,
                contentDescription = "toggle hide text",
            )
        }
        IconButton(onClick = { onToggleLineBreak() }) {
            Icon(
                imageVector = if (!lineBreak) Icons.AutoMirrored.Filled.WrapText else Icons.Default.FormatAlignJustify,
                contentDescription = "toggle line break",
            )
        }
        IconButton(onClick = { onToggleFullscreen() }) {
            Icon(
                imageVector = Icons.Default.Fullscreen,
                contentDescription = "toggle fullscreen"
            )
        }

        if (hasUrl) {
            Box {
                IconButton(onClick = { showUrlsMenu = showUrlsMenu.not()}) {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = "URL's"
                    )
                }
                if (showUrlsMenu)
                    UrlMenu(
                        source = uiState.value.source,
                        youtube = uiState.value.youtube,
                        onDismiss = { showUrlsMenu = false }
                    )
            }
        }
    }
}

@Preview(showBackground = true, name = "Not bookmarked")
@Composable
fun KtabDetailsIconBoxPreviewNotBookmarked() {
    val sampleState = remember {
        mutableStateOf(
            KTabRoom(
                kTabId = "1",
                title = "Preview Song",
                interpreter = "Preview Artist",
                bookmarked = false, // Song ist NICHT als Favorit markiert
                source = "https://example.com",
                youtube = "https://youtube.com/watch?v=123",
                updated = Date()
            )
        )
    }

    KtabDetailsIconBox(
        uiState = sampleState,
        lineBreak = false,
        hideText = false,
        onToggleHideText = { /* In der Vorschau leer */ },
        onToggleFavorite = { /* In der Vorschau leer */ },
        onDecreaseFontSize = { /* In der Vorschau leer */ },
        onToggleFullscreen = { /* In der Vorschau leer */ },
        onIncreaseFontSize = { /* In der Vorschau leer */ },
        onToggleLineBreak = { /* In der Vorschau leer */ },
        hasUrl = true
    )
}

@Preview(showBackground = true, name = "Bookmarked, No URLs")
@Composable
fun KtabDetailsIconBoxPreviewBookmarked() {
    val sampleState = remember {
        mutableStateOf(
            KTabRoom(
                kTabId = "2",
                title = "Another Song",
                interpreter = "Another Artist",
                bookmarked = true, // Song ist als Favorit markiert
                updated = Date()
            )
        )
    }

    KtabDetailsIconBox(
        uiState = sampleState,
        lineBreak = true, // Zeilenumbruch ist aktiv
        hideText = true,
        onToggleHideText = { /* In der Vorschau leer */ },
        onToggleFavorite = { },
        onDecreaseFontSize = { },
        onToggleFullscreen = { },
        onIncreaseFontSize = { },
        onToggleLineBreak = { },
        hasUrl = false // Es gibt keine URLs, das Sprach-Icon wird nicht angezeigt
    )
}