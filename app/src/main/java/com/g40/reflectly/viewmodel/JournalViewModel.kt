package com.g40.reflectly.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g40.reflectly.data.FirestoreJournalRepository
import com.g40.reflectly.data.JournalRepository
import com.g40.reflectly.data.models.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class JournalViewModel(
    private val repository: JournalRepository = FirestoreJournalRepository() // Default repository uses Firestore
) : ViewModel() {

    // Observed text content of the journal
    private val _text = MutableStateFlow("")
    val text: StateFlow<String> = _text

    // Loading state for fetching journal entry
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Error message if input is invalid
    private val _textError = MutableStateFlow<String?>(null)
    val textError: StateFlow<String?> = _textError

    // Internally tracked selected date
    private var selectedDate: String = ""

    // Holds the current saved entry (if exists)
    private var existingEntry: JournalEntry? = null

    // Controls visibility of XP popup
    private val _xpPopupVisible = MutableStateFlow(false)
    val xpPopupVisible: StateFlow<Boolean> = _xpPopupVisible

    // Load a journal entry for a specific date
    fun loadEntry(date: String) {
        selectedDate = date
        _isLoading.value = true
        viewModelScope.launch {
            repository.getJournalEntry(date).collectLatest { entry ->
                existingEntry = entry
                _text.value = entry?.content ?: "" // Set content if exists, else empty
                _isLoading.value = false
            }
        }
    }

    // Update journal text and clear error if text is not blank
    fun onTextChange(newText: String) {
        _text.value = newText
        if (newText.isNotBlank()) _textError.value = null
    }

    // Save the journal entry
    fun saveEntry(
        gamificationViewModel: GamificationViewModel,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            val journalEntry = JournalEntry(
                content = text.value,
                timestamp = System.currentTimeMillis()
            )

            repository.saveJournalEntry(selectedDate, journalEntry)

            // If it's a new journal (not an update), reward XP and streak
            if (existingEntry == null) {
                gamificationViewModel.addXp(15)
                gamificationViewModel.updateStreak()
                _xpPopupVisible.value = true
            }

            existingEntry = journalEntry
            onSaved() // Trigger the navigation back
        }
    }

    // Hide XP popup after it shows
    fun resetXpPopup() {
        _xpPopupVisible.value = false
    }
}
