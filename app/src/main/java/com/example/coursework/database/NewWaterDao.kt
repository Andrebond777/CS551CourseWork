package com.example.coursework.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.coursework.model.NewWaterData
import kotlinx.coroutines.flow.Flow

@Dao
interface NewWaterDao {
    @Upsert
    suspend fun upsertWaterTrigger(newWaterData: NewWaterData)

    @Query("SELECT * FROM newWaterTable")
    fun getWaterData(): Flow<List<NewWaterData>>
}