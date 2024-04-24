package com.example.coursework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.coursework.model.StepsData
import kotlinx.coroutines.flow.Flow

@Dao
interface StepDataDao {
    @Query("SELECT * FROM steps")
    fun getAllStepData(): Flow<List<StepsData>>

    @Update
    suspend fun updateUserStepData(setps: StepsData)

    @Query("DELETE FROM steps WHERE id = :userId")
    suspend fun deleteUserStepData(userId: Int)

    @Query("SELECT SUM(stepCount) FROM steps WHERE dateAndTimeAdded >= :todayStart")
    fun getStepsToday(todayStart: Long): Int

    @Query("SELECT SUM(stepCount) FROM steps WHERE dateAndTimeAdded >= :sevenDaysAgo AND dateAndTimeAdded <= :todayEnd")
    fun getStepsLastSevenDays(sevenDaysAgo: Long, todayEnd: Long): Int

    @Insert
    suspend fun addSteps(stepsData: StepsData)
}