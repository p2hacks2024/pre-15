package com.example.acceleration.screen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.acceleration.R
import com.example.acceleration.ui.theme.AccelerationTheme

class HomeScreen : AppCompatActivity() {

    private var savedImageId by mutableIntStateOf(R.drawable.no_image) // State管理

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 通知権限をリクエスト
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        setContent {
            AccelerationTheme {
                HomeScreenContent()
            }
        }
    }

    // 通知権限をリクエスト
    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1
            )
        }
    }

    @Composable
    fun HomeScreenContent() {
        savedImageId = getSelectedImageFromPreferences()
        val context = LocalContext.current

        DisposableEffect(Unit) {
            val prefs = context.getSharedPreferences("AppPrefs", MODE_PRIVATE)
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
                if (key == "selectedImage") {
                    savedImageId = getSelectedImageFromPreferences()
                }
            }
            prefs.registerOnSharedPreferenceChangeListener(listener)

            onDispose {
                prefs.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = savedImageId),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.End
            ) {

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(context, CharacterScreen::class.java)
                        context.startActivity(intent)
                    },
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.setting),
                        contentDescription = "Settings",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(58.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (savedImageId == R.drawable.no_image) {

                            showNoImageSelectedMessage(context)
                        } else {
                            val intent = Intent(context, PreviewScreen::class.java)
                            context.startActivity(intent)
                        }
                    },
                    shape = CircleShape,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.face),
                        contentDescription = "Preview",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(58.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

    private fun showNoImageSelectedMessage(context: Context) {

        Toast.makeText(context, "画像が選択されていません", Toast.LENGTH_SHORT).show()
    }

    private fun getSelectedImageFromPreferences(): Int {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        return sharedPref.getInt("selectedImage", R.drawable.no_image)
    }
}
