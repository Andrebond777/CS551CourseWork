package com.example.coursework.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursework.data.UiState
import com.example.coursework.data.WeatherApi
import com.example.coursework.data.WeatherData
import com.example.coursework.model.StepsData
import com.example.coursework.model.UserData
import com.example.coursework.repository.UserRepository
import com.example.coursework.worker.NotificationWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar


var notificationIdCounter = 0

// Weather API
sealed class WeatherState{
    object Loading:WeatherState()
    data class Success(val weatherData: WeatherData):WeatherState()
    data class Error(val message:String):WeatherState()
}

class AppViewModel(private val context: Context, private val repository: UserRepository) : ViewModel(), SensorEventListener {
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()
    private val _stepsToday = MutableStateFlow<Int?>(0)
    val stepsToday: StateFlow<Int?> = _stepsToday
    private val _stepsWeek = MutableStateFlow<Int?>(0)
    var _stepsEveryDayWeek = mutableListOf<Int?>()
    val stepsWeek: StateFlow<Int?> = _stepsWeek

    private val _waterGiven = MutableStateFlow<Int?>(0)
    val waterGiven: StateFlow<Int?> = _waterGiven

    //variable that fetches the output of the gps worker from the repository
    val location: Flow<DoubleArray> = repository.outputWorkInfo
        .map { info ->
            info.outputData.getDoubleArray("GPS") ?: DoubleArray(2)
        }


    init {
        startSensorListener()

    }

    private fun startSensorListener() {
        stepSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }
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

    override fun onSensorChanged(event: SensorEvent) {
        val totalSteps = event.values[0].toInt()
        _stepsToday.value?.let { currentSteps ->
            if (totalSteps != currentSteps) {
                val newSteps = if (currentSteps == 0) totalSteps else totalSteps - currentSteps
                viewModelScope.launch(Dispatchers.IO) {
                    repository.addSteps(StepsData(stepCount = newSteps))
                }
                _stepsToday.value = totalSteps
            }
        } ?: run {
            _stepsToday.value = totalSteps
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }

    fun updateStepsToday() {
        viewModelScope.launch(Dispatchers.IO) {
            _stepsToday.value = repository.getStepsToday(System.currentTimeMillis())
        }
    }

    fun updateStepsWeek() {
        viewModelScope.launch(Dispatchers.IO) {
            _stepsWeek.value = repository.getStepsLastSevenDays(System.currentTimeMillis() - 604800000L, System.currentTimeMillis())
        }
    }


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

    fun getStepsEveryDayLastSevenDays() {
        viewModelScope.launch(Dispatchers.IO) {
            _stepsEveryDayWeek = repository.getStepsEveryDayLastSevenDays(sevenDaysAgo, todayEnd).toMutableList()
        }
    }

    // return last same date value
    // -1 if no value of same date exists
    fun getLastWaterDataSameDate() {
        viewModelScope.launch(Dispatchers.IO) {
            val waterGiven = repository.getLastWaterDataSameDate()
            _waterGiven.value = waterGiven
        }
    }

    fun addMockDataWater() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMockData()
        }
    }

    // check if same date value exists
    // if not
    // create a row with value set to 1
    fun setLastWaterDataToOne() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setLastWaterDataToOne()
            val waterGiven = repository.getLastWaterDataSameDate()
            _waterGiven.value = waterGiven
        }
    }

    fun addSteps(step: StepsData) {
        viewModelScope.launch {
            repository.addSteps(step)
        }
    }

    // room database function ends here -------->

    //Use to Run GPS worker to fetch GPS location into
    fun runGPSWorker(){
        repository.runGPSWorker()
    }

    // If todays water drinking is not been trigger
    // NOT BEEN TRIGGER VALUE SHOULD BE 0
    // AND
    // the steps is over 1000
    // Hit the trigger
    fun runStepsWatcher(){
        val isTrigger = getLastWaterDataSameDate() // *** Expecting getting int but i getting Kotlin.Unit
        val noti = NotificationWorker()

//        if(!isTrigger){
//            if(stepsToday.value?.toInt()!! > 1000){
//                noti.triggerNotification( context, "Stay Hydrated", "Time to drink some water.")
//            }
//        }
        // Then update the waterTrigger to 1
    }

    fun testReturnSteps(): StateFlow<Int?> {
        return stepsToday
    }

    //Runs Weather Watcher Workers
    fun runWeatherWatcherWorker(){
        repository.runWeatherWatcherWorker()
    }

    //Function for testing purposes
    fun testWeatherWatcherWorker(){
        repository.testWeatherWatcherWorker()
    }


    private val _recommendedSteps = MutableStateFlow<Int>(10000) // Default value, adjust based on actual calculation
    val recommendedSteps: StateFlow<Int> = _recommendedSteps.asStateFlow()

    private fun calculateRecommendedSteps() {
        viewModelScope.launch {
            repository.getAllUserData().collect { userDataList ->
                if (userDataList.isNotEmpty()) {
                    val userData = userDataList.first() // Assuming the latest or the only user data is what you need
                    _recommendedSteps.value = calculateStepsBasedOnUserData(userData)
                }
            }
        }
    }



    private fun calculateStepsBasedOnUserData(userData: UserData): Int {
        val heightInMeters = userData.height.toDouble() / 100  // Convert cm to meters
        val weightInKg = userData.weight.toDouble()
        val age = userData.age.toInt()

        val bmi = weightInKg / (heightInMeters * heightInMeters)

        // Determine steps based on BMI and age categories
        val baseSteps = when {
            age <= 18 -> 12000
            age in 19..50 -> 10000
            age > 50 -> 8000
            else -> 10000  // default if no age provided
        }

        // Adjust base steps according to BMI
        return when {
            bmi < 18.5 -> baseSteps + 1000  // underweight, encourage more activity
            bmi in 18.5..24.9 -> baseSteps  // normal weight
            bmi in 25.0..29.9 -> baseSteps - 1000  // overweight, start with less strain
            bmi >= 30.0 -> baseSteps - 2000  // obese, consider lower impact activities
            else -> baseSteps
        }
    }


    fun checkAndNotifyMilestones(currentSteps: Int, goalSteps: Int, isWeekly: Boolean, context: Context) {
        val milestones = listOf(25, 50, 75, 100) // Percent milestones
        milestones.forEach { percent ->
            val milestoneValue = goalSteps * percent / 100
            if (currentSteps >= milestoneValue && !alreadyNotified(milestoneValue, isWeekly, context)) {
                val title = if (isWeekly) "Weekly Goal Reached" else "Daily Goal Reached"
                val text = "Congratulations! You've reached $percent% of your goal: $currentSteps steps."
                showNotification(title, text, context)
                markAsNotified(milestoneValue, isWeekly, context)
            }
        }
    }


    private fun alreadyNotified(milestone: Int, isWeekly: Boolean, context: Context): Boolean {
        val prefs = context.getSharedPreferences("MilestonePrefs", Context.MODE_PRIVATE)
        return prefs.getBoolean(getKey(milestone, isWeekly), false)
    }

    private fun markAsNotified(milestone: Int, isWeekly: Boolean, context: Context) {
        val prefs = context.getSharedPreferences("MilestonePrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean(getKey(milestone, isWeekly), true)
            apply()
        }
    }


    private fun getKey(milestone: Int, isWeekly: Boolean): String {
        return "notified_${if (isWeekly) "weekly" else "daily"}_$milestone"
    }

    private fun showNotification(title: String, text: String, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Log the lack of permissions
                Log.d("Notification", "POST_NOTIFICATIONS permission is not granted.")
                // Request the permission here

                return
            }
        }

        val notificationManager = NotificationManagerCompat.from(context)
        val notificationId = System.currentTimeMillis().toInt()  // Unique ID for each notification

        val builder = NotificationCompat.Builder(context, "your_channel_id")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationId, builder.build())
        Log.d("Notification", "Notification posted: $title - $text")
    }

     fun checkStepsActivityAndNotify() {
         Log.d("HI", "entered checkStepsActivity")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d("HI", "in build version")
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Log the lack of permissions
                Log.d("HI", "POST_NOTIFICATIONS permission is not granted.")
                // Request the permission here

                return
            } else {
                Log.d("HI", "entered else")
                val currentCalendar = Calendar.getInstance()
                val endOfWeek = currentCalendar.timeInMillis
                val todayStart: Long = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val startOfWeek = endOfWeek - (7 * 24 * 60 * 60 * 1000) // 7 days in milliseconds

                // Get total steps for the current week
                val today = repository.getStepsToday(todayStart)
                Log.d("HI", "Steps today $today")

                // Get total steps for the last week
                val lastWeekSteps = repository.getStepsLastSevenDays(startOfWeek - (7 * 24 * 60 * 60 * 1000), startOfWeek)
                // Calculate average steps for last week
                Log.d("HI", "Steps lastweek  $lastWeekSteps")
                val averageLastWeekSteps = lastWeekSteps / 7
                Log.d("HI", "Steps average  $averageLastWeekSteps")

                // If current week's steps are less than last week's average steps, trigger notification
                if (today < averageLastWeekSteps) {
                    Log.d("HI", "in step comparison")
                    // Trigger notification or perform any action
                    // For simplicity, I'll just print a log message
                    val notificationManager = NotificationManagerCompat.from(context)
                    val notificationId = notificationIdCounter++

                    val builder = NotificationCompat.Builder(context, "your_channel_id")
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("Update")
                        .setContentText("Activity less than average")
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)


                    notificationManager.notify(notificationId, builder.build())
                    Log.d("HI", "Steps activity is less than usual!")
                }
            }
        }
    }
}