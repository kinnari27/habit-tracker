package com.habittracker.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.habittracker.data.Habit
import com.habittracker.data.HabitProgress
import com.habittracker.data.HabitRepository
import com.habittracker.data.StreakStats
import com.habittracker.notification.HabitNotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainViewModel(
    private val repository: HabitRepository,
    private val notificationHelper: HabitNotificationHelper
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(null) // null = system default
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    // Reactively combine habits and completion logs
    val habitsState: StateFlow<HabitListState> = combine(
        repository.allHabits,
        repository.allProgress,
        _selectedDate
    ) { habits, progress, date ->
        val dateString = date.format(dateFormatter)

        // Map completions for the selected date
        val completedIds = progress
            .filter { it.completionDate == dateString }
            .map { it.habitId }
            .toSet()

        // Calculate streaks for all habits
        val streaks = habits.associate { habit ->
            val completions = progress.filter { it.habitId == habit.id }
            habit.id to repository.calculateStreak(completions)
        }

        HabitListState(
            habits = habits,
            completedHabitIds = completedIds,
            streakStats = streaks,
            allProgress = progress
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HabitListState()
    )

    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    fun resetThemeToSystem() {
        _isDarkMode.value = null
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            val id = repository.insertHabit(habit)
            if (habit.isReminderEnabled && !habit.reminderTime.isNullOrEmpty()) {
                notificationHelper.scheduleHabitReminder(
                    id.toInt(),
                    habit.title,
                    habit.reminderTime
                )
            }
        }
    }

    fun toggleHabitCompletion(habitId: Int) {
        viewModelScope.launch {
            repository.toggleHabitCompletion(habitId, _selectedDate.value)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
            notificationHelper.cancelHabitReminder(habit.id)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            habitsState.value.habits.forEach { habit ->
                notificationHelper.cancelHabitReminder(habit.id)
            }
            repository.clearAllData()
        }
    }

    fun triggerTestReminder(habit: Habit) {
        notificationHelper.triggerInstantTestNotification(habit.title)
    }

    // Custom ViewModel Factory for Manual DI injection
    class Factory(
        private val repository: HabitRepository,
        private val notificationHelper: HabitNotificationHelper
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(repository, notificationHelper) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class HabitListState(
    val habits: List<Habit> = emptyList(),
    val completedHabitIds: Set<Int> = emptySet(),
    val streakStats: Map<Int, StreakStats> = emptyMap(),
    val allProgress: List<HabitProgress> = emptyList()
)
