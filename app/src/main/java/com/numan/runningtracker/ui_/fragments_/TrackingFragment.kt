package com.numan.runningtracker.ui_.fragments_

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.numan.runningtracker.R
import com.numan.runningtracker.other_.Constants.ACTION_PAUSE_SERVICE
import com.numan.runningtracker.other_.Constants.ACTION_START_OR_RESUME_SERVICE
import com.numan.runningtracker.other_.Constants.MAP_ZOOM
import com.numan.runningtracker.other_.Constants.POLYLINE_COLOR
import com.numan.runningtracker.other_.Constants.POLYLINE_WIDTH
import com.numan.runningtracker.services_.Polyline
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

    private var isTracking = false
    private var pathPoints = mutableListOf<Polyline>()


    var map: GoogleMap? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        * And Now we'll get map fragment lifeCycle methods here
        * */
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync {
            map = it
            /* it only calls when fragment is
            * created
            * so it'll add the polyLines already to map*/
            addAllPolylines()
        }
        subscribeToObservers()
        btnToggleRun.setOnClickListener {
            toggleRun()
            // sendCommandToService(Constants.ACTION_START_OR_RESUME_SERVICE)
        }
    }

    private fun subscribeToObservers() {
        TrackingServices.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })

        TrackingServices.pathPoints.observe(viewLifecycleOwner, Observer {
            pathPoints = it
            /* we knew that New location is coming
            * from our observer and we just want to add
            * new Line*/
            addLatestPolyline()
            moveCameraToUser()
        })
    }

    private fun toggleRun() {

        if (isTracking) {
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
        /*
        * After this I'll write function above to subscribe to service
        * */
    }


    private fun updateTracking(isTracking: Boolean) {

        this.isTracking = isTracking
        if (!isTracking) {
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        } else {
            btnToggleRun.text = "Stop"
            btnFinishRun.visibility = View.GONE
        }

        /*
        * And Now I also need functionality to toggle
        * my Run:
        * To start if it's in stop state or to
        * Resume if it's in Pause state
        * I will make method above
        *
        * */
    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last().last(),
                    MAP_ZOOM
                )
            )
            /*
            * And Now what we have to do is
            * to Observe data from our service and react to those
            * changes
            * And for this I'll create a function above
            * */
        }

    }

    private fun addAllPolylines() {
        for (polyline in pathPoints) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polylineOptions)
            /*
            * Now after this we'll write a function
            * to animate Camera above
            * */
        }
    }

    /*
    * Function to draw polyline and I should know when to draw it
    * This function is to connect last polylines but it
    * will not draw whole polyline on map
    * or it'll not recreate whole polylines
    * when we rotate our device
    * and For this I'll create another function above
    * */
    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2] // 2nd last cordinate
            val lastLatLng = pathPoints.last().last() // last coordinate
            val polygonOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polygonOptions)
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingServices::class.java).also {
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