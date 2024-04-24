package com.example.coursework.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val height: String,
    val weight: String,
    val age: String,
    val dateAndTimeAdded: Long = System.currentTimeMillis() // Default to current timestamp
)
