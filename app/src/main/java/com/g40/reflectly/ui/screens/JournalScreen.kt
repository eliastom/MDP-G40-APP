package com.g40.reflectly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import com.g40.reflectly.data.utils.DateUtils
import com.g40.reflectly.viewmodel.JournalViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun JournalScreen(
    selectedDate: String,
    viewModel: JournalViewModel,
    onBack: () -> Unit
) {
            JournalScreenContent(selectedDate, viewModel, onBack)
}

@Composable
private fun JournalScreenContent(
    selectedDate: String,
    viewModel: JournalViewModel,
    onBack: () -> Unit
) {
    val journalState by viewModel.entry.collectAsState()
    var text by remember { mutableStateOf("") }
    var isInitialLoad by remember { mutableStateOf(true) }

    // Load entry when screen is shown
    LaunchedEffect(selectedDate) {
        viewModel.loadEntry(selectedDate)
        isInitialLoad = true // Reset this whenever date changes
    }

    // Only pre-fill if the user hasn't typed anything yet
    LaunchedEffect(journalState) {
        journalState?.let {
            // Only update if the text hasn't been modified manually
            if (text.isBlank() || text == it.content) {
                text = it.content
            }
        }
    }


    val formattedDate = remember(selectedDate) {
        DateUtils.formatDate(selectedDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = formattedDate,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        val isToday = selectedDate == LocalDate.now().toString()
        val placeholderText = when {
            isToday -> "Write your journal..."
            journalState == null || journalState?.content.isNullOrBlank() -> "No journal written for this day."
            else -> ""
        }

        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(placeholderText) },
            modifier = Modifier.fillMaxWidth()
        )


        Button(onClick = onBack) {
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
