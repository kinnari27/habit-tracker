package com.habittracker.data

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

data class StreakStats(
    val currentStreak: Int,
    val longestStreak: Int
)

class HabitRepository(private val dao: HabitDao) {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val allHabits: Flow<List<Habit>> = dao.getAllHabits()
    val allProgress: Flow<List<HabitProgress>> = dao.getAllProgress()

    suspend fun insertHabit(habit: Habit): Long = dao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) = dao.updateHabit(habit)

    suspend fun deleteHabit(habit: Habit) = dao.deleteHabit(habit)

    suspend fun toggleHabitCompletion(habitId: Int, date: LocalDate) {
        val dateString = date.format(dateFormatter)
        val progressList = dao.getProgressForHabit(habitId).first()
        val exists = progressList.any { it.completionDate == dateString }

        if (exists) {
            dao.deleteProgress(habitId, dateString)
        } else {
            dao.insertProgress(HabitProgress(habitId, dateString))
        }
    }

    fun getProgressForHabit(habitId: Int): Flow<List<HabitProgress>> =
        dao.getProgressForHabit(habitId)

    fun getProgressForDate(date: LocalDate): Flow<List<HabitProgress>> {
        return dao.getProgressForDate(date.format(dateFormatter))
    }

    fun calculateStreak(completions: List<HabitProgress>): StreakStats {
        if (completions.isEmpty()) return StreakStats(0, 0)

        // Parse unique sorted dates (ascending)
        val completedDates = completions
            .map { LocalDate.parse(it.completionDate, dateFormatter) }
            .distinct()
            .sorted()

        val today = LocalDate.now()
        val yesterday = today.minusDays(1)

        // 1. Calculate Current Streak
        var currentStreak = 0
        val latestDate = completedDates.last()

        // If latest completion is today or yesterday, we have a continuous streak
        if (latestDate == today || latestDate == yesterday) {
            var checkDate = latestDate
            var index = completedDates.size - 1

            while (index >= 0) {
                if (completedDates[index] == checkDate) {
                    currentStreak++
                    checkDate = checkDate.minusDays(1)
                    index--
                } else if (completedDates[index].isAfter(checkDate)) {
                    // Skip if duplicate or future (should not happen due to sorting and uniqueness)
                    index--
                } else {
                    // Gap detected in streak
                    break
                }
            }
        }

        // 2. Calculate Longest Streak
        var longestStreak = 0
        var tempStreak = 0
        var prevDate: LocalDate? = null

        for (date in completedDates) {
            if (prevDate == null) {
                tempStreak = 1
            } else {
                val diff = ChronoUnit.DAYS.between(prevDate, date)
                if (diff == 1L) {
                    tempStreak++
                } else if (diff > 1L) {
                    longestStreak = maxOf(longestStreak, tempStreak)
                    tempStreak = 1
                }
            }
            prevDate = date
        }
        longestStreak = maxOf(longestStreak, tempStreak)

        return StreakStats(currentStreak, longestStreak)
    }

    suspend fun clearAllData() {
        dao.deleteAllHabits()
    }
}
