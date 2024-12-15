package com.example.acceleration.screen

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.example.acceleration.R
import com.example.acceleration.ui.theme.AccelerationTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.unit.dp

class CharacterScreen : AppCompatActivity() {

    private var selectedImageId: Int? = null
    private var selectedSoundId: Int? = null
    private var selectedVideoId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AccelerationTheme {
                CharacterScreenContent()
            }
        }
    }

    @Composable
    fun CharacterScreenContent() {
        val showDialog = remember { mutableStateOf(false) }
        val confirmAction = remember { mutableStateOf({}) }

        val savedImageId = remember { mutableIntStateOf(0) }
        val savedSoundId = remember { mutableIntStateOf(0) }
        val savedVideoId = remember { mutableIntStateOf(0) }

        LaunchedEffect(savedImageId.intValue, savedSoundId.intValue, savedVideoId.intValue) {
            selectedImageId = savedImageId.intValue
            selectedSoundId = savedSoundId.intValue
            selectedVideoId = savedVideoId.intValue
        }

        val scrollState = rememberScrollState()

        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {

                val imageModifier = Modifier
                    .aspectRatio(0.4875f)
                    .weight(1f)

                val imageList = listOf(
                    listOf (R.drawable.kao1 , R.raw.kao1vv , R.raw.notification_sound10),
                    listOf (R.drawable.kao2 , R.raw.koe2vv , R.raw.notification_sound2), //かえたじょ
                    listOf (R.drawable.kao3 , R.raw.kao3vv , R.raw.notification_sound9), //かえた
                    listOf (R.drawable.kao4 , R.raw.kao4v , R.raw.notification_sound4),//音声変えないで
                    listOf (R.drawable.kao5 , R.raw.kao5vv , R.raw.notification_sound7), //音声変えた
                    listOf (R.drawable.kao6 , R.raw.kao6vv , R.raw.notification_sound11),//音声変えたほうがいいかも
                    listOf (R.drawable.kao7 , R.raw.kao7vv , R.raw.notification_sound6),//音声変えないで
                    listOf (R.drawable.kao8 , R.raw.kao8vv , R.raw.notification_sound8), //かえたよ
                )

                imageList.chunked(2).forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        item.forEach { (imageRes, videoRes, soundRes) ->
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "Character face_tracking",
                                modifier = imageModifier
                                    .weight(1f)
                                    .clickable {
                                        selectedImageId = imageRes
                                        selectedSoundId = soundRes
                                        selectedVideoId = videoRes
                                        showDialog.value = true
                                        confirmAction.value = {
                                            saveSelectedContent(
                                                selectedImageId,
                                                selectedSoundId,
                                                selectedVideoId,
                                                selectedSoundId
                                            )
                                            showDialog.value = false
                                        }
                                    }
                            )
                        }
                    }
                }
            }


            if (showDialog.value) {
                AlertDialog(
                    onDismissRequest = { showDialog.value = false },
                    title = { androidx.compose.material3.Text("この表情にしますか？") },
                    dismissButton = {
                        Button(onClick = confirmAction.value) {
                            androidx.compose.material3.Text("はい")
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showDialog.value = false }) {
                            androidx.compose.material3.Text("いいえ")
                        }
                    }
                )
            }
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
        return sharedPref.getInt("selectedVideo", -1)
    }

    private fun saveSelectedContent(
        imageId: Int?,
        soundId: Int?,
        videoId: Int?,
        notificationSoundId: Int?
    ) {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putInt("selectedImage", imageId ?: R.drawable.kao1)
        (soundId ?: soundId)?.let { editor.putInt("selectedSound", it) }
        editor.putInt("selectedVideo", videoId ?: -1)
        notificationSoundId?.let { editor.putInt("notificationSound", it) }
        editor.apply()
    }
}


