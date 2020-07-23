package com.popalay.tracktor

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class DefaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@DefaultApplication)
            androidLogger(Level.ERROR)
            modules(modules)
        }
    }
}