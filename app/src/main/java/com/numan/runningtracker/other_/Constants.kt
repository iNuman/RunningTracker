package com.numan.runningtracker.other_

object Constants {

    const val RUNNING_DATABASE_NAME = "running_db"
    const val REQUEST_CODE_LOCATION_PERMISSION = 0

    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    /*
    * These aren't accurate values
    * average interval can be of 3 sec or may be 6 secs
    * these are approximate intervals
    * */
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L // we can't go more then this fast like 1sec

    const val NOTIFICATION_ID = 1
    const val NOTIFICATION_CHANNEL_ID = "notificationchannel"
    const val NOTIFICATION_CHANNEL_NAME = "tracking"
}