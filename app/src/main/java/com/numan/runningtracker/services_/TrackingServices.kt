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
import com.numan.runningtracker.other_.Constants.TIMER_UPDATE_INTERVAL
import com.numan.runningtracker.other_.TrackingUtility
import com.numan.runningtracker.ui_.activities_.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.numan.runningtracker.other_.Constants as Constants

typealias  Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class TrackingServices : LifecycleService() {

    var isFirstRun = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val timeRunInSeconds = MutableLiveData<Long>()

    companion object {
        val timeRunInMillis = MutableLiveData<Long>()
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
        timeRunInSeconds.postValue(0L)
        timeRunInMillis.postValue(0L)
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
                        startTimer()
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
    * Function to start timer
    * */
    private var isTimerEnabled = false
    private var lapTime = 0L /* the time from beginning when the timer started So when we we start run
    * we're going to add time to this varibale
    * But when we stop timer and resume again this will start from 0
    */
    private var timeRun = 0L
    private var timeStarted = 0L /* timestamp for when we started the time*/
    private var lastSecondTimestamp = 0L

    private fun startTimer() {
/*
* I cutted the addEmptyPolyline form startForeGroundService and pasted it here
* */
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        /*
        * Now how do we want to track our current time
        * Or Stop the current time
        * So I want to implement this with coroutines
        * because I don't want to call observers all the time
        * it'll be bad performance otherwise
        *
        * What I actually want is to Track current time
        * in Coroutine and after that delay coroutine
        * for few milliSeconds
        * */

        CoroutineScope(Dispatchers.Main).launch {
            while (isTracking.value!!) {
                /* time difference between now and time started */
                lapTime = System.currentTimeMillis() - timeStarted
                /* Post the new lapTime ,    this is total time which we want to observe*/
                timeRunInMillis.postValue(timeRun + lapTime)
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                /* Now I will delay the coroutine for some milliSeconds which
                * is not noticeable for user but for phones
                *  */
                delay(TIMER_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    /*
    * To pause the Service
    * */
    private fun pauseService() {

        isTracking.postValue(false)
        isTimerEnabled = false
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

        startTimer()
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

