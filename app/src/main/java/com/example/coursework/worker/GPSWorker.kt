package com.example.coursework.worker

import android.Manifest
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.workDataOf
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

private const val TAG = "GPSWorker"
private const val GPS_WORKER_OUTPUT_TAG = "GPS"

class GPSWorker (ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    //Location provider object used to get Location from device
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ctx)

    //Method worker uses to work in the background
    override suspend fun doWork(): Result {

        Log.d(TAG, "GPS worker started")
        return withContext(Dispatchers.IO) {
            return@withContext try {
                //Checks for location permission
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

                //Gets permission, waits until it has been fetched
                val task = fusedLocationClient.lastLocation
                while (!task.isSuccessful) {
                    delay(1000)
                }
                //location split to DoubleArray
                //More attributes can be added to array if further data is needed
                var array = DoubleArray(2)
                array[0] = task.result?.latitude ?: -1.0
                array[1] = task.result?.longitude ?: -1.0

                //data passed to repository
                val outputData = workDataOf(GPS_WORKER_OUTPUT_TAG to array)
                Result.success(outputData)
            } catch (throwable: Throwable) {
                Log.e(TAG,("ERROR GETTING GPS LOCATION"),throwable)
                Result.failure()
            }
        }
    }
}