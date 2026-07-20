package com.brunoapp.fittrack.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Minimal dependency-free line chart. Draws a polyline with point markers,
 * scaled to min/max of the values. Shows min and max labels underneath.
 */
@Composable
fun SimpleLineChart(
    values: List<Double>,
    modifier: Modifier = Modifier,
    minLabel: String = "",
    maxLabel: String = ""
) {
    if (values.size < 2) return

    val lineColor = MaterialTheme.colorScheme.primary
    val pointColor = MaterialTheme.colorScheme.primary
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            val min = values.min()
            val max = values.max()
            val range = (max - min).takeIf { it > 0.0 } ?: 1.0
            val paddingY = size.height * 0.12f
            val chartHeight = size.height - paddingY * 2
            val stepX = size.width / (values.size - 1)

            fun pointAt(index: Int): Offset {
                val normalized = ((values[index] - min) / range).toFloat()
                return Offset(
                    x = index * stepX,
                    y = paddingY + chartHeight * (1f - normalized)
                )
            }

            // Grid lines (3 horizontal)
            for (i in 0..2) {
                val y = paddingY + chartHeight * i / 2f
                drawLine(
                    color = gridColor,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Polyline
            val path = Path()
            values.indices.forEach { index ->
                val point = pointAt(index)
                if (index == 0) path.moveTo(point.x, point.y)
                else path.lineTo(point.x, point.y)
            }
            drawPath(path, color = lineColor, style = Stroke(width = 2.5.dp.toPx()))

            // Points
            values.indices.forEach { index ->
                drawCircle(
                    color = pointColor,
                    radius = 4.dp.toPx(),
                    center = pointAt(index)
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = minLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = maxLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
