package com.popalay.tracktor.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.util.toRadians
import kotlin.math.cos
import kotlin.math.sin

fun createPolygon(size: Size, sideCount: Int, radius: Float = size.minDimension / 2): Path {
    val centerX: Float = size.width / 2
    val centerY: Float = size.height / 2
    return Path().apply {
        for (cornerNumber in 0 until sideCount) {
            val angleToCorner: Double = cornerNumber * (360.0 / sideCount)
            val cornerX = (centerX + radius * cos(angleToCorner.toRadians())).toFloat()
            val cornerY = (centerY + radius * sin(angleToCorner.toRadians())).toFloat()
            if (cornerNumber == 0) {
                moveTo(cornerX, cornerY)
            } else {
                lineTo(cornerX, cornerY)
            }
        }
        close()
    }
}