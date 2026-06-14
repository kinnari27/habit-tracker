package com.habittracker

import android.app.Application
import com.habittracker.data.HabitDatabase
import com.habittracker.data.HabitRepository

class HabitApp : Application() {

    val database: HabitDatabase by lazy {
        HabitDatabase.getDatabase(this)
    }

    val repository: HabitRepository by lazy {
        HabitRepository(database.dao)
    }

    override fun onCreate() {
        super.onCreate()
        // Here we can initialize notifications or logger later
    }
}
