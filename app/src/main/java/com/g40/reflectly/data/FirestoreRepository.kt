package com.g40.reflectly.data.firestore

import android.util.Log
import com.g40.reflectly.data.JournalRepository
import com.g40.reflectly.data.models.JournalEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreJournalRepository : JournalRepository {

    companion object {
        private const val USERS = "users"
        private const val JOURNALS = "journals"
        private const val TAG = "FirestoreRepo"
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override suspend fun saveJournalEntry(id: String, entry: JournalEntry) {
        val uid = auth.currentUser?.uid ?: return
        try {
            firestore.collection(USERS)
                .document(uid)
                .collection(JOURNALS)
                .document(id)
                .set(entry)
                .await()
            Log.d(TAG, "Saved journal [$id]")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving journal: ${e.localizedMessage}")
        }
    }

    override fun getJournalEntry(id: String): Flow<JournalEntry?> = flow {
        val uid = auth.currentUser?.uid ?: return@flow
        try {
            val snapshot = firestore.collection(USERS)
                .document(uid)
                .collection(JOURNALS)
                .document(id)
                .get()
                .await()
            emit(snapshot.toObject(JournalEntry::class.java))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching journal: ${e.localizedMessage}")
            emit(null)
        }
    }
}
