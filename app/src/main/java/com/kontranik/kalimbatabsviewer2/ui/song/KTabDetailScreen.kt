package com.kontranik.kalimbatabsviewer2.ui.song

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.kalimbatabsviewer2.AppViewModelProvider
import com.kontranik.kalimbatabsviewer2.R
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer2.ui.appbar.AppBar
import com.kontranik.kalimbatabsviewer2.ui.appbar.AppBarAction
import com.kontranik.kalimbatabsviewer2.ui.common.ConfirmDialog
import com.kontranik.kalimbatabsviewer2.ui.common.ConfirmDialogData
import com.kontranik.kalimbatabsviewer2.ui.playlist.PlaylistSelectDialog
import com.kontranik.kalimbatabsviewer2.ui.settings.SettingsViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun KtabDetailScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    viewModel: KTabDetailsViewModel = viewModel(factory = AppViewModelProvider .Factory),
    settingsViewModel: SettingsViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState = viewModel.ktabUiState.collectAsStateWithLifecycle(KTabRoom(""))

    var confirmData by rememberSaveable { mutableStateOf(ConfirmDialogData()) }

    var fullScreen by rememberSaveable { mutableStateOf(false) }
    var expandedMenu by rememberSaveable { mutableStateOf(false) }
    var showPlayListDialog by rememberSaveable { mutableStateOf(false) }

    var settings = settingsViewModel.settingsState.collectAsStateWithLifecycle()

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            if (!fullScreen) {
                AppBar(
                    title = {
                        ItemTitle(uiState.value, modifier = Modifier.padding(16.dp))
                    },
                    drawerState = drawerState,
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { navigateBack() } }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(id = R.string.back)
                            )
                        }
                    },
                    appBarActions = listOf{
                        AppBarAction(
                            appBarAction = AppBarAction(
                                vector = Icons.Filled.MoreVert,
                                description = R.string.menu,
                                onClick = { expandedMenu = true }
                            )
                        )
                        DropdownMenu(
                            expanded = expandedMenu,
                            onDismissRequest = { expandedMenu = false }
                        ) {
                            DropdownMenuItem(text = {
                                    Text(stringResource(R.string.add_to_playlist))
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                        contentDescription = stringResource(R.string.add_to_playlist)
                                    )
                                },
                                onClick = { showPlayListDialog = true }
                            )
                        }

                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
    ) { padding ->
        Column(Modifier
            .padding(padding)
            .fillMaxSize()) {
            ConfirmDialog(
                data = confirmData
            )
            KTabViewContent(
                uiState,
                settings = settings,
                onToggleFavorite = {
                    coroutineScope.launch {
                        viewModel.toggleFavorite()
                    }
                },
                onToggleFullscreen = {
                    fullScreen = fullScreen.not()
                },
                onToggleLineBreak = {
                    coroutineScope.launch {
                        settingsViewModel.toggleLineBreak()
                    }
                },
                onDecreaseFontSize = {
                    coroutineScope.launch {
                        settingsViewModel.decreaseFontSize()
                    }
                },
                onIncreaseFontSize = {
                    coroutineScope.launch {
                        settingsViewModel.increaseFontSize()
                    }
                },
                onToggleHideText = {
                    coroutineScope.launch {
                        settingsViewModel.toggleHideText()
                    }
                }
            )
        }

        if (showPlayListDialog) {
            PlaylistSelectDialog(
                onDismiss = { showPlayListDialog = false },
                onConfirm = { playlist ->
                    coroutineScope.launch {
                        viewModel.addToPlaylist(uiState.value.kTabId, playlist)
                        showPlayListDialog = false
                        expandedMenu = false
                        snackbarHostState.showSnackbar(
                            context.getString(
                                R.string.added_to_playlist,
                                playlist.playlistName
                            ))
                    }
                }
            )
        }
    }

}




