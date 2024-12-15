package com.example.acceleration.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.example.acceleration.R
import com.example.acceleration.screen.PreviewScreen

class AccelerationNotification(private val context: Context) {

    private val CHANNEL_ID = "acceleration_channel_id"

    init {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existingChannel = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existingChannel != null) {
                notificationManager.deleteNotificationChannel(CHANNEL_ID)
            }
            val name = "Acceleration Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val soundUri: Uri = Uri.parse("android.resource://${context.packageName}/raw/custom_notification")
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.setSound(soundUri, null)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // 通知音をSharedPreferencesから取得
    private fun getNotificationSoundFromPreferences(): Int {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getInt("notificationSound", R.raw.notification_sound)
    }

    fun showNotification(acceleration: Float) {
        val intent = Intent(context, PreviewScreen::class.java)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(PreviewScreen::class.java)
        stackBuilder.addNextIntent(intent)

        val notificationSoundId = getNotificationSoundFromPreferences()
        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/$notificationSoundId")
        val dynamicChannelId = "acceleration_channel_id_$notificationSoundId"

        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existingChannel = notificationManager.getNotificationChannel(dynamicChannelId)
            if (existingChannel == null) {
                val name = "Acceleration Notifications"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(dynamicChannelId, name, importance)
                channel.setSound(soundUri, null)
                notificationManager.createNotificationChannel(channel)
            }
        }

        val notification = NotificationCompat.Builder(context, dynamicChannelId)
            .setContentTitle("ごめんね！")
            .setContentText("タップしてあやまってもらう")
            .setSmallIcon(android.R.drawable.ic_notification_overlay)
            .setContentIntent(pendingIntent)
            .setSound(soundUri)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
    }
