package com.example.coursework.repository


import com.example.coursework.database.StepDataDao
import com.example.coursework.model.StepsData
import kotlinx.coroutines.flow.Flow

class StepsRepository(private val stepDataDao: StepDataDao) {

    val allStepsData: Flow<List<StepsData>> = stepDataDao.getAllStepData()

    suspend fun addStep(stepData: StepsData) {
        stepDataDao.addSteps(stepData)
    }

    suspend fun updateStep(stepData: StepsData) {
        stepDataDao.updateUserStepData(stepData)
    }

    suspend fun deleteStep(id: Int) {
        stepDataDao.deleteUserStepData(id)
    }

    fun getTodaySteps(todayStart: Long): Int {
        return stepDataDao.getStepsToday(todayStart)
    }

    fun getWeekSteps(sevenDaysAgo: Long, todayEnd: Long): Int {
        return stepDataDao.getStepsLastSevenDays(sevenDaysAgo, todayEnd)
    }
}