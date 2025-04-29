package com.g40.reflectly

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.g40.reflectly.ui.navigation.AppNavGraph
import com.g40.reflectly.ui.theme.ReflectlyTheme
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ReflectlyTheme {
                val navController = rememberNavController()

                val primaryColor = MaterialTheme.colorScheme.background

                LaunchedEffect(primaryColor) {
                    window.setBackgroundDrawable(ColorDrawable(primaryColor.toArgb()))
                }

                AppNavGraph(navController = navController)
            }
        }
    }
}
