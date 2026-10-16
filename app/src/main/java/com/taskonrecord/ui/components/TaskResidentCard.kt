package com.taskonrecord.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taskonrecord.data.model.*
import com.taskonrecord.ui.theme.*

/**
 * A single task card shown inside a house screen.
 * Displays the pixel character, task name, priority, and status.
 */
@Composable
fun TaskResidentCard(
    task: Task,
    onClick: () -> Unit,
    onMarkDone: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statusColor = when (task.status) {
        TaskStatus.PENDING -> StatusPending
        TaskStatus.IN_PROGRESS -> StatusInProgress
        TaskStatus.DONE -> StatusDone
        TaskStatus.OVERDUE -> StatusOverdue
    }

    val priorityColor = when (task.priority) {
        Priority.LOW -> PriorityLow
        Priority.MEDIUM -> PriorityMedium
        Priority.HIGH -> PriorityHigh
    }

    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = statusColor.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Pixel Character
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(DarkNight, RoundedCornerShape(4.dp))
                    .border(1.dp, CardBorder, RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                PixelCharacter(
                    residentType = task.residentType,
                    status = task.status,
                    size = 48.dp
                )
            }

            // Task info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = StarWhite,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Status badge
                    StatusBadge(status = task.status)
                    // Priority badge
                    PriorityBadge(priority = task.priority)
                }
                // Due date
                task.dueDateMillis?.let { due ->
                    Spacer(modifier = Modifier.height(4.dp))
                    val isOverdue = due < System.currentTimeMillis() && task.status != TaskStatus.DONE
                    Text(
                        text = "📅 ${formatDate(due)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOverdue) StatusOverdue else PixelGray
                    )
                }
            }

            // Quick actions
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (task.status != TaskStatus.DONE) {
                    IconButton(
                        onClick = onMarkDone,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("✅", fontSize = 16.sp)
                    }
                }
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("⋮", fontSize = 20.sp, color = PixelGray)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = { showMenu = false; onClick() }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = StatusOverdue) },
                            onClick = { showMenu = false; onDelete() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: TaskStatus) {
    val (label, color) = when (status) {
        TaskStatus.PENDING -> "💤 Idle" to StatusPending
        TaskStatus.IN_PROGRESS -> "⚡ Active" to StatusInProgress
        TaskStatus.DONE -> "🌟 Done" to StatusDone
        TaskStatus.OVERDUE -> "😢 Overdue" to StatusOverdue
    }
    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PriorityBadge(priority: Priority) {
    val (label, color) = when (priority) {
        Priority.LOW -> "↓ Low" to PriorityLow
        Priority.MEDIUM -> "→ Med" to PriorityMedium
        Priority.HIGH -> "↑ High" to PriorityHigh
    }
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatDate(millis: Long): String {
    val sdf = java.text.SimpleDateFormat("MMM d, h:mm a", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(millis))
}
