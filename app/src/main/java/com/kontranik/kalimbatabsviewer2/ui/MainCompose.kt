package com.kontranik.kalimbatabsviewer2.ui

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.kontranik.kalimbatabsviewer2.AppViewModelProvider
import com.kontranik.kalimbatabsviewer2.R
import com.kontranik.kalimbatabsviewer2.ui.appdrawer.AppDrawerContent
import com.kontranik.kalimbatabsviewer2.ui.appdrawer.AppDrawerItemInfo
import com.kontranik.kalimbatabsviewer2.ui.settings.SettingsViewModel
import com.kontranik.kalimbatabsviewer2.ui.theme.KalimbaTabsViewer2Theme
import kotlinx.coroutines.launch

data class DarkTheme(val isDark: Boolean = false)

val LocalTheme = compositionLocalOf { DarkTheme() }

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainCompose(
    navController: NavHostController = rememberNavController(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {
    val scope = rememberCoroutineScope()

    val settingsViewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val closeDrawer: () -> Unit = {
        if (drawerState.isOpen)
            scope.launch { drawerState.close() }
        else
            scope.launch { navController.popBackStack() }
    }

    // val interfaceTheme =  // settingsViewModel.interfaceTheme.collectAsState(GlobalSettingsViewModel.INTERFACE_THEME_LIGHT)

//    val darkTheme = when (interfaceTheme.value) {
//        GlobalSettingsViewModel.INTERFACE_THEME_LIGHT -> DarkTheme(false)
//        GlobalSettingsViewModel.INTERFACE_THEME_DARK -> DarkTheme(true)
//        else -> DarkTheme(isSystemInDarkTheme())
//    }

    CompositionLocalProvider(LocalTheme provides DarkTheme(false)) {
        KalimbaTabsViewer2Theme(darkTheme = LocalTheme.current.isDark)
        {
            Surface {
                BackHandler(enabled = drawerState.isOpen, onBack = closeDrawer)
                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        AppDrawerContent(
                            drawerState = drawerState,
                            menuItems = DrawerParams.drawerButtons,
                            defaultPick = MainNavOption.KTabList
                        ) { onUserPickedOption ->
                            when (onUserPickedOption) {
                                MainNavOption.KTabList,
                                MainNavOption.KTab,
                                MainNavOption.Bookmarks,
                                MainNavOption.Playlist,
                                MainNavOption.Settings
                                    -> {
                                    navController.navigate(onUserPickedOption.name) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(NavRoutes.MainRoute.name) {
                                            saveState = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) {
                    NavHost(
                        navController,
                        startDestination = NavRoutes.MainRoute.name
                    ) {
                        mainGraph(
                            drawerState,
                            navController,
                            settingsViewModel)
                    }
                }
            }
        }
    }
}

enum class NavRoutes {
    MainRoute,
}

object DrawerParams {
    val drawerButtons = arrayListOf(
        AppDrawerItemInfo(
            drawerOption =  MainNavOption.KTabList,
            descriptionId = R.string.menu_all_songs,
            imageVector = Icons.AutoMirrored.Filled.LibraryBooks,
            title = R.string.menu_all_songs
        ),
        AppDrawerItemInfo(
            drawerOption =  MainNavOption.Settings,
            descriptionId = R.string.settings,
            imageVector = Icons.Default.Settings,
            title = R.string.settings
        )
    )
}
