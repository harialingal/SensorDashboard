package com.example.sensordashboard.data

import android.hardware.Sensor
import androidx.compose.ui.graphics.Color
import com.example.sensordashboard.theme.EnvironmentColor
import com.example.sensordashboard.theme.MotionColor
import com.example.sensordashboard.theme.NeonPurple
import com.example.sensordashboard.theme.PositionColor

enum class SensorCategory(val displayName: String, val color: Color) {
    MOTION("Motion", MotionColor),
    ENVIRONMENT("Environment", EnvironmentColor),
    POSITION("Position", PositionColor),
    OTHER("Other", NeonPurple)
}

data class SensorInfo(
    val sensorType: Int,
    val name: String,
    val vendor: String,
    val version: Int,
    val resolution: Float,
    val maxRange: Float,
    val minDelay: Int,
    val power: Float,
    val category: SensorCategory,
    val axisLabels: List<String>,
    val unit: String,
)

data class SensorReading(
    val sensorType: Int,
    val values: FloatArray,
    val accuracy: Int,
    val timestamp: Long,
    val history: List<Float> = emptyList(), // sparkline data (first axis)
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SensorReading) return false
        return sensorType == other.sensorType &&
                values.contentEquals(other.values) &&
                accuracy == other.accuracy &&
                timestamp == other.timestamp
    }

    override fun hashCode(): Int {
        var result = sensorType
        result = 31 * result + values.contentHashCode()
        result = 31 * result + accuracy
        result = 31 * result + timestamp.hashCode()
        return result
    }
}

fun Sensor.toCategory(): SensorCategory = when (type) {
    Sensor.TYPE_ACCELEROMETER,
    Sensor.TYPE_LINEAR_ACCELERATION,
    Sensor.TYPE_GRAVITY,
    Sensor.TYPE_GYROSCOPE,
    Sensor.TYPE_GYROSCOPE_UNCALIBRATED,
    Sensor.TYPE_ACCELEROMETER_UNCALIBRATED,
    Sensor.TYPE_SIGNIFICANT_MOTION,
    Sensor.TYPE_STEP_DETECTOR,
    Sensor.TYPE_STEP_COUNTER -> SensorCategory.MOTION

    Sensor.TYPE_AMBIENT_TEMPERATURE,
    Sensor.TYPE_PRESSURE,
    Sensor.TYPE_RELATIVE_HUMIDITY,
    Sensor.TYPE_LIGHT -> SensorCategory.ENVIRONMENT

    Sensor.TYPE_MAGNETIC_FIELD,
    Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED,
    Sensor.TYPE_ROTATION_VECTOR,
    Sensor.TYPE_GAME_ROTATION_VECTOR,
    Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
    Sensor.TYPE_ORIENTATION,
    Sensor.TYPE_PROXIMITY -> SensorCategory.POSITION

    else -> SensorCategory.OTHER
}

fun Sensor.toAxisLabels(): List<String> = when (type) {
    Sensor.TYPE_ACCELEROMETER,
    Sensor.TYPE_LINEAR_ACCELERATION,
    Sensor.TYPE_GRAVITY,
    Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> listOf("X", "Y", "Z")

    Sensor.TYPE_GYROSCOPE,
    Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> listOf("X", "Y", "Z")

    Sensor.TYPE_MAGNETIC_FIELD,
    Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> listOf("X", "Y", "Z")

    Sensor.TYPE_ROTATION_VECTOR,
    Sensor.TYPE_GAME_ROTATION_VECTOR,
    Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> listOf("X", "Y", "Z", "W")

    Sensor.TYPE_ORIENTATION -> listOf("Azimuth", "Pitch", "Roll")

    Sensor.TYPE_LIGHT -> listOf("Lux")
    Sensor.TYPE_PRESSURE -> listOf("hPa")
    Sensor.TYPE_PROXIMITY -> listOf("cm")
    Sensor.TYPE_AMBIENT_TEMPERATURE -> listOf("°C")
    Sensor.TYPE_RELATIVE_HUMIDITY -> listOf("%")
    Sensor.TYPE_STEP_COUNTER -> listOf("Steps")
    else -> listOf("Value")
}

fun Sensor.toUnit(): String = when (type) {
    Sensor.TYPE_ACCELEROMETER,
    Sensor.TYPE_LINEAR_ACCELERATION,
    Sensor.TYPE_GRAVITY,
    Sensor.TYPE_ACCELEROMETER_UNCALIBRATED -> "m/s²"

    Sensor.TYPE_GYROSCOPE,
    Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> "rad/s"

    Sensor.TYPE_MAGNETIC_FIELD,
    Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> "µT"

    Sensor.TYPE_ROTATION_VECTOR,
    Sensor.TYPE_GAME_ROTATION_VECTOR,
    Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> "unit vec"

    Sensor.TYPE_LIGHT -> "lx"
    Sensor.TYPE_PRESSURE -> "hPa"
    Sensor.TYPE_PROXIMITY -> "cm"
    Sensor.TYPE_AMBIENT_TEMPERATURE -> "°C"
    Sensor.TYPE_RELATIVE_HUMIDITY -> "%"
    Sensor.TYPE_STEP_COUNTER -> "steps"
    Sensor.TYPE_ORIENTATION -> "°"
    else -> ""
}

fun Sensor.toSensorInfo(): SensorInfo = SensorInfo(
    sensorType = type,
    name = name,
    vendor = vendor,
    version = version,
    resolution = resolution,
    maxRange = maximumRange,
    minDelay = minDelay,
    power = power,
    category = toCategory(),
    axisLabels = toAxisLabels(),
    unit = toUnit()
)
