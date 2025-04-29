package com.g40.reflectly.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g40.reflectly.data.FirestoreTaskRepository
import com.g40.reflectly.data.TaskRepository
import com.g40.reflectly.data.models.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditTaskViewModel(
    private val initialTask: Task, // Receives task to edit
    private val repository: TaskRepository = FirestoreTaskRepository()
) : ViewModel() {

    // Task fields
    private val _title = MutableStateFlow(initialTask.title)
    val title: StateFlow<String> = _title

    private val _description = MutableStateFlow(initialTask.description)
    val description: StateFlow<String> = _description

    private val _timeState = MutableStateFlow(TextFieldValue(initialTask.time.ifBlank { "__ : __" }))
    val timeState: StateFlow<TextFieldValue> = _timeState

    private val _dateState = MutableStateFlow(
        TextFieldValue(
            LocalDate.parse(initialTask.date).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        )
    )
    val dateState: StateFlow<TextFieldValue> = _dateState

    // Validation states
    private val _titleError = MutableStateFlow<String?>(null)
    val titleError: StateFlow<String?> = _titleError

    private val _timeError = MutableStateFlow<String?>(null)
    val timeError: StateFlow<String?> = _timeError

    private val _dateError = MutableStateFlow<String?>(null)
    val dateError: StateFlow<String?> = _dateError

    private var hasDateFieldBeenFocused = false
    private var isDateFieldJustCleared = false

    // --- Field Input Handlers ---

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
        if (newTitle.isNotBlank()) _titleError.value = null
    }

    fun onDescriptionChange(newDescription: String) {
        _description.value = newDescription
    }

    // Formats the input for Time field
    fun onTimeChange(newValue: TextFieldValue) {
        val oldText = _timeState.value.text
        val newText = newValue.text
        val isBackspace = oldText.length > newText.length
        var digits = newText.replace(":", "").filter { it.isDigit() }

        if (isBackspace && oldText.getOrNull(_timeState.value.selection.start - 1) == ':') {
            if (digits.isNotEmpty()) digits = digits.dropLast(1)
        }

        var hour = ""
        var minute = ""

        for (i in digits.indices) {
            if (i < 2) hour += digits[i]
            else if (i < 4) minute += digits[i]
        }

        if (hour.length == 2 && hour.toInt() > 23) hour = "23"
        if (minute.length == 2 && minute.toInt() > 59) minute = "59"

        val formatted = when {
            hour.isEmpty() -> "__ : __"
            hour.length == 1 -> hour + "_ : __"
            hour.length == 2 && minute.isEmpty() -> "$hour: __"
            hour.length == 2 && minute.length == 1 -> "$hour:${minute}_"
            hour.length == 2 && minute.length == 2 -> "$hour:$minute"
            else -> "__ : __"
        }

        val cursorPosition = when {
            hour.length < 2 -> hour.length
            minute.length < 2 -> hour.length + 1 + minute.length
            else -> 5
        }

        _timeState.value = TextFieldValue(text = formatted, selection = TextRange(cursorPosition))

        if (digits.isNotEmpty()) _timeError.value = null
    }

    // Formats the input for Date field
    fun onDateChange(newValue: TextFieldValue) {
        if (isDateFieldJustCleared) {
            isDateFieldJustCleared = false
            return
        }

        val oldText = _dateState.value.text
        val newText = newValue.text
        val isBackspace = oldText.length > newText.length
        var digits = newText.replace("/", "").filter { it.isDigit() }

        if (isBackspace && oldText.getOrNull(_dateState.value.selection.start - 1) == '/') {
            if (digits.isNotEmpty()) digits = digits.dropLast(1)
        }

        var day = ""
        var month = ""
        var year = ""

        for (i in digits.indices) {
            when {
                i < 2 -> day += digits[i]
                i < 4 -> month += digits[i]
                i < 8 -> year += digits[i]
            }
        }

        if (day.length == 2 && day.toInt() > 31) day = "31"
        if (month.length == 2) {
            val monthInt = month.toInt()
            month = when {
                monthInt == 0 -> "01"
                monthInt > 12 -> "12"
                else -> month
            }
        }

        val formatted = when {
            day.isEmpty() -> "__ /__ /____"
            day.length == 1 -> "${day}_ /__ /____"
            day.length == 2 && month.isEmpty() -> "$day/__ /____"
            month.length == 1 -> "$day/${month}_ /____"
            month.length == 2 && year.isEmpty() -> "$day/$month/____"
            year.length in 1..3 -> "$day/$month/${year}${"_".repeat(4 - year.length)}"
            year.length == 4 -> "$day/$month/$year"
            else -> "__ /__ /____"
        }

        val cursorPosition = when {
            day.length < 2 -> day.length
            month.length < 2 -> day.length + 1 + month.length
            year.length < 4 -> day.length + 1 + month.length + 1 + year.length
            else -> 10
        }

        _dateState.value = TextFieldValue(text = formatted, selection = TextRange(cursorPosition))

        if (digits.isNotEmpty()) _dateError.value = null
    }

    // Resets the date field when focused
    fun onDateFieldFocusChanged(isFocused: Boolean) {
        if (isFocused && !hasDateFieldBeenFocused) {
            hasDateFieldBeenFocused = true
            isDateFieldJustCleared = true
            _dateState.value = TextFieldValue(text = "__ /__ /____", selection = TextRange(0))
        }
    }

    // --- Validate and Update Task ---

    fun validateAndUpdateTask(onSuccess: () -> Unit) {
        var hasError = false

        if (_title.value.isBlank()) {
            _titleError.value = "* Title is required"
            hasError = true
        }
        if (_timeState.value.text.contains("_") || _timeState.value.text == "__ : __") {
            _timeError.value = "* Time is required"
            hasError = true
        }
        if (_dateState.value.text.contains("_") || _dateState.value.text == "__ /__ /____") {
            _dateError.value = "* Date is required"
            hasError = true
        }

        if (hasError) return

        try {
            val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val parsedDate = LocalDate.parse(_dateState.value.text, inputFormatter)

            val parts = _dateState.value.text.split("/")
            val inputDay = parts[0].toInt()
            val inputMonth = parts[1].toInt()
            val inputYear = parts[2].toInt()

            if (parsedDate.dayOfMonth != inputDay || parsedDate.monthValue != inputMonth || parsedDate.year != inputYear) {
                _dateError.value = "* Invalid calendar date"
                return
            }

            val formattedDate = parsedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))

            val updatedTask = initialTask.copy(
                title = _title.value,
                description = _description.value,
                date = formattedDate,
                time = _timeState.value.text
            )

            viewModelScope.launch {
                repository.updateTask(updatedTask)
                onSuccess()
            }

        } catch (e: Exception) {
            _dateError.value = "* Invalid calendar date"
        }
    }

    // --- Delete Task ---

    fun deleteTask(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.deleteTask(initialTask.id)
            onSuccess()
        }
    }
}
