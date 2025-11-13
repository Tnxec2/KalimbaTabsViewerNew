package com.kontranik.kalimbatabsviewer2.ui

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
import com.kontranik.kalimbatabsviewer2.ui.playlist.PlaylistKtabsScreen
import com.kontranik.kalimbatabsviewer2.ui.playlist.PlaylistListScreen
import com.kontranik.kalimbatabsviewer2.ui.settings.SettingsScreen
import com.kontranik.kalimbatabsviewer2.ui.settings.SettingsViewModel
import com.kontranik.kalimbatabsviewer2.ui.song.KtabDetailScreen
import com.kontranik.kalimbatabsviewer2.ui.songlist.SongListScreen

import kotlinx.coroutines.launch

// Navigation Setup
@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(
    drawerState: DrawerState,
    navController: NavHostController,
    settingsViewModel: SettingsViewModel,
) {

    val start = MainNavOption.KTabList.name

    navigation(
        startDestination = start,
        route = NavRoutes.MainRoute.name
    ) {

        composable(
            route = MainNavOption.KTabList.name,
        ) {
            val coroutineScope = rememberCoroutineScope()
            SongListScreen(
                drawerState = drawerState,
                navigateBack = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
                openSong = { ktabid ->
                    navController.navigate("${MainNavOption.KTab.name}/${ktabid}") {
                        popUpTo(MainNavOption.KTab.name) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${MainNavOption.KTab.name}/{ktabid}",
            arguments = listOf(navArgument("ktabid") { type = NavType.StringType })
        ) {
            KtabDetailScreen(
                drawerState = drawerState,
                navigateBack = { navController.navigateUp() },
                settingsViewModel = settingsViewModel,
            )
        }

        composable(MainNavOption.Playlist.name) {
            val coroutineScope = rememberCoroutineScope()
            PlaylistListScreen(
                drawerState = drawerState,
                navigateBack = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
                openPlaylist = { playlistId ->
                    navController.navigate("${MainNavOption.PlaylistKtabList.name}/${playlistId}") {
                        popUpTo(MainNavOption.PlaylistKtabList.name) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = "${MainNavOption.PlaylistKtabList.name}/{playlistId}",
            arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
        ) {
            PlaylistKtabsScreen(
                drawerState = drawerState,
                navigateBack = { navController.navigateUp() },
                openKtab = { ktabid ->
                    navController.navigate("${MainNavOption.KTab.name}/${ktabid}") {
                        popUpTo(MainNavOption.KTab.name) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                }
            )
        }


        composable(
            route = MainNavOption.Settings.name,
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

enum class MainNavOption {
    KTabList,
    KTab,
    Playlist,
    PlaylistKtabList,
    Settings,
}