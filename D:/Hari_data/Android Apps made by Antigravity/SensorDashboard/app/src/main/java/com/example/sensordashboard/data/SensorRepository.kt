package com.example.sensordashboard.data

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val SPARKLINE_MAX_POINTS = 40

class SensorRepository(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    /** Returns metadata for all sensors available on the device. */
    fun getAvailableSensors(): List<SensorInfo> {
        return sensorManager
            .getSensorList(Sensor.TYPE_ALL)
            .distinctBy { it.type } // show one of each type
            .map { it.toSensorInfo() }
            .sortedWith(compareBy({ it.category.ordinal }, { it.name }))
    }

    /** Emits live [SensorReading]s for all sensors, updating whenever any sensor fires. */
    fun observeAllSensors(): Flow<Map<Int, SensorReading>> = callbackFlow {
        val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL).distinctBy { it.type }
        val readingsMap = mutableMapOf<Int, SensorReading>()
        val historyMap = mutableMapOf<Int, MutableList<Float>>()

        val listeners = mutableListOf<SensorEventListener>()

        sensors.forEach { sensor ->
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    val history = historyMap.getOrPut(event.sensor.type) { mutableListOf() }
                    if (event.values.isNotEmpty()) {
                        history.add(event.values[0])
                        if (history.size > SPARKLINE_MAX_POINTS) history.removeAt(0)
                    }
                    readingsMap[event.sensor.type] = SensorReading(
                        sensorType = event.sensor.type,
                        values = event.values.copyOf(),
                        accuracy = event.sensor.type, // reuse field
                        timestamp = event.timestamp,
                        history = history.toList()
                    )
                    trySend(readingsMap.toMap())
                }

                override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
            }
            val registered = sensorManager.registerListener(
                listener, sensor, SensorManager.SENSOR_DELAY_UI
            )
            if (registered) listeners.add(listener)
        }

        awaitClose {
            listeners.forEach { sensorManager.unregisterListener(it) }
        }
    }

    /** Observes a single sensor type with accuracy tracking. */
    fun observeSensor(sensorType: Int): Flow<SensorReading?> = callbackFlow {
        val sensor = sensorManager.getDefaultSensor(sensorType)
        if (sensor == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val history = mutableListOf<Float>()
        var lastAccuracy = 0

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.values.isNotEmpty()) {
                    history.add(event.values[0])
                    if (history.size > SPARKLINE_MAX_POINTS) history.removeAt(0)
                }
                trySend(
                    SensorReading(
                        sensorType = sensorType,
                        values = event.values.copyOf(),
                        accuracy = lastAccuracy,
                        timestamp = event.timestamp,
                        history = history.toList()
                    )
                )
            }

            override fun onAccuracyChanged(s: Sensor, accuracy: Int) {
                lastAccuracy = accuracy
            }
        }

        sensorManager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
        awaitClose { sensorManager.unregisterListener(listener) }
    }
}
