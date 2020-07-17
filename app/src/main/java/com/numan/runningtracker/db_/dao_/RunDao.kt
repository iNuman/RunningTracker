package com.numan.runningtracker.db_.dao_

import androidx.lifecycle.LiveData
import androidx.room.*
import com.numan.runningtracker.db_.Run

@Dao
interface RunDao {
    /*
    * When we want insert a run that already
    * exists then new will be replaces by old
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    /*
    * Latest runs will be on top of the list
    * and we want to sort according to all attributes of our
    * Entity table
    * */
    @Query("SELECT * FROM running_table ORDER BY timestamp DESC")
    fun getAllRunsSortedByDate(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY timeInMillis DESC")
    fun getAllRunsSortedByTimeInMillis(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY caloriesBurned DESC")
    fun getAllRunsSortedByCaloriesBurned(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY avgSpeedInKMH DESC")
    fun getAllRunsSortedByAvgSpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM running_table ORDER BY distanceInMeters DESC")
    fun getAllRunsSortedByDistance(): LiveData<List<Run>>


     /*
     * Calculating sum using sq-lite function sum
     * */
    @Query("SELECT SUM(timeInMillis) FROM running_table")
    fun getTotalTimeInMillis(): LiveData<Long>


    @Query("SELECT SUM(caloriesBurned) FROM running_table")
    fun getTotalTotalCaloriesBurned(): LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM running_table")
    fun getTotalTotalDistanceInMeters(): LiveData<Int>

    /*
    * Now for avg we we're not going to use sum instead we'll use avg
    * */
    @Query("SELECT AVG(avgSpeedInKMH) FROM running_table")
    fun getTotalAvgSpeed(): LiveData<Float>
}