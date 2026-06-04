package com.example.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromPriority(priority: TaskPriority): String {
        return priority.name
    }

    @TypeConverter
    fun toPriority(value: String): TaskPriority {
        return try {
            TaskPriority.valueOf(value)
        } catch (e: Exception) {
            TaskPriority.LOW
        }
    }
}
