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
import androidx.ui.geometry.Size
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.graphics.Path
import androidx.ui.graphics.drawscope.DrawScope
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
import com.popalay.tracktor.utils.getSubPath
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
fun SimpleChartWidget(
    data: List<Double>,
    gradient: List<Color>,
    modifier: Modifier = Modifier,
    lineWidth: Dp = ChartLineWidth,
    animate: Boolean = true
) {
    Transition(
        definition = definition,
        initState = if (animate) STATE_START else STATE_END,
        toState = STATE_END
    ) { transitionState ->
        Canvas(modifier) {
            val points = createPoints(data, size, 0F, 0F, 1F)
            val (conPoints1, conPoints2) = createConnectionPoints(points)

            if (points.isEmpty() || conPoints1.isEmpty() || conPoints2.isEmpty()) return@Canvas

            val borderPath = createBorderPath(points, conPoints1, conPoints2)

            val partPath = borderPath.getSubPath(0F, transitionState[amplifierKey])
            drawPath(partPath, createBrush(gradient, size), 1.0F, Stroke(lineWidth.toPx()))
        }
    }
}

@Composable
fun ChartWidget(
    data: List<Double>,
    gradient: List<Color>,
    touchable: Boolean,
    modifier: Modifier = Modifier.preferredHeight(ChartDefaultHeight),
    pointColor: Color = MaterialTheme.colors.onSurface,
    lineWidth: Dp = ChartLineWidth,
    labelRadius: Dp = ChartLabelRadius,
    topOffset: Dp = ChartLabelRadius,
    animate: Boolean = true,
    onPointSelected: (Offset, Int) -> Unit = { _, _ -> },
    onPointUnSelected: () -> Unit = {}
) {
    Transition(
        definition = definition,
        initState = if (animate) STATE_START else STATE_END,
        toState = STATE_END
    ) { transitionState ->
        val touchPosition = state<Offset?> { null }
        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .let {
                    if (touchable) {
                        it.pressIndicatorGestureFilter(
                            onStart = { touchPosition.value = it },
                            onStop = { touchPosition.value = null },
                            onCancel = { touchPosition.value = null }
                        )
                    } else it
                }
        ) {
            val touchArea = labelRadius.toPx() * 4

            val points = createPoints(data, size, labelRadius.toPx(), topOffset.toPx(), transitionState[amplifierKey])
            val (conPoints1, conPoints2) = createConnectionPoints(points)

            if (points.isEmpty() || conPoints1.isEmpty() || conPoints2.isEmpty()) return@Canvas

            val borderPath = createBorderPath(points, conPoints1, conPoints2)
            val fillPath = createFillPath(borderPath, size)

            drawPath(fillPath, createBrush(gradient, size), 0.5F)
            drawPath(borderPath, createBrush(gradient, size), 1.0F, Stroke(lineWidth.toPx()))

            if (touchable) {
                drawTouchable(data, touchPosition.value, points, touchArea, labelRadius, pointColor, topOffset, onPointUnSelected, onPointSelected)
            }
        }
    }
}

private fun DrawScope.drawTouchable(
    data: List<Double>,
    touchPosition: Offset?,
    points: List<Offset>,
    touchArea: Float,
    labelRadius: Dp,
    pointColor: Color,
    topOffset: Dp,
    onPointUnSelected: () -> Unit,
    onPointSelected: (Offset, Int) -> Unit
) {
    val touchedPoint = if (touchPosition == null) null else points.fastFirstOrNull { offset ->
        (touchPosition - offset).let { abs(it.x) <= touchArea }
    }

    points.forEach {
        val center = Offset(max(min(it.x, size.width - labelRadius.toPx()), labelRadius.toPx()), it.y)
        drawCircle(
            color = pointColor,
            radius = if (it == touchedPoint) (labelRadius + topOffset).toPx() else labelRadius.toPx(),
            center = center
        )
    }

    if (touchedPoint == null) {
        onPointUnSelected()
    } else {
        onPointSelected(touchedPoint, points.indexOf(touchedPoint))
    }
}

private fun createPoints(
    data: List<Double>,
    size: Size,
    labelRadiusPx: Float,
    topOffsetPx: Float,
    amplifier: Float
): List<Offset> {
    val bottomY = size.height
    val xDiff = size.width / (data.size - 1)

    val minData = data.min() ?: 0.0
    val optimizedData = data.map { it - minData }
    val maxData = optimizedData.max()?.toFloat() ?: 0F

    val yMax = max(bottomY - (maxData / maxData * bottomY), labelRadiusPx + topOffsetPx)
    val animatedYMax = min(yMax / amplifier, size.height)

    return optimizedData.mapIndexed { index, item ->
        val y = max(bottomY - (item.toFloat() / maxData * bottomY), labelRadiusPx + topOffsetPx)
        val animatedY = min(y / amplifier, size.height)
        Offset(xDiff * index, min(animatedY, max(animatedYMax, y)))
    }
}

private fun createConnectionPoints(points: List<Offset>): Pair<List<Offset>, List<Offset>> {
    val conPoints1 = mutableListOf<Offset>()
    val conPoints2 = mutableListOf<Offset>()
    for (i in 1 until points.size) {
        conPoints1.add(Offset((points[i].x + points[i - 1].x) / 2, points[i - 1].y))
        conPoints2.add(Offset((points[i].x + points[i - 1].x) / 2, points[i].y))
    }
    return conPoints1 to conPoints2
}

private fun createBorderPath(
    points: List<Offset>,
    conPoints1: List<Offset>,
    conPoints2: List<Offset>
): Path {
    val borderPath = Path()

    borderPath.reset()
    borderPath.moveTo(points.first().x, points.first().y)

    for (i in 1 until points.size) {
        borderPath.cubicTo(
            conPoints1[i - 1].x, conPoints1[i - 1].y,
            conPoints2[i - 1].x, conPoints2[i - 1].y,
            points[i].x, points[i].y
        )
    }
    return borderPath
}

private fun createFillPath(borderPath: Path, size: Size): Path {
    val fillPath = Path().apply {
        addPath(borderPath)
    }

    fillPath.lineTo(size.width, size.height)
    fillPath.lineTo(0F, size.height)
    return fillPath
}

private fun createBrush(gradient: List<Color>, size: Size) = HorizontalGradient(
    colors = gradient,
    startX = 0F,
    endX = size.width
)

private val ChartDefaultHeight = 100.dp
private val ChartLabelRadius = 4.dp
private val ChartLineWidth = 2.dp

@Preview
@Composable
fun PlotPreview() {
    ChartWidget(
        data = listOf(20.0, 12.0, 30.0, 2.0),
        gradient = listOf(Color(0xFF64BFE1), Color(0xFFA091B7), Color(0xFFE0608A)),
        touchable = true
    )
}