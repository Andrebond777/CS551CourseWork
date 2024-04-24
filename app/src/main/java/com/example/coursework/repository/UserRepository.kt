package com.example.coursework.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.asFlow
import androidx.room.Room
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.coursework.database.AppDatabase
import com.example.coursework.model.StepsData
import com.example.coursework.model.UserData
import com.example.coursework.worker.GPSWorker
import com.example.coursework.worker.StepWorker
import com.example.coursework.worker.WeatherWatcherWorker
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

    //Workmanager used to run workers
    private val workManager = WorkManager.getInstance(context)

    //variable that keeps the output of the GPSworker
    var outputWorkInfo = workManager.getWorkInfosByTagLiveData(OUTPUT_TAG).asFlow().mapNotNull {
        if (it.isNotEmpty()) it.first() else null
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

    fun getStepsEveryDayLastSevenDays(sevenDaysAgo: Long, todayEnd: Long): List<Int> {
        return stepDao.getStepsEveryDayLastSevenDays(sevenDaysAgo, todayEnd)
    }

    suspend fun addSteps(stepsData: StepsData) {
        stepDao.addSteps(stepsData)
    }

    //Runs the GPS Worker
    fun runGPSWorker() {
        val gpsBuilder = OneTimeWorkRequestBuilder<GPSWorker>()
            .addTag(OUTPUT_TAG)

        // Start the work
        workManager.enqueue(gpsBuilder.build())
    }

    fun runWeatherWatcherWorker(){
        val currentTime = Calendar.getInstance().timeInMillis % 86400000
        val delay = ((86400000 - currentTime) + 28800000)
        //Log.d("TESTING1", currentTime.toString())
        //Log.d("TESTING2", delay.toString())

        val weatherWatcherBuilder = PeriodicWorkRequestBuilder<StepWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork("StepTracking", ExistingPeriodicWorkPolicy.KEEP, weatherWatcherBuilder)
    }

    fun testWeatherWatcherWorker() {
        val gpsBuilder = OneTimeWorkRequestBuilder<WeatherWatcherWorker>()

        // Start the work
        workManager.enqueue(gpsBuilder.build())
    }

    fun scheduleStepTracking() {
        val workRequest = PeriodicWorkRequestBuilder<StepWorker>(15, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork("StepTracking", ExistingPeriodicWorkPolicy.KEEP, workRequest)
    }


}

