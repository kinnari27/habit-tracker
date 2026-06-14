package com.habittracker.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.habittracker.data.Habit
import com.habittracker.ui.theme.OchreAmber

@Composable
fun HabitCard(
    habit: Habit,
    isCompleted: Boolean,
    streak: Int,
    onToggleComplete: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val outlineColor = MaterialTheme.colorScheme.outline.copy(alpha = if (enabled) 0.5f else 0.2f)
    val cardColor = if (isCompleted) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (enabled) 0.6f else 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    val scale by animateFloatAsState(
        targetValue = if (isCompleted && enabled) 1.05f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "CompleteScale"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .combinedClickable(
                onClick = {}, // Handle click elsewhere
                onLongClick = onLongClick
            )
            .border(
                width = 1.5.dp,
                color = outlineColor,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Circle
            val categoryColor = Color(android.graphics.Color.parseColor(habit.colorHex))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .background(categoryColor.copy(alpha = 0.15f), CircleShape)
                    .border(1.dp, categoryColor.copy(alpha = 0.4f), CircleShape)
            ) {
                Icon(
                    imageVector = getCategoryIcon(habit.category),
                    contentDescription = habit.category,
                    tint = categoryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Habit Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (habit.description.isNotEmpty()) {
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Streak Badge & Reminder Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (streak > 0) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = OchreAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "$streak day streak",
                            style = MaterialTheme.typography.labelSmall,
                            color = OchreAmber
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    if (habit.isReminderEnabled && !habit.reminderTime.isNullOrEmpty()) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Reminder",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = habit.reminderTime,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Beautiful Circular Toggle Checkbox
            val checkboxColor by animateColorAsState(
                targetValue = if (isCompleted) MaterialTheme.colorScheme.secondary else Color.Transparent,
                label = "CheckboxBgColor"
            )
            val checkboxBorderColor by animateColorAsState(
                targetValue = if (isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
                label = "CheckboxBorderColor"
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(checkboxColor.copy(alpha = if (enabled) 1f else 0.4f))
                    .border(
                        1.5.dp,
                        checkboxBorderColor.copy(alpha = if (enabled) 1f else 0.4f),
                        CircleShape
                    )
                    .clickable(enabled = enabled) { onToggleComplete() }
            ) {
                if (isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

fun getCategoryIcon(categoryName: String): ImageVector {
    return when (categoryName) {
        "HEALTH" -> Icons.Default.DirectionsRun
        "MIND" -> Icons.Default.SelfImprovement
        "WORK" -> Icons.Default.Computer
        "FINANCE" -> Icons.Default.Payments
        "SOCIAL" -> Icons.Default.Diversity3
        else -> Icons.Default.Star
    }
}
