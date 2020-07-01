package com.popalay.tracktor.ui.list

import androidx.compose.Composable
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

@Composable
fun PlotWidget(data: List<Double>, gradient: List<Color>) {
    Canvas(modifier = Modifier.preferredHeight(100.dp).fillMaxWidth()) {

        val conPoints1 = mutableListOf<Offset>()
        val conPoints2 = mutableListOf<Offset>()

        val bottomY = size.height
        val xDiff = size.width / (data.size - 1)

        val maxData = data.max()?.toFloat() ?: 0F

        val points = data.mapIndexed { index, item ->
            val y = bottomY - (item.toFloat() / maxData * bottomY) + 5
            Offset(xDiff * index, y)
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
        drawPath(borderPath, color = Color.White, style = Stroke(width = 5F))
    }
}

@Preview
@Composable
fun PlotPreview() {
    PlotWidget(
        data = listOf(20.0, 12.0, 30.0, 2.0),
        gradient = listOf(Color(0xFF64BFE1), Color(0xFFA091B7), Color(0xFFE0608A))
    )
}