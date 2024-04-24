package com.example.coursework.worker

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.coursework.database.AppDatabase
import com.example.coursework.model.StepsData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StepWorker(appContext: Context, workerParams: WorkerParameters)
    : CoroutineWorker(appContext, workerParams), SensorEventListener {

    private var sensorManager: SensorManager = appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var stepsCount = 0

    override suspend fun doWork(): Result {
        // Register the sensor listener
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)

        // Wait to collect some step data
        try {
            Thread.sleep(10000)  // Sleep for 10 seconds
        } catch (e: InterruptedException) {
            return Result.failure()
        }

        // Unregister the listener to conserve battery
        sensorManager.unregisterListener(this)

        // Save the steps data into the database
        if (stepsCount > 0) {
            val stepsData = StepsData(stepCount = stepsCount)
            withContext(Dispatchers.IO) {
                AppDatabase.getInstance(applicationContext).stepDao().addSteps(stepsData)
            }
        }

        return Result.success()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            stepsCount = event.values[0].toInt()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Can be implemented if needed
    }
}
