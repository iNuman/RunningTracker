package com.numan.runningtracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.numan.runningtracker.db_.dao_.RunDao
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var runDao: RunDao
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
       println("ffnet: Run Dao: ${runDao.hashCode()}")
    }
}
