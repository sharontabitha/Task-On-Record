package com.taskonrecord.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isPrivate: Boolean = false
)
