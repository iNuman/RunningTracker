package com.numan.runningtracker.ui_.fragments_

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.numan.runningtracker.R
import com.numan.runningtracker.ui_.viewmodels_.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*

/*
* Whenever we want to inject stuff in Android Components
* we will Annotate that fragment/Activity with:
* */
@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run) {
    /*
    * We can't just inject view model because we have to specify
    * which view model will be injected here
    * So,
    * */
    val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
        }
    }

}