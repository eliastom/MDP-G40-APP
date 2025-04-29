import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.g40.reflectly.data.models.Task
import com.g40.reflectly.data.utils.DateUtils.getTodayRaw
import com.g40.reflectly.ui.components.ReflectlyXpBar
import com.g40.reflectly.ui.screens.CalendarScreen
import com.g40.reflectly.ui.screens.TaskScreen
import com.g40.reflectly.viewmodel.AuthViewModel
import com.g40.reflectly.viewmodel.GamificationViewModel
import com.g40.reflectly.viewmodel.HomeViewModel
import com.g40.reflectly.viewmodel.JournalViewModel
import com.g40.reflectly.viewmodel.TaskViewModel
import java.time.YearMonth

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    homeViewModel: HomeViewModel,
    taskViewModel: TaskViewModel,
    journalViewModel: JournalViewModel,
    gamificationViewModel: GamificationViewModel,
    onDateSelected: (String) -> Unit,
    onLogOut: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onAddTask: () -> Unit
) {
    val today = getTodayRaw()

    // Observing today's tasks
    val todayTasks by taskViewModel.getTasksForDate(today).collectAsState()

    // Observing ViewModel state for calendar and journal
    val currentMonth by homeViewModel.currentMonth.collectAsState()
    val allTaskDates by homeViewModel.allTaskDates.collectAsState()
    val todayJournalText by journalViewModel.text.collectAsState()
    val todayJournalLoading by journalViewModel.isLoading.collectAsState()

    // Load gamification stats
    LaunchedEffect(Unit) {
        gamificationViewModel.reload()
    }

    // Load today's journal
    LaunchedEffect(today) {
        journalViewModel.loadEntry(today)
    }

    // Load tasks whenever month changes
    LaunchedEffect(currentMonth) {
        homeViewModel.loadTasksForSurroundingMonths(currentMonth)
    }

    // Load today's tasks
    LaunchedEffect(today) {
        taskViewModel.loadTasksForDate(today)
    }

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
                GamificationHeader(gamificationViewModel)

                Spacer(modifier = Modifier.height(50.dp))

                // Agenda Title
                Text(
                    "Agenda",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Task list for today
                AgendaSection(
                    tasks = todayTasks,
                    onTaskCheckedChange = { taskId, isChecked ->
                        taskViewModel.updateTaskDoneStatus(taskId, isChecked)
                    },
                    onTaskClick = onTaskClick,
                    onAddTask = onAddTask
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Journal area with today's entry and calendar
                JournalSection(
                    currentMonth = currentMonth,
                    onMonthChanged = { homeViewModel.loadTasksForSurroundingMonths(it) },
                    taskDays = allTaskDates,
                    onDateSelected = onDateSelected,
                    todayJournalText = todayJournalText,
                    todayJournalLoading = todayJournalLoading
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Button for adding dummy test tasks
                Button(onClick = { homeViewModel.addDummyTasks("2025-03-01") }) {
                    Text("Add Dummy Tasks")
                }

                // Button for logout action
                Button(onClick = { authViewModel.logOut(onLogOut) }) {
                    Text("Log out")
                }
            }
        }
    }
}

@Composable
fun GamificationHeader(
    gamificationViewModel: GamificationViewModel
) {
    val gamificationState = gamificationViewModel.state
    val (currentXpProgress, xpForNextLevel) = com.g40.reflectly.data.utils.getXpProgressWithinLevel(gamificationState.totalXp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(modifier = Modifier.width(60.dp)) // Empty spacer to balance layout
        Spacer(modifier = Modifier.width(8.dp))

        ReflectlyXpBar(
            currentXp = currentXpProgress,
            xpForNextLevel = xpForNextLevel,
            level = gamificationState.level,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.width(60.dp)) {
            StreakCounter(
                streak = gamificationState.streak,
                modifier = Modifier
            )
        }
    }
}

@Composable
fun AgendaSection(
    tasks: List<Task>,
    onTaskCheckedChange: (taskId: String, isChecked: Boolean) -> Unit,
    onTaskClick: (Task) -> Unit,
    onAddTask: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        // Displays today's tasks using TaskScreen
        TaskScreen(
            tasks = tasks,
            onTaskCheckedChange = onTaskCheckedChange,
            onTaskClick = onTaskClick,
            onAddTask = onAddTask,
            showAddButton = true
        )
    }
}

@Composable
fun JournalSection(
    currentMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit,
    taskDays: List<String>,
    onDateSelected: (String) -> Unit,
    todayJournalText: String,
    todayJournalLoading: Boolean
) {
    val today = getTodayRaw()

    Column {
        Text(
            "Journal",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Journal button to edit today's entry
        OutlinedButton(
            onClick = { onDateSelected(today) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            if (todayJournalLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = if (todayJournalText.isBlank()) "Write your journal..."
                    else todayJournalText.take(150) + if (todayJournalText.length > 150) "..." else "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Calendar to navigate journal entries and tasks
        CalendarScreen(
            onDateSelected = onDateSelected,
            taskDays = taskDays,
            currentMonth = currentMonth,
            onMonthChanged = onMonthChanged
        )
    }
}
