package com.numan.runningtracker.ui_.fragments_

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.numan.runningtracker.R
import com.numan.runningtracker.other_.Constants.KEY_FIRST_TIME_TOGGLE
import com.numan.runningtracker.other_.Constants.KEY_NAME
import com.numan.runningtracker.other_.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    /*
    * As boolean is primitive data type we can't simply say
    * @Inject here instead
    * @set:Inject will be use
    * */
    @set:Inject
    var isFirstAppOpen = true



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*
        * if it's not the first launch of the app then remove the setup fragment from
        * backStack and navigate to run fragment
        * we poped up the fragment from backStack
        *
        * Because when we press backButton
        * then it'll again navigate to setup fragment which we don't want
        * so we removed the fragment from the backStack
        * */
        if (!isFirstAppOpen) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment,
            savedInstanceState,
            navOptions
            )
        }

        tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all the fields", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean {

        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharedPreferences.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(
                KEY_FIRST_TIME_TOGGLE,
                false
            ) // it means user had already opened the app it's not the first time run
            .apply()
        val toolbarText = "Let's go $name"
        requireActivity().tvToolbarTitle.text = toolbarText
        return true
    }
}