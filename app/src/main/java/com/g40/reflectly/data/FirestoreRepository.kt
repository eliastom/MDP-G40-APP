package com.g40.reflectly.data

import android.util.Log
import com.g40.reflectly.data.models.GamificationState
import com.g40.reflectly.data.models.JournalEntry
import com.g40.reflectly.data.models.Task

import com.g40.reflectly.data.utils.DateUtils
import com.g40.reflectly.utils.getCurrentUserId
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirestoreJournalRepository : JournalRepository {

    companion object {
        private const val USERS = "users"
        private const val JOURNALS = "journals"
        private const val TAG = "FirestoreJournalRepo"
    }

    private val firestore = FirebaseFirestore.getInstance()

    // Save or overwrite a journal entry for the given date
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

    // Load a journal entry by date
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

    // Add a new task (Firestore will generate a unique ID)
    override suspend fun addTask(task: Task) {
        val uid = getCurrentUserId() ?: return
        try {
            val docRef = firestore.collection(USERS)
                .document(uid)
                .collection(TASKS)
                .document()

            val taskWithId = task.copy(id = docRef.id)
            docRef.set(taskWithId).await()
            Log.d(TAG, "Task added with ID: ${docRef.id}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add task: ${e.localizedMessage}")
        }
    }

    // Load all tasks for a given date
    override fun loadTasksForDate(date: String): Flow<List<Task>> = flow {
        val uid = getCurrentUserId() ?: return@flow
        try {
            val snapshot = firestore.collection(USERS)
                .document(uid)
                .collection(TASKS)
                .whereEqualTo("date", date)
                .get()
                .await()

            val tasks = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Task::class.java)?.copy(id = doc.id)
            }

            Log.d(TAG, "Successfully fetched ${tasks.size} tasks for date: $date")
            emit(tasks)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading tasks for $date: ${e.localizedMessage}")
            emit(emptyList())
        }
    }

    // Update only the completed status of a task
    override suspend fun updateTaskDoneStatus(taskId: String, completed: Boolean) {
        val uid = getCurrentUserId() ?: return
        try {
            firestore.collection(USERS)
                .document(uid)
                .collection(TASKS)
                .document(taskId)
                .update("completed", completed)
                .await()
            Log.d(TAG, "Task [$taskId] updated with completed = $completed")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update task [$taskId]: ${e.localizedMessage}")
        }
    }

    // Overwrite entire task document
    override suspend fun updateTask(task: Task) {
        val uid = getCurrentUserId() ?: return
        try {
            firestore.collection(USERS)
                .document(uid)
                .collection(TASKS)
                .document(task.id)
                .set(task)
                .await()
            Log.d(TAG, "Task [${task.id}] fully updated")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fully update task [${task.id}]: ${e.localizedMessage}")
        }
    }

    // Delete a task by ID
    override suspend fun deleteTask(taskId: String) {
        val uid = getCurrentUserId() ?: return
        try {
            firestore.collection(USERS)
                .document(uid)
                .collection(TASKS)
                .document(taskId)
                .delete()
                .await()
            Log.d(TAG, "Task [$taskId] deleted")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete task [$taskId]: ${e.localizedMessage}")
        }
    }

    // Fetch all tasks between two dates (used for calendar dot logic)
    override suspend fun getTasksBetween(start: String, end: String): List<Task> {
        val uid = getCurrentUserId() ?: return emptyList()
        return try {
            val snapshot = firestore.collection(USERS)
                .document(uid)
                .collection(TASKS)
                .whereGreaterThanOrEqualTo("date", start)
                .whereLessThanOrEqualTo("date", end)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Task::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting tasks between $start and $end: ${e.localizedMessage}")
            emptyList()
        }
    }
}
class FirestoreGamificationRepository : GamificationRepository {

    companion object {
        private const val USERS = "users"
        private const val GAMIFICATION = "gamification"
        private const val TAG = "FirestoreGamificationRepo"
    }

    private val firestore = FirebaseFirestore.getInstance()

    // Save user's XP, level, and streak state
    override suspend fun saveGamificationState(state: GamificationState) {
        val uid = getCurrentUserId() ?: return
        try {
            firestore.collection(USERS)
                .document(uid)
                .collection(GAMIFICATION)
                .document("state")
                .set(state)
                .await()
            Log.d(TAG, "Gamification state saved for user [$uid]")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save gamification state: ${e.localizedMessage}")
        }
    }

    // Load gamification state for user
    override fun loadGamificationState(): Flow<GamificationState?> = flow {
        val uid = getCurrentUserId() ?: return@flow
        try {
            val snapshot = firestore.collection(USERS)
                .document(uid)
                .collection(GAMIFICATION)
                .document("state")
                .get()
                .await()

            val state = snapshot.toObject(GamificationState::class.java)
            Log.d(TAG, "Gamification state loaded for user [$uid]")
            emit(state)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load gamification state: ${e.localizedMessage}")
            emit(null)
        }
    }
}
