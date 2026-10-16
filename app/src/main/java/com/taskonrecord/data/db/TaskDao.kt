package com.taskonrecord.data.db

import androidx.room.*
import com.taskonrecord.data.model.Task
import com.taskonrecord.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE categoryId = :categoryId ORDER BY createdAt ASC")
    fun getTasksByCategory(categoryId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Query("SELECT * FROM tasks WHERE status != 'DONE' AND dueDateMillis IS NOT NULL AND dueDateMillis < :now")
    suspend fun getOverdueTasks(now: Long = System.currentTimeMillis()): List<Task>

    @Query("SELECT * FROM tasks WHERE reminderMillis IS NOT NULL AND status != 'DONE'")
    suspend fun getTasksWithReminders(): List<Task>

    @Query("UPDATE tasks SET status = :status WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, status: TaskStatus)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("SELECT COUNT(*) FROM tasks WHERE categoryId = :categoryId AND status != 'DONE'")
    fun getPendingTaskCount(categoryId: Long): Flow<Int>
}
