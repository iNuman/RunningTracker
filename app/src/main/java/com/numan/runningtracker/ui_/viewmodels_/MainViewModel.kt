package com.numan.runningtracker.ui_.viewmodels_

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.numan.runningtracker.db_.repository_.MainRepository

/*
* This injection will work even without creating implementation behind the scenes
* because With new Dagger hilt  injection of
* ViewModelFactory will be done behind the scenes
* And for repository we already had defined way in behind scenes inside di package
* */
class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {
}