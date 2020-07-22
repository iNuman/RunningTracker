package com.numan.runningtracker.ui_.fragments_

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.GoogleMap
import com.numan.runningtracker.R
import com.numan.runningtracker.services_.TrackingServices
import com.numan.runningtracker.ui_.viewmodels_.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_tracking.*
import com.numan.runningtracker.other_.Constants as Constants

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
    var map: GoogleMap? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        * And Now we'll get map fragment lifeCycle methods here
        * */
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
        }
        btnToggleRun.setOnClickListener { 
            sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(),TrackingServices::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}