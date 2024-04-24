package com.example.coursework.worker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.coursework.data.Condition
import com.example.coursework.data.Current
import com.example.coursework.data.Location
import com.example.coursework.data.WeatherApi
import com.example.coursework.data.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "WeatherWatcherWorker"

class WeatherWatcherWorker (ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    //Location provider object used to get Location from device
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)

    //Location provider object used to get Location from device
    val notificationWorker = NotificationWorker()

    //Method worker uses to work in the background
    override suspend fun doWork(): Result {

        Log.d(TAG, "Weather Watcher Worker started")
        return withContext(Dispatchers.IO) {
            return@withContext try {
                if (ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //In case of no permission, worker fails
                    Log.e(TAG,("LOCATION PERMISSIONS NOT GRANTED"))
                    Result.failure()
                }

                var getLocationTask = fusedLocationClient.lastLocation
                while (!getLocationTask.isSuccessful) {
                    delay(1000)
                }

                val location = getLocationTask.result
                Log.d(TAG, (location?.latitude.toString() + "," + location?.longitude.toString()))

                var response: WeatherData = WeatherData(Location("E", "EE","E"),
                    Current(30.0, 5, 5.0, Condition("Windy", "Icon", 1282)))
                val task = launch {
                    try {
                        response = WeatherApi.weatherService.getWeather(
                            apiKey = "c4c9834ef0a048e1a4f45144231605",
                            location = (location?.latitude.toString() + "," + location?.longitude.toString())
                        )

                    } catch (e: Exception) {
                        Log.d(TAG,"Failed to fetch weather data.")
                        Result.failure()
                    }
                }

                task.join()

                //Log.d(TAG, response.current.condition.code.toString())
                //Log.d(TAG, response.location.name)
                if(response.current.condition.code == 1003 || response.current.condition.code == 1000){
                    notificationWorker.triggerNotification(applicationContext, "It's nice outside","Be sure to reach your daily step goal!")
                }

                Result.success()
            } catch (throwable: Throwable) {
                Log.e(TAG,("ERROR GETTING GPS LOCATION"),throwable)
                Result.failure()
            }
        }
    }
}