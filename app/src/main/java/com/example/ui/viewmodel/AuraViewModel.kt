package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.FocusSessionEntity
import com.example.data.model.HabitEntity
import com.example.data.model.MoodEntity
import com.example.data.repository.AuraRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    object TriggerCelebration : UiEvent()
}

class AuraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AuraRepository

    val habits: StateFlow<List<HabitEntity>>
    val moodLogs: StateFlow<List<MoodEntity>>
    val focusSessions: StateFlow<List<FocusSessionEntity>>
    val totalFocusMinutes: StateFlow<Int>

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    // Focus / Breath Timer state
    private val _timerSeconds = MutableStateFlow(25 * 60)
    val timerSeconds: StateFlow<Int> = _timerSeconds.asStateFlow()

    private val _timerInitialMinutes = MutableStateFlow(25)
    val timerInitialMinutes: StateFlow<Int> = _timerInitialMinutes.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _selectedTimerMode = MutableStateFlow("Deep Focus")
    val selectedTimerMode: StateFlow<String> = _selectedTimerMode.asStateFlow()

    private var timerJob: Job? = null

    // Theme state
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = AuraRepository(database.habitDao(), database.moodDao(), database.focusDao())

        val habitsFlow = MutableStateFlow<List<HabitEntity>>(emptyList())
        habits = habitsFlow.asStateFlow()

        val moodLogsFlow = MutableStateFlow<List<MoodEntity>>(emptyList())
        moodLogs = moodLogsFlow.asStateFlow()

        val focusSessionsFlow = MutableStateFlow<List<FocusSessionEntity>>(emptyList())
        focusSessions = focusSessionsFlow.asStateFlow()

        val totalMinutesFlow = MutableStateFlow(0)
        totalFocusMinutes = totalMinutesFlow.asStateFlow()

        viewModelScope.launch {
            repository.allHabits.collect { list ->
                if (list.isEmpty()) {
                    repository.seedInitialDataIfEmpty()
                } else {
                    habitsFlow.value = list
                }
            }
        }

        viewModelScope.launch {
            repository.allMoodLogs.collect { list ->
                moodLogsFlow.value = list
            }
        }

        viewModelScope.launch {
            repository.allFocusSessions.collect { list ->
                focusSessionsFlow.value = list
            }
        }

        viewModelScope.launch {
            repository.totalFocusMinutes.collect { mins ->
                totalMinutesFlow.value = mins ?: 0
            }
        }
    }

    fun toggleHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habit)
            if (!habit.isCompletedToday) {
                _eventFlow.emit(UiEvent.TriggerCelebration)
            }
        }
    }

    fun addHabit(title: String, description: String, category: String, colorHex: String) {
        viewModelScope.launch {
            val newHabit = HabitEntity(
                title = title,
                description = description,
                category = category,
                colorHex = colorHex
            )
            repository.insertHabit(newHabit)
            _eventFlow.emit(UiEvent.ShowToast("Habit added successfully!"))
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            _eventFlow.emit(UiEvent.ShowToast("Habit removed."))
        }
    }

    fun logMood(rating: Int, moodName: String, note: String, tags: String) {
        viewModelScope.launch {
            val mood = MoodEntity(
                moodRating = rating,
                moodName = moodName,
                note = note,
                tags = tags
            )
            repository.insertMood(mood)
            _eventFlow.emit(UiEvent.ShowToast("Logged mood: $moodName"))
        }
    }

    // Timer controls
    fun setTimerDuration(minutes: Int) {
        if (!_isTimerRunning.value) {
            _timerInitialMinutes.value = minutes
            _timerSeconds.value = minutes * 60
        }
    }

    fun setTimerMode(mode: String) {
        _selectedTimerMode.value = mode
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    private fun startTimer() {
        _isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value && _timerSeconds.value > 0) {
                delay(1000L)
                _timerSeconds.value -= 1
            }
            if (_timerSeconds.value <= 0 && _isTimerRunning.value) {
                _isTimerRunning.value = false
                completeSession()
            }
        }
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
    }

    fun resetTimer() {
        pauseTimer()
        _timerSeconds.value = _timerInitialMinutes.value * 60
    }

    private fun completeSession() {
        viewModelScope.launch {
            val session = FocusSessionEntity(
                durationMinutes = _timerInitialMinutes.value,
                mode = _selectedTimerMode.value,
                completed = true
            )
            repository.insertFocusSession(session)
            _eventFlow.emit(UiEvent.TriggerCelebration)
            _eventFlow.emit(UiEvent.ShowToast("Session Completed! Great focus!"))
        }
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
            repository.seedInitialDataIfEmpty()
            _eventFlow.emit(UiEvent.ShowToast("Data reset to defaults."))
        }
    }
}
