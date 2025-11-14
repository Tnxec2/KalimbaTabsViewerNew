package com.kontranik.kalimbatabsviewer.ui.appdrawer


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.kontranik.kalimbatabsviewer.ui.theme.paddingMedium


@Composable
fun <T> AppDrawerItem(item: AppDrawerItemInfo<T>, onClick: (options: T) -> Unit) =
    Surface(
        color = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier
            .width(300.dp)
            .padding(bottom = 8.dp),
        onClick = { onClick(item.drawerOption) },
        shape = RoundedCornerShape(25),
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
        ) {
            if (item.drawableId != null)
                Icon(
                    painter = painterResource(id = item.drawableId),
                    contentDescription = stringResource(id = item.descriptionId),
                    modifier = Modifier
                        .size(24.dp)
                )
            if (item.imageVector != null)
                Icon(
                    imageVector = item.imageVector,
                    contentDescription = stringResource(id = item.descriptionId),
                    modifier = Modifier
                        .size(24.dp)
                )
            Spacer(modifier = Modifier.width(paddingMedium))
            Text(
                text = stringResource(id = item.title),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
