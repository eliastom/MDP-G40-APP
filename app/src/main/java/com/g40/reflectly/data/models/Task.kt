package com.g40.reflectly.data.models

import java.io.Serializable

data class Task(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var date: String = "",   // "yyyy-MM-dd"
    var time: String = "",
    var completed: Boolean = false
) :Serializable
