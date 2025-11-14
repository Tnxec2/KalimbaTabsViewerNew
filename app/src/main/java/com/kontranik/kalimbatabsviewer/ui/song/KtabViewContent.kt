package com.kontranik.kalimbatabsviewer.ui.song


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kontranik.kalimbatabsviewer.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer.ui.settings.Settings
import com.kontranik.kalimbatabsviewer.ui.theme.paddingSmall


@Composable
fun KTabViewContent(
    uiState: State<KTabRoom>,
    settings: State<Settings>,
    onToggleFavorite: () -> Unit,
    onToggleFullscreen: () -> Unit,
    onToggleLineBreak: () -> Unit,
    onToggleHideText: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onIncreaseFontSize: () -> Unit,
) {
    val verticalScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(verticalScrollState)
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        KtabDetailsIconBox(
            uiState = uiState,
            lineBreak = settings.value.lineBreak,
            onIncreaseFontSize = onIncreaseFontSize,
            onDecreaseFontSize = onDecreaseFontSize,
            onToggleFavorite = onToggleFavorite,
            onToggleLineBreak = onToggleLineBreak,
            onToggleFullscreen = onToggleFullscreen,
            hasUrl = uiState.value.hasUrl(),
            hideText = settings.value.hideText,
            onToggleHideText = onToggleHideText
        )
        Box(Modifier.fillMaxWidth()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                .fillMaxWidth()
            ) {
                ItemTitle(uiState.value)

                KTabRoomText(
                    uiState,
                    settings
                )
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun SongDetailScreenPreview() {
    Surface(Modifier.height(500.dp)) {
        Column(Modifier.fillMaxSize()) {
            ItemTitle(mocupKtabRoomState.value, modifier = Modifier.padding(paddingSmall))
            KTabViewContent(
                remember {
                    mutableStateOf(
                        mocupKtabRoomState.value.copy(
                            bookmarked = false,
                        )
                    )
                },
                onToggleFavorite = {},
                onToggleFullscreen = {},
                onToggleLineBreak = {},
                onDecreaseFontSize = {},
                onIncreaseFontSize = {},
                onToggleHideText = {},
                settings = remember { mutableStateOf(Settings().copy(lineBreak = false,)) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SongDetailScreenPreviewWithMenuFullScreen() {
    Surface(Modifier.height(500.dp)) {
        Column(Modifier.fillMaxSize()) {
            KTabViewContent(
                remember {
                    mutableStateOf(
                        mocupKtabRoomState.value.copy(
                            bookmarked = false,
                            difficulty = 5
                        )
                    )
                },
                onToggleFavorite = {},
                onToggleFullscreen = {},
                onToggleLineBreak = {},
                onDecreaseFontSize = {},
                onIncreaseFontSize = {},
                onToggleHideText = {},
                settings = remember { mutableStateOf(Settings()) }
            )
        }
    }
}