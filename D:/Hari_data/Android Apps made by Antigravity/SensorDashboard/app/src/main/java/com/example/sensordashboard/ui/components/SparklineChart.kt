package com.example.sensordashboard.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun SparklineChart(
    data: List<Float>,
    color: Color,
    modifier: Modifier = Modifier,
) {
    if (data.size < 2) return

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val min = data.min()
        val max = data.max()
        val range = if (max - min < 0.001f) 1f else max - min

        fun xAt(index: Int) = index.toFloat() / (data.size - 1) * width
        fun yAt(value: Float) = height - ((value - min) / range) * height * 0.85f - height * 0.075f

        // Build fill path
        val fillPath = Path().apply {
            moveTo(xAt(0), height)
            lineTo(xAt(0), yAt(data[0]))
            for (i in 1 until data.size) {
                val x0 = xAt(i - 1); val y0 = yAt(data[i - 1])
                val x1 = xAt(i); val y1 = yAt(data[i])
                val cpX = (x0 + x1) / 2
                cubicTo(cpX, y0, cpX, y1, x1, y1)
            }
            lineTo(xAt(data.size - 1), height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.35f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        // Build line path
        val linePath = Path().apply {
            moveTo(xAt(0), yAt(data[0]))
            for (i in 1 until data.size) {
                val x0 = xAt(i - 1); val y0 = yAt(data[i - 1])
                val x1 = xAt(i); val y1 = yAt(data[i])
                val cpX = (x0 + x1) / 2
                cubicTo(cpX, y0, cpX, y1, x1, y1)
            }
        }

        drawPath(
            path = linePath,
            brush = Brush.horizontalGradient(
                colors = listOf(color.copy(alpha = 0.4f), color)
            ),
            style = Stroke(width = 3f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Draw last point dot
        val lastX = xAt(data.size - 1)
        val lastY = yAt(data.last())
        drawCircle(color = color, radius = 5f, center = Offset(lastX, lastY))
        drawCircle(color = color.copy(alpha = 0.3f), radius = 10f, center = Offset(lastX, lastY))
    }
}
