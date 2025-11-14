package com.kontranik.kalimbatabsviewer.ui.song

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kontranik.kalimbatabsviewer.helper.Transpose
import com.kontranik.kalimbatabsviewer.helper.Transpose.Companion.PREFIX_TEXT_LINE
import com.kontranik.kalimbatabsviewer.helper.TransposeTypes
import com.kontranik.kalimbatabsviewer.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer.ui.settings.Settings
import kotlin.text.append

private val TextStyle = SpanStyle(fontFamily = FontFamily.Monospace)

fun formatedText(
    text: String,
    tune: String,
    noteColor: Color,
    hideText: Boolean = false
): AnnotatedString {
    val noteStyle = SpanStyle(color = noteColor, fontFamily = FontFamily.Monospace)

// Verwenden Sie den modernen Builder für bessere Kontrolle und Performance
    return buildAnnotatedString {
        // Setzen Sie den Stil für den gesamten Textblock
        withStyle(style = SpanStyle(fontFamily = FontFamily.Monospace)) {
            val processedText = Transpose.transposeNumber(tune, text)

            processedText.lines().forEach { line ->
                if (hideText && line.startsWith(PREFIX_TEXT_LINE)) {
                    // Zeile überspringen, nichts anfügen
                } else if (line.startsWith(PREFIX_TEXT_LINE)) {
                    // Textzeilen bekommen einen normalen Stil
                    withStyle(style = TextStyle) {
                        append(line.removePrefix(PREFIX_TEXT_LINE))
                        append("\n")
                    }
                } else {
                    // Notenzeilen (oder der Rest)
                    // Sie können hier sogar die Noten farblich hervorheben, falls gewünscht
                    val styleToApply = if (!hideText) noteStyle else TextStyle
                    withStyle(style = styleToApply) {
                        append(line)
                        append("\n")
                    }
                }
            }
        }
    }
}

@Composable
fun KTabRoomText(
    uiState: State<KTabRoom>,
    settings: State<Settings>,
) {
    val dynamicNoteColor = MaterialTheme.colorScheme.primary

    val formattedSongText by remember(
        uiState.value,
        settings.value
    ) {
        mutableStateOf(
            formatedText(
                text = uiState.value.text,
                tune =  TransposeTypes.NUMBER,
                noteColor = dynamicNoteColor,
                hideText = settings.value.hideText
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