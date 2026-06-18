package com.example.sensordashboard

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Main : NavKey

@Serializable
data class SensorDetail(val sensorType: Int) : NavKey
