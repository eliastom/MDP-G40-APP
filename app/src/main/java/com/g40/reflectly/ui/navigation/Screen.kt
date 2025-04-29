package com.g40.reflectly.ui.navigation

// Defines all navigation routes/screens in the app
enum class Screen {
    Loading,
    SignUp,
    Home,
    Journal,
    SignIn,
    NewTask,
    EditTask,
    Welcome;

    // Defines route string for each screen
    val route: String
        get() = when (this) {
            Journal -> "journal/{date}" // Journal screen expects a date argument
            else -> name.lowercase() // Other screens use lowercase enum name
        }

    // Helper to generate route string with arguments
    fun withArgs(vararg args: String): String {
        return when (this) {
            Journal -> "journal/${args[0]}" // Replaces {date} with actual date value
            else -> route
        }
    }
}
