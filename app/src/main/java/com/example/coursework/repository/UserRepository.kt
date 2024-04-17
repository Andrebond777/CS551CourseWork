package com.example.healthapproomdb.repository

import android.content.Context
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import com.example.coursework.database.AppDatabase
import com.example.healthapproomdb.model.UserData
import com.example.healthapproomdb.model.StepsData
import kotlinx.coroutines.flow.Flow

class UserRepository(context: Context) {

    // Initialize the Room Database instance
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "user-database"
    ).build()

    // Get the User DAO
    private val userDao = db.userDao()

    private val stepDao = db.stepDao()

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
}

