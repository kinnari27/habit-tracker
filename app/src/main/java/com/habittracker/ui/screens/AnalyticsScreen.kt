package com.habittracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habittracker.ui.components.CompletionHeatmap
import com.habittracker.ui.components.WeeklyProgressChart
import com.habittracker.ui.theme.OchreAmber
import com.habittracker.data.HabitProgress
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val listState by viewModel.habitsState.collectAsState()
    val scrollState = rememberScrollState()

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Parse all completed dates overall
    val completedDates = remember(listState.allProgress) {
        listState.allProgress
            .mapNotNull {
                try {
                    LocalDate.parse(it.completionDate, dateFormatter)
                } catch (e: Exception) {
                    null
                }
            }
            .distinct()
    }

    // Streaks calculation
    val maxCurrentStreak = remember(listState.streakStats) {
        listState.streakStats.values.maxOfOrNull { it.currentStreak } ?: 0
    }
    val maxLongestStreak = remember(listState.streakStats) {
        listState.streakStats.values.maxOfOrNull { it.longestStreak } ?: 0
    }
    val totalCompletions = listState.allProgress.size

    // Calculate completions for the last 7 days ending today
    val weeklyCompletions = remember(listState.allProgress) {
        val today = LocalDate.now()
        val range = (0..6).map { today.minusDays(6 - it.toLong()) }

        range.associateWith { date ->
            val dateStr = date.format(dateFormatter)
            listState.allProgress.count { it.completionDate == dateStr }
        }
    }

    // Milestones/Badges
    val badges = listOf(
        BadgeItem(
            name = "First Scribble",
            desc = "Logged your first habit completion.",
            isUnlocked = totalCompletions > 0,
            iconColor = MaterialTheme.colorScheme.secondary
        ),
        BadgeItem(
            name = "Weekly Warrior",
            desc = "Achieved a streak of 7 days on any habit.",
            isUnlocked = maxLongestStreak >= 7,
            iconColor = OchreAmber
        ),
        BadgeItem(
            name = "Unstoppable",
            desc = "Achieved a streak of 30 days.",
            isUnlocked = maxLongestStreak >= 30,
            iconColor = MaterialTheme.colorScheme.primary
        ),
        BadgeItem(
            name = "Diverse Journal",
            desc = "Created habits in at least 3 categories.",
            isUnlocked = listState.habits.map { it.category }.distinct().size >= 3,
            iconColor = MaterialTheme.colorScheme.tertiary
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Analytics",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Your journal consistency and metrics",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Metrics Grid Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Current Streak",
                value = "$maxCurrentStreak d",
                desc = "Max active",
                modifier = Modifier.weight(1f),
                tint = MaterialTheme.colorScheme.secondary
            )
            MetricCard(
                title = "Longest Streak",
                value = "$maxLongestStreak d",
                desc = "All time record",
                modifier = Modifier.weight(1f),
                tint = OchreAmber
            )
            MetricCard(
                title = "Total Logged",
                value = "$totalCompletions",
                desc = "Completions",
                modifier = Modifier.weight(1f),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Heatmap Consistency Grid
        CompletionHeatmap(completedDates = completedDates)

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly Progress Chart
        WeeklyProgressChart(
            weeklyCompletions = weeklyCompletions,
            maxPossible = listState.habits.size
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Badges Section
        Text(
            text = "Unlocked Milestones",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            badges.forEach { badge ->
                BadgeCard(badge = badge)
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    desc: String,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.border(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            shape = RoundedCornerShape(16.dp)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = tint,
                fontSize = 22.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun BadgeCard(badge: BadgeItem) {
    val borderCol = if (badge.isUnlocked) {
        MaterialTheme.colorScheme.outline
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    }

    val cardColor = if (badge.isUnlocked) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, borderCol, RoundedCornerShape(14.dp))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (badge.isUnlocked) badge.iconColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.outline.copy(
                            alpha = 0.1f
                        ),
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = if (badge.isUnlocked) badge.iconColor.copy(alpha = 0.4f) else Color.Transparent,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = if (badge.isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                    contentDescription = badge.name,
                    tint = if (badge.isUnlocked) badge.iconColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.4f
                    ),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = badge.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.5f
                    ),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = badge.desc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(
                        alpha = 0.4f
                    )
                )
            }
        }
    }
}

data class BadgeItem(
    val name: String,
    val desc: String,
    val isUnlocked: Boolean,
    val iconColor: Color
)
