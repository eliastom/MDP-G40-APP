package com.g40.reflectly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.g40.reflectly.viewmodel.AuthResult
import com.g40.reflectly.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel = viewModel(), // Default ViewModel instance
    onNavigate: () -> Unit                  // Callback to go to HomeScreen after success
) {
    // Local state for user input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observe signup state (Loading, Success, Error)
    val state = viewModel.signUpState

    // Navigate when signup is successful
    LaunchedEffect(state) {
        if (state is AuthResult.Success) {
            onNavigate()
            viewModel.resetState() // Reset state so it's clean when coming back
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Email input field
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        // Password input field
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(Modifier.height(16.dp))

        // Button to trigger signup
        Button(onClick = {
            viewModel.signUp(email, password)
        }) {
            Text("Sign Up")
        }

        Spacer(Modifier.height(16.dp))

        // Handle different signup states
        when (state) {
            is AuthResult.Loading -> CircularProgressIndicator() // Show loading spinner
            is AuthResult.Error -> Text("Error: ${state.message}") // Show error message
            else -> {} // No action for success here, as handled by LaunchedEffect
        }
    }
}
