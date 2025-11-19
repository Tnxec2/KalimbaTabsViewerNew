package com.kontranik.kalimbatabsviewer.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kontranik.kalimbatabsviewer.room.dao.KTabsDao
import com.kontranik.kalimbatabsviewer.room.dao.PlaylistDao
import com.kontranik.kalimbatabsviewer.room.dao.PlaylistKtabDao
import com.kontranik.kalimbatabsviewer.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer.room.model.Playlist
import com.kontranik.kalimbatabsviewer.room.model.PlaylistKtabCrossRef
import com.kontranik.kalimbatabsviewer.room.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Database(entities = [
    KTabRoom::class,
    Playlist::class,
    PlaylistKtabCrossRef::class
 ], version = 3,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class KTabRoomDatabase : RoomDatabase() {

    abstract fun kTabsDao(): KTabsDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistKtabDao(): PlaylistKtabDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: KTabRoomDatabase? = null

        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(
            NUMBER_OF_THREADS
        )

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): KTabRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KTabRoomDatabase::class.java,
                    "ktabs_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}