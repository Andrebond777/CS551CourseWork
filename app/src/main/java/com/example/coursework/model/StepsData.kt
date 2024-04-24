package com.example.coursework.model


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "steps")
data class StepsData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val stepCount: Int = 0,
    val dateAndTimeAdded: Long = System.currentTimeMillis()
) {
    constructor(stepCount: Int, previousDateMillis: Long) : this(stepCount = stepCount, dateAndTimeAdded = previousDateMillis)
}