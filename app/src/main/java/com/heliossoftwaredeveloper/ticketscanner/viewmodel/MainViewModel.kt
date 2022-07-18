package com.heliossoftwaredeveloper.ticketscanner.viewmodel

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.fragment.app.FragmentActivity
import com.heliossoftwaredeveloper.ticketscanner.state.MainState
import com.heliossoftwaredeveloper.ticketscanner.common.viewmodel.BaseViewModel
import com.heliossoftwaredeveloper.ticketscanner.location.LocationManager
import com.heliossoftwaredeveloper.ticketscanner.repository.VenueRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val locationManager: LocationManager,
    private val venueRepository: VenueRepository
) : BaseViewModel() {

    private val _state by lazy { PublishSubject.create<MainState>() }
    val state: Observable<MainState> = _state

    fun getLocation(fragmentActivity: FragmentActivity) {
        locationManager.getLocation(fragmentActivity).subscribeOn(schedulers.io())
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe (
                { result ->
                    _state.onNext(MainState.CurrentLocation(result))
                    getVenues(result.latitude, result.longitude)
                },
                {
                    _state.onNext(MainState.LocationNotAvailableOnDevice)
                }
            )
    }

    fun checkLocationEnabled(activity: FragmentActivity, retryLocationRequest: ActivityResultLauncher<IntentSenderRequest>) {
        locationManager.checkLocationEnabled(activity, retryLocationRequest, true)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .toSingleDefault(true)
            .onErrorReturnItem(false)
            .doFinally { locationManager.onClear() }
            .subscribe (
                { result ->
                    if (result) {
                        checkPermission(activity)
                    } else {
                        _state.onNext(MainState.LocationNotAvailableOnDevice)
                    }
                },
                {
                    _state.onNext(MainState.LocationNotAvailableOnDevice)
                }
            )
    }

    fun getVenues(latitude: Double, longitude: Double) {
        venueRepository.getVenues(latitude, longitude)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .doOnSubscribe {
                _state.onNext(MainState.ShowLoading)
            }
            .doFinally {
                _state.onNext(MainState.HideLoading)
            }
            .subscribe(
                { result ->
                    _state.onNext(MainState.ShowVenueList(result))
                },
                {
                    _state.onNext(MainState.ShowErrorNetwork)
                }
            ).apply { disposables.add(this) }
    }

    fun scanTicket(venueCode: String, barcode: String) {
        venueRepository.scanTicket(venueCode, barcode)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .doOnSubscribe {
                _state.onNext(MainState.ShowLoading)
            }
            .doFinally {
                _state.onNext(MainState.HideLoading)
            }
            .subscribe(
                { result ->
                    _state.onNext(MainState.ShowScanResult(result))
                },
                {
                    _state.onNext(MainState.ShowErrorNetwork)
                }
            ).apply { disposables.add(this) }
    }

    private fun checkPermission(activity: FragmentActivity) {
        locationManager.isPreciseLocationEnabled(activity)
            .subscribeOn(schedulers.io())
            .observeOn(schedulers.ui())
            .subscribe (
                { result ->
                    if (result) {
                        _state.onNext(MainState.LocationPermissionGranted)
                    } else {
                        _state.onNext(MainState.ShowLocationPermission)
                    }
                },
                {
                    _state.onNext(MainState.LocationNotAvailableOnDevice)
                }
            )
    }
}
