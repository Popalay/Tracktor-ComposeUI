package com.popalay.tracktor.ui.list

import androidx.animation.FastOutSlowInEasing
import androidx.animation.FloatPropKey
import androidx.animation.transitionDefinition
import androidx.compose.Composable
import androidx.ui.animation.Transition
import androidx.ui.core.Modifier
import androidx.ui.foundation.Canvas
import androidx.ui.geometry.Offset
import androidx.ui.graphics.Color
import androidx.ui.graphics.HorizontalGradient
import androidx.ui.graphics.Path
import androidx.ui.graphics.drawscope.Stroke
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.preferredHeight
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import com.popalay.tracktor.ui.list.ChartAnimationState.STATE_END
import com.popalay.tracktor.ui.list.ChartAnimationState.STATE_START
import java.lang.Float.max
import java.lang.Float.min

private val amplifierKey = FloatPropKey()

enum class ChartAnimationState {
    STATE_START, STATE_END
}

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
    currentState: ChartAnimationState = STATE_START
) {
    Transition(
        definition = definition,
        initState = currentState,
        toState = STATE_END
    ) { state ->
        Canvas(modifier = Modifier.preferredHeight(100.dp).fillMaxWidth()) {

            val conPoints1 = mutableListOf<Offset>()
            val conPoints2 = mutableListOf<Offset>()

            val bottomY = size.height
            val xDiff = size.width / (data.size - 1)
            val lineWidth = 2.dp.toPx().value
            val labelRadius = 4.dp.toPx().value

            val maxData = data.max()?.toFloat() ?: 0F

            val yMax = max(bottomY - (maxData / maxData * bottomY), labelRadius)
            val animatedYMax = min(yMax / state[amplifierKey], size.height)

            val points = data.mapIndexed { index, item ->
                val y = max(bottomY - (item.toFloat() / maxData * bottomY), labelRadius)
                val animatedY = min(y / state[amplifierKey], size.height)

                Offset(xDiff * index, min(animatedY, max(animatedYMax, y)))
            }

            for (i in 1 until points.size) {
                conPoints1.add(Offset((points[i].dx + points[i - 1].dx) / 2, points[i - 1].dy))
                conPoints2.add(Offset((points[i].dx + points[i - 1].dx) / 2, points[i].dy))
            }

            if (points.isEmpty() || conPoints1.isEmpty() || conPoints2.isEmpty()) return@Canvas

            val path = Path()

            path.reset()
            path.moveTo(points.first().dx, points.first().dy)

            for (i in 1 until points.size) {
                path.cubicTo(
                    conPoints1[i - 1].dx, conPoints1[i - 1].dy,
                    conPoints2[i - 1].dx, conPoints2[i - 1].dy,
                    points[i].dx, points[i].dy
                )
            }

            val borderPath = Path()
            borderPath.addPath(path)

            path.lineTo(size.width, size.height)
            path.lineTo(0F, size.height)
            drawPath(
                path,
                brush = HorizontalGradient(
                    colors = gradient,
                    startX = 0F,
                    endX = size.width
                )
            )
            drawPath(borderPath, color = Color.White, style = Stroke(width = lineWidth))

            points.forEach {
                val center = Offset(max(min(it.dx, size.width - labelRadius), labelRadius), it.dy)
                drawCircle(
                    color = Color.White,
                    radius = labelRadius,
                    center = center
                )
            }
        }
    }
}

@Preview
@Composable
fun PlotPreview() {
    ChartWidget(
        data = listOf(20.0, 12.0, 30.0, 2.0),
        gradient = listOf(Color(0xFF64BFE1), Color(0xFFA091B7), Color(0xFFE0608A))
    )
}