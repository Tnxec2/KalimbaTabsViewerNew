package com.kontranik.kalimbatabsviewer.helper

import java.util.*

class KTabTextHelper {
    companion object {
        fun formatedText(tune: String, text: String, showText: Boolean): String {
            val result = StringBuilder()
            val scanner = Scanner(Transpose.transposeNumber(tune, text))
            while (scanner.hasNextLine()) {
                val line: String = scanner.nextLine()
                if (line.startsWith(">")) {
                    if (showText) result.appendLine(line.subSequence(1, line.length))
                } else {
                    result.appendLine(line)
                }
            }
            scanner.close()
            return result.toString()
        }
    }
}