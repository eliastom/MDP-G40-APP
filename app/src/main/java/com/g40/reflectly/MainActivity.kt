package com.g40.reflectly

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.g40.reflectly.ui.navigation.AppNavGraph

class MainActivity : ComponentActivity() {

    // Ensures this function only runs on Android 8.0 (API 26) and above
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge display (content behind status/navigation bars)
        enableEdgeToEdge()

        // Sets the main UI content using Jetpack Compose
        setContent {

            // Creates and remembers a NavController instance for app navigation
            val navController = rememberNavController()

            // Launches the app's navigation graph which manages all composable screens
            AppNavGraph(navController = navController)
        }
    }
}
