package com.example.coursework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.coursework.model.StepsData
import com.example.coursework.model.WaterData
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDataDao {
    @Query("SELECT * FROM water ORDER BY dateAndTimeOfNotification DESC LIMIT 1")
    fun getLastWaterData(): WaterData

    @Insert
    suspend fun insertWaterData(waterData: WaterData)

    @Update
    suspend fun updateWaterGiven(waterData: WaterData)

}