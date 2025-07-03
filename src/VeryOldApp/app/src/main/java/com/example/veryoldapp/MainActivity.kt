package com.example.veryoldapp

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    val timer = Timer()
    class MyTask : TimerTask() {
        override fun run() {
            // 繰り返したい処理
            Log.i(TAG, "hello")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(TAG, "onCreate")

        val task = MyTask()
        timer.schedule(task, 1000, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.i(TAG, "onDestroy")

        // finish() する場合はこれがないと timer が生き続ける
        timer.cancel()
    }

    fun exitButtonClicked(view: View) {
        Log.i(TAG, "exitButtonClicked")

        // onDestroy() は呼ばれるが timer は行き続ける
        finish()
        // こちらなら終了する模様
        //System.exit(0)
    }
}