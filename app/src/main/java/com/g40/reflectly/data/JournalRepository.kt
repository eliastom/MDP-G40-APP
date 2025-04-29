package com.g40.reflectly.data

import com.g40.reflectly.data.models.JournalEntry
import kotlinx.coroutines.flow.Flow

// Contract for reading and writing journal entries
interface JournalRepository {

    // Save or update a journal entry for a specific date (ID = date string)
    suspend fun saveJournalEntry(id: String, entry: JournalEntry)

    // Load a journal entry by ID (date); emits null if none exists
    fun getJournalEntry(id: String): Flow<JournalEntry?>
}
