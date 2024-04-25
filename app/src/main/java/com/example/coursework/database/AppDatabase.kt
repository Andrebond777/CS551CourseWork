package com.example.coursework.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.coursework.model.NewWaterData
import com.example.coursework.model.StepsData
import com.example.coursework.model.UserData
import com.example.coursework.model.WaterData

@Database(entities = [UserData::class, StepsData::class, WaterData::class, NewWaterData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDataDao
    abstract fun stepDao(): StepDataDao

    abstract fun waterDao(): WaterDataDao

    abstract fun newWaterDao(): NewWaterDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app-database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}