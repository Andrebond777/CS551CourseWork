package com.example.coursework.worker

import android.app.Activity
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.coursework.data.Condition
import com.example.coursework.data.Current
import com.example.coursework.data.Location
import com.example.coursework.data.WeatherData
import java.text.SimpleDateFormat
import java.util.Date

class ScheduleManager(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        // Fetch the current weather in Glasgow
//        val weatherData = getWeatherData()

        // Check if the weather has changed and send a notification if needed
        sendNotificationIfWeatherChanged()

        // Return the appropriate result
        return Result.success()
    }

    private suspend fun getWeatherData(): WeatherData {
        // Implement logic to fetch the current weather data
        // This can be done using a weather API or any other data source
        return WeatherData(
            location = Location(
                name = "Glasgow",
                region = "UK",
                country = "UK1"
            ),
            current = Current(
                temp_c = 12.1,
                humidity = 123,
                wind_kph = 123.0,
                condition = Condition(
                    text = "Sunny",
                    icon = "",
                    code = 123
                )
            )
        )
    }

        private fun sendNotificationIfWeatherChanged() {
        // Check if the weather has changed compared to the previous state
        // and send a notification if needed
        // You can use a local database or shared preferences to keep track of the previous weather state

        // Example notification code:
//        val notificationManager =
//            ContextCompat.getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager
//
//        val notification = NotificationCompat.Builder(applicationContext, "weather_updates")
//            .setContentTitle("Weather Update")
//            .setContentText("The weather in Glasgow has changed.")
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setPriority(NotificationManager.IMPORTANCE_HIGH)
//            .build()
//
//        notificationManager.notify(0, notification)

            // date time
            val sdf = SimpleDateFormat("HH:mm:ss")

            // on below line we are creating a variable for
            // current date and time and calling a simple
            // date format in it.
            val currentDateAndTime = sdf.format(Date())

            // Call function from NotificationWorker and trigger
            val noti = NotificationWorker()

            noti.triggerNotification(applicationContext as Activity, applicationContext, "Work Manager works", "Time now ${currentDateAndTime}")
    }
}