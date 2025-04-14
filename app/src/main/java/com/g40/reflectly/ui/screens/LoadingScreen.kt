package com.g40.reflectly.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onReady: (Boolean) -> Unit // Callback to notify if user is logged in
) {
    LoadingScreenContent(onReady)
}

@Composable
private fun LoadingScreenContent(
    onReady: (Boolean) -> Unit
) {
    val context = LocalContext.current // Get the current context (needed for Firebase check)

    // Side-effect that runs once when the composable enters composition
    LaunchedEffect(Unit) {
        delay(300) // Optional short delay to let things settle (UI or Firebase)

        // Check if Firebase is initialized
        val isFirebaseInitialized = FirebaseApp.getApps(context).isNotEmpty()

        if (isFirebaseInitialized) {
            // Check if user is logged in
            val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
            onReady(isLoggedIn) // Navigate based on login state
        } else {
            // Firebase wasn't set up correctly
            Log.e("LoadingScreen", "❌ Firebase not initialized")
            onReady(false) // You might want to route to an error screen here
        }
    }

    // UI: Fullscreen loading spinner centered
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator() // Spinner animation
    }
}