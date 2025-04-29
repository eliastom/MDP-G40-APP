package com.g40.reflectly.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.g40.reflectly.data.models.Task
import com.g40.reflectly.ui.components.ReflectlyButton

@Composable
fun TaskScreen(
    tasks: List<Task>,
    onTaskCheckedChange: (taskId: String, isChecked: Boolean) -> Unit,
    onAddTask: (() -> Unit)? = null,
    onTaskClick: (Task) -> Unit,
    showAddButton: Boolean = true
) {
    // Sort tasks: incomplete tasks first, earlier times first
    val sortedTasks = tasks.sortedWith(
        compareBy<Task> { it.completed }
            .thenBy { it.time.ifEmpty { "23:59" } }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 350.dp, max = 800.dp)
        ) {
            if (tasks.isEmpty()) {
                // Show empty state if no tasks
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text(
                            text = "No tasks...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(30.dp))
                    }
                }

            } else {
                // List each task using a TaskCard
                items(
                    items = sortedTasks,
                    key = { it.id }
                ) { task ->
                    TaskCard(
                        title = task.title,
                        completed = task.completed,
                        onCheckedChange = { checked ->
                            onTaskCheckedChange(task.id, checked)
                        },
                        time = task.time,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTaskClick(task) }
                    )
                }
            }

            // Optional add button at the bottom
            if (showAddButton && onAddTask != null) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ReflectlyButton(onAddTask, usePrimary = false, modifier = Modifier) {
                            Text(
                                "Add +",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaskCard(
    title: String,
    completed: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    time: String = "",
    modifier: Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 8.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.Top
        ) {
            // Checkbox for task completion
            Checkbox(
                checked = completed,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.size(24.dp),
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onBackground,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Bottom
            ) {
                // Task title text
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (completed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .weight(1f)
                        .alignByBaseline()
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Task time (optional)
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDecoration = if (completed) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (completed) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.alignByBaseline(),
                )
            }
        }
    }
}
