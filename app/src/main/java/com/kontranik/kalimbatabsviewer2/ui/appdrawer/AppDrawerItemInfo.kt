package com.kontranik.kalimbatabsviewer2.ui.appdrawer


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

data class AppDrawerItemInfo<T>(
    val drawerOption: T,
    @StringRes val title: Int,
    @DrawableRes val drawableId: Int? = null,
    val imageVector: ImageVector? = null,
    @StringRes val descriptionId: Int
)