import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.g40.reflectly.viewmodel.HomeScreenViewModel
import java.time.LocalDate

@Composable
fun HomeScreen(viewModel: HomeScreenViewModel, onDateSelected: (String) -> Unit) {
    val today = remember { LocalDate.now() }

    val dates = remember {
        (0..30).map { offset ->
            today.minusDays(offset.toLong()).toString() // Ensure it's a String!
        }
    }

    Scaffold { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {

            Text("Select a date to open your journal", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(dates) { date ->
                    Button(
                        onClick = { onDateSelected(date) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = date)
                    }
                }
            }
        }
    }
}
