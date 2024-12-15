package com.example.acceleration.screen

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acceleration.R
import kotlinx.coroutines.delay

class TitleScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DisplayTitleScreen()
        }
    }

    @Composable
    fun DisplayTitleScreen() {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LaunchedEffect(Unit) {
                delay(1000) // 1秒間の待機

                // Intentを使用してHomeScreenに遷移
                val intent = Intent(this@TitleScreen, HomeScreen::class.java)
                startActivity(intent)

                // TitleScreenを終了
                finish()
            }

            Image(
                painter = painterResource(id = R.drawable.title),
                contentDescription = "Logo",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
