package com.popalay.tracktor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Providers
import androidx.ui.core.setContent
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler

class MainActivity : AppCompatActivity() {
    private val backPressHandler = BackPressHandler()

    private val lightThemeColors = lightColorPalette(
        primary = Color(0xFF855446),
        primaryVariant = Color(0xFF9C684B),
        secondary = Color(0xFF000000),
        secondaryVariant = Color(0xFF0AC9F0),
        background = Color.White,
        surface = Color.White,
        error = Color(0xFFB00020),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.Black,
        onSurface = Color.Black,
        onError = Color.White
    )

    private val darkThemeColors = darkColorPalette(
        primary = Color(0xFF1F1F1F),
        primaryVariant = Color(0xFF3E2723),
        secondary = Color(0xFF03DAC5),
        background = Color(0xFF121212),
        surface = Color.Black,
        error = Color(0xFFCF6679),
        onPrimary = Color.White,
        onSecondary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White,
        onError = Color.Black
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colors = lightThemeColors) {
                Providers(AmbientBackPressHandler provides backPressHandler) {
                    HomeScreen(defaultRouting = Routing.ListDestination)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!backPressHandler.handle()) {
            super.onBackPressed()
        }
    }
}