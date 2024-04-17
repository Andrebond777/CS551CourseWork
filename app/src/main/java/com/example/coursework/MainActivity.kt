package com.example.coursework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.coursework.database.AppDatabase
import com.example.coursework.ui.AppViewModel
import com.example.coursework.ui.theme.CourseWorkTheme
import com.example.healthapproomdb.repository.UserRepository

class MainActivity : ComponentActivity() {
    lateinit var appDatabase: AppDatabase // Declare AppDatabase instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userRepository = UserRepository(this)
        setContent {
            CourseWorkTheme {
                val appViewModel = AppViewModel(userRepository)
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreen(appViewModel)
                }
            }
        }
    }
}



