package com.taskonrecord.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Priority levels for tasks.
 */
enum class Priority { LOW, MEDIUM, HIGH }

/**
 * Task completion/progress states — each maps to a visual character state.
 */
enum class TaskStatus {
    PENDING,      // Character is idle/sleeping
    IN_PROGRESS,  // Character is active/moving
    DONE,         // Character dances / plant blooms
    OVERDUE       // Character looks sad / plant wilts
}

/**
 * The "resident" character type within a house.
 * College → animal, Home → human, Garden → plant
 */
enum class ResidentType {
    // College residents
    PIG, DOG, CAT, MOUSE,
    // Home residents
    BOY, GIRL, BABY, GRANNY,
    // Garden residents
    SUNFLOWER, ROSE, TULIP,   // flowers
    WHEAT, CARROT, CORN        // crops
}

/**
 * A single task — represented as a living resident in its category house.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val status: TaskStatus = TaskStatus.PENDING,
    val residentType: ResidentType,
    val dueDateMillis: Long? = null,     // null = no due date
    val reminderMillis: Long? = null,    // exact alarm time
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
