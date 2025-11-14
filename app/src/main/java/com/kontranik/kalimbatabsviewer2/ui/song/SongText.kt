package com.kontranik.kalimbatabsviewer2.ui.song

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kontranik.kalimbatabsviewer2.helper.Transpose
import com.kontranik.kalimbatabsviewer2.helper.Transpose.Companion.PREFIX_TEXT_LINE
import com.kontranik.kalimbatabsviewer2.helper.TransposeTypes
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer2.ui.settings.Settings


fun formatedText(
    text: String,
    tune: String,
    hideText: Boolean = false
): AnnotatedString {


    return AnnotatedString.fromHtml(
        "<tt>" +
                Transpose.transposeNumber(tune, text)
                    .lines()
                    .filter { line -> (!hideText || !line.startsWith(PREFIX_TEXT_LINE)) }
                    .map { line ->
                        if (line.startsWith(PREFIX_TEXT_LINE))
                                line.removePrefix(PREFIX_TEXT_LINE)
                        else
                            line
                    }
                    .map{ l -> l.replace(" ", "&nbsp;")} // replace spaces with &nbsp;
                    .joinToString("\n") { l -> "$l<br/>" }
                + "</tt>"
    )
}

@Composable
fun KTabRoomText(
    uiState: State<KTabRoom>,
    settings: State<Settings>,
) {
    val formattedSongText by remember(
        uiState.value.text,
        settings.value.hideText
    ) {
        mutableStateOf(
            formatedText(
                text = uiState.value.text,
                tune =  TransposeTypes.NUMBER,
                settings.value.hideText
            )
        )
    }

    Text(
        text = formattedSongText,
        fontSize = settings.value.fontSize.sp,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Start,
        modifier =
            Modifier
                .padding(horizontal = 8.dp)
                .fillMaxWidth()
                .then(
                    if (settings.value.lineBreak.not())
                        Modifier.horizontalScroll(rememberScrollState())
                    else
                        Modifier
                )
    )
}

@Composable
@Preview
fun TextPreview() {
    val state = remember {
        mutableStateOf(
            mocupKtabRoomState.value.copy(
                bookmarked = false,
                difficulty = 5
            )
        )
    }
    Surface(Modifier.height(500.dp)) {
        KTabRoomText(
            uiState = state,
            settings = remember { mutableStateOf(Settings().copy(hideText = false)) }
        )
    }
}