package com.g40.reflectly

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp

class ReflectlyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Log.d("ReflectlyApp", "✅ Firebase initialized!")
    }
}
