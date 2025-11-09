package com.kontranik.kalimbatabsviewer2.helper


import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit
import kotlin.time.Duration


fun fetchData(url: String): String {
    Log.d("FETCH", "Fetching data from: $url")
    val client = OkHttpClient.Builder()
        .connectTimeout(120L, TimeUnit.SECONDS)
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