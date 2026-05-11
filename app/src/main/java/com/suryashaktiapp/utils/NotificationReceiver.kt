package com.suryashaktiapp.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.util.Calendar

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour in 10..15 -> NotificationHelper.showPeakSunAlert(context)
            hour == 20 -> NotificationHelper.showDailyReminder(context)
        }
    }
}