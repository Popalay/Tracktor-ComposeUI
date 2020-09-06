package com.popalay.tracktor.ui.widget

import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.popalay.tracktor.ui.widget.SimpleChartAnimationState.STATE_END
import com.popalay.tracktor.ui.widget.SimpleChartAnimationState.STATE_START
import com.popalay.tracktor.utils.getSubPath

private enum class SimpleChartAnimationState {
    STATE_START, STATE_END
}

private val amplifierKey = FloatPropKey()

private val tweenDefinition = transitionDefinition<SimpleChartAnimationState> {
    state(STATE_START) {
        this[amplifierKey] = 0F
    }
    state(STATE_END) {
        this[amplifierKey] = 1F
    }
    transition(fromState = STATE_START, toState = STATE_END) {
        amplifierKey using tween(
            durationMillis = 1500,
            delayMillis = 500
        )
    }
}

@Composable
fun SimpleChartWidget(
    data: List<Double>,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    lineWidth: Dp = ChartLineWidth,
    animate: Boolean = true
) {
    val transitionState = transition(
        definition = tweenDefinition,
        toState = STATE_END,
        initState = if (animate) STATE_START else STATE_END
    )
    Canvas(modifier) {
        val points = createPoints(data, size, lineWidth)
        val (conPoints1, conPoints2) = createConnectionPoints(points)

        if (points.isEmpty() || conPoints1.isEmpty() || conPoints2.isEmpty()) return@Canvas

        val borderPath = createBorderPath(points, conPoints1, conPoints2)

        val partPath = borderPath.getSubPath(0F, transitionState[amplifierKey])
        drawPath(partPath, createBrush(gradient, size), 1.0F, Stroke(lineWidth.toPx(), cap = StrokeCap.Round))
    }
}