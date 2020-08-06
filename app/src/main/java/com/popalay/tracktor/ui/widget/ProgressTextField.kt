package com.popalay.tracktor.ui.widget

import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.popalay.tracktor.success

@Composable
fun ProgressTextField(
    progress: Double,
    color: Color = if (progress >= 0) MaterialTheme.colors.success else MaterialTheme.colors.error,
    modifier: Modifier = Modifier
) {
    val progressPercent = (progress * 100).toInt()
    val arrow = if (progressPercent >= 0) "↑" else "↓"
    Text(
        text = "$arrow$progressPercent%",
        color = color,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.caption,
        modifier = modifier
    )
}