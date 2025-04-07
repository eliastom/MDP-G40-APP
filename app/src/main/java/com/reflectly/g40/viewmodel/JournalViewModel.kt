package com.reflectly.g40.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.reflectly.g40.model.JournalEntry
import java.util.*

class JournalViewModel : ViewModel() {
    private val _entries = mutableStateListOf<JournalEntry>()
    val entries: List<JournalEntry> get() = _entries

    fun addEntry(content: String) {
        val entry = JournalEntry(
            id = UUID.randomUUID().toString(),
            content = content,
            timestamp = System.currentTimeMillis()
        )
        _entries.add(entry)
    }
}