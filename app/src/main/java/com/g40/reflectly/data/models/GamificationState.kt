package com.g40.reflectly.data.models

data class GamificationState(
    val totalXp: Int = 0,
    val level: Int = 1,
    val streak: Int = 0,
    val lastActiveDay: String = "", // format: "yyyy-MM-dd"
    val badges: List<String> = emptyList()
)
