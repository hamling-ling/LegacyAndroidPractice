package com.example.myapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class MyForegroundService : Service() {

    companion object {
        const val TAG = "MyForegroundService"
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
    }


    // This is triggered when another android component sends an Intent to this running service
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.i(TAG, "onStartCommand")
        // Do the work that the service needs to do here
        when (intent?.action) {
            Actions.START.toString() -> {
                // This method tells the system that a service will become a foreground service soon.
                //
                start() // Call start() to create the notification and start in foreground
            }
            Actions.STOP.toString() -> stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    enum class Actions {
        START, STOP
    }

    private fun start() {

        Log.i(TAG, "start")

        val notification = NotificationCompat.Builder(this, "ForegroundServiceChannelId")
            //.setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Foreground Service")
            .setContentText("Foreground service is running")
            .build()

        // Start the service in the foreground
        startForeground(1, notification)
    }

    override fun onDestroy() {
        Log.i(TAG, "onDestroy")

        super.onDestroy()
        // Clean up any resources here
    }
}
