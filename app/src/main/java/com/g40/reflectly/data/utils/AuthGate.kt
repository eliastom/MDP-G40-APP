package com.g40.reflectly.data.utils

import android.util.Log
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthGate(
    onAuthReady: @Composable (String) -> Unit,
    fallback: @Composable () -> Unit = {
        CircularProgressIndicator()
    }
) {
    val user = FirebaseAuth.getInstance().currentUser

    // Logging (side-effect free)
    Log.d("AuthGate", if (user != null) "✅ Auth ready: ${user.uid}" else "❌ Auth not ready")

    if (user != null) {
        // ✅ Safe: calling composable from composable context
        onAuthReady(user.uid)
    } else {
        fallback()
    }
}
