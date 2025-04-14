package com.g40.reflectly.data.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    // Returns today's date as "yyyy-MM-dd" (good for database keys)
    fun getTodayRaw(): String {
        return LocalDate.now().toString() // e.g., "2025-04-14"
    }

    // Returns today's date as "Monday, April 14"
    fun getTodayFormatted(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
        return today.format(formatter)
    }

    // Format any given date (from Firestore or input) like "Monday, April 14"
    fun formatDate(dateString: String): String {
        val date = LocalDate.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
        return date.format(formatter)
    }
}
