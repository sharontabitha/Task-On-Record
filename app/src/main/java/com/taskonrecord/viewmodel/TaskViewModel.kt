package com.taskonrecord.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.taskonrecord.data.db.AppDatabase
import com.taskonrecord.data.model.*
import com.taskonrecord.data.repository.TaskRepository
import com.taskonrecord.notification.TaskScheduler
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = TaskRepository(db.categoryDao(), db.taskDao())

    // For editing an existing task (null if creating new)
    val taskId: Long? = savedStateHandle.get<Long>("taskId")?.takeIf { it != -1L }
    val categoryId: Long = checkNotNull(savedStateHandle["categoryId"])

    val existingTask: StateFlow<Task?> = if (taskId != null) {
        flow { emit(repo.getTaskById(taskId)) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    } else {
        MutableStateFlow(null)
    }

    val category: StateFlow<Category?> = flow {
        emit(repo.getCategoryById(categoryId))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveTask(
        title: String,
        description: String,
        priority: Priority,
        residentType: ResidentType,
        dueDateMillis: Long?,
        reminderMillis: Long?,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            val task = if (taskId != null) {
                // Update existing
                val existing = repo.getTaskById(taskId) ?: return@launch
                existing.copy(
                    title = title,
                    description = description,
                    priority = priority,
                    residentType = residentType,
                    dueDateMillis = dueDateMillis,
                    reminderMillis = reminderMillis
                )
            } else {
                // Create new
                Task(
                    categoryId = categoryId,
                    title = title,
                    description = description,
                    priority = priority,
                    residentType = residentType,
                    dueDateMillis = dueDateMillis,
                    reminderMillis = reminderMillis
                )
            }

            val id = if (taskId != null) {
                repo.updateTask(task)
                taskId
            } else {
                repo.insertTask(task)
            }

            // Schedule reminder if set
            if (reminderMillis != null && reminderMillis > System.currentTimeMillis()) {
                val savedTask = repo.getTaskById(id) ?: task.copy(id = id)
                TaskScheduler.scheduleReminder(getApplication(), savedTask)
            }

            onSaved()
        }
    }
}
