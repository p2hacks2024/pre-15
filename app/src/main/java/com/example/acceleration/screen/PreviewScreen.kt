package com.example.acceleration.screen

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.example.acceleration.R
import com.example.acceleration.ui.theme.AccelerationTheme

class PreviewScreen : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var surfaceView: SurfaceView? = null
    private lateinit var surfaceHolder: SurfaceHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectedImage = getSelectedImageFromPreferences()
        val selectedSound = getSelectedSoundFromPreferences()
        val selectedVideo = getSelectedVideoFromPreferences()
        val vibrationDuration = getVibrationDurationFromPreferences()

        if (selectedVideo != -1) {
            // 動画IDがある場合、SurfaceViewを利用して動画を再生
            setContentView(R.layout.activity_screen_flash)
            surfaceView = findViewById(R.id.surfaceView)
            surfaceHolder = surfaceView!!.holder

            surfaceHolder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceCreated(holder: SurfaceHolder) {
                    playVideo(selectedVideo)
                }

                override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
                override fun surfaceDestroyed(holder: SurfaceHolder) {
                    mediaPlayer?.release()
                    mediaPlayer = null
                }
            })

            // 戻るボタンの処理
            val backButton = findViewById<ImageButton>(R.id.backButton)
            backButton.setOnClickListener {
                val intent = Intent(this, HomeScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            }

        } else {
            setContent {
                AccelerationTheme {
                    FlashScreenContent(
                        onBack = {
                            val intent = Intent(this, HomeScreen::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        },
                        selectedImage = selectedImage
                    )
                }
            }
            playSoundDelayed(selectedSound)
            playVibration(vibrationDuration)
        }
    }


    // 動画の再生処理
    private fun playVideo(videoResId: Int) {
        val videoUri = Uri.parse("android.resource://$packageName/$videoResId")
        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@PreviewScreen, videoUri)
            setDisplay(surfaceHolder)
            isLooping = true
            setOnPreparedListener { mp ->
                mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT)
                mp.start()
            }
            prepareAsync()
        }
    }

    // 音声の再生処理
    private fun playSoundDelayed(soundId: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, soundId)
        mediaPlayer?.setOnPreparedListener {
            it.start()
        }
    }

    @Composable
    fun FlashScreenContent(onBack: () -> Unit, selectedImage: Int) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = selectedImage),
                contentDescription = "selectedImage",
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    private fun getSelectedImageFromPreferences(): Int {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPref.getInt("selectedImage", R.drawable.kao1)
    }

    private fun getSelectedSoundFromPreferences(): Int {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPref.getInt("selectedSound", R.raw.custom_notification)
    }

    private fun getSelectedVideoFromPreferences(): Int {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPref.getInt("selectedVideo", -1) // デフォルト値は-1
    }

    private fun getVibrationDurationFromPreferences(): Long {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPref.getLong("vibrationDuration", 0L) // デフォルトは0ms
    }

    private fun playVibration(duration: Long) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}