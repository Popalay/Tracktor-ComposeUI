package com.popalay.tracktor

import androidx.compose.Composable
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import com.popalay.tracktor.model.TrackableUnit

val lightThemeColors = lightColorPalette(
    primary = Color(0xFF1F1F1F),
    primaryVariant = Color(0xFF404040),
    secondary = Color(0xFF000000),
    secondaryVariant = Color(0xFF0AC9F0),
    background = Color(0xFFFBFBFB),
    surface = Color.White,
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

val darkThemeColors = darkColorPalette(
    primary = Color(0xFF1F1F1F),
    primaryVariant = Color(0xFF404040),
    secondary = Color(0xFFC2C2C2),
    background = Color(0xFF121212),
    surface = Color.Black,
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

val gradients = mapOf(
    TrackableUnit.Kilograms to listOf(Color(0xFF64BFE1), Color(0xFFA091B7), Color(0xFFE0608A)),
    TrackableUnit.Quantity to listOf(Color(0xFF64BFE1), Color(0xFF45A190), Color(0xFF348F50)),
    TrackableUnit.Minutes to listOf(Color(0xFF64BFE1), Color(0xFF86A7E7), Color(0xFF8360C3)),
    TrackableUnit.Word to listOf(Color(0xFF64BFE1), Color(0xFF959089), Color(0xFFFF4B1F)),
    TrackableUnit.None to listOf(Color.Black, Color.Black)
)

@Composable
fun AppTheme(isDarkTheme: Boolean, content: @Composable() () -> Unit) =
    MaterialTheme(
        colors = if (isDarkTheme) darkThemeColors else lightThemeColors,
        content = content
    )