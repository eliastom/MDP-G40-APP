package com.g40.reflectly.data

import com.g40.reflectly.data.models.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    suspend fun saveJournalEntry(id: String, entry: JournalEntry)
    fun getJournalEntry(id: String): Flow<JournalEntry?>
}
