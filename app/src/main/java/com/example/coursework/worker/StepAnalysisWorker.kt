package com.example.coursework.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.coursework.repository.UserRepository
import com.example.coursework.ui.AppViewModel

class StepAnalysisWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val userRepository = UserRepository(applicationContext)
        val viewModel = AppViewModel(applicationContext, userRepository)
        viewModel.checkStepsActivityAndNotify()
        return Result.success()
    }
}