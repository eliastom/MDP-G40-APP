package com.g40.reflectly.utils

import com.google.firebase.auth.FirebaseAuth

// Safely gets the currently logged-in user's UID
fun getCurrentUserId(): String? {
    return FirebaseAuth.getInstance().currentUser?.uid
}
