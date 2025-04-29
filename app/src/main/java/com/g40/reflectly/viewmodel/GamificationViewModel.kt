package com.g40.reflectly.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.g40.reflectly.data.FirestoreGamificationRepository
import com.g40.reflectly.data.GamificationRepository
import com.g40.reflectly.data.models.GamificationState
import com.g40.reflectly.data.utils.getLevelFromTotalXp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class GamificationViewModel(
    private val repository: GamificationRepository = FirestoreGamificationRepository()
) : ViewModel() {

    var state by mutableStateOf(GamificationState())
        private set

    init {
        viewModelScope.launch {
            repository.loadGamificationState().collectLatest { savedState ->
                if (savedState != null) {
                    state = savedState
                }
            }
        }
    }

    fun addXp(amount: Int) {
        val newXp = state.totalXp + amount
        val newLevel = getLevelFromTotalXp(newXp)
        state = state.copy(totalXp = newXp, level = newLevel)
        saveState()
    }

    fun updateStreak(currentDate: String = LocalDate.now().format(DateTimeFormatter.ISO_DATE)) {
        val lastDate = state.lastActiveDay
        val today = LocalDate.parse(currentDate)
        val yesterday = today.minusDays(1)

        val newStreak = when {
            lastDate == today.toString() -> state.streak
            lastDate == yesterday.toString() -> state.streak + 1
            else -> 1
        }

        state = state.copy(streak = newStreak, lastActiveDay = currentDate)
        saveState()
    }

    fun reload() {
        viewModelScope.launch {
            repository.loadGamificationState().collectLatest { savedState ->
                if (savedState != null) {
                    state = savedState
                }
            }
        }
    }


    fun grantBadge(badge: String) {
        if (badge !in state.badges) {
            state = state.copy(badges = state.badges + badge)
            saveState()
        }
    }

    fun reset() {
        state = GamificationState()
        saveState()
    }

    private fun saveState() {
        viewModelScope.launch {
            repository.saveGamificationState(state)
        }
    }
}
