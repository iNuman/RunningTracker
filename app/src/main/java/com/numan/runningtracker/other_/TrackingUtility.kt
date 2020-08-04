package com.numan.runningtracker.other_

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.numan.runningtracker.services_.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.sql.Time
import java.util.concurrent.TimeUnit

object TrackingUtility {
    fun hasLocationPermissions(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {

            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }

    fun calculatePolylineLength(polyline: Polyline): Float {
        /*
        * Calculating distance between two points
        * */
        var distance = 0f
        for (i in 0..polyline.size - 2) {
            val pos1 =  polyline[i]
            val pos2 = polyline[i + 1]

            val result  = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distance += result[0]
        }
        return distance
    }



    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false): String {
        var milliSeconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds)
        milliSeconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds)
        milliSeconds -= TimeUnit.MILLISECONDS.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds)
        if (!includeMillis) {
            // if hours are less then 10 add 0 before those hours else if equal we add nothing
            return "${if (hours < 10) "0" else ""} $hours:" +
                    "${if (minutes < 10) "0" else ""} $minutes:" +
                    "${if (seconds < 10) "0" else ""} $seconds"
        }
        /*And if we want to print milliSeconds then we'll not go inside that if instead*/
        milliSeconds -= TimeUnit.SECONDS.toMillis(seconds)
        milliSeconds /= 10 // because we only one two digit number for milliSeconds
        return "${if (hours < 10) "0" else ""} $hours:" +
                "${if (minutes < 10) "0" else ""} $minutes:" +
                "${if (seconds < 10) "0" else ""} $seconds:" +
                "${if (milliSeconds < 10) "0" else ""} $milliSeconds"


    }
}