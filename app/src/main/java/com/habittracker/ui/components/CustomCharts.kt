package com.habittracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CompletionHeatmap(
    completedDates: List<LocalDate>,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    // We want a 7x5 grid representing 35 days ending today.
    // To align properly, we find the start date (34 days ago)
    val totalDays = 35
    val startDate = today.minusDays((totalDays - 1).toLong())
    val completedSet = completedDates.toSet()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Last 5 Weeks Consistency",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Day of week labels (M, W, F)
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.height(110.dp)
                ) {
                    Text(
                        "M",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "W",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "F",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Columns of Weeks
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (weekIndex in 0 until 5) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            for (dayIndex in 0 until 7) {
                                val dayOffset = weekIndex * 7 + dayIndex
                                val cellDate = startDate.plusDays(dayOffset.toLong())
                                val isCompleted = completedSet.contains(cellDate)
                                val isFuture = cellDate.isAfter(today)

                                val color = when {
                                    isFuture -> Color.Transparent
                                    isCompleted -> MaterialTheme.colorScheme.secondary // Sage Green
                                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f) // Empty light paper gray
                                }

                                val borderModifier = if (isFuture) {
                                    Modifier.size(12.dp)
                                } else {
                                    Modifier
                                        .size(12.dp)
                                        .background(color = color, shape = RoundedCornerShape(3.dp))
                                        .border(
                                            width = 0.5.dp,
                                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                            shape = RoundedCornerShape(3.dp)
                                        )
                                }

                                Box(modifier = borderModifier)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Less",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                            RoundedCornerShape(2.dp)
                        )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "More",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun WeeklyProgressChart(
    weeklyCompletions: Map<LocalDate, Int>, // Date to number of completed habits
    maxPossible: Int,
    modifier: Modifier = Modifier
) {
    val barColor = MaterialTheme.colorScheme.primary // Terracotta
    val emptyBarColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val textColor = MaterialTheme.colorScheme.onSurface
    val subTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.5.dp,
                color = outlineColor,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Weekly Activity",
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Number of habits completed over the last 7 days",
                style = MaterialTheme.typography.bodyMedium,
                color = subTextColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val sortedDays = weeklyCompletions.keys.sorted()

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val width = size.width
                val height = size.height
                val barCount = 7
                val paddingBetween = 24.dp.toPx()
                val availableWidth = width - (paddingBetween * (barCount - 1))
                val barWidth = availableWidth / barCount

                sortedDays.forEachIndexed { index, date ->
                    val completedCount = weeklyCompletions[date] ?: 0
                    val ratio = if (maxPossible > 0) completedCount.toFloat() / maxPossible else 0f
                    val barHeight = height * ratio

                    val x = index * (barWidth + paddingBetween)

                    // Draw background empty track (sketch style)
                    drawRoundRect(
                        color = emptyBarColor,
                        topLeft = Offset(x, 0f),
                        size = Size(barWidth, height),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )

                    // Draw actual completions bar
                    if (barHeight > 0) {
                        drawRoundRect(
                            color = barColor,
                            topLeft = Offset(x, height - barHeight),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Day Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                sortedDays.forEach { date ->
                    val dayName =
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                    Text(
                        text = dayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = subTextColor,
                        modifier = Modifier.width(36.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}
