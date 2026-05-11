package com.suryashaktiapp.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.suryashaktiapp.MainActivity

object NotificationHelper {

    private const val CHANNEL_ID = "surya_shakti_channel"
    private const val CHANNEL_NAME = "Surya-Shakti Alerts"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Solar energy alerts and reminders"
            }
            val manager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showBatteryLowAlert(context: Context, batteryLevel: Float) {
        if (batteryLevel > 20f) return
        showNotification(
            context,
            id = 1,
            title = "🔋 Battery Low Alert!",
            message = "Battery at ${batteryLevel.toInt()}%! " +
                    "Minimize usage to conserve power."
        )
    }

    fun showPeakSunAlert(context: Context) {
        showNotification(
            context,
            id = 2,
            title = "☀️ Peak Sun Hours!",
            message = "10 AM - 3 PM: Ideal time to run " +
                    "washing machine, pump & iron!"
        )
    }

    fun showOverGenerationAlert(context: Context, exportKwh: Float) {
        showNotification(
            context,
            id = 3,
            title = "⚡ Over-Generation Detected!",
            message = "You are exporting ${
                String.format("%.2f", exportKwh)
            } kWh to grid! Great job! 🌱"
        )
    }

    fun showHighUsageAlert(context: Context, consumed: Float, generated: Float) {
        if (consumed <= generated) return
        showNotification(
            context,
            id = 4,
            title = "⚠️ High Energy Usage!",
            message = "Consuming more than generating! " +
                    "Reduce usage to save money."
        )
    }

    fun showDailyReminder(context: Context) {
        showNotification(
            context,
            id = 5,
            title = "📋 Daily Log Reminder",
            message = "Don't forget to log today's solar " +
                    "energy generation! ☀️"
        )
    }

    fun showWeeklySummary(context: Context, totalSavings: Float) {
        showNotification(
            context,
            id = 6,
            title = "📊 Weekly Summary",
            message = "This week you saved ₹${
                String.format("%.2f", totalSavings)
            } using solar energy! 🌱"
        )
    }

    private fun showNotification(
        context: Context,
        id: Int,
        title: String,
        message: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager
        manager.notify(id, notification)
    }
}