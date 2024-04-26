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

    @Query("SELECT * FROM newWaterTable LIMIT 1")
    fun getWaterData(): NewWaterData

    @Query("SELECT EXISTS(SELECT 1 FROM newWaterTable where idx = 1)")
    fun newWaterDataExists(): Boolean
}