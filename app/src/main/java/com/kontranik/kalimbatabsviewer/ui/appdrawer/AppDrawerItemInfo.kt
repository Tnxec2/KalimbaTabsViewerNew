package com.kontranik.kalimbatabsviewer.ui.appdrawer


import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class AppDrawerItemInfo<T>(
    val drawerOption: T,
    @StringRes val title: Int,
    @DrawableRes val drawableId: Int? = null,
    val imageVector: ImageVector? = null,
    @StringRes val descriptionId: Int
)