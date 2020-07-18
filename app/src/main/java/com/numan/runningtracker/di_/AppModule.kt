package com.numan.runningtracker.di_

import android.content.Context
import androidx.room.Room
import com.numan.runningtracker.db_.RunningDatabase
import com.numan.runningtracker.other_.Constants.RUNNING_DATABASE_NAME
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
}