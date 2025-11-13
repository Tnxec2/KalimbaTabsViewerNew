package com.kontranik.kalimbatabsviewer2.ui.settings

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kontranik.kalimbatabsviewer2.AppViewModelProvider
import com.kontranik.kalimbatabsviewer2.R
import com.kontranik.kalimbatabsviewer2.ui.appbar.AppBar
import com.kontranik.kalimbatabsviewer2.ui.settings.SettingsViewModel.Companion.INTERFACE_ENTRIES
import com.kontranik.kalimbatabsviewer2.ui.settings.elements.SettingsCard
import com.kontranik.kalimbatabsviewer2.ui.settings.elements.SettingsList
import com.kontranik.kalimbatabsviewer2.ui.settings.elements.SettingsTitle
import com.kontranik.kalimbatabsviewer2.ui.theme.paddingSmall
import kotlinx.coroutines.launch

data class SettingsItem(
    @StringRes val title: Int,
    @DrawableRes val drawable:  Int?,
    val onClick: () -> Unit,
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsScreen(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val settingsState = settingsViewModel.settingsState.collectAsState()


    SettingsContent(
        drawerState = drawerState,
        navigateBack = { coroutineScope.launch { navigateBack() }},
        settingsState = settingsState.value,
        onChangeInterfaceTheme = {
            settingsViewModel.changeInterfaceTheme(it)
        }
    )
}


@Composable
fun SettingsContent(
    drawerState: DrawerState,
    navigateBack: () -> Unit,
    settingsState: Settings,
    onChangeInterfaceTheme: (String) -> Unit
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

            SettingsTitle(
                text = stringResource(id = R.string.interface_header),
                modifier = Modifier.padding(bottom = paddingSmall)
            )
            SettingsCard {
                SettingsList(
                    title = stringResource(id = R.string.interface_theme_title),
                    entryValues = INTERFACE_ENTRIES,
                    entries = listOf(
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

//            if (showBackupDialog) BackupDialog(
//                context = LocalContext.current,
//                onChange = {},
//                onDismiss = {showBackupDialog = false }
//            )
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
                onChangeInterfaceTheme = {}
            )
        }


}