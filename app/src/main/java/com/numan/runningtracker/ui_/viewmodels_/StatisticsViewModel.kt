package com.numan.runningtracker.ui_.viewmodels_

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.numan.runningtracker.db_.repository_.MainRepository

class StatisticsViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {
}