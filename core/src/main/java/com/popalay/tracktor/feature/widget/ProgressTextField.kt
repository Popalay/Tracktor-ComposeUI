package com.popalay.tracktor.feature.widget

import androidx.compose.foundation.Text
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.popalay.tracktor.data.model.ProgressDirection
import com.popalay.tracktor.success

@Composable
fun ProgressTextField(
    progress: Double,
    direction: ProgressDirection,
    modifier: Modifier = Modifier
) {
    val progressPercent = (progress * 100).toInt()
    val (arrow, color) = when {
        progressPercent == 0 -> "" to MaterialTheme.colors.success
        progressPercent > 0 && direction == ProgressDirection.ASCENDING -> "↑" to MaterialTheme.colors.success
        progressPercent < 0 && direction == ProgressDirection.DESCENDING -> "↑" to MaterialTheme.colors.success
        else -> "↓" to MaterialTheme.colors.error
    }

    Text(
        text = "$arrow$progressPercent%",
        color = color,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.caption,
        modifier = modifier
    )
}