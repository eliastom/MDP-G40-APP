package com.g40.reflectly.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.g40.reflectly.viewmodel.AuthResult
import com.g40.reflectly.viewmodel.AuthViewModel

@Composable
fun LogInScreen (
    viewModel: AuthViewModel = viewModel(),
    onLogIn: () -> Unit,
    onBack: () -> Unit
) {
    LogInScreenContent(viewModel,onLogIn,onBack)
}

@Composable
private fun LogInScreenContent (
    viewModel: AuthViewModel = viewModel(),
    onLogIn: () -> Unit,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state = viewModel.authState

    LaunchedEffect(state) {
        if (state is AuthResult.Success) {
            onLogIn()
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

        Button(onClick = onBack) {
            Text("Back")
        }

        // Button to trigger signup
        Button(onClick = {
            viewModel.logIn(email, password)
        }) {
            Text("Log In")
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
