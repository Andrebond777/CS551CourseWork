package com.example.coursework

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.coursework.database.AppDatabase
import com.example.coursework.repository.UserRepository
import com.example.coursework.ui.AppViewModel
import com.example.coursework.ui.theme.CourseWorkTheme
import com.example.coursework.worker.StepWorker

class MainActivity : ComponentActivity() {
    lateinit var appDatabase: AppDatabase // Declare AppDatabase instance
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        val userRepository = UserRepository(this)
        setContent {
            CourseWorkTheme {
                // Pass both the context and the repository to the AppViewModel
                val appViewModel = AppViewModel(this@MainActivity, userRepository)
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppScreen(appViewModel)
                }
            }
        }
        setupStepWorker()
    }

    private fun setupStepWorker() {
        val workRequest = OneTimeWorkRequestBuilder<StepWorker>()
            .build()

        WorkManager.getInstance(this).enqueue(workRequest)

    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("your_channel_id", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


}



