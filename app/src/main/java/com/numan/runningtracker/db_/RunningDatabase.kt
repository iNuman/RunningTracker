package com.numan.runningtracker.db_

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.numan.runningtracker.db_.dao_.RunDao
import com.numan.runningtracker.db_.typeconverters_.Converters

@Database(
    entities = [Run::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class RunningDatabase : RoomDatabase() {
    abstract fun getRunDao(): RunDao
}