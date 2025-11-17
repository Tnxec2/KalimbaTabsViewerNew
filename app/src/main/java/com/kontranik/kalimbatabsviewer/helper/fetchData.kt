package com.kontranik.kalimbatabsviewer.helper


import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit


fun fetchData(url: String, first: Boolean = false): String {
    Log.d("FETCH", "Fetching data from: $url")
    val client = OkHttpClient.Builder()
        .readTimeout( if (first) 120 else 10, TimeUnit.SECONDS)
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