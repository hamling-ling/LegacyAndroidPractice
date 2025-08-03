package com.example.veryoldapp

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.veryoldapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask
import kotlin.system.exitProcess


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private var player: ExoPlayer? = null

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = mainViewModel

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

    override fun onStart() {
        super.onStart()

        initializePlayer()
    }

    override fun onStop() {
        super.onStop()

        finalizePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.videoView.player = exoPlayer
            }

        mainViewModel.contents.observe(this, ::onCodntentsChanged)
    }

    private fun finalizePlayer() {
        player?.release()
        player = null
        mainViewModel.contents.removeObserver(::onCodntentsChanged)
    }

    private fun onCodntentsChanged(contents: List<String>) {
        Log.i(TAG, "contents: $contents")

        val exoPlayer = player ?: return
        val mediaItems = contents.map { fileName ->
            val assetUri = Uri.parse("asset:///$fileName")
            MediaItem.fromUri(assetUri)
        }
        // Update playing media
        exoPlayer.stop()
        exoPlayer.setMediaItems(mediaItems)
        // Prepare the player
        exoPlayer.prepare()
        // Start the playback
        exoPlayer.play()
    }

    fun exitButtonClicked(view: View) {
        Log.i(TAG, "exitButtonClicked")

        // onDestroy() は呼ばれるが timer は行き続ける
        //finish()
        // こちらなら終了する模様
        exitProcess(0)
    }
}