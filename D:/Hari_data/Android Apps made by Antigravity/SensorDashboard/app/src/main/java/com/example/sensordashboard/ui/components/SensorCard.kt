package com.example.sensordashboard.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensordashboard.data.SensorInfo
import com.example.sensordashboard.data.SensorReading
import com.example.sensordashboard.theme.DarkCard
import com.example.sensordashboard.theme.GlassBorder
import com.example.sensordashboard.theme.GlassWhite
import com.example.sensordashboard.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun SensorCard(
    info: SensorInfo,
    reading: SensorReading?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val accentColor = info.category.color

    // Flash animation when new reading arrives
    var flashAlpha by remember { mutableStateOf(0f) }
    val animatedFlash by animateFloatAsState(
        targetValue = flashAlpha,
        animationSpec = tween(300),
        label = "flash"
    )
    LaunchedEffect(reading?.timestamp) {
        if (reading != null) {
            flashAlpha = 1f
            delay(300)
            flashAlpha = 0f
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        DarkCard,
                        DarkCard.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.5f * animatedFlash + 0.15f),
                        GlassBorder
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // Subtle glow overlay on update
        if (animatedFlash > 0f) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(accentColor.copy(alpha = 0.04f * animatedFlash))
            )
        }

        Column {
            // Header row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Category dot
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(accentColor, CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = info.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                // Live indicator
                if (reading != null) {
                    LiveIndicator(color = accentColor)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Values display
            if (reading != null) {
                val labels = info.axisLabels
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    reading.values.take(labels.size).forEachIndexed { i, value ->
                        AxisValue(
                            label = labels.getOrElse(i) { "V${i + 1}" },
                            value = value,
                            unit = if (i == 0) info.unit else "",
                            color = accentColor,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Sparkline
                if (reading.history.size >= 3) {
                    Spacer(Modifier.height(10.dp))
                    SparklineChart(
                        data = reading.history,
                        color = accentColor,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    )
                }
            } else {
                // No data yet
                Text(
                    text = "Waiting for data...",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(Modifier.height(50.dp))
            }

            Spacer(Modifier.height(8.dp))

            // Footer
            Text(
                text = info.unit.ifBlank { info.vendor },
                style = MaterialTheme.typography.labelSmall,
                color = accentColor.copy(alpha = 0.6f),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun AxisValue(
    label: String,
    value: Float,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            fontSize = 9.sp,
        )
        Text(
            text = formatValue(value),
            style = MaterialTheme.typography.bodyMedium,
            color = color,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            maxLines = 1,
        )
    }
}

@Composable
private fun LiveIndicator(color: Color) {
    var visible by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(800)
            visible = !visible
        }
    }
    Box(
        modifier = Modifier
            .size(6.dp)
            .background(
                color = if (visible) color else Color.Transparent,
                shape = CircleShape
            )
    )
}

fun formatValue(value: Float): String {
    val abs = abs(value)
    return when {
        abs >= 10000f -> "%.0f".format(value)
        abs >= 100f -> "%.1f".format(value)
        abs >= 1f -> "%.3f".format(value)
        abs >= 0.001f -> "%.4f".format(value)
        else -> "%.2e".format(value)
    }
}
