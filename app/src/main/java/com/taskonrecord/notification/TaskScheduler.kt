package com.taskonrecord.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.taskonrecord.data.model.Task

object TaskScheduler {

    private const val EXTRA_TASK_ID = "task_id"
    private const val EXTRA_TASK_TITLE = "task_title"
    private const val EXTRA_IS_DUE_SOON = "is_due_soon"

    fun scheduleReminder(context: Context, task: Task) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) return
        }

        // 1. Schedule user custom reminder if set
        task.reminderMillis?.let { reminderTime ->
            if (reminderTime > System.currentTimeMillis()) {
                val intent = Intent(context, TaskReminderReceiver::class.java).apply {
                    action = "com.taskonrecord.ACTION_TASK_REMINDER"
                    putExtra(EXTRA_TASK_ID, task.id)
                    putExtra(EXTRA_TASK_TITLE, task.title)
                    putExtra(EXTRA_IS_DUE_SOON, false)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (task.id * 10).toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminderTime,
                    pendingIntent
                )
            }
        }

        // 2. Schedule automatic notification 5 hours before due date if due date is set
        task.dueDateMillis?.let { dueDate ->
            val fiveHoursBefore = dueDate - (5 * 60 * 60 * 1000L)
            if (fiveHoursBefore > System.currentTimeMillis()) {
                val intent = Intent(context, TaskReminderReceiver::class.java).apply {
                    action = "com.taskonrecord.ACTION_TASK_REMINDER"
                    putExtra(EXTRA_TASK_ID, task.id)
                    putExtra(EXTRA_TASK_TITLE, task.title)
                    putExtra(EXTRA_IS_DUE_SOON, true)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    (task.id * 10 + 1).toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    fiveHoursBefore,
                    pendingIntent
                )
            }
        }
    }

    fun cancelReminder(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        // Cancel custom reminder
        val intent1 = Intent(context, TaskReminderReceiver::class.java)
        val pi1 = PendingIntent.getBroadcast(
            context,
            (taskId * 10).toInt(),
            intent1,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pi1)

        // Cancel due-soon reminder
        val intent2 = Intent(context, TaskReminderReceiver::class.java)
        val pi2 = PendingIntent.getBroadcast(
            context,
            (taskId * 10 + 1).toInt(),
            intent2,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pi2)
    }
}
