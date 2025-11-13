package com.kontranik.kalimbatabsviewer2.ui.appdrawer

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kontranik.kalimbatabsviewer2.ui.DrawerParams
import com.kontranik.kalimbatabsviewer2.ui.MainNavOption
import com.kontranik.kalimbatabsviewer2.ui.theme.paddingMedium

import kotlinx.coroutines.launch

@Composable
fun <T : Enum<T>> AppDrawerContent(
    drawerState: DrawerState,
    menuItems: List<AppDrawerItemInfo<T>>,
    defaultPick: T,
    onClick: (T) -> Unit
) {
    //var currentPick by remember { mutableStateOf(defaultPick) }
    val coroutineScope = rememberCoroutineScope()

    ModalDrawerSheet {
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
                LazyColumn(
                    modifier = Modifier.padding(paddingMedium),
                ) {
                    items(menuItems) { item ->
                        AppDrawerItem(item = item) { navOption ->

//                            if (currentPick == navOption) {
//                                coroutineScope.launch {
//                                    drawerState.close()
//                                }
//                                return@AppDrawerItem
//                            }

                            //currentPick = navOption
                            coroutineScope.launch {
                                drawerState.close()
                            }
                            onClick(navOption)
                        }
                    }
                }

        }
    }
}

@Preview
@Composable
private fun AppDrawerContentPreview() {
    Surface {
        AppDrawerContent(
            rememberDrawerState(initialValue = DrawerValue.Open),
            menuItems = DrawerParams.drawerButtons,
            defaultPick = MainNavOption.KTabList
        ) { }
    }
}