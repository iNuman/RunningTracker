package com.numan.runningtracker.ui_.fragments_

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.numan.runningtracker.R
import com.numan.runningtracker.adapters_.RunAdapter
import com.numan.runningtracker.db_.Run
import com.numan.runningtracker.extensions_.toastFrag
import com.numan.runningtracker.other_.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.numan.runningtracker.other_.SortType
import com.numan.runningtracker.other_.TrackingUtility.hasLocationPermissions
import com.numan.runningtracker.ui_.viewmodels_.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.hasPermissions
import com.numan.runningtracker.other_.TrackingUtility as TrackingUtility

/*
* Whenever we want to inject stuff in Android Components
* we will Annotate that fragment/Activity with:
* */
@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks,
    RunAdapter.Interaction {

    override fun onItemSelected(position: Int, item: Run?) {
        toastFrag("Clicked")
    }


    /*
    * We can't just inject view model because we have to specify
    * which view model will be injected here
    * So,
    * */
    val viewModel: MainViewModel by viewModels()
    lateinit var runAdapter: RunAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
        setupRecyclerView()

        when (viewModel.sortType) {
            SortType.DATE -> spFilter.setSelection(0)
            SortType.DISTANCE -> spFilter.setSelection(1)
            SortType.AVG_SPEED -> spFilter.setSelection(2)
            SortType.CALORIES_BURNED -> spFilter.setSelection(3)
            SortType.RUNNING_TIME -> spFilter.setSelection(4)
        }

        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.DISTANCE)
                    2 -> viewModel.sortRuns(SortType.AVG_SPEED)
                    3 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
                    4 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                }
            }
        }
        viewModel.runs.observe(viewLifecycleOwner, Observer { runAdapter.submitList(it) })

        fab.setOnClickListener { findNavController().navigate(R.id.action_runFragment_to_trackingFragment) }

    }

    private fun setupRecyclerView() = rvRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun requestPermission() {
        if (hasLocationPermissions(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this@RunFragment,
                "You need to accept the Location Permissions",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this@RunFragment,
                "You need to accept the Location Permissions",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        /*
        * Here perm contain the list of permissions that has been denied
        * */
        if (EasyPermissions.somePermissionPermanentlyDenied(this@RunFragment, perms)) {
            AppSettingsDialog.Builder(this@RunFragment).build().show()
        } else requestPermission()

    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this@RunFragment
        )
    }

}