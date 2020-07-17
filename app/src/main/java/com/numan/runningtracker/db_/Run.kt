package com.numan.runningtracker.db_

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey


/*
* For a single run
* */
@Entity(tableName = "running_table")
data class Run(
    val img: Bitmap? = null,
    val timestamp: Long = 0L, // describes when our run was
    val avgSpeedInKMH: Float = 0f,
    val distanceInMeters: Int = 0,
    val timeInMillis: Long = 0L, // describes how long our run was
    val caloriesBurned: Int = 0
) {
    /*
    * Since I don't want to create primary key inside constructor
    *  I put it here
    * */
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}