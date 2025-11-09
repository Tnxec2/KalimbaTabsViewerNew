package com.kontranik.kalimbatabsviewer2.ui.appbar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector

data class AppBarAction(
    @DrawableRes val icon: Int? = null,
    val vector: ImageVector? = null,
    @StringRes val description: Int,
    val onClick: () -> Unit
)
