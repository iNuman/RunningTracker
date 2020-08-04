package com.numan.runningtracker.ui_.viewmodels_

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numan.runningtracker.db_.Run
import com.numan.runningtracker.db_.repository_.MainRepository
import kotlinx.coroutines.launch

/*
* This injection will work even without creating implementation behind the scenes
* because With new Dagger hilt  injection of
* ViewModelFactory will be done behind the scenes
* And for repository we already had defined way in behind scenes inside di package
* */
class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    val runsSortedByDate = mainRepository.getAllRunsSortedByDate()
    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
}