package com.g40.reflectly.ui.navigation

enum class Screen {
    Loading,
    SignUp,
    Home,
    Journal,
    SignIn,
    NewTask,
    Welcome;

    val route: String
        get() = when (this) {
            Journal -> "journal/{date}"
            else -> name.lowercase()
        }

    fun withArgs(vararg args: String): String {
        return when (this) {
            Journal -> "journal/${args[0]}"
            else -> route
        }
    }
}
