package com.g40.reflectly.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.g40.reflectly.ui.components.LabeledTextField
import com.g40.reflectly.ui.components.ReflectlyButton
import com.g40.reflectly.viewmodel.EditTaskViewModel

@Composable
fun EditTaskScreen(
    viewModel: EditTaskViewModel,
    onNavigation: () -> Unit
) {
    // Observing all field states from ViewModel
    val title by viewModel.title.collectAsState()
    val description by viewModel.description.collectAsState()
    val timeState by viewModel.timeState.collectAsState()
    val dateState by viewModel.dateState.collectAsState()
    val titleError by viewModel.titleError.collectAsState()
    val timeError by viewModel.timeError.collectAsState()
    val dateError by viewModel.dateError.collectAsState()

    val fieldHeight = 52.dp
    val timeFieldWidth = 95.dp
    val dateFieldWidth = 135.dp
    val fieldShape = RoundedCornerShape(8.dp)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {

                Spacer(modifier = Modifier.height(16.dp))

                // --- Back Button ---
                IconButton(onClick = { onNavigation() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                // --- Title Field ---
                BasicTextField(
                    value = title,
                    onValueChange = viewModel::onTitleChange,
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    ),
                    decorationBox = { innerTextField ->
                        if (title.isEmpty()) {
                            Text(
                                text = "Edit task...",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                                )
                            )
                        }
                        innerTextField()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 64.dp)
                )

                // Display title error if any
                if (titleError != null) {
                    Text(
                        text = titleError ?: "",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // --- Time Field ---
                LabeledTextField(
                    label = "Time:",
                    value = timeState,
                    onValueChange = viewModel::onTimeChange,
                    modifier = Modifier
                        .width(timeFieldWidth)
                        .height(fieldHeight),
                    isError = timeError != null,
                    textAlign = TextAlign.Center
                )

                if (timeError != null) {
                    Text(
                        text = timeError ?: "",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Date Field ---
                LabeledTextField(
                    label = "Date:",
                    value = dateState,
                    onValueChange = viewModel::onDateChange,
                    modifier = Modifier
                        .width(dateFieldWidth)
                        .height(fieldHeight)
                        .onFocusChanged { focusState ->
                            viewModel.onDateFieldFocusChanged(focusState.isFocused)
                        },
                    isError = dateError != null
                )

                if (dateError != null) {
                    Text(
                        text = dateError ?: "",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Description Field ---
                OutlinedTextField(
                    value = description,
                    onValueChange = viewModel::onDescriptionChange,
                    placeholder = {
                        Text(
                            text = "Note...",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.secondary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 120.dp),
                    shape = fieldShape,
                    textStyle = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- Delete and Update Buttons ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ReflectlyButton(
                        onClick = {
                            viewModel.deleteTask {
                                onNavigation()
                            }
                        },
                        usePrimary = false,
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = "Delete",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    ReflectlyButton(
                        onClick = {
                            viewModel.validateAndUpdateTask {
                                onNavigation()
                            }
                        },
                        modifier = Modifier.wrapContentWidth()
                    ) {
                        Text(
                            text = "Update",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }

            }
        }
    }
}
