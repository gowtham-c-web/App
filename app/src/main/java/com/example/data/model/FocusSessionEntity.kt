package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,
    val mode: String = "Deep Focus", // Deep Focus, Breathwork, Ambient Flow
    val completed: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)
