package com.kontranik.kalimbatabsviewer2.helper

import com.kontranik.kalimbatabsviewer2.R


class TransposeTypes {


    companion object {
        fun getFromTitle(t: String): Pair<String, Int>? {
            return TRANSPOSE_ENTRIES.firstOrNull { it.first == t }
        }

        const val NUMBER = "NUMBER"
        const val C_TUNE = "C_TUNE"
        const val G_TUNE = "G_TUNE"

        val TRANSPOSE_ENTRIES = listOf(
            NUMBER to R.string.tune_number,
            C_TUNE to R.string.tune_c,
            G_TUNE to R.string.tune_g
        )
    }
}