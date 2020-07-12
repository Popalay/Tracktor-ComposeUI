package com.popalay.tracktor

import androidx.compose.Composable
import androidx.ui.layout.Column
import androidx.ui.tooling.preview.Preview
import com.popalay.tracktor.ui.list.CreateTrackedValue
import com.popalay.tracktor.ui.widget.ChartAnimationState
import com.popalay.tracktor.ui.widget.ChartWidget
import kotlin.random.Random

@Preview("Light CreateTrackedValue")
@Composable
fun previewCreateTrackedValueLight() {
    ThemedPreview {
        CreateTrackedValue()
    }
}

@Preview("Dark CreateTrackedValue")
@Composable
fun previewCreateTrackedValueDark() {
    ThemedPreview(isDarkTheme = true) {
        CreateTrackedValue()
    }
}

@Preview("ChartWidget")
@Composable
fun previewChartWidget() {
    AppTheme(isDarkTheme = true) {
        Column {
            gradients.forEach {
                val data = listOf(Random.nextDouble(), Random.nextDouble(), Random.nextDouble())
                ChartWidget(
                    data,
                    it.value,
                    touchable = true,
                    currentState = ChartAnimationState.STATE_END
                )
            }
        }
    }
}