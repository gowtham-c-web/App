package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_logs")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moodRating: Int, // 1 to 5
    val moodName: String, // Energetic, Calm, Focused, Grateful, Relaxed
    val note: String = "",
    val tags: String = "", // Comma-separated tags (e.g. "Workout, Meditation")
    val timestamp: Long = System.currentTimeMillis()
)
