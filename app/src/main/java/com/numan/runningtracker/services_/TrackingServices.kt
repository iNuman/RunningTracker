package com.numan.runningtracker.services_

import android.content.Intent
import androidx.lifecycle.LifecycleService
import timber.log.Timber
import java.util.*
import com.numan.runningtracker.other_.Constants as Constants

class TrackingServices : LifecycleService(){
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action){

                Constants.ACTION_START_OR_RESUME_SERVICE ->{
                    Timber.d("Started or Resume Service")
                }
                Constants.ACTION_PAUSE_SERVICE ->{
                    Timber.d("Paused Service")
                }
                Constants.ACTION_STOP_SERVICE ->{
                    Timber.d("Stopped Service")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }
}

