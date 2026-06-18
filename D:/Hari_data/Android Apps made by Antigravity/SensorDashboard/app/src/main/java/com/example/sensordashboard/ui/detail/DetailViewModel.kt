package com.example.sensordashboard.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sensordashboard.data.SensorInfo
import com.example.sensordashboard.data.SensorReading
import com.example.sensordashboard.data.SensorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailUiState(
    val info: SensorInfo? = null,
    val reading: SensorReading? = null,
    val isLoading: Boolean = true,
)

class DetailViewModel(
    private val repository: SensorRepository,
    private val sensorType: Int,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    init {
        val info = repository.getAvailableSensors().firstOrNull { it.sensorType == sensorType }
        _uiState.update { it.copy(info = info, isLoading = false) }

        viewModelScope.launch {
            repository.observeSensor(sensorType).collect { reading ->
                _uiState.update { it.copy(reading = reading) }
            }
        }
    }

    class Factory(
        private val repository: SensorRepository,
        private val sensorType: Int,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DetailViewModel(repository, sensorType) as T
        }
    }
}
