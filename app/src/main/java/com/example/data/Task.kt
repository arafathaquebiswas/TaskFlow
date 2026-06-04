package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Long, // timestamp
    val priority: TaskPriority,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
