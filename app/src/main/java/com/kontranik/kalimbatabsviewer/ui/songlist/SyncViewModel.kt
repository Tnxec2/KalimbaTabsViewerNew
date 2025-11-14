package com.kontranik.kalimbatabsviewer.ui.songlist

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kontranik.kalimbatabsviewer.helper.fetchData
import com.kontranik.kalimbatabsviewer.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer.room.repository.KTabsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/*
backend:
get page of ktabs
https://kalimba-tabs-backend.onrender.com

 */

class SyncViewModel(
    private val kTabsRepository: KTabsRepository,
) : ViewModel() {

    val syncState = MutableStateFlow(SyncState())

    @OptIn(ExperimentalCoroutinesApi::class)
    val syncMessage = syncState.flatMapLatest { s ->
        flowOf(
            when (s.state) {
                SyncStateType.PREPARE_SYNC_LIST -> {
                    "Sync is preparing..."
                }

                SyncStateType.SYNCING -> {
                    "sync in progress ${s.count} / ${s.total}"
                }

                SyncStateType.SAVING -> {
                    "saving ${s.count} / ${s.total}"
                }

                SyncStateType.FINISHED -> {
                    s.error?.let { error ->
                        "sync finished with error"
                    } ?: "sync finished ${s.count}"
                }

                else -> null
            }
        )
    }

    fun syncSongs() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                syncState.value = SyncState().copy(state = SyncStateType.PREPARE_SYNC_LIST)
                // Erstellen Sie das richtige Format für den ISO-String: 2025-10-06T12:45:10.230Z
                val dateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
                // Wichtig: Die 'Z' am Ende bedeutet UTC. Wir müssen dem Parser die Zeitzone mitteilen.
                dateFormat.timeZone = TimeZone.getTimeZone("UTC")

                val lastUpdated = kTabsRepository.getLastUpdated()
                val lastUpdatedString =
                    lastUpdated?.updated?.let { date -> dateFormat.format(date) }


                val url =
                    if (lastUpdatedString != null) "${BACKEND_URI_SYNC_COUNT}?lastupdated=${lastUpdatedString}" else BACKEND_URI_SYNC_COUNT
                Log.d("SYNC COUNT URL", "url: $url")
                var result = fetchData(url)
                if (result.contains("count")) {
                    val jsonObject = JSONObject(result)
                    val count = jsonObject.getInt("count")

                    if (count > 0) {
                        var page = 1
                        var fetched = 0
                        val size = 20

                        val ktabs = mutableListOf<KTabRoom>()

                        syncState.value = SyncState().copy(
                            state = SyncStateType.SYNCING,
                            total = count,
                            count = 0
                        )
                        // Erstellen Sie das richtige Format für den ISO-String
                        val dateFormat = SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                            java.util.Locale.getDefault()
                        )
                        // Wichtig: Die 'Z' am Ende bedeutet UTC. Wir müssen dem Parser die Zeitzone mitteilen.
                        dateFormat.timeZone = TimeZone.getTimeZone("UTC")

                        do {
                            result =
                                fetchData(
                                    url = "${BACKEND_URI_SYNC}?page=$page&size=$size"
                                            + (if (lastUpdatedString != null) "&lastupdated=${lastUpdatedString}" else "")
                                )
                            val jsonObject = JSONObject(result)
                            val ktabsArray = jsonObject.getJSONArray("result")

                            if (ktabsArray.length() == 0) break

                            for (i in 0 until ktabsArray.length()) {
                                val ktabJson = ktabsArray.getJSONObject(i)

                                Log.d(TAG, ktabJson.getString("updated"))
                                val updatedDate = dateFormat.parse(ktabJson.getString("updated"))

                                val ktab = KTabRoom(
                                    kTabId = ktabJson.getString("_id"),
                                    difficulty = ktabJson.getInt("difficulty"),
                                    interpreter = ktabJson.getString("interpreter"),
                                    source = ktabJson.getString("source"),
                                    youtube = ktabJson.getString("youtube"),
                                    text = ktabJson.getString("text"),
                                    title = ktabJson.getString("title"),
                                    updated = updatedDate ?: Date(),
                                )
                                ktabs.add(ktab)

                                fetched += 1
                                syncState.value = syncState.value.copy(
                                    count = fetched
                                )
                            }
                            page++
                        } while (true)
                        Log.d(TAG, "fetched: $fetched, inList: ${ktabs.size}")
                        val updated = kTabsRepository.updateAll(ktabs)
                        Log.d(TAG, "updated: ${updated.size}")
                    }

                }

                syncState.value = syncState.value.copy(
                    state = SyncStateType.FINISHED,
                )
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e(TAG, "Error: ${e.message}")
                syncState.value = syncState.value.copy(
                    state = SyncStateType.FINISHED,
                    error = if (e.message == "timeout") "Timeout. Try again later." else e.message
                )
            }
        }

    }

    companion object {
        const val TAG = "SYNC"
        const val BACKEND_URI_SYNC = "https://kalimba-tabs-backend.onrender.com/sync"
        const val BACKEND_URI_SYNC_COUNT = "https://kalimba-tabs-backend.onrender.com/sync/count"
    }
}

enum class SyncStateType {
    PREPARE_SYNC_LIST,
    SYNCING,
    SAVING,
    FINISHED,
    NONE
}

data class SyncState(
    val state: SyncStateType = SyncStateType.NONE,
    val count: Int = 0,
    val total: Int = 0,
    val error: String? = null
) {
    val isInProgress: Boolean = state != SyncStateType.FINISHED && state != SyncStateType.NONE
    val isFinished: Boolean = state == SyncStateType.FINISHED || state == SyncStateType.NONE
}
