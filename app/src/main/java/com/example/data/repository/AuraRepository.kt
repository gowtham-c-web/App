package com.example.data.repository

import com.example.data.local.FocusDao
import com.example.data.local.HabitDao
import com.example.data.local.MoodDao
import com.example.data.model.FocusSessionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.MoodEntity
import kotlinx.coroutines.flow.Flow

class AuraRepository(
    private val habitDao: HabitDao,
    private val moodDao: MoodDao,
    private val focusDao: FocusDao
) {
    val allHabits: Flow<List<HabitEntity>> = habitDao.getAllHabits()
    val allMoodLogs: Flow<List<MoodEntity>> = moodDao.getAllMoodLogs()
    val allFocusSessions: Flow<List<FocusSessionEntity>> = focusDao.getAllFocusSessions()
    val totalFocusMinutes: Flow<Int?> = focusDao.getTotalFocusMinutes()

    suspend fun insertHabit(habit: HabitEntity): Long = habitDao.insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = habitDao.updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = habitDao.deleteHabit(habit)

    suspend fun toggleHabitCompletion(habit: HabitEntity) {
        val newCompletion = !habit.isCompletedToday
        val newStreak = if (newCompletion) habit.currentStreak + 1 else maxOf(0, habit.currentStreak - 1)
        val newTotal = if (newCompletion) habit.totalCompletions + 1 else maxOf(0, habit.totalCompletions - 1)
        val updated = habit.copy(
            isCompletedToday = newCompletion,
            currentStreak = newStreak,
            totalCompletions = newTotal,
            lastCompletedDate = if (newCompletion) System.currentTimeMillis() else habit.lastCompletedDate
        )
        habitDao.updateHabit(updated)
    }

    suspend fun insertMood(mood: MoodEntity) = moodDao.insertMood(mood)
    suspend fun insertFocusSession(session: FocusSessionEntity) = focusDao.insertSession(session)

    suspend fun seedInitialDataIfEmpty() {
        // Sample habits if empty
        val defaultHabits = listOf(
            HabitEntity(
                title = "Morning Meditation",
                description = "10 minutes of deep mindfulness",
                category = "Mindfulness",
                colorHex = "#818CF8",
                iconName = "SelfImprovement",
                isCompletedToday = false,
                currentStreak = 4,
                totalCompletions = 18
            ),
            HabitEntity(
                title = "Hydrate 2 Liters",
                description = "Drink crisp water throughout the day",
                category = "Health",
                colorHex = "#2DD4BF",
                iconName = "LocalWater",
                isCompletedToday = true,
                currentStreak = 7,
                totalCompletions = 25
            ),
            HabitEntity(
                title = "Deep Focus Work",
                description = "45 minutes uninterrupted flow state",
                category = "Productivity",
                colorHex = "#F472B6",
                iconName = "Timer",
                isCompletedToday = false,
                currentStreak = 3,
                totalCompletions = 12
            ),
            HabitEntity(
                title = "Evening Reflection",
                description = "Log mood and grateful moments",
                category = "Mindfulness",
                colorHex = "#F59E0B",
                iconName = "MenuBook",
                isCompletedToday = false,
                currentStreak = 5,
                totalCompletions = 15
            )
        )

        for (h in defaultHabits) {
            habitDao.insertHabit(h)
        }

        // Seed sample mood log
        moodDao.insertMood(
            MoodEntity(
                moodRating = 4,
                moodName = "Calm",
                note = "Had a wonderfully productive morning with smooth focus.",
                tags = "Flow, Clean Energy"
            )
        )

        // Seed sample focus session
        focusDao.insertSession(
            FocusSessionEntity(
                durationMinutes = 25,
                mode = "Deep Focus",
                completed = true
            )
        )
    }

    suspend fun resetAllData() {
        habitDao.clearAll()
        moodDao.clearAll()
        focusDao.clearAll()
    }
}
