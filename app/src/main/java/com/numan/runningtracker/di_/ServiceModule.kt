package com.numan.runningtracker.di_

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.numan.runningtracker.R
import com.numan.runningtracker.other_.Constants
import com.numan.runningtracker.ui_.activities_.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

/*
* And Now dependencies here will live
* as long as our service lives
* */

/*
* If we're dealing with ActivityComponents then we'll use ActivityScoped else
* if we're dealing with Service we'll use ServiceScoped to create single Instance
* */
@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {

    /* FusedLocation Provides funciton */
    @ServiceScoped // service has it's very own annotation to declare single instance instead of singleton which is this one
    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)

    /* Pending Intent provides function */
    @ServiceScoped
    @Provides
    fun provideMainActivityPendingIntent(
        @ApplicationContext app: Context
    )  = PendingIntent.getActivity(
    app,
    0,
    Intent(app, MainActivity::class.java).also {
        it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
    },
    PendingIntent.FLAG_UPDATE_CURRENT
    )
    /* Notification Builder */
    @ServiceScoped
    @Provides
    fun provideBaseNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    )  = NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_directions_run_black_24dp)
            .setContentText(app.getString(R.string.app_name))
            .setContentText("00:00:00")
            .setContentIntent(pendingIntent)


}