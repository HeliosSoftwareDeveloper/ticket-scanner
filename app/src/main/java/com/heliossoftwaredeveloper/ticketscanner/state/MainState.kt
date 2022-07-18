package com.heliossoftwaredeveloper.ticketscanner.state

import android.location.Location
import com.heliossoftwaredeveloper.ticketscanner.model.ScanResult
import com.heliossoftwaredeveloper.ticketscanner.model.VenueResult

sealed class MainState {

    object LocationNotAvailableOnDevice : MainState()

    object LocationPermissionGranted : MainState()

    object ShowLocationPermission : MainState()

    object ShowErrorNetwork : MainState()

    object ShowLoading : MainState()

    object HideLoading : MainState()

    data class CurrentLocation(val location: Location) : MainState()

    data class ShowVenueList(val venues: List<VenueResult>) : MainState()

    data class ShowScanResult(val result: ScanResult) : MainState()
}
