package com.numan.runningtracker.ui_.viewmodels_

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.numan.runningtracker.db_.Run
import com.numan.runningtracker.db_.repository_.MainRepository
import com.numan.runningtracker.other_.SortType
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

    private val runsSortedByDate = mainRepository.getAllRunsSortedByDate()
    private val runsSortedByDistance = mainRepository.getAllRunsSortedByDistance()
    private val runsSortedByCaloriesBurned = mainRepository.getAllRunsSortedByCaloriesBurned()
    private val runsSortedByTimeInMillis = mainRepository.getAllRunsSortedByTimeInMillis()
    private val runsSortedByAvgSpeed = mainRepository.getAllRunsSortedByAvgSpeed()
    /*
    * After this we'll be implementing sorting functionality
    * but in our Run Fragment we're observing changes on a single live data object
    * And when we change sorting type in spinner then we can't simply change
    * which live data we observe on
    * And for this I'll be creating special type of live data which is
    *
    * called "MediatorLiveData" it's type of live data which allow us to merge several
    * type of live data's and write our custom logic for that when we want to emit
    * which kind of data that'll also mean we don't need that live data
    * objects from the outside so we can all of the above private
    * */

    val runs = MediatorLiveData<List<Run>>() // and I'll be creating enum class for sort types

    var sortType = SortType.DATE

    init {
        runs.addSource(runsSortedByDate) { result ->
            if (sortType == SortType.DATE) {
                result?.let { runs.value == it }
            }
        }
        runs.addSource(runsSortedByDistance) { result ->
            if (sortType == SortType.DISTANCE) {
                result?.let { runs.value == it }
            }
        }
        runs.addSource(runsSortedByAvgSpeed) { result ->
            if (sortType == SortType.AVG_SPEED) {
                result?.let { runs.value == it }
            }
        }
        runs.addSource(runsSortedByCaloriesBurned) { result ->
            if (sortType == SortType.CALORIES_BURNED) {
                result?.let { runs.value == it }
            }
        }
        runs.addSource(runsSortedByTimeInMillis) { result ->
            if (sortType == SortType.RUNNING_TIME) {
                result?.let { runs.value == it }
            }
        }
    }


    fun sortRuns(sortType: SortType) = when(sortType){
        SortType.DATE -> runsSortedByDate.value?.let { runs.value = it }
        SortType.DISTANCE -> runsSortedByDistance.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runsSortedByAvgSpeed.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runsSortedByCaloriesBurned.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runsSortedByTimeInMillis.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }



    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
}