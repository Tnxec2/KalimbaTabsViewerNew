package com.kontranik.kalimbatabsviewer2

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class KTabApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this, applicationScope)
    }
}