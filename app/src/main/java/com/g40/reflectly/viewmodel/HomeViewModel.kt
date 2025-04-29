package com.g40.reflectly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g40.reflectly.data.TaskRepository
import com.g40.reflectly.data.FirestoreTaskRepository
import com.g40.reflectly.data.models.Task
import com.g40.reflectly.data.utils.extractTaskDates
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth

class HomeViewModel(
    private val repository: TaskRepository = FirestoreTaskRepository() // Using Firestore as default repo implementation
) : ViewModel() {

    // Holds all tasks for the current, previous, and next month
    private val _monthlyTasks = MutableStateFlow<List<Task>>(emptyList())
    val monthlyTasks: StateFlow<List<Task>> = _monthlyTasks

    // Tracks the center month (used to fetch surrounding data)
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    // Holds all task dates to render dots/highlights on the calendar
    private val _allTaskDates = MutableStateFlow<List<String>>(emptyList())
    val allTaskDates: StateFlow<List<String>> = _allTaskDates

    // Loads tasks from the previous, current, and next month
    fun loadTasksForSurroundingMonths(centerMonth: YearMonth) {
        _currentMonth.value = centerMonth

        val months = listOf(
            centerMonth.minusMonths(1),
            centerMonth,
            centerMonth.plusMonths(1)
        )

        viewModelScope.launch {
            // Flatten results from all three months
            val allTasks = months.flatMap { month ->
                val start = month.atDay(1).toString()
                val end = month.atEndOfMonth().toString()
                repository.getTasksBetween(start, end) // Get tasks for month range
            }

            _monthlyTasks.value = allTasks

            // Extract unique dates to help mark calendar dots
            val taskDates = extractTaskDates(allTasks).map { it.toString() }.distinct()
            _allTaskDates.value = taskDates
        }
    }

    // Adds temporary dummy tasks for testing
    fun addDummyTasks(date: String) {
        viewModelScope.launch {
            val dummyTasks = listOf(
                Task(
                    title = "Test Task 1",
                    date = date,
                    completed = false,
                    time = "09:00"
                ),
                Task(
                    title = "Test Task 2",
                    date = date,
                    completed = false,
                    time = "14:00"
                ),
                Task(
                    title = "Test Task 3",
                    date = date,
                    completed = true,
                    time = "18:00"
                ),
                Task(
                    title = "Test Task 4",
                    date = date,
                    completed = false,
                    time = "20:00"
                )
            )

            // Save each dummy task to the repository
            dummyTasks.forEach { task ->
                repository.addTask(task)
            }
        }
    }
}
