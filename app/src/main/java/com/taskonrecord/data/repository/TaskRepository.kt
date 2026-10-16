package com.taskonrecord.data.repository

import com.taskonrecord.data.db.CategoryDao
import com.taskonrecord.data.db.TaskDao
import com.taskonrecord.data.model.*
import kotlinx.coroutines.flow.Flow

class TaskRepository(
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao
) {
    // ── Categories ──────────────────────────────────────────────
    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)

    suspend fun insertCategory(category: Category): Long = categoryDao.insertCategory(category)

    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)

    // ── Tasks ────────────────────────────────────────────────────
    fun getTasksByCategory(categoryId: Long): Flow<List<Task>> =
        taskDao.getTasksByCategory(categoryId)

    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()

    suspend fun getTaskById(id: Long): Task? = taskDao.getTaskById(id)

    suspend fun insertTask(task: Task): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: Task) = taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    suspend fun markTaskDone(id: Long) {
        taskDao.updateTaskStatus(id, TaskStatus.DONE)
        taskDao.getTaskById(id)?.let { task ->
            taskDao.updateTask(task.copy(status = TaskStatus.DONE, completedAt = System.currentTimeMillis()))
        }
    }

    suspend fun updateOverdueTasks() {
        val now = System.currentTimeMillis()
        val overdue = taskDao.getOverdueTasks(now)
        overdue.forEach { task ->
            if (task.status == TaskStatus.PENDING || task.status == TaskStatus.IN_PROGRESS) {
                taskDao.updateTask(task.copy(status = TaskStatus.OVERDUE))
            }
        }
    }

    fun getPendingTaskCount(categoryId: Long): Flow<Int> = taskDao.getPendingTaskCount(categoryId)

    suspend fun getTasksWithReminders(): List<Task> = taskDao.getTasksWithReminders()
}
