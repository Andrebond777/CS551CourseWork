package com.example.coursework.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework.data.UiState
import com.example.coursework.data.WeatherApi
import com.example.coursework.data.WeatherData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Weather API
sealed class WeatherState{
    object Loading:WeatherState()
    data class Success(val weatherData: WeatherData):WeatherState()
    data class Error(val message:String):WeatherState()
}

class AppViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // View Model for Weather API
    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState


    // Function of Fetch Weather Data from API
    fun fetchWeatherData() {
        viewModelScope.launch {
            try {
                val response = WeatherApi.weatherService.getWeather(
                    apiKey = "c4c9834ef0a048e1a4f45144231605",
                    location = "Glasgow"
                )
                _weatherState.value = WeatherState.Success(response)

            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error("Failed to fetch weather data.")
            }
        }
    } // end of function fetchWeatherData

}