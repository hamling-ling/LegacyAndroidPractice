package com.example.veryoldapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


class BootReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "BootReceiver"
        const val DELAY_SEC = 30
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Log.i(TAG, "ACTION_BOOT_COMPLETED")
            // アプリのメインアクティビティを起動
            val activityIntent = Intent(
                context,
                MainActivity::class.java
            ).run { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }

            val pendingIntent = PendingIntent.getActivity(context, 0, activityIntent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

            val alarmTime = System.currentTimeMillis() + (1000*DELAY_SEC)

            val mgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            mgr.set(AlarmManager.RTC, alarmTime, pendingIntent);
            Log.i(TAG, "alarm $alarmTime sec later")
        }
    }
}
