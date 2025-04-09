package com.g40.reflectly.data.firestore

import com.g40.reflectly.data.models.JournalEntry
import com.g40.reflectly.utils.getCurrentUserId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Save a journal entry to Firestore
    suspend fun saveJournalEntry(id: String, entry: JournalEntry) {
        val uid = getCurrentUserId() ?: return
        firestore.collection("users")
            .document(uid)
            .collection("journals")
            .document(id)
            .set(entry)
            .await()
    }


    // Get a journal entry by ID (usually the date)
    fun getJournalEntry(id: String): Flow<JournalEntry?> = flow {
        val uid = getCurrentUserId() ?: return@flow // Check if user is logged in

        val snapshot = firestore.collection("users")
            .document(uid)
            .collection("journals")
            .document(id)
            .get()
            .await() // Suspend until Firestore returns the result

        val entry = snapshot.toObject(JournalEntry::class.java) // Convert document to model
        emit(entry) // Emit to be collected in ViewModel
    }
}
