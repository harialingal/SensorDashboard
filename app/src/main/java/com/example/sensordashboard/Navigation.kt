package com.example.sensordashboard

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.sensordashboard.ui.detail.DetailScreen
import com.example.sensordashboard.ui.main.DashboardScreen

@Composable
fun MainNavigation() {
    val backStack = rememberNavBackStack(Main)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<Main> {
                DashboardScreen(
                    onSensorClick = { sensorType ->
                        backStack.add(SensorDetail(sensorType))
                    }
                )
            }
            entry<SensorDetail> { key ->
                DetailScreen(
                    sensorType = key.sensorType,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        },
    )
}
