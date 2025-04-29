package com.g40.reflectly.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.g40.reflectly.data.models.Task
import com.g40.reflectly.data.utils.DateUtils
import com.g40.reflectly.ui.components.ReflectlyButton
import com.g40.reflectly.ui.components.XpPopup
import com.g40.reflectly.viewmodel.GamificationViewModel
import com.g40.reflectly.viewmodel.JournalViewModel
import com.g40.reflectly.viewmodel.TaskViewModel
import java.time.LocalDate

@Composable
fun JournalScreen(
    selectedDate: String,
    viewModel: JournalViewModel,
    taskViewModel: TaskViewModel,
    gamificationViewModel: GamificationViewModel,
    onBack: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onAddTask: () -> Unit
) {
    // Observing all relevant state
    val text by viewModel.text.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val textError by viewModel.textError.collectAsState()
    val journalTasks by taskViewModel.getTasksForDate(selectedDate).collectAsState()
    val xpPopupVisible by viewModel.xpPopupVisible.collectAsState()

    // Load journal and tasks for the selected date
    LaunchedEffect(selectedDate) {
        viewModel.loadEntry(selectedDate)
        taskViewModel.loadTasksForDate(selectedDate)
    }

    // Hide XP popup after showing for 1 second
    LaunchedEffect(xpPopupVisible) {
        if (xpPopupVisible) {
            kotlinx.coroutines.delay(1000)
            viewModel.resetXpPopup()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Back button
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Selected date title
                    Text(
                        text = DateUtils.formatDate(selectedDate),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // Journal text field
                    OutlinedTextField(
                        value = text,
                        onValueChange = viewModel::onTextChange,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.secondary,
                            unfocusedContainerColor = MaterialTheme.colorScheme.secondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 350.dp),
                        placeholder = {
                            Text(
                                text = getPlaceholderText(selectedDate, text),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Show text validation error if exists
                    if (textError != null) {
                        Text(
                            text = textError ?: "",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save button for journal entry
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ReflectlyButton(
                            onClick = {
                                viewModel.saveEntry(
                                    gamificationViewModel = gamificationViewModel,
                                    onSaved = { onBack() }
                                )
                            },
                            usePrimary = true,
                            modifier = Modifier
                        ) {
                            Text(
                                text = "Save",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Agenda section title
                    Text(
                        "Agenda",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    // List of tasks for the selected date
                    TaskScreen(
                        tasks = journalTasks,
                        onTaskCheckedChange = { taskId, isChecked ->
                            taskViewModel.updateTaskDoneStatus(taskId, isChecked)
                        },
                        onTaskClick = onTaskClick,
                        onAddTask = onAddTask,
                        showAddButton = false
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // XP reward popup when journal is saved
            XpPopup(
                visible = xpPopupVisible,
                xpAmount = 15,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )
        }
    }
}

@Composable
private fun getPlaceholderText(selectedDate: String, journalContent: String?): String {
    val isToday = selectedDate == LocalDate.now().toString()
    return when {
        isToday -> "Write your journal..."
        journalContent.isNullOrBlank() -> "No journal written for this day."
        else -> ""
    }
}
