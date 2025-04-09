package com.g40.reflectly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.g40.reflectly.data.utils.AuthGate
import com.g40.reflectly.viewmodel.NewJournalViewModel

@Composable
fun NewJournalScreen(
    selectedDate: String,
    viewModel: NewJournalViewModel,
    onNavigate: () -> Unit
) {
    AuthGate(
        onAuthReady = {
            JournalScreenContent(selectedDate, viewModel, onNavigate)
        }
    )
}

@Composable
private fun JournalScreenContent(
    selectedDate: String,
    viewModel: NewJournalViewModel,
    onNavigate: () -> Unit
) {
    val journalState by viewModel.entry.collectAsState()
    var text by remember { mutableStateOf("") }

    // Load entry only once when entering screen
    LaunchedEffect(selectedDate) {
        viewModel.loadEntry(selectedDate)
    }

    // Populate text field when entry is loaded
    LaunchedEffect(journalState) {
        journalState?.let {
            text = it.content
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Write your journal...") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = onNavigate) {
            Text("Back")
        }

        Button(
            onClick = {
                if (text.isNotBlank()) {
                    viewModel.saveEntry(selectedDate, text)
                }
            },
            modifier = Modifier
                .padding(bottom = 36.dp)
                .align(Alignment.End)
        ) {
            Text("Save")
        }
    }
}
