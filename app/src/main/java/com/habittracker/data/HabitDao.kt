package com.habittracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits ORDER BY dateCreated DESC")
    fun getAllHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: Int): Flow<Habit?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    // Habit Progress queries
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: HabitProgress)

    @Query("DELETE FROM habit_progress WHERE habitId = :habitId AND completionDate = :date")
    suspend fun deleteProgress(habitId: Int, date: String)

    @Query("SELECT * FROM habit_progress WHERE habitId = :habitId")
    fun getProgressForHabit(habitId: Int): Flow<List<HabitProgress>>

    @Query("SELECT * FROM habit_progress WHERE completionDate = :date")
    fun getProgressForDate(date: String): Flow<List<HabitProgress>>

    @Query("SELECT * FROM habit_progress")
    fun getAllProgress(): Flow<List<HabitProgress>>

    @Query("DELETE FROM habits")
    suspend fun deleteAllHabits()
}
