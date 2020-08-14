package com.popalay.tracktor

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.model.TrackableUnit
import kotlin.math.absoluteValue

private val lightThemeColors = lightColors(
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

private val darkThemeColors = darkColors(
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

private val shapes = Shapes(
    medium = RoundedCornerShape(16.dp)
)

val Colors.success get() = Color(0xFF348F50)

val gradients = listOf(
    listOf(Color(0xFF64BFE1), Color(0xFFA091B7), Color(0xFFE0608A)),
    listOf(Color(0xFF64BFE1), Color(0xFF45A190), Color(0xFF348F50)),
    listOf(Color(0xFF64BFE1), Color(0xFF86A7E7), Color(0xFF8360C3)),
    listOf(Color(0xFF64BFE1), Color(0xFF959089), Color(0xFFFF4B1F)),
    listOf(Color(0xFF7F7FD5), Color(0xFF86A8E7), Color(0xFF91EAE4)),
    listOf(Color(0xFFFEAC5E), Color(0xFFC779D0), Color(0xFF4BC0C8)),
    listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121)),
    listOf(Color(0xFFdd3e54), Color(0xFF6be585)),
    listOf(Color(0xFFfc00ff), Color(0xFF00dbde)),
    listOf(Color(0xFF5f2c82), Color(0xFF49a09d)),
    listOf(Color(0xFF24C6DC), Color(0xFF514A9D)),
    listOf(Color(0xFF314755), Color(0xFF26a0da))
)

val TrackableUnit.gradient: List<Color>
    get() {
        val gradientIndex = hashCode().absoluteValue % gradients.size
        return gradients[gradientIndex]
    }

@Composable
fun AppTheme(isDarkTheme: Boolean, content: @Composable () -> Unit) =
    MaterialTheme(
        colors = if (isDarkTheme) darkThemeColors else lightThemeColors,
        shapes = shapes,
        content = content
    )