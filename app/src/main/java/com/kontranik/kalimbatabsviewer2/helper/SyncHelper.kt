package com.kontranik.kalimbatabsviewer2.helper

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer2.room.viewmodel.KtabRoomViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

import java.util.*

/*
backend:
get page of ktabs
https://kalimba-tabs-backend.onrender.com

 */

const val BACKEND_URI_SYNC = "https://kalimba-tabs-backend.onrender.com/sync"
const val BACKEND_URI_SYNC_COUNT = "https://kalimba-tabs-backend.onrender.com/sync/count"

class SyncHelper(private val context: Context, private val viewModel: KtabRoomViewModel) {

    fun sync(syncEnabled: Boolean, lastUpdatedModel: KTabRoom?){
        Log.d(TAG, "sync enabled: $syncEnabled")
        if (syncEnabled) {
            if (lastUpdatedModel != null) {
                startSync(lastUpdatedModel.updated)
            }
        }
    }

    private fun startSync(lastUpdated: Date? = null) {
        // send http request to BACKEND_URI_SYNC_COUNT

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (lastUpdated != null) {

                    var result = fetchData("$BACKEND_URI_SYNC_COUNT?lastUpdated=$lastUpdated")
                    if (result.contains("count")) {
                        val jsonObject = JSONObject(result)
                        val count = jsonObject.getInt("count")

                        if (count > 0) {
                            var page = 1
                            val size = 20

                            do {
                                result = fetchData("$BACKEND_URI_SYNC?page=$page&size=$size&lastUpdated=$lastUpdated")
                                val jsonObject = JSONObject(result)
                                val ktabsArray = jsonObject.getJSONArray("result")

                                if (ktabsArray.length() == 0) break

                                for (i in 0 until ktabsArray.length()) {
                                    val ktabJson = ktabsArray.getJSONObject(i)

                                    val ktab = KTabRoom(
                                        kTabId = ktabJson.getString("_id"),
                                        difficulty = ktabJson.getInt("difficulty"),
                                        interpreter = ktabJson.getString("interpreter"),
                                        source = ktabJson.getString("source"),
                                        youtube = ktabJson.getString("youtube"),
                                        text = ktabJson.getString("text"),
                                        title = ktabJson.getString("title"),
                                        updated = Date(ktabJson.getString("updated")),
                                    )
                                    viewModel.updateOrInsert(ktab)
                                }
                                page++
                            } while (true)
                            Toast.makeText(context, "Sync ended successful", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
                Toast.makeText(context, "Sync failed. Read next failed", Toast.LENGTH_LONG).show()
            }
        }

    }


    companion object {
        const val TAG = "SYNC"

    }
}