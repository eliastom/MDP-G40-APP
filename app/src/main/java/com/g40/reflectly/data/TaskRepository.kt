package com.g40.reflectly.data

import com.g40.reflectly.data.models.Task
import kotlinx.coroutines.flow.Flow

// Interface defining methods to interact with tasks
interface TaskRepository {

    // Add a new task to the repository (Firestore or local DB)
    suspend fun addTask(task: Task)

    // Load tasks for a specific date, returns a Flow of task list
    fun loadTasksForDate(date: String): Flow<List<Task>>

    // Update the "done" status of a task (completed / not completed)
    suspend fun updateTaskDoneStatus(taskId: String, completed: Boolean)

    // Update the entire task document (e.g., editing task details)
    suspend fun updateTask(task: Task)

    // Delete a task by its unique ID
    suspend fun deleteTask(taskId: String)

    // Retrieve tasks that fall between two dates
    suspend fun getTasksBetween(start: String, end: String): List<Task>
}
