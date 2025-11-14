package com.kontranik.kalimbatabsviewer.helper

import java.util.Scanner

class Transpose {
    companion object {
        const val PREFIX_TEXT_LINE = ">"
        private val tine_numbers : Array<String> = arrayOf( "1", "2", "3", "4", "6", "5", "7")
        private val c_tune : Array<String>       = arrayOf( "C", "D", "E", "F", "A", "G", "B")
        private val g_tune  : Array<String>      = arrayOf( "G", "A", "B", "C", "E", "D", "F#")

        private fun translate(inp: Array<String>, out: Array<String>, text: String): String {
            val result = StringBuilder()

            val scanner = Scanner(text)
            while (scanner.hasNextLine()) {
                val line: String = scanner.nextLine()
                if ( !line.startsWith(PREFIX_TEXT_LINE)) {
                    for (c in line) {
                        val index = inp.indexOf(c.toString().uppercase())
                        if (index >= 0) {
                            result.append(out[index])
                        } else {
                            result.append(c)
                        }
                    }
                    result.appendLine()
                } else {
                    result.appendLine(line)
                }
            }
            scanner.close()

            return result.toString()
        }

        fun number_to_c(text: String) = translate(tine_numbers, c_tune, text)
        fun number_to_g(text: String) = translate(tine_numbers, g_tune, text)

        fun c_to_number(text: String) = translate(c_tune, tine_numbers, text)
        fun c_to_g(text: String) = translate(c_tune, g_tune, text)

        fun g_to_number(text: String) = translate(g_tune, tine_numbers, text)
        fun g_to_c(text: String) = translate(g_tune, c_tune, text)

        fun transposeNumber(tune: String, text: String): String {
            return when (tune) {
                TransposeTypes.C_TUNE -> number_to_c(text)
                TransposeTypes.G_TUNE -> number_to_g(text)
                else -> text
            }
        }
    }
}

