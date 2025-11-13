package com.kontranik.kalimbatabsviewer2.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kontranik.kalimbatabsviewer2.room.dao.KTabsDao
import com.kontranik.kalimbatabsviewer2.room.dao.PlaylistDao
import com.kontranik.kalimbatabsviewer2.room.dao.PlaylistKtabDao
import com.kontranik.kalimbatabsviewer2.room.model.KTabRoom
import com.kontranik.kalimbatabsviewer2.room.model.Playlist
import com.kontranik.kalimbatabsviewer2.room.model.PlaylistKtabCrossRef
import com.kontranik.kalimbatabsviewer2.room.model.PlaylistWithKtabs
import com.kontranik.kalimbatabsviewer2.room.utils.Converters
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
            scope: CoroutineScope): KTabRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KTabRoomDatabase::class.java,
                    "ktabs_database"
                ).fallbackToDestructiveMigration()
                    .addCallback(KTabDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class KTabDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.kTabsDao(), database.playlistDao(), database.playlistKtabDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */
        suspend fun populateDatabase(kTabsDao: KTabsDao, playlistDao: PlaylistDao, playlistKtabDao: PlaylistKtabDao) {
            // on start routine,
        }
    }
}