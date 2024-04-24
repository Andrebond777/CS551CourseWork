package com.example.coursework.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar

@Entity(tableName = "water")
data class WaterData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val waterNotificationGiven: Int = 0,
    val dateAndTimeOfNotification: Long = System.currentTimeMillis()
) {
    fun isNotificationOnSameDay(): Boolean {
        val notificationCalendar = Calendar.getInstance()
        notificationCalendar.timeInMillis = dateAndTimeOfNotification

        val currentCalendar = Calendar.getInstance()

        return notificationCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                notificationCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)
    }
}