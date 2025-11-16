package com.kontranik.kalimbatabsviewer.ui.song

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kontranik.kalimbatabsviewer.helper.Transpose
import com.kontranik.kalimbatabsviewer.helper.Transpose.Companion.PREFIX_TEXT_LINE
import com.kontranik.kalimbatabsviewer.helper.TransposeTypes
import com.kontranik.kalimbatabsviewer.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer.ui.settings.Settings
import com.kontranik.kalimbatabsviewer.ui.theme.paddingSmall

data class TabLinePair(val noteLine: String, val textLine: String?)
data class TabLineChunk(val noteChunk: String, val textChunk: String?)
private val SimpleTextStyle = SpanStyle(fontFamily = FontFamily.Monospace)

/*
    Liedtext in paare aufteilen: Notenzeile + Textzeile
 */
fun parseTextToLinePairs(text: String): List<TabLinePair> {
    val lines = text.lines()
    val pairs = mutableListOf<TabLinePair>()
    var i = 0
    while (i < lines.size) {
        val currentLine = lines[i]
        if (currentLine.startsWith(PREFIX_TEXT_LINE)) {
            // Eine alleinstehende Textzeile (z.B. ein Intro/Outro)
            pairs.add(TabLinePair("", currentLine))
            i++
        } else {
            // Eine Notenzeile
            val noteLine = currentLine
            val textLine = if (i + 1 < lines.size && lines[i + 1].startsWith(PREFIX_TEXT_LINE)) {
                lines[i + 1]
            } else {
                null // Keine zugehörige Textzeile gefunden
            }
            pairs.add(TabLinePair(noteLine, textLine))
            i += if (textLine != null) 2 else 1
        }
    }
    return pairs
}

fun formatSingleLine(line: String, style: SpanStyle): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style) {
            append(line)
        }
    }
}

//fun formatedText(
//    text: String,
//    tune: String,
//    noteColor: Color,
//    hideText: Boolean = false
//): AnnotatedString {
//    val noteStyle = SpanStyle(color = noteColor, fontFamily = FontFamily.Monospace)
//
//// Verwenden Sie den modernen Builder für bessere Kontrolle und Performance
//    return buildAnnotatedString {
//        // Setzen Sie den Stil für den gesamten Textblock
//        withStyle(style = SpanStyle(fontFamily = FontFamily.Monospace)) {
//            val processedText = Transpose.transposeNumber(tune, text)
//
//            processedText.lines().forEach { line ->
//                if (hideText && line.startsWith(PREFIX_TEXT_LINE)) {
//                    // Zeile überspringen, nichts anfügen
//                } else if (line.startsWith(PREFIX_TEXT_LINE)) {
//                    // Textzeilen bekommen einen normalen Stil
//                    withStyle(style = TextStyle) {
//                        append(line.removePrefix(PREFIX_TEXT_LINE))
//                        append("\n")
//                    }
//                } else {
//                    // Notenzeilen (oder der Rest)
//                    // Sie können hier sogar die Noten farblich hervorheben, falls gewünscht
//                    val styleToApply = if (!hideText) noteStyle else TextStyle
//                    withStyle(style = styleToApply) {
//                        append(line)
//                        append("\n")
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun KTabRoomText(
    uiState: State<KTabRoom>,
    settings: State<Settings>,
    modifier: Modifier = Modifier
) {
    val dynamicNoteColor = MaterialTheme.colorScheme.primary
    val noteStyle = SpanStyle(color = dynamicNoteColor, fontSize = settings.value.fontSize.sp, fontFamily = FontFamily.Monospace)
    val textStyle = SpanStyle(fontSize = settings.value.fontSize.sp, fontFamily = FontFamily.Monospace)

    // 2. Den Rohtext nur einmal parsen und die Paare im `remember` speichern
    val linePairs = remember(uiState.value.text) {
        val processedText = Transpose.transposeNumber(TransposeTypes.NUMBER, uiState.value.text)
        parseTextToLinePairs(processedText)
    }

    val textMeasurer = rememberTextMeasurer()

    val scrollState = rememberScrollState()

    val modifier = if (!settings.value.lineBreak) modifier.horizontalScroll(scrollState) else modifier

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(paddingSmall)
    ) {
        items(linePairs) { pair ->
            TabLinePairItem(
                pair = pair,
                settings = settings.value,
                noteStyle = noteStyle,
                textStyle = textStyle,
                textMeasurer = textMeasurer
            )
        }
    }
}

/**
 * Hilfsfunktion zum Rendern einer einzelnen, einfachen Zeile, um Code-Duplikation zu vermeiden.
 */
@Composable
private fun SimpleLineRenderer(text: String, settings: Settings, spanStyle: SpanStyle) {
    if (text.isNotEmpty()) {
        Text(
            text = text,
            color = spanStyle.color,
            fontSize = settings.fontSize.sp,
            softWrap = settings.lineBreak,
            fontFamily = spanStyle.fontFamily,
        )
    }
}

@Composable
fun TabLinePairItem(
    pair: TabLinePair,
    settings: Settings,
    textStyle: SpanStyle,
    noteStyle: SpanStyle,
    textMeasurer: TextMeasurer
) {

    if (!settings.lineBreak) {
        Column() {
            SimpleLineRenderer(pair.noteLine, settings, noteStyle)
            if (pair.textLine != null && !settings.hideText) {
                SimpleLineRenderer(pair.textLine.removePrefix(PREFIX_TEXT_LINE), settings, textStyle)
            }
        }
        return // Frühzeitiger Abbruch, wenn kein Umbruch nötig
    }


    // BoxWithConstraints liefert uns die maximale Breite
    BoxWithConstraints {
        val chunks = remember(pair, settings, maxWidth) {
            splitLineToFit(
                noteLine = pair.noteLine,
                textLine = pair.textLine?.removePrefix(PREFIX_TEXT_LINE),
                textMeasurer = textMeasurer,
                textStyle = textStyle.toTextStyle(),
                noteStyle = noteStyle.toTextStyle(),
                maxWidth = (constraints.maxWidth - paddingSmall.value*2).toInt()
            )
        }

        Column( modifier = Modifier.padding(bottom = paddingSmall)) {
            chunks.forEach { chunk ->
                SimpleLineRenderer(chunk.noteChunk, settings, noteStyle)
                if (chunk.textChunk != null && !settings.hideText) {
                    SimpleLineRenderer(chunk.textChunk, settings, textStyle)
                }
            }
        }
    }
}

private fun SpanStyle.toTextStyle(): TextStyle {
    return TextStyle(
        color = this.color,
        fontSize = this.fontSize,
        fontFamily = this.fontFamily
    )
}

/**
 * Algorithmus zum Aufteilen der Zeilen, der die Breite beider Zeilen berücksichtigt.
 */
fun splitLineToFit(
    noteLine: String,
    textLine: String?,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    noteStyle: TextStyle,
    maxWidth: Int
): List<TabLineChunk> {
    val chunks = mutableListOf<TabLineChunk>()

    // --- Vorab-Prüfung: Passt die komplette Zeile bereits? ---
    val initialNoteWidth = textMeasurer.measure(noteLine, noteStyle).size.width
    val initialTextWidth = textLine?.let { textMeasurer.measure(it, textStyle).size.width } ?: 0

    // Wenn beide Zeilen von Anfang an passen, ist die Arbeit getan.
    if (initialNoteWidth <= maxWidth && initialTextWidth <= maxWidth) {
        chunks.add(TabLineChunk(noteLine, textLine))
        return chunks
    }

    // --- Splitting-Logik ---
    var currentNoteLine = noteLine
    var currentTextLine = textLine ?: ""

    // Solange eine der beiden Zeilen zu lang ist, weiter aufteilen.
    while (
        (currentNoteLine.isNotEmpty() && textMeasurer.measure(currentNoteLine, noteStyle).size.width >= maxWidth) ||
        (currentTextLine.isNotEmpty() && textMeasurer.measure(currentTextLine, textStyle).size.width >= maxWidth)
    ) {
        // Finde den besten Punkt zum Splitten (rückwärts von der max. Breite)
        var splitIndex = -1

        // Wir iterieren rückwärts durch die LÄNGERE der beiden Zeilen, um sicherzugehen, dass wir alles abdecken.
        val searchLength = maxOf(currentNoteLine.length, currentTextLine.length)

        for (i in searchLength downTo 0) {
            val noteSub = if (i <= currentNoteLine.length) currentNoteLine.take(i) else currentNoteLine
            val textSub = if (i <= currentTextLine.length) currentTextLine.take(i) else currentTextLine

            val noteSubWidth = textMeasurer.measure(noteSub, noteStyle).size.width
            val textSubWidth = textMeasurer.measure(textSub, textStyle).size.width

            // Der Substring passt, wenn BEIDE Teile kürzer als die maximale Breite sind.
            if (noteSubWidth < maxWidth && textSubWidth < maxWidth) {
                // Finde das letzte Leerzeichen in beiden Substrings
                val noteSplitPos = noteSub.lastIndexOf(' ')
                val textSplitPos = textSub.lastIndexOf(' ')

                // Wähle den kleinsten der beiden Umbruchpunkte (der nicht -1 ist),
                // um sicherzustellen, dass wir nicht zu viel abschneiden.
                // Dies sorgt für eine bessere Synchronisation.
                var potentialSplitIndex = -1
                if (noteSplitPos != -1 && textSplitPos != -1) {
                    potentialSplitIndex = minOf(noteSplitPos, textSplitPos)
                } else if (noteSplitPos != -1) {
                    potentialSplitIndex = noteSplitPos
                } else if (textSplitPos != -1) {
                    potentialSplitIndex = textSplitPos
                }

                splitIndex = if (potentialSplitIndex != -1 && potentialSplitIndex > 0) {
                    // Wir haben ein Leerzeichen gefunden, also brechen wir dort um.
                    potentialSplitIndex
                } else {
                    // Kein Leerzeichen gefunden (oder nur am Anfang).
                    // Als Fallback brechen wir an der letzten Position um, die noch passt.
                    i
                }
                break // Wir haben den besten Umbruchpunkt für diese Iteration gefunden.

            }
        }

        Log.d("TabLinePairItem", "maxwidth: $maxWidth splitIndex: $splitIndex")

        if (splitIndex <= 0) { // Failsafe, um eine Endlosschleife zu verhindern.
            chunks.add(TabLineChunk(currentNoteLine, currentTextLine))
            return chunks
        }

        // Erstelle den Chunk mit dem gefundenen Index.
        val noteChunk = currentNoteLine.take(splitIndex)
        val textChunk = if (splitIndex <= currentTextLine.length) currentTextLine.take(splitIndex) else currentTextLine

        Log.d("TabLinePairItem", "noteChunkWidth: ${textMeasurer.measure(noteChunk, noteStyle).size.width}," +
                " textChunk: ${textMeasurer.measure(textChunk, textStyle).size.width}  ")

        chunks.add(TabLineChunk(noteChunk, textChunk))

        // Bereite die verbleibenden Zeilenteile für die nächste Iteration vor.
        currentNoteLine = if (splitIndex < currentNoteLine.length) currentNoteLine.substring(splitIndex) else ""
        currentTextLine =
            if (splitIndex < currentTextLine.length) currentTextLine.substring(splitIndex) else ""

        // bereinige überflüssige Leerzeichen am Anfang der Zeilen.
        while ( currentNoteLine.startsWith(" ") && currentTextLine.startsWith(" ")) {
            currentNoteLine = currentNoteLine.substring(1)
            currentTextLine = currentTextLine.substring(1)
        }

        // Sicherheitsabbruch, falls die Zeilen nicht kürzer werden.
        if (noteChunk.isEmpty() && textChunk.isEmpty()) {
            break
        }
    }

    // Den verbleibenden Rest als letzten Chunk hinzufügen.
    if (currentNoteLine.isNotEmpty() || currentTextLine.isNotEmpty()) {
        chunks.add(TabLineChunk(currentNoteLine, currentTextLine))
    }

    return chunks
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
    Surface(Modifier
        .width(200.dp)
        .height(300.dp)) {
        KTabRoomText(
            uiState = state,
            settings = remember { mutableStateOf(Settings().copy(hideText = false)) },
            modifier = Modifier.padding(paddingSmall)

        )
    }
}

@Composable
@Preview
fun TextPreviewNoText() {
    val state = remember {
        mutableStateOf(
            mocupKtabRoomState.value.copy(
                bookmarked = false,
                difficulty = 5
            )
        )
    }
    Surface(Modifier
        .width(200.dp)
        .height(300.dp)) {
        KTabRoomText(
            uiState = state,
            settings = remember { mutableStateOf(Settings().copy(hideText = true)) },
            modifier = Modifier.padding(paddingSmall)
        )
    }
}


@Composable
@Preview
fun TextPreviewNoLineBreak() {
    val state = remember {
        mutableStateOf(
            mocupKtabRoomState.value.copy(
                bookmarked = false,
                difficulty = 5
            )
        )
    }
    Surface(Modifier
        .width(200.dp)
        .height(300.dp)) {
        KTabRoomText(
            uiState = state,
            settings = remember { mutableStateOf(Settings().copy(hideText = false, lineBreak = false)) },
            modifier = Modifier.padding(paddingSmall)
        )
    }
}