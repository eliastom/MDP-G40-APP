package com.g40.reflectly.data

import com.g40.reflectly.data.models.GamificationState
import kotlinx.coroutines.flow.Flow

// Interface for managing and persisting gamification data (XP, level, streak)
interface GamificationRepository {

    // Save the gamification state (e.g., XP, level, streak) to a data source
    suspend fun saveGamificationState(state: GamificationState)

    // Load the current gamification state (e.g., XP, level, streak)
    fun loadGamificationState(): Flow<GamificationState?>
}
