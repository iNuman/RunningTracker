package com.numan.runningtracker.extensions_

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import timber.log.Timber


fun Fragment.toastFrag(msg: String, len: Int = Toast.LENGTH_SHORT) {
    requireContext().toast(msg, len)
}

fun Context.toast(msg: String, len: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, msg, len).show()
}

fun log(msg: String) {
    Timber.tag("🌀❤💕😎🤷 ffnet :: ‍").i(msg)
}
