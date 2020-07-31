package com.popalay.tracktor

import androidx.compose.Composable
import androidx.ui.graphics.Color
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.darkColorPalette
import androidx.ui.material.lightColorPalette
import com.popalay.tracktor.model.TrackableUnit
import kotlin.math.absoluteValue

val lightThemeColors = lightColorPalette(
    primary = Color(0xFF1F1F1F),
    primaryVariant = Color(0xFF404040),
    secondary = Color.Black,
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
    secondary = Color.White,
    background = Color(0xFF121212),
    surface = Color.Black,
    error = Color(0xFFCF6679),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

val ColorPalette.success get() = Color(0xFF348F50)

val gradientColors = listOf(
    Color(0xFFE0608A),
    Color(0xFF348F50),
    Color(0xFF8360C3),
    Color(0xFFF9BB2F),
    Color(0xFF0254D5),
    Color(0xFFEFFCAE),
    Color(0xFFC679D0),
    Color(0xFFF5A470),
    Color(0xFFD64B5E),
    Color(0xFF4B4576),
    Color(0xFF88FAE8),
    Color(0xFFFF4B1F)
)

val TrackableUnit.gradient: List<Color>
    get() {
        val colorIndex = hashCode().absoluteValue % gradientColors.size
        val initialColor = Color(0xFF64BFE1)
        val mediumColor = gradientColors.reversed()[colorIndex]
        val lastColor = gradientColors[colorIndex]
        return listOf(initialColor, mediumColor, lastColor)
    }

@Composable
fun AppTheme(isDarkTheme: Boolean, content: @Composable () -> Unit) =
    MaterialTheme(
        colors = if (isDarkTheme) darkThemeColors else lightThemeColors,
        content = content
    )