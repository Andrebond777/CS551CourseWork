package com.example.coursework.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "newWaterTable")
data class NewWaterData (
    @PrimaryKey(autoGenerate = true)
    val idx: Int,
    val isTrigger: Int,
    val dateTrigger: String
)