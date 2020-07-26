package com.numan.runningtracker.services_

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.numan.runningtracker.R
import com.numan.runningtracker.extensions_.log
import com.numan.runningtracker.other_.TrackingUtility
import com.numan.runningtracker.ui_.activities_.MainActivity
import com.numan.runningtracker.other_.Constants as Constants

typealias  Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingServices : LifecycleService() {

    var isFirstRun = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()

        /*we'll have list of coordinates*/
        /* instead of this: MutableList<MutableList<LatLng>> we can write the typealias
        *  Polylines */
        val pathPoints = MutableLiveData<Polylines>()
        /*
        * The internal MutableList<LatLng> is just a simple polyLine
        * of co-ordinates for our map
        * */

    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this@TrackingServices)
        /*
        * This is only possible because we extend the service class with LifecycleService
        * Otherwise we wouldn't be able to pass the service as lifeCycle Owner
        * */
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            /*
            * And final thing we need to do When we start our service is
            * to pass the isTracking=true in our is Tracking Service
            * and to do this we'll go into startForeground Service method and will post isTracking to true
            * */
        })
    }

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
                        startForegroundService()
                    }

                }
                Constants.ACTION_PAUSE_SERVICE -> {
                    log("Paused Service")
                    pauseService()
                }
                Constants.ACTION_STOP_SERVICE -> {
                    log("Stopped Service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    /*
    * To pause the Service
    * */
    private fun pauseService() {

        isTracking.postValue(false)
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

    /*
    * Function to update our location tracking
    * */
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasLocationPermissions(this)) {
                val request = LocationRequest().apply {
                    interval =
                        Constants.LOCATION_UPDATE_INTERVAL // how often/average iterval we want to get location updates
                    fastestInterval = Constants.FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    /* Now we'll be using fused location to get the
    * location on consistent basis
    * */
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (isTracking.value!!) {
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        log("Locations Updates: ${location.latitude}, ${location.longitude}")
                    }
                }
            }
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos) // last indicates last location of coordinate
                pathPoints.postValue(this)
            }
        }
    }

    /* To add first empty polyLine*/
    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)

    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun startForegroundService() {

        addEmptyPolyline()
        isTracking.postValue(true)
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

