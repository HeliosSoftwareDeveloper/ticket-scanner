package com.heliossoftwaredeveloper.ticketscanner.location

import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

object LocationHelper {

    fun checkGoogleApi(activity: FragmentActivity, showError: Boolean = true): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        when (val status = apiAvailability.isGooglePlayServicesAvailable(activity)) {
            ConnectionResult.SUCCESS -> return true
            else -> if (showError && apiAvailability.isUserResolvableError(status)) {
                apiAvailability.showErrorDialogFragment(activity, status, 0)
            }
        }
        return false
    }
}

