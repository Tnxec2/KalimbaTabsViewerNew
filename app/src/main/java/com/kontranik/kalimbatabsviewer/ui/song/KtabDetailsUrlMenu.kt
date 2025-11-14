package com.kontranik.kalimbatabsviewer.ui.song

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.kontranik.kalimbatabsviewer.R


@Composable
fun UrlMenu(
    source: String?,
    youtube: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // val patternYoutube = "^(http(s)?://)?((w){3}.)?youtu(be|.be)?(\\.com)?/.+".toRegex()

    val youtubeIcon = ImageVector.vectorResource(R.drawable.youtube_vector)

    if ((source != null && source.isNotEmpty()) || (youtube != null && youtube.isNotEmpty()))
        DropdownMenu(
            expanded = true,
            onDismissRequest = { onDismiss() },
        ) {
            if (source != null && source.isNotEmpty())
                DropdownMenuItem(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, source.toUri())
                        startActivity(context, intent, null)
                    },
                    text = {
                          Text(
                              text = source,
                              style = MaterialTheme.typography.labelSmall,
                              fontStyle = FontStyle.Italic,
                          )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = stringResource(
                                R.string.web_link
                            ),
                        )
                    }
                )
            if (youtube != null && youtube.isNotEmpty())
                DropdownMenuItem(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, youtube.toUri())
                        startActivity(context, intent, null)
                    },
                    text = {
                        Text(
                            text = youtube,
                            style = MaterialTheme.typography.labelSmall,
                            fontStyle = FontStyle.Italic,
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = youtubeIcon,
                            contentDescription = stringResource(R.string.youtube_link),
                        )
                    }
                )
        }


}