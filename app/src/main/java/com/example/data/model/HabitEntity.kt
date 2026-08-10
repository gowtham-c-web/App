package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String = "General", // Health, Mind, Productivity, Fitness, Mindfulness
    val colorHex: String = "#818CF8",
    val iconName: String = "Check",
    val targetDaysPerWeek: Int = 7,
    val isCompletedToday: Boolean = false,
    val currentStreak: Int = 0,
    val totalCompletions: Int = 0,
    val lastCompletedDate: Long = 0L, // Timestamp in millis
    val createdAt: Long = System.currentTimeMillis()
)
