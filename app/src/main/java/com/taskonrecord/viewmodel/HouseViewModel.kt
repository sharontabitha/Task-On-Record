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

class HouseViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = TaskRepository(db.categoryDao(), db.taskDao())

    val categoryId: Long = checkNotNull(savedStateHandle["categoryId"])

    val category: StateFlow<Category?> = flow {
        emit(repo.getCategoryById(categoryId))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tasks: StateFlow<List<Task>> = repo.getTasksByCategory(categoryId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            TaskScheduler.cancelReminder(getApplication(), task.id)
            repo.deleteTask(task)
        }
    }

    fun markTaskDone(task: Task) {
        viewModelScope.launch {
            TaskScheduler.cancelReminder(getApplication(), task.id)
            repo.markTaskDone(task.id)
        }
    }

    fun updateTaskStatus(task: Task, status: TaskStatus) {
        viewModelScope.launch {
            repo.updateTask(task.copy(status = status))
        }
    }
}
