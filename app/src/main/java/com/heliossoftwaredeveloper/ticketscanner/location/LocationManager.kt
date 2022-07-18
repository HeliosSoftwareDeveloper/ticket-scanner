package com.heliossoftwaredeveloper.ticketscanner.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.location.Location
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.heliossoftwaredeveloper.ticketscanner.location.LocationHelper.checkGoogleApi
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.atomic.AtomicBoolean

interface LocationManager {

    fun isPreciseLocationEnabled(activity: FragmentActivity): Single<Boolean>

    fun isApproxLocationEnabled(activity: FragmentActivity): Single<Boolean>

    fun checkLocationEnabledBypassPermission(
        activity: FragmentActivity,
        hasLocationPermission: Boolean
    ): Completable

    fun checkLocationEnabled(
        activity: FragmentActivity,
        retryLocationEnableRequest: ActivityResultLauncher<IntentSenderRequest>?,
        showError: Boolean
    ): Completable

    fun getLocations(activity: FragmentActivity): Observable<Location>

    fun getLocation(activity: FragmentActivity): Single<Location>

    fun onClear()
}

class LocationManagerImpl : LocationManager {

    private var fusedClient: FusedLocationProviderClient? = null

    private var cancellationTokenSource: CancellationTokenSource? = null

    companion object {
        private const val REQUEST_INTERVAL = 300L
        private const val REQUEST_FAST_INTERVAL = 100L
    }

    private val locationRequest by lazy {
        LocationRequest.create().apply {
            interval = REQUEST_INTERVAL
            fastestInterval = REQUEST_FAST_INTERVAL
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
    }

    override fun isPreciseLocationEnabled(activity: FragmentActivity): Single<Boolean> =
        checkLocationEnablement(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    override fun isApproxLocationEnabled(activity: FragmentActivity): Single<Boolean> =
        checkLocationEnablement(activity, Manifest.permission.ACCESS_COARSE_LOCATION)

    private fun checkLocationEnablement(
        activity: FragmentActivity,
        vararg perms: String
    ): Single<Boolean> {
        val hasLocationPermission = PermissionUtils.hasSelfPermissions(activity, *perms)
        return checkLocationEnabledBypassPermission(activity, hasLocationPermission)
            .toSingleDefault(true)
            .onErrorReturnItem(false)
    }

    /**
     * Checks if device has enabled location permission without showing any error to the user if it is not.
     * Returns [Completable] which completes on success, or fails if location is not enabled.
     */
    override fun checkLocationEnabledBypassPermission(
        activity: FragmentActivity,
        hasLocationPermission: Boolean
    ): Completable =
        // checks if device can get location
        if (checkGoogleApi(activity, false) && hasLocationPermission) {
            // checks if location is toggled ON
            checkLocationEnabled(activity, null, false)
        } else {
            Completable.error(RuntimeException("Location permission is not available or not enabled"))
        }

    /**
     * Checks if location permission is given on the device, prompting user in case if it is not.
     * Returns [Completable] which completes on success, or fails if location is not enabled.
     */
    override fun checkLocationEnabled(
        activity: FragmentActivity,
        retryLocationEnableRequest: ActivityResultLauncher<IntentSenderRequest>?,
        showError: Boolean
    ): Completable {
        val setting = LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val client = LocationServices.getSettingsClient(activity)
        val error = AtomicBoolean(showError)
        return checkLocationEnabledObservable(
            activity,
            retryLocationEnableRequest,
            client,
            setting,
            error
        )
    }

    private fun checkLocationEnabledObservable(
        activity: FragmentActivity,
        checkLocationEnabled: ActivityResultLauncher<IntentSenderRequest>?,
        client: SettingsClient,
        settings: LocationSettingsRequest,
        showError: AtomicBoolean
    ) = Completable.create { e ->
        client.checkLocationSettings(settings).apply {
            addOnSuccessListener {
                if (fusedClient == null) {
                    fusedClient = LocationServices.getFusedLocationProviderClient(activity)
                }
                e.onComplete()
            }
            addOnFailureListener { exception ->
                (exception as? ResolvableApiException)?.resolve(checkLocationEnabled, showError)
                e.onError(RuntimeException("Check location settings failed", exception))
            }
        }
    }

    private fun ResolvableApiException.resolve(
        checkLocationEnabled: ActivityResultLauncher<IntentSenderRequest>?,
        showError: AtomicBoolean
    ) {
        if (showError.get()) {
            showError.set(false)
            // Location settings are not satisfied, but this can be fixed
            // by showing the user a dialog.
            try {
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                checkLocationEnabled?.launch(IntentSenderRequest.Builder(resolution).build())
            } catch (e: ActivityNotFoundException) {
                // Ignore the error.
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(e: ObservableEmitter<Location>) {
        if (cancellationTokenSource == null) {
            cancellationTokenSource = CancellationTokenSource()
        }
        cancellationTokenSource?.let {
            fusedClient?.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, it.token)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        e.onNext(task.result)
                    } else {
                        e.onError(Throwable("Location service is not available"))
                    }
                } ?: e.onError(Throwable("Location service is not available"))
        }
    }

    override fun getLocations(
        activity: FragmentActivity
    ): Observable<Location> {
        return Observable.create { emitter ->
            if (fusedClient == null) {
                fusedClient = LocationServices.getFusedLocationProviderClient(activity)
            }

            getCurrentLocation(emitter)
        }
    }

    override fun getLocation(activity: FragmentActivity): Single<Location> =
        getLocations(activity).firstOrError()

    override fun onClear() {
        fusedClient = null
        cancellationTokenSource = null
    }
}
