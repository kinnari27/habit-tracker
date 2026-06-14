package com.habittracker.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // Maps to Category enum name
    val colorHex: String, // Warm Theme color code (e.g., "#D67C65")
    val reminderTime: String? = null, // Format: "HH:mm"
    val isReminderEnabled: Boolean = false,
    val dateCreated: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habit_progress",
    primaryKeys = ["habitId", "completionDate"],
    foreignKeys = [
        ForeignKey(
            entity = Habit::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("habitId")]
)
data class HabitProgress(
    val habitId: Int,
    val completionDate: String // Format: "yyyy-MM-dd"
)

enum class Category(val displayName: String, val iconName: String, val defaultColorHex: String) {
    HEALTH("Health", "DirectionsRun", "#8D997F"),     // Sage Green
    MIND("Mind", "SelfImprovement", "#E28B75"),      // Terracotta
    WORK("Work", "Computer", "#8294A5"),             // Slate Blue
    FINANCE("Finance", "Payments", "#E5B25D"),       // Amber Gold
    SOCIAL("Social", "Diversity3", "#D1A39E")        // Dusty Rose
}
