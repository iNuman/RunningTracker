package com.numan.runningtracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.numan.runningtracker.other_.Constants
import com.numan.runningtracker.other_.Constants.NOTIFICATION_CHANNEL_ID
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
@RequiresApi(Build.VERSION_CODES.O)
@HiltAndroidApp
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        createNotificationChannel()
    }


    private fun createNotificationChannel() {
            val name = applicationContext.getString(R.string.app_name)
            val channelName = Constants.NOTIFICATION_CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = channelName
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

    }
}