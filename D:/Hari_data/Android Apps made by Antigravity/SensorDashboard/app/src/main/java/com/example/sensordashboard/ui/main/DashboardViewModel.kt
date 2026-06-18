package com.example.sensordashboard.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sensordashboard.data.SensorCategory
import com.example.sensordashboard.data.SensorInfo
import com.example.sensordashboard.data.SensorReading
import com.example.sensordashboard.data.SensorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val sensors: List<SensorInfo> = emptyList(),
    val readings: Map<Int, SensorReading> = emptyMap(),
    val selectedCategory: SensorCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
)

class DashboardViewModel(private val repository: SensorRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        val sensors = repository.getAvailableSensors()
        _uiState.update { it.copy(sensors = sensors, isLoading = false) }

        viewModelScope.launch {
            repository.observeAllSensors().collect { readings ->
                _uiState.update { it.copy(readings = readings) }
            }
        }
    }

    fun selectCategory(category: SensorCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun filteredSensors(): List<SensorInfo> {
        val state = _uiState.value
        return state.sensors.filter { sensor ->
            val matchesCategory = state.selectedCategory == null || sensor.category == state.selectedCategory
            val matchesSearch = state.searchQuery.isBlank() ||
                    sensor.name.contains(state.searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    class Factory(private val repository: SensorRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
    }
}
