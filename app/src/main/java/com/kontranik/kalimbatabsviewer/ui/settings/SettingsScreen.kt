package com.kontranik.kalimbatabsviewer.ui.settings

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.kalimbatabsviewer.AppViewModelProvider
import com.kontranik.kalimbatabsviewer.R
import com.kontranik.kalimbatabsviewer.helper.SortHelper
import com.kontranik.kalimbatabsviewer.helper.TransposeTypes
import com.kontranik.kalimbatabsviewer.helper.TransposeTypes.Companion.TRANSPOSE_ENTRIES
import com.kontranik.kalimbatabsviewer.ui.appbar.AppBar
import com.kontranik.kalimbatabsviewer.ui.settings.SettingsViewModel.Companion.INTERFACE_ENTRIES
import com.kontranik.kalimbatabsviewer.ui.settings.elements.SettingsCard
import com.kontranik.kalimbatabsviewer.ui.settings.elements.SettingsList
import com.kontranik.kalimbatabsviewer.ui.theme.paddingSmall
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    val settingsState = settingsViewModel.settingsState.collectAsState()

    SettingsContent(
        drawerState = drawerState,
        navigateBack = { coroutineScope.launch { navigateBack() }},
        settingsState = settingsState.value,
        onChangeInterfaceTheme = {
            settingsViewModel.changeInterfaceTheme(it)
        },
        onChangeTransposeTune = {
            settingsViewModel.changeTransposeTune(it)
        },
        onChangeDefaultSortAllSongs = {
            settingsViewModel.changeDefaultSortAllSongs(it)
        },
        onChangeDefaultSortPlaylists = {
            settingsViewModel.changeDefaultSortPlaylists(it)
        }
    )
}

@Composable
fun SettingsContent(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    settingsState: Settings,
    onChangeInterfaceTheme: (String) -> Unit,
    onChangeTransposeTune: (String) -> Unit,
    onChangeDefaultSortAllSongs: (String) -> Unit,
    onChangeDefaultSortPlaylists: (String) -> Unit
) {
    Scaffold(
        topBar = {
            AppBar(
                titleRes = R.string.settings,
                drawerState = drawerState,
                navigationIcon = {
                    IconButton(onClick = {  navigateBack() }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = stringResource(R.string.menu))
                    }
                },)
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(paddingSmall)
                .verticalScroll(rememberScrollState())
        ) {

            SettingsCard(title = stringResource(R.string.interface_header), modifier = Modifier.padding(top = paddingSmall)) {
                SettingsList(
                    title = stringResource(id = R.string.interface_theme_title),
                    entryValues = INTERFACE_ENTRIES,
                    entryTitles = listOf(
                        stringResource(R.string.theme_light),
                        stringResource(R.string.theme_dark),
                        stringResource(R.string.theme_system),
                    ),
                    defaultValue = settingsState.interfaceTheme,
                    icon = R.drawable.ic_baseline_preview_24,
                    onChange = { onChangeInterfaceTheme(it) },
                    showDefaultValue = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SettingsCard(title = stringResource(R.string.default_tune), modifier = Modifier.padding(top = paddingSmall)) {
                SettingsList(
                    title = stringResource(id = R.string.transpose),
                    entryValues = TRANSPOSE_ENTRIES.map{it.first},
                    entryTitles = TRANSPOSE_ENTRIES.map{stringResource(it.second)},
                    defaultValue = settingsState.tune,
                    defaultValueTitle = TransposeTypes.getFromTitle(settingsState.tune)?.second?.let { stringResource(it) },
                    icon = R.drawable.ic_baseline_music_note_24,
                    onChange = { onChangeTransposeTune(it) },
                    showDefaultValue = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            SettingsCard(title = stringResource(R.string.sort), modifier = Modifier.padding(top = paddingSmall)) {
                SettingsList(
                    title = stringResource(id = R.string.default_sort_all_songs),
                    entryValues = SortHelper.sortSettingsEntries,
                    entryTitles = SortHelper.sortSettingsValues.map{stringResource(it)},
                    defaultValue = settingsState.sortAllSongs.toString(),
                    icon = R.drawable.ic_baseline_sort_24,
                    onChange = { onChangeDefaultSortAllSongs(it) },
                    showDefaultValue = true,
                    modifier = Modifier.fillMaxWidth()
                )
                SettingsList(
                    title = stringResource(id = R.string.default_sort_playlists),
                    entryValues = SortHelper.sortSettingsEntries,
                    entryTitles = SortHelper.sortSettingsValues.map{stringResource(it)},
                    defaultValue = settingsState.sortPlaylists.toString(),
                    icon = R.drawable.ic_baseline_sort_24,
                    onChange = { onChangeDefaultSortPlaylists(it) },
                    showDefaultValue = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Preview
@Composable
private fun SettingsContentPreview() {

        Surface {
            SettingsContent(
                drawerState = DrawerState(DrawerValue.Closed),
                navigateBack = {},
                settingsState = Settings(),
                onChangeInterfaceTheme = {},
                onChangeTransposeTune = {},
                onChangeDefaultSortAllSongs = {},
                onChangeDefaultSortPlaylists = {},
            )
        }


}