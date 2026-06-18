package com.example.sensordashboard.ui.detail

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sensordashboard.data.SensorInfo
import com.example.sensordashboard.data.SensorReading
import com.example.sensordashboard.data.SensorRepository
import com.example.sensordashboard.theme.DarkBackground
import com.example.sensordashboard.theme.DarkCard
import com.example.sensordashboard.theme.GlassBorder
import com.example.sensordashboard.theme.TextSecondary
import com.example.sensordashboard.ui.components.SparklineChart
import com.example.sensordashboard.ui.components.formatValue
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DetailScreen(
    sensorType: Int,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val repository = remember { SensorRepository(context) }
    val viewModel: DetailViewModel = viewModel(
        factory = DetailViewModel.Factory(repository, sensorType)
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Top bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = state.info?.name ?: "Sensor Detail",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.info?.category?.displayName ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = state.info?.category?.color ?: TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            state.info?.let { info ->
                // Gauge + sparkline row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Accuracy gauge
                    AccuracyGauge(
                        reading = state.reading,
                        color = info.category.color,
                        modifier = Modifier.size(120.dp)
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(120.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "History",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                        val history = state.reading?.history ?: emptyList()
                        if (history.size >= 3) {
                            SparklineChart(
                                data = history,
                                color = info.category.color,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No history yet",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Live values
                LiveValuesSection(info = info, reading = state.reading)

                Spacer(Modifier.height(24.dp))

                // Sensor metadata card
                MetadataSection(info = info)
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun LiveValuesSection(info: SensorInfo, reading: SensorReading?) {
    val accentColor = info.category.color

    SectionCard(title = "Live Readings", accentColor = accentColor) {
        if (reading != null) {
            reading.values.take(info.axisLabels.size).forEachIndexed { i, value ->
                val label = info.axisLabels.getOrElse(i) { "V${i + 1}" }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formatValue(value),
                            style = MaterialTheme.typography.bodyLarge,
                            color = accentColor,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = info.unit,
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                }
                if (i < reading.values.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(GlassBorder)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Awaiting sensor data...", color = TextSecondary, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun MetadataSection(info: SensorInfo) {
    SectionCard(title = "Sensor Info", accentColor = info.category.color) {
        listOf(
            "Vendor" to info.vendor,
            "Version" to "v${info.version}",
            "Resolution" to "${info.resolution} ${info.unit}",
            "Max Range" to "${info.maxRange} ${info.unit}",
            "Power" to "${info.power} mA",
            "Min Delay" to if (info.minDelay == 0) "N/A" else "${info.minDelay} µs",
        ).forEachIndexed { i, (label, value) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, color = TextSecondary, style = MaterialTheme.typography.bodySmall)
                Text(
                    value,
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace
                )
            }
            if (i < 5) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(GlassBorder))
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    accentColor: Color,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkCard)
            .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = accentColor,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(16.dp))
        content()
    }
}

@Composable
private fun AccuracyGauge(
    reading: SensorReading?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gauge_spin")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // Track ring
            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Spinning arc
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(Color.Transparent, color),
                    center = center
                ),
                startAngle = sweepAngle,
                sweepAngle = 120f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (reading != null) "LIVE" else "N/A",
                style = MaterialTheme.typography.labelSmall,
                color = if (reading != null) color else TextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontSize = 10.sp
            )
            Text(
                text = "Sensor",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontSize = 9.sp
            )
        }
    }
}
