package com.g40.reflectly.data.firestore

import android.util.Log
import com.g40.reflectly.data.JournalRepository
import com.g40.reflectly.data.TaskRepository
import com.g40.reflectly.data.models.JournalEntry
import com.g40.reflectly.data.models.Task
import com.g40.reflectly.data.utils.DateUtils
import com.g40.reflectly.utils.getCurrentUserId
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

    override suspend fun saveJournalEntry(id: String, entry: JournalEntry) {
        val uid = getCurrentUserId() ?: return
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
        val uid = getCurrentUserId() ?: return@flow
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

class FirestoreTaskRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : TaskRepository {

    companion object {
        private const val USERS = "users"
        private const val TASKS = "tasks"
        private const val TAG = "FirestoreTaskRepo"
    }

    override suspend fun addTask(task: Task) {
        val uid = getCurrentUserId() ?: return
        try {
            firestore.collection(USERS)
                .document(uid)
                .collection(TASKS)
                .add(task)
                .await()
            Log.d(TAG, "Task added: ${task.title}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add task: ${e.localizedMessage}")
        }
    }

    override fun getTasksForToday(): Flow<List<Task>> = flow {
        val uid = getCurrentUserId() ?: return@flow
        val today = DateUtils.getTodayRaw()

        try {
            val snapshot = firestore.collection(USERS)
                .document(uid)
                .collection(TASKS)
                .whereEqualTo("date", today)
                .get()
                .await()

            val tasks = snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
            emit(tasks)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading tasks: ${e.localizedMessage}")
            emit(emptyList())
        }
    }
}