package com.g40.reflectly.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g40.reflectly.data.TaskRepository
import com.g40.reflectly.data.FirestoreTaskRepository
import com.g40.reflectly.data.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository = FirestoreTaskRepository() // Default to Firestore-based repository
) : ViewModel() {

    // Stores tasks grouped by date
    private val _tasksByDate = mutableMapOf<String, MutableStateFlow<List<Task>>>()

    // Expose tasks for a specific date
    fun getTasksForDate(date: String): StateFlow<List<Task>> {
        return _tasksByDate.getOrPut(date) { MutableStateFlow(emptyList()) }
    }

    // Load tasks from repository for a specific date
    fun loadTasksForDate(date: String) {
        Log.d("TaskViewModel", "Loading tasks for date: $date")
        viewModelScope.launch {
            repository.loadTasksForDate(date).collectLatest { tasks ->
                Log.d("TaskViewModel", "Loaded ${tasks.size} tasks for $date")
                val taskFlow = _tasksByDate.getOrPut(date) { MutableStateFlow(emptyList()) }
                taskFlow.value = tasks
            }
        }
    }

    // Update the 'done' status of a specific task
    fun updateTaskDoneStatus(taskId: String, isDone: Boolean) {
        viewModelScope.launch {
            repository.updateTaskDoneStatus(taskId, isDone)

            // Reflect the updated task locally in all cached dates
            _tasksByDate.forEach { (date, taskFlow) ->
                val updatedTasks = taskFlow.value.map { task ->
                    if (task.id == taskId) {
                        task.copy(completed = isDone)
                    } else {
                        task
                    }
                }
                taskFlow.value = updatedTasks
            }
        }
    }

    // Find a task by ID across all dates (useful for navigation to detail screens)
    fun getTaskById(taskId: String): Task? {
        return _tasksByDate.values
            .flatMap { it.value }
            .find { it.id == taskId }
    }
}
