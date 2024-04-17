package com.example.coursework.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursework.data.UiState
import com.example.coursework.data.WeatherApi
import com.example.coursework.data.WeatherData
import com.example.healthapproomdb.model.StepsData
import com.example.healthapproomdb.model.UserData
import com.example.healthapproomdb.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

// Weather API
sealed class WeatherState{
    object Loading:WeatherState()
    data class Success(val weatherData: WeatherData):WeatherState()
    data class Error(val message:String):WeatherState()
}

class AppViewModel(private val repository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // steps params for toady
    private val _stepsToday = MutableStateFlow<Int?>(0)
    val stepsToday: StateFlow<Int?> = _stepsToday

    // steps params for last 7 days
    private val _stepsWeek = MutableStateFlow<Int?>(0)
    val stepsWeek: StateFlow<Int?> = _stepsWeek

    init {
        // Trigger getStepsToday() and seven days when ViewModel is initialized
        getStepsToday()
        getStepsLastSevenDays()
    }

    // date time
    val todayStart: Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val todayEnd: Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 999)
    }.timeInMillis

    val sevenDaysAgo: Long = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, -7)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis


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

    // room database functions starts here ------>
    // Function to get all usersData
    fun getAllUsers(): Flow<List<UserData>> {
        return repository.getAllUserData()
    }

    // Function to insert a usersData
    fun insertUserData(user: UserData) {
        viewModelScope.launch {
            repository.insertUserData(user)
        }
    }

    // Function to update a usersData
    fun updateUser(user: UserData) {
        viewModelScope.launch {
            repository.updateUserData(user)
        }
    }

    // Function to delete a usersData
    fun deleteUser(user: UserData) {
        viewModelScope.launch {
            repository.deleteUserData(user)
        }
    }

    fun getStepsToday() {
        viewModelScope.launch(Dispatchers.IO) {
            val stepsToday = repository.getStepsToday(todayStart)
            _stepsToday.value = stepsToday
        }
    }

    fun getStepsLastSevenDays() {
        viewModelScope.launch(Dispatchers.IO) {
            val stepsWeek = repository.getStepsLastSevenDays(sevenDaysAgo, todayEnd)
            _stepsWeek.value = stepsWeek
        }
    }


    fun addSteps(step: StepsData) {
        viewModelScope.launch {
            repository.addSteps(step)
        }
    }

    // room database function ends here -------->

}