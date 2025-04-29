package com.g40.reflectly.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.g40.reflectly.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onReady: (Boolean) -> Unit
) {
    LoadingScreenContent(onReady)
}

@Composable
private fun LoadingScreenContent(
    onReady: (Boolean) -> Unit
) {
    val context = LocalContext.current

    // Start loading when the Composable is launched
    LaunchedEffect(Unit) {
        delay(3000) // Delay for splash screen appearance

        val isFirebaseInitialized = FirebaseApp.getApps(context).isNotEmpty()

        if (isFirebaseInitialized) {
            val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
            onReady(isLoggedIn)
        } else {
            Log.e("LoadingScreen", "Firebase not initialized")
            onReady(false)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Reflectly Logo",
                modifier = Modifier.size(150.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Loading Spinner
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
