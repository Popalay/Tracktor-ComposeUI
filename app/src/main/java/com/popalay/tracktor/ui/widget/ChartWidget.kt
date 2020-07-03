package com.popalay.tracktor.ui.widget

import androidx.animation.FastOutSlowInEasing
import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.animation.Transition
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.pressIndicatorGestureFilter
import androidx.ui.foundation.Canvas
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.graphics.Path
import androidx.ui.graphics.drawscope.Stroke
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import androidx.ui.util.fastFirstOrNull
import com.popalay.tracktor.ui.widget.ChartAnimationState.STATE_END
import com.popalay.tracktor.ui.widget.ChartAnimationState.STATE_START
import java.lang.Float.max
import java.lang.Float.min
import kotlin.math.abs

enum class ChartAnimationState {
    STATE_START, STATE_END
}

private val amplifierKey = FloatPropKey()

private val definition = transitionDefinition {
    state(STATE_START) {
        this[amplifierKey] = 0F
    }
    state(STATE_END) {
        this[amplifierKey] = 1F
    }
    transition(fromState = STATE_START, toState = STATE_END) {
        amplifierKey using tween {
            duration = 1500
            easing = FastOutSlowInEasing
        }
    }
}

@Composable
fun ChartWidget(
    data: List<Double>,
    gradient: List<Color>,
    pointColor: Color = MaterialTheme.colors.onSurface,
    lineWidth: Dp = ChartLineWidth,
    labelRadius: Dp = ChartLabelRadius,
    currentState: ChartAnimationState = STATE_START,
    onPointSelected: (Double) -> Unit,
    onPointUnSelected: () -> Unit
) {
    Transition(
        definition = definition,
        initState = currentState,
        toState = STATE_END
    ) { transitionState ->
        val touchPosition = state<Offset?> { null }

        Canvas(
            modifier = Modifier.preferredHeight(100.dp)
                .fillMaxWidth()
                .pressIndicatorGestureFilter(
                    onStart = { touchPosition.value = it },
                    onStop = { touchPosition.value = null },
                    onCancel = { touchPosition.value = null }
                )
        ) {
            val conPoints1 = mutableListOf<Offset>()
            val conPoints2 = mutableListOf<Offset>()

            val bottomY = size.height
            val xDiff = size.width / (data.size - 1)
            val topOffset = labelRadius.toPx()
            val touchArea = labelRadius.toPx() * 4

            val maxData = data.max()?.toFloat() ?: 0F

            val yMax = max(bottomY - (maxData / maxData * bottomY), labelRadius.toPx() + topOffset)
            val animatedYMax = min(yMax / transitionState[amplifierKey], size.height)

            val points = data.mapIndexed { index, item ->
                val y = max(bottomY - (item.toFloat() / maxData * bottomY), labelRadius.toPx() + topOffset)
                val animatedY = min(y / transitionState[amplifierKey], size.height)

                Offset(xDiff * index, min(animatedY, max(animatedYMax, y)))
            }

            for (i in 1 until points.size) {
                conPoints1.add(Offset((points[i].x + points[i - 1].x) / 2, points[i - 1].y))
                conPoints2.add(Offset((points[i].x + points[i - 1].x) / 2, points[i].y))
            }

            if (points.isEmpty() || conPoints1.isEmpty() || conPoints2.isEmpty()) return@Canvas

            val path = Path()

            path.reset()
            path.moveTo(points.first().x, points.first().y)

            for (i in 1 until points.size) {
                path.cubicTo(
                    conPoints1[i - 1].x, conPoints1[i - 1].y,
                    conPoints2[i - 1].x, conPoints2[i - 1].y,
                    points[i].x, points[i].y
                )
            }

            val borderPath = Path()
            borderPath.addPath(path)

            path.lineTo(size.width, size.height)
            path.lineTo(0F, size.height)
            drawPath(
                path,
                brush = HorizontalGradient(
                    colors = gradient.map { it.copy(alpha = 0.5F) },
                    startX = 0F,
                    endX = size.width
                )
            )
            drawPath(
                borderPath,
                brush = HorizontalGradient(
                    colors = gradient,
                    startX = 0F,
                    endX = size.width
                ),
                style = Stroke(width = lineWidth.toPx())
            )

            val touchedPoint = if (touchPosition.value == null) null else points.fastFirstOrNull { offset ->
                (touchPosition.value!! - offset).let { abs(it.x) <= touchArea && abs(it.y) <= touchArea }
            }

            points.forEach {
                val center = Offset(max(min(it.x, size.width - labelRadius.toPx()), labelRadius.toPx()), it.y)
                drawCircle(
                    color = pointColor,
                    radius = if (it == touchedPoint) labelRadius.toPx() + topOffset else labelRadius.toPx(),
                    center = center
                )
            }

            if (touchedPoint == null) {
                onPointUnSelected()
            } else {
                onPointSelected(data.reversed()[points.reversed().indexOf(touchedPoint)])
            }
        }
    }
}

private val ChartLabelRadius = 4.dp
private val ChartLineWidth = 2.dp

@Preview
@Composable
fun PlotPreview() {
    ChartWidget(
        data = listOf(20.0, 12.0, 30.0, 2.0),
        gradient = listOf(Color(0xFF64BFE1), Color(0xFFA091B7), Color(0xFFE0608A)),
        onPointSelected = {},
        onPointUnSelected = {}
    )
}