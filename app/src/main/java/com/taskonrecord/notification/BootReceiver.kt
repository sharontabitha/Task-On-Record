package com.taskonrecord.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taskonrecord.data.db.AppDatabase
import com.taskonrecord.data.repository.TaskRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers

/**
 * Re-schedules all task reminders after device reboot,
 * since AlarmManager alarms are cleared on reboot.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            GlobalScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getInstance(context)
                val repo = TaskRepository(db.categoryDao(), db.taskDao())
                val tasks = repo.getTasksWithReminders()
                tasks.forEach { task ->
                    TaskScheduler.scheduleReminder(context, task)
                }
            }
        }
    }
}
