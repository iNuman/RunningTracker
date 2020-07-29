package com.numan.runningtracker.ui_.fragments_

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.numan.runningtracker.R
import com.numan.runningtracker.other_.Constants.ACTION_PAUSE_SERVICE
import com.numan.runningtracker.other_.Constants.ACTION_START_OR_RESUME_SERVICE
import com.numan.runningtracker.other_.Constants.ACTION_STOP_SERVICE
import com.numan.runningtracker.other_.Constants.MAP_ZOOM
import com.numan.runningtracker.other_.Constants.POLYLINE_COLOR
import com.numan.runningtracker.other_.Constants.POLYLINE_WIDTH
import com.numan.runningtracker.other_.TrackingUtility
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

    private var map: GoogleMap? = null

    private var menu: Menu? = null

    private var currentTimeInMillis = 0L /* how long the run was*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true) // since we only want to show the menu in tracking so this should be true
        // In activities this function calls by default
        return super.onCreateView(inflater, container, savedInstanceState)
    }

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
        /* New observer From Service*/
        TrackingServices.timeRunInMillis.observe(viewLifecycleOwner, Observer {
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeInMillis, true)
            tvTimer.text = formattedTime
        })


    }

    private fun toggleRun() {

        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
        /*
        * After this I'll write function above to subscribe to service
        * */
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.miCancelTracking -> {
                showCancelTrackingDialog()
            }
        }
    }
    private fun showCancelTrackingDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle("Cancel The Run")
            .setMessage("Are you sure to cancel the current run and delete all its' data?")
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton("Yes"){ _ , _ ->
                stopRun()

            }
            .setNegativeButton("No"){ dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
        dialog.show()
    }
    private fun stopRun() {
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }
    private fun updateTracking(isTracking: Boolean) {

        this.isTracking = isTracking
        if (!isTracking) {
            btnToggleRun.text = "Start"
            btnFinishRun.visibility = View.VISIBLE
        } else {
            btnToggleRun.text = "Stop"
            menu?.getItem(0)?.isVisible = true
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