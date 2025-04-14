package com.g40.reflectly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g40.reflectly.data.TaskRepository
import com.g40.reflectly.data.firestore.FirestoreTaskRepository
import com.g40.reflectly.data.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository = FirestoreTaskRepository() // Same as JournalViewModel setup
) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    fun loadTodayTasks() {
        viewModelScope.launch {
            repository.getTasksForToday().collectLatest {
                _tasks.value = it
            }
        }
    }

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTask(task)
            loadTodayTasks() // refresh after adding
        }
    }

    fun clearTasks() {
        _tasks.value = emptyList()
    }
}
