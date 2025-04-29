package com.g40.reflectly.data.utils

import com.g40.reflectly.data.models.Task
import java.time.LocalDate

fun extractTaskDates(tasks: List<Task>): List<LocalDate> {
    return tasks.mapNotNull { task ->
        try {
            LocalDate.parse(task.date) // must be in "yyyy-MM-dd" format
        } catch (e: Exception) {
            null
        }
    }.distinct()
}
