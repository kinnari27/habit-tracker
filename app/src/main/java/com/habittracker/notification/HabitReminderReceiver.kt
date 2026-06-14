package com.habittracker.notification

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.habittracker.HabitApp
import com.habittracker.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HabitReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val helper = HabitNotificationHelper(context)

        when (intent.action) {
            HabitNotificationHelper.ACTION_SHOW_REMINDER -> {
                val habitId = intent.getIntExtra(HabitNotificationHelper.EXTRA_HABIT_ID, 0)
                val habitTitle =
                    intent.getStringExtra(HabitNotificationHelper.EXTRA_HABIT_TITLE) ?: "Habit"

                // Post the reminder notification
                showNotification(context, habitId, habitTitle)

                // Reschedule for tomorrow (since one-shot alarms are used on Android)
                val app = context.applicationContext as HabitApp
                CoroutineScope(Dispatchers.IO).launch {
                    val habit = app.repository.allHabits.first().find { it.id == habitId }
                    if (habit != null && habit.isReminderEnabled && !habit.reminderTime.isNullOrEmpty()) {
                        helper.scheduleHabitReminder(habit.id, habit.title, habit.reminderTime)
                    }
                }
            }

            Intent.ACTION_BOOT_COMPLETED -> {
                // Device rebooted. Reschedule all active reminders.
                val app = context.applicationContext as HabitApp
                val notificationHelper = HabitNotificationHelper(context)

                CoroutineScope(Dispatchers.IO).launch {
                    val habits = app.repository.allHabits.first()
                    habits.forEach { habit ->
                        if (habit.isReminderEnabled && !habit.reminderTime.isNullOrEmpty()) {
                            notificationHelper.scheduleHabitReminder(
                                habit.id,
                                habit.title,
                                habit.reminderTime
                            )
                        }
                    }
                }
            }
        }
    }

    private fun showNotification(context: Context, habitId: Int, habitTitle: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            habitId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Quotes to inspire habit updates
        val quotes = listOf(
            "Little strokes fell great oaks. 📒",
            "Consistency is key. Time to check in! ✨",
            "Be proud of showing up. Log your habit! 🌸",
            "One step closer to your goals. 🚀",
            "Today's effort is tomorrow's ease. 📒"
        )
        val randomQuote = quotes.random()

        val notification = NotificationCompat.Builder(context, HabitNotificationHelper.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Fallback alarm icon
            .setContentTitle("Journal Check-In: $habitTitle")
            .setContentText(randomQuote)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManagerCompat = NotificationManagerCompat.from(context)
            notificationManagerCompat.notify(habitId, notification)
        } catch (e: SecurityException) {
            // Missing notifications permission
        }
    }
}
