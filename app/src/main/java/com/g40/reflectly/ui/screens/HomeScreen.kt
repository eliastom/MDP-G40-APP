package com.g40.reflectly.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.g40.reflectly.viewmodel.HomeScreenViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel, onNavigate: () -> Unit) {
    // Scaffold provides layout structure (e.g., padding, app bars, FAB, etc.)
    Scaffold { innerPadding ->


        // Column layout with padding to avoid overlapping system UI (e.g. status bar)
        Column(modifier = Modifier.padding(innerPadding)) {

            // Display a piece of info from the ViewModel
            Text(text = viewModel.info)

            // Button to navigate to NewJournalScreen
            Button(onClick = onNavigate) {
                Text("New journal")
            }
        }
    }
}
