package com.popalay.tracktor

import android.app.Application
import com.popalay.tracktor.domain.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class DefaultApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@DefaultApplication)
            androidLogger(Level.ERROR)
        }
    }
}