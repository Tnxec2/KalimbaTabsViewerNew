package com.kontranik.kalimbatabsviewer.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.DrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.kontranik.kalimbatabsviewer.ui.playlist.PlaylistKtabsScreen
import com.kontranik.kalimbatabsviewer.ui.playlist.PlaylistListScreen
import com.kontranik.kalimbatabsviewer.ui.settings.SettingsScreen
import com.kontranik.kalimbatabsviewer.ui.settings.SettingsViewModel
import com.kontranik.kalimbatabsviewer.ui.song.KtabDetailScreen
import com.kontranik.kalimbatabsviewer.ui.songlist.AllKTabsListScreen

import kotlinx.coroutines.launch

// Navigation Setup
@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(
    drawerState: DrawerState,
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
) {

    val start = Screen.KTabList.route

    navigation(
        startDestination = start,
        route = NavRoutes.MainRoute.name
    ) {

        composable(
            route = Screen.KTabList.route,
        ) {
            val coroutineScope = rememberCoroutineScope()
            AllKTabsListScreen(
                drawerState = drawerState,
                navigateBack = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
                openSong = { ktabid ->
                    navController.navigate(Screen.KTabDetails.createRoute(ktabid)) {
                        popUpTo(Screen.KTabDetails.route) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.KTabDetails.route,
            arguments = listOf(navArgument("ktabid") { type = NavType.StringType })
        ) {
            KtabDetailScreen(
                drawerState = drawerState,
                navigateBack = { navController.navigateUp() },
                settingsViewModel = settingsViewModel,
            )
        }

        composable(Screen.Playlist.route) {
            val coroutineScope = rememberCoroutineScope()
            PlaylistListScreen(
                drawerState = drawerState,
                navigateBack = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
                openPlaylist = { playlistId ->
                    navController.navigate(Screen.PlaylistKtabList.createRoute(playlistId)) {
                        popUpTo(Screen.PlaylistKtabList.route) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.PlaylistKtabList.route,
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) {
            PlaylistKtabsScreen(
                drawerState = drawerState,
                navigateBack = { navController.navigateUp() },
                openKtab = { ktabid ->
                    navController.navigate(Screen.KTabDetails.createRoute(ktabid)) {
                        popUpTo(Screen.KTabDetails.route) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }


        composable(
            route = Screen.Settings.route,
        ) {
            val coroutineScope = rememberCoroutineScope()
            SettingsScreen(
                drawerState = drawerState,
                navigateBack = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
                settingsViewModel = settingsViewModel
            )
        }
    }
}
