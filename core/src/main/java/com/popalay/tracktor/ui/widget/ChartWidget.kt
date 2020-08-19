package com.popalay.tracktor.ui.widget

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FloatPropKey
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.transitionDefinition
import androidx.compose.animation.core.tween
import androidx.compose.animation.transition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.state
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.HorizontalGradient
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.popalay.tracktor.ui.widget.ChartAnimationState.STATE_END
import com.popalay.tracktor.ui.widget.ChartAnimationState.STATE_START
import com.popalay.tracktor.utils.dragGestureFilter
import com.popalay.tracktor.utils.getSubPath
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

private enum class ChartAnimationState {
    STATE_START, STATE_END
}

private val amplifierKey = FloatPropKey()

private val tweenDefinition = transitionDefinition<ChartAnimationState> {
    state(STATE_START) {
        this[amplifierKey] = 0F
    }
    state(STATE_END) {
        this[amplifierKey] = 1F
    }
    transition(fromState = STATE_START, toState = STATE_END) {
        amplifierKey using tween(
            durationMillis = 1500,
            easing = FastOutSlowInEasing
        )
    }
}

private val springDefinition = transitionDefinition<ChartAnimationState> {
    state(STATE_START) {
        this[amplifierKey] = 0F
    }
    state(STATE_END) {
        this[amplifierKey] = 1F
    }
    transition(fromState = STATE_START, toState = STATE_END) {
        amplifierKey using spring(
            dampingRatio = 0.7F,
            stiffness = 80F
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
        val points = createPoints(data, size)
        val (conPoints1, conPoints2) = createConnectionPoints(points)

        if (points.isEmpty() || conPoints1.isEmpty() || conPoints2.isEmpty()) return@Canvas

        val borderPath = createBorderPath(points, conPoints1, conPoints2)

        val partPath = borderPath.getSubPath(0F, transitionState[amplifierKey])
        drawPath(partPath, createBrush(gradient, size), 1.0F, Stroke(lineWidth.toPx(), cap = StrokeCap.Round))
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
    val transitionState = transition(
        definition = springDefinition,
        initState = if (animate) STATE_START else STATE_END,
        toState = STATE_END
    )
    val touchPosition = state<Offset?> { null }
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .dragGestureFilter(
                onStart = { touchPosition.value = it },
                onDrag = { touchPosition.value = touchPosition.value?.plus(it);it },
                onStop = { touchPosition.value = null },
                onCancel = { touchPosition.value = null },
                canDrag = { touchable },
                startDragImmediately = true
            )
    ) {
        val amplifier = transitionState[amplifierKey]
        val shift = Offset(0F, size.height * (1 - amplifier))
        val points = createPoints(data, size, labelRadius.toPx(), topOffset.toPx(), topOffset.toPx()).map { it + shift }
        val (conPoints1, conPoints2) = createConnectionPoints(points)

        if (points.isEmpty() || conPoints1.isEmpty() || conPoints2.isEmpty()) return@Canvas

        val borderPath = createBorderPath(points, conPoints1, conPoints2)
        val fillPath = createFillPath(borderPath, size)

        drawPath(fillPath, createBrush(gradient, size), 0.5F)
        drawPath(borderPath, createBrush(gradient, size), 1.0F, Stroke(lineWidth.toPx()))

        if (touchable) {
            drawTouchable(touchPosition.value, points, labelRadius, pointColor, topOffset, onPointUnSelected, onPointSelected)
        }
    }
}

private fun DrawScope.drawTouchable(
    touchPosition: Offset?,
    points: List<Offset>,
    labelRadius: Dp,
    pointColor: Color,
    topOffset: Dp,
    onPointUnSelected: () -> Unit,
    onPointSelected: (Offset, Int) -> Unit
) {
    val touchedPoint = if (touchPosition == null) null else points.minByOrNull { (it.x - touchPosition.x).absoluteValue }

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
    labelRadiusPx: Float = 0F,
    topOffsetPx: Float = 0F,
    bottomOffsetPx: Float = 0F
): List<Offset> {
    val bottomY = size.height - bottomOffsetPx
    val xDiff = size.width / max(data.size - 1, 1)

    val minData = data.minOrNull() ?: 0.0
    val optimizedData = data.let { if (it.size == 1) emptyList() else it }
        .map { it - minData }.ifEmpty { listOf(0.0, 0.0) }
    val maxData = optimizedData.maxOrNull()?.toFloat() ?: 0F

    return optimizedData.mapIndexed { index, item ->
        val y = max(bottomY - item.toFloat() / maxData * bottomY, labelRadiusPx + topOffsetPx)
        Offset(xDiff * index, y)
    }.let { offsets ->
        if (optimizedData.all { it == 0.0 }) {
            offsets.map { it.copy(y = bottomY / 2) }
        } else offsets
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