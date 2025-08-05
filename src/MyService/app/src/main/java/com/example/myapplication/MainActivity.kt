package com.example.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Create the notification channel (required for Android 8.0 and above)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "ForegroundServiceChannelId",
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            // service provided by Android Operating system to show notification outside of our app
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val startClick = findViewById<Button>(R.id.startButton)
        startClick.setOnClickListener { view ->
            Intent(applicationContext, MyForegroundService::class.java).also {
                it.action = MyForegroundService.Actions.START.toString()
                startService(it)
            }
        }

        val stopClick = findViewById<Button>(R.id.stopButton)
        stopClick.setOnClickListener { view ->
            Intent(applicationContext, MyForegroundService::class.java).also {
                it.action = MyForegroundService.Actions.STOP.toString()
                startService(it)
            }
        }
    }
}