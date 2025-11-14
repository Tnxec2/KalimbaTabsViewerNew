package com.kontranik.kalimbatabsviewer

import android.content.Context
import com.kontranik.kalimbatabsviewer.room.KTabRoomDatabase

import com.kontranik.kalimbatabsviewer.room.repository.KTabsRepository
import com.kontranik.kalimbatabsviewer.room.repository.PlaylistKtabRepository
import com.kontranik.kalimbatabsviewer.room.repository.PlaylistRepository
import kotlinx.coroutines.CoroutineScope

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val kTabsRepository: KTabsRepository
    val playlistRepository: PlaylistRepository
    val playlistKtabRepository: PlaylistKtabRepository
}


class AppDataContainer(
    private val context: Context,
    private val applicationScope: CoroutineScope
) : AppContainer {

    override val kTabsRepository: KTabsRepository by lazy {
        KTabsRepository(
            KTabRoomDatabase.getDatabase(context, applicationScope).kTabsDao(),
        )
    }

    override val playlistRepository: PlaylistRepository by lazy {
        PlaylistRepository(
            KTabRoomDatabase.getDatabase(context, applicationScope).playlistDao(),

        )
    }

    override val playlistKtabRepository: PlaylistKtabRepository by lazy {
        PlaylistKtabRepository(
            KTabRoomDatabase.getDatabase(context, applicationScope).playlistKtabDao(),

            )
    }

}
