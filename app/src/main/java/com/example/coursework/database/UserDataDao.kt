package com.example.coursework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.coursework.model.UserData
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDataDao {
    @Query("SELECT * FROM user")
    fun getAllUserData(): Flow<List<UserData>>

    @Insert
    suspend fun insertUserData(user: UserData)

    @Update
    suspend fun updateUserData(user: UserData)

    @Query("DELETE FROM user WHERE id = :userId")
    suspend fun deleteUserData(userId: Int)
}
