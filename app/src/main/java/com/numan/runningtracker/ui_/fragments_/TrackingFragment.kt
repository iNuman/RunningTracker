package com.numan.runningtracker.ui_.fragments_

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.numan.runningtracker.R
import com.numan.runningtracker.ui_.viewmodels_.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/*
* Whenever we want to inject stuff in Android Components
* we will Annotate that fragment/Activity with:
* */
@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    /*
   * We can't just inject view model because we have to specify
   * which view model will be injected here
   * So,
   * */

    val viewModel: MainViewModel by viewModels()
}