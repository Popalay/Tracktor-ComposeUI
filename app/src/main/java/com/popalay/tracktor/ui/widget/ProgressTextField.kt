package com.popalay.tracktor.ui.widget

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import androidx.ui.text.font.FontWeight
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