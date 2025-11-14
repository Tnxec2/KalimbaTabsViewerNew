package com.kontranik.kalimbatabsviewer.helper


import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request


fun fetchData(url: String): String {
    Log.d("FETCH", "Fetching data from: $url")
    val client = OkHttpClient.Builder()
        .build()

    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).execute().use { response ->
        if (!response.isSuccessful) {
            throw FetchDataException("Unexpected code ${response.code} ${response.message}", response.code)
        }
        return response.body!!.string()
    }
}


data class FetchDataException(
    override val message: String,
    val code: Int,
): Exception(
    message
)