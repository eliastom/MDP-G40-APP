package com.g40.reflectly.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    onDateSelected: (String) -> Unit,
    taskDays: List<String>,
    currentMonth: YearMonth,
    onMonthChanged: (YearMonth) -> Unit
) {
    val today = LocalDate.now()

    // Define the order of days (starting from Monday)
    val daysOfWeek = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 240.dp, max = 360.dp)
    ) {
        // Header with month switcher
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous month button
            IconButton(onClick = { onMonthChanged(currentMonth.minusMonths(1)) }) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Animated month display
            Box(
                modifier = Modifier
                    .width(160.dp)
                    .height(IntrinsicSize.Min),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentMonth,
                    transitionSpec = {
                        val slideSpec = tween<IntOffset>(durationMillis = 500, easing = FastOutSlowInEasing)
                        val from = initialState
                        val to = targetState
                        val direction = if (to > from) 1 else -1

                        slideInHorizontally(slideSpec) { fullWidth -> direction * fullWidth }
                            .togetherWith(slideOutHorizontally(slideSpec) { fullWidth -> -direction * fullWidth })
                    },
                    label = "MonthSwitch"
                ) { month ->
                    Text(
                        text = "${month.month.name} ${month.year}",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Next month button
            IconButton(onClick = { onMonthChanged(currentMonth.plusMonths(1)) }) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Days of week labels (Mon-Sun)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.getDefault()).uppercase(),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Build list of days for the month, inserting blank cells if needed
        val firstDayOfMonth = currentMonth.atDay(1)
        val lastDayOfMonth = currentMonth.atEndOfMonth()
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.ordinal % 7
        val totalDays = lastDayOfMonth.dayOfMonth

        val calendarDays = buildList {
            repeat(firstDayOfWeek) { add(null) } // Add empty days at the start
            for (day in 1..totalDays) {
                add(currentMonth.atDay(day))
            }
        }

        // Grid showing days of the month
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(2.dp)
        ) {
            items(calendarDays.size) { index ->
                val day = calendarDays[index]
                val isToday = day == today
                val hasTask = day?.toString() in taskDays
                val isEmptyCell = day == null
                val isWeekend = day?.dayOfWeek == DayOfWeek.SATURDAY || day?.dayOfWeek == DayOfWeek.SUNDAY

                Box(
                    modifier = Modifier
                        .aspectRatio(1f) // Keep day cells square
                        .padding(4.dp)
                        .background(color = when {
                            isToday -> MaterialTheme.colorScheme.primary
                            isEmptyCell -> Color.Transparent
                            else -> MaterialTheme.colorScheme.secondary
                        })
                        .clickable(enabled = day != null) {
                            day?.let { onDateSelected(it.toString()) }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Day number
                        Text(
                            text = day?.dayOfMonth?.toString() ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                isToday -> MaterialTheme.colorScheme.onPrimary
                                isWeekend -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.onBackground
                            }
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Small dot indicating a task
                        Box(
                            modifier = Modifier
                                .padding(top = 0.5.dp)
                                .size(5.dp)
                                .background(
                                    color = if (hasTask) {
                                        if (isToday) MaterialTheme.colorScheme.onPrimary
                                        else MaterialTheme.colorScheme.tertiary
                                    } else {
                                        Color.Transparent
                                    },
                                    shape = MaterialTheme.shapes.small
                                )
                        )
                    }
                }
            }
        }
    }
}
