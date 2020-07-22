package com.numan.runningtracker.services_

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.numan.runningtracker.R
import com.numan.runningtracker.extensions_.log
import com.numan.runningtracker.ui_.activities_.MainActivity
import timber.log.Timber
import java.util.*
import com.numan.runningtracker.other_.Constants as Constants

class TrackingServices : LifecycleService() {

    var isFirstRun = true
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {

                Constants.ACTION_START_OR_RESUME_SERVICE -> {
                    /*
                    * Since this action should start our service if it
                    * was sent for the first time
                    * And resume if it's in the past state
                    * So we need boolean there
                    * */
                    if (isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                       log("Resuming Service")
                    }

                }
                Constants.ACTION_PAUSE_SERVICE -> {
                   log("Paused Service")
                }
                Constants.ACTION_STOP_SERVICE -> {
                    log("Stopped Service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /*
    * So in here what we actually doing is
    * Whenever we get main activity we check for the action attach to it
    * if it is then we'll simply navigate to that tracking fragment
    *
    * But right Now we don't have option in our navigation graph to be able to navigate to our
    * Tracking Fragment from wherever we are
    * So to Accomplish this we'll be defining global action in our navigation graph
    *
    * We define this action at the top of our navigation graph
        *   <action android:id="@+id/action_global_trackingFragment"
        *   app:destination="@id/trackingFragment"
        *   app:launchSingleTop="true"/>
    * app:launchSingleTop="true" this means it'll always launch single instance not multiple of fragment
    * */

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this@TrackingServices,
        0,
        Intent(this@TrackingServices, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun startForegroundService() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationBuilder =
            NotificationCompat.Builder(this@TrackingServices, Constants.NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
                .setContentText(getString(R.string.app_name))
                .setContentText("00:00:00")
                .setContentIntent(getMainActivityPendingIntent())

        startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build())
    }
}

