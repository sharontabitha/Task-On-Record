package com.taskonrecord.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.taskonrecord.data.db.AppDatabase
import com.taskonrecord.data.model.*
import com.taskonrecord.data.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VillageViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val repo = TaskRepository(db.categoryDao(), db.taskDao())
    private val noteDao = db.noteDao()

    val categories: StateFlow<List<Category>> = repo.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<Note>> = noteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCategory(name: String, type: HouseType, description: String = "") {
        viewModelScope.launch {
            repo.insertCategory(Category(name = name, type = type, description = description))
        }
    }

    fun updateCategoryName(category: Category, newName: String) {
        viewModelScope.launch {
            repo.updateCategory(category.copy(name = newName))
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repo.deleteCategory(category)
        }
    }

    fun getPendingCount(categoryId: Long): Flow<Int> = repo.getPendingTaskCount(categoryId)

    fun getTasks(categoryId: Long): Flow<List<Task>> = repo.getTasksByCategory(categoryId)

    fun addNote(title: String, content: String, isPrivate: Boolean) {
        viewModelScope.launch {
            if (title.isNotBlank() || content.isNotBlank()) {
                noteDao.insertNote(Note(title = title.ifBlank { "Diary Entry" }, content = content, isPrivate = isPrivate))
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
        }
    }
}
