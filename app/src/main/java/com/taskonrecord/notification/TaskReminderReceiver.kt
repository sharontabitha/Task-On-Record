package com.taskonrecord.notification

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.taskonrecord.MainActivity
import com.taskonrecord.R
import com.taskonrecord.TaskOnRecordApp

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("task_id", -1L)
        val taskTitle = intent.getStringExtra("task_title") ?: "Task Reminder"
        val isDueSoon = intent.getBooleanExtra("is_due_soon", false)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("navigate_to_task", taskId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, taskId.toInt(), openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isDueSoon) "🚨 Due in 5 Hours!" else "⏰ Village Alarm Ringing!"
        val text = if (isDueSoon)
            "Resident's task '$taskTitle' is due in 5 hours! Hurry up!"
        else
            "Your village resident rings the alarm: $taskTitle"

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notification = NotificationCompat.Builder(context, TaskOnRecordApp.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 500, 500, 500, 500))
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify((taskId + if (isDueSoon) 1000 else 0).toInt(), notification)
    }
}
