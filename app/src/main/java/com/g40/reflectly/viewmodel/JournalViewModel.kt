package com.g40.reflectly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g40.reflectly.data.JournalRepository
import com.g40.reflectly.data.firestore.FirestoreJournalRepository
import com.g40.reflectly.data.models.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class JournalViewModel(
    private val repository: JournalRepository = FirestoreJournalRepository() // Now using interface!
) : ViewModel() {

    private val _entry = MutableStateFlow<JournalEntry?>(null)
    val entry: StateFlow<JournalEntry?> = _entry

    fun loadEntry(date: String) {
        viewModelScope.launch {
            repository.getJournalEntry(date).collectLatest {
                _entry.value = it
            }
        }
    }

    fun saveEntry(date: String, content: String) {
        val journal = JournalEntry(
            content = content,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.saveJournalEntry(date, journal)
            _entry.value = journal
        }
    }

    fun clearEntry() {
        _entry.value = null
    }
}
