package com.example.coursework.repository

import android.content.Context
import androidx.lifecycle.asFlow
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.coursework.database.AppDatabase
import com.example.coursework.model.StepsData
import com.example.coursework.model.UserData
import com.example.coursework.model.WaterData
import com.example.coursework.worker.GPSWorker
import com.example.coursework.worker.StepWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import java.util.Calendar
import java.util.concurrent.TimeUnit

const val OUTPUT_TAG = "OUTPUT"
class UserRepository(context: Context) {


    // Initialize the Room Database instance
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "user-database"
    ).build()

    // Get the User DAO
    private val userDao = db.userDao()

    private val stepDao = db.stepDao()

    private val waterDao = db.waterDao()

    //Workmanager used to run workers
    private val workManager = WorkManager.getInstance(context)

    //variable that keeps the output of the GPSworker
    var outputWorkInfo = workManager.getWorkInfosByTagLiveData(OUTPUT_TAG).asFlow().mapNotNull {
        if (it.isNotEmpty()) it.first() else null
    }

    private fun isSameDay(date1: Long, date2: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = date1 }
        val cal2 = Calendar.getInstance().apply { timeInMillis = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    // Function to get all users
    fun getAllUserData(): Flow<List<UserData>> {
        return userDao.getAllUserData()
    }

    // Function to insert a user
    suspend fun insertUserData(user: UserData) {
        userDao.insertUserData(user)
    }

    // Function to update a user
    suspend fun updateUserData(user: UserData) {
        userDao.updateUserData(user)
    }

    // Function to delete a user
    suspend fun deleteUserData(user: UserData) {
        userDao.deleteUserData(user.id)
    }

    // get steps today


    fun getStepsToday(todayStart: Long): Int{
        return stepDao.getStepsToday(todayStart)
    }

    fun getStepsLastSevenDays(sevenDaysAgo: Long, todayEnd: Long): Int {
        return stepDao.getStepsLastSevenDays(sevenDaysAgo, todayEnd)
    }

    suspend fun addSteps(stepsData: StepsData) {
        stepDao.addSteps(stepsData)
    }

    // water
    fun getLastWaterData(): WaterData {
        return waterDao.getLastWaterData()
    }

    fun getLastWaterDataSameDate(): Int {
        val lastWaterData = getLastWaterData()
        if (lastWaterData != null) {
            val currentDate = Calendar.getInstance().apply {
                // Reset hours, minutes, seconds, and milliseconds to 0 for comparison
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // if data exists for the same date
            if (isSameDay(lastWaterData.dateAndTimeOfNotification, currentDate)) {
                return lastWaterData.waterNotificationGiven
            }
        }
        return -1
    }

    // get last water data, check if the date is same and the value if 1 (true)
    // if the value is true, return 1
    // if not true, set true and return 1
    // if record does not exists, create a record and set value to true and return 1
    // sql error return -1
    suspend fun setLastWaterDataToOne() {
        val lastWaterData = getLastWaterData()
        // if no record exists with the current date
        // create a new record and set to 0
        if (lastWaterData != null) {
            val currentDate = Calendar.getInstance().apply {
                // Reset hours, minutes, seconds, and milliseconds to 0 for comparison
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            // if data exists for the same date
            if (isSameDay(lastWaterData.dateAndTimeOfNotification, currentDate)) {
                // if the last record is 0
                if (lastWaterData.dateAndTimeOfNotification.equals(0)) {
                    // change record to 1
                    waterDao.updateWaterGiven(
                        WaterData(
                            lastWaterData.id,
                            1,
                            lastWaterData.dateAndTimeOfNotification
                        )
                    )
                }
            }
        } else {
            waterDao.insertWaterData(WaterData(waterNotificationGiven = 1))
        }
    }

    //Runs the GPS Worker
    fun getGPSLocation() {
        val gpsBuilder = OneTimeWorkRequestBuilder<GPSWorker>()
            .addTag(OUTPUT_TAG)

        // Start the work
        workManager.enqueue(gpsBuilder.build())
    }

    fun scheduleStepTracking() {
        val workRequest = PeriodicWorkRequestBuilder<StepWorker>(15, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork("StepTracking", ExistingPeriodicWorkPolicy.KEEP, workRequest)
    }


}

