package com.numan.runningtracker.di_

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.google.android.gms.maps.model.CameraPosition
import com.numan.runningtracker.db_.RunningDatabase
import com.numan.runningtracker.other_.Constants.KEY_FIRST_TIME_TOGGLE
import com.numan.runningtracker.other_.Constants.KEY_NAME
import com.numan.runningtracker.other_.Constants.KEY_WEIGHT
import com.numan.runningtracker.other_.Constants.RUNNING_DATABASE_NAME
import com.numan.runningtracker.other_.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    /*
    * If we leave this function like this and whenever we call this function let say
    * we need this function in two classes
    * then there'll be two different instances of database
    * but we want it to be singleton/same instance in both classes
    * So, for this purpose we we'll be annotating it with @Singleton
    * Then instance of this function/db will be singleton application wise
    *
    *
    * Generally we don't need to access this database class instead
    * We need to access DAO Class for this we'll make another method down below
    * */
    @Singleton
    @Provides
    fun provideRunningDatabase(
        @ApplicationContext app: Context
        /* since dagger don't know where this context come from so we'll annotate it with @App....
         * and this it the beauty of dagger hilt library because there's other things
         * to handle except only writing this @App.. for context */
    ) = Room.databaseBuilder(
        app,
        RunningDatabase::class.java,
        RUNNING_DATABASE_NAME
    ).build()


    @Singleton
    @Provides
    fun provideRunDao(db: RunningDatabase) = db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(KEY_NAME, "") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPreferences: SharedPreferences) = sharedPreferences.getBoolean(
        KEY_FIRST_TIME_TOGGLE, true
    )
}