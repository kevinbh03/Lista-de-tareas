package com.example.tasklist.domain

data class TaskModel(
    val id: Long = System.currentTimeMillis(),
    val task: String,
    val name: String,
    val lastname: String,
    val mail: String,
    val date: String,
    var selected: Boolean = false
)