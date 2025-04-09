package com.g40.reflectly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {

    // UI state for signup process (Idle, Loading, Success, Error)
    var signUpState by mutableStateOf<AuthResult>(AuthResult.Idle)
        private set // restrict external modification

    // Function to handle user sign-up using Firebase
    fun signUp(email: String, password: String) {
        signUpState = AuthResult.Loading // set state to loading

        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                // Update state based on success or failure
                signUpState = if (task.isSuccessful) {
                    AuthResult.Success
                } else {
                    AuthResult.Error(task.exception?.message ?: "Unknown error")
                }
            }
    }

    // Resets the signup state to Idle (e.g., after navigating away)
    fun resetState() {
        signUpState = AuthResult.Idle
    }
}

// Represents the different states of the sign-up flow
sealed class AuthResult {
    object Idle : AuthResult()                  // Nothing happening yet
    object Loading : AuthResult()               // Sign-up in progress
    object Success : AuthResult()               // Sign-up succeeded
    data class Error(val message: String) : AuthResult() // Sign-up failed with error message
}
