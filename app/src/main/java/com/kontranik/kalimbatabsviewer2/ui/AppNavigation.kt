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
import com.kontranik.kalimbatabsviewer2.ui.songlist.SongListScreen

import kotlinx.coroutines.launch

// Navigation Setup
@RequiresApi(Build.VERSION_CODES.O)
fun NavGraphBuilder.mainGraph(
    drawerState: DrawerState,
    navController: NavHostController
) {

    val start = MainNavOption.SongList.name

    navigation(
        startDestination = start,
        route = NavRoutes.MainRoute.name
    ) {
        composable(
            route = MainNavOption.SongList.name
        ) {
            val coroutineScope = rememberCoroutineScope()
            SongListScreen(
                drawerState = drawerState,
                navigateBack = {
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
                openSong = { songId ->
                    navController.navigate("${MainNavOption.Song.name}/${songId}") {
                        popUpTo(MainNavOption.Song.name) {
                            saveState = true
                            //saveState = true:
                            //It allows saving the states of these fragments during the process of clearing the fragment/states up to the point specified with popUpTo. In this way, the contents of the fragments are not lost.
                        }
                        restoreState = true
                        //It allows restoring the old fragment states after the redirection process. In this way, when the user returns to the navigation history, he can see the old states of the fragments.
                        launchSingleTop = true
                        //During the routing process to the target route, if the target route is already at the top (existing one), it allows using the existing instance instead of creating a new instance. This prevents a page from being opened repeatedly.
                    }
                }
            )
        }






//        composable(
//            route = MainNavOption.SystemSettings.name,
//        ) {
//            val coroutineScope = rememberCoroutineScope()
//            SettingsScreen(
//                drawerState = drawerState,
//                navigateBack = {
//                    coroutineScope.launch {
//                        drawerState.open()
//                    }
//                },
//                globalSettingsViewModel = settingsViewModel
//            )
//        }
    }
}

enum class MainNavOption {
    SongList,
    Song,
    Bookmarks,
    Playlist,
    Settings,
}