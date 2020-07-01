package com.popalay.tracktor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.ui.core.setContent
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.popalay.tracktor.data.TrackingRepository

class MainActivity : AppCompatActivity() {
    private val backPressHandler = BackPressHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TrackingRepository.init(applicationContext)
        setContent {
            App()
        }
    }

    override fun onBackPressed() {
        if (!backPressHandler.handle()) {
            super.onBackPressed()
        }
    }
}