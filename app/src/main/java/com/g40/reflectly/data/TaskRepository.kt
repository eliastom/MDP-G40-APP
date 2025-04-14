package com.g40.reflectly.data

import com.g40.reflectly.data.models.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun addTask(task: Task)
    fun getTasksForToday(): Flow<List<Task>>
}