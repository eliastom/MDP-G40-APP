package com.g40.reflectly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    // UI state for authentication actions
    enum class AuthAction { SIGN_UP, LOG_IN, NONE }

    var currentAction by mutableStateOf(AuthAction.NONE)
        private set

    var authState by mutableStateOf<AuthResult>(AuthResult.Idle)
        private set

    // --- Authentication Logic ---

    // Handles user sign-up with Firebase
    fun signUp(email: String, password: String) {
        currentAction = AuthAction.SIGN_UP
        authState = AuthResult.Loading

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                authState = if (task.isSuccessful) {
                    AuthResult.Success
                } else {
                    AuthResult.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    // Handles user log-in with Firebase
    fun logIn(email: String, password: String) {
        currentAction = AuthAction.LOG_IN
        authState = AuthResult.Loading

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                authState = if (task.isSuccessful) {
                    AuthResult.Success
                } else {
                    AuthResult.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    // Handles user log-out
    fun logOut(onLogOut: () -> Unit) {
        FirebaseAuth.getInstance().signOut()
        onLogOut()
    }

    // Resets authentication state back to Idle
    fun resetState() {
        authState = AuthResult.Idle
    }
}

// --- Authentication Result Sealed Class ---
// Represents the different states during authentication flow
sealed class AuthResult {
    object Idle : AuthResult()                // Nothing happening
    object Loading : AuthResult()             // Authentication in progress
    object Success : AuthResult()             // Authentication succeeded
    data class Error(val message: String) : AuthResult() // Authentication failed
}
