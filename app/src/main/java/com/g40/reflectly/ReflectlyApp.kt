package com.g40.reflectly

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class ReflectlyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val firebaseApp = FirebaseApp.initializeApp(this)
        if (firebaseApp == null) {
            Log.e("ReflectlyApp", "Firebase failed to initialize.")
        } else {
            Log.d("ReflectlyApp", "Firebase initialized successfully.")
        }
    }
}
