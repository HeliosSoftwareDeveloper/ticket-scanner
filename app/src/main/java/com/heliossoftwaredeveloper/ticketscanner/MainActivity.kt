package com.heliossoftwaredeveloper.ticketscanner

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import com.heliossoftwaredeveloper.ticketscanner.adapter.VenueListAdapter
import com.heliossoftwaredeveloper.ticketscanner.common.base.BaseViewModelActivity
import com.heliossoftwaredeveloper.ticketscanner.databinding.ActivityMainBinding
import com.heliossoftwaredeveloper.ticketscanner.model.VenueResult
import com.heliossoftwaredeveloper.ticketscanner.state.MainState
import com.heliossoftwaredeveloper.ticketscanner.viewmodel.MainViewModel
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.concurrent.timerTask


@AndroidEntryPoint
class MainActivity : BaseViewModelActivity<ActivityMainBinding, MainViewModel>() {

    private lateinit var venueListAdapter : VenueListAdapter
    private var lastKnownVenueCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen https://developer.android.com/guide/topics/ui/splash-screen/migrate
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setUpViewList()
        viewModel
            .state
            .observeOn(scheduler.ui())
            .subscribe {
                handleState(it)
            }
            .apply { disposables.add(this) }

        checkLocationPermission()
    }

    private fun setUpViewList() {
        venueListAdapter = VenueListAdapter(object : VenueListAdapter.VenueListAdapterListener{
            override fun onVenueSelect(item: VenueResult) {
                lastKnownVenueCode = item.code
                barcodeLauncher.launch(ScanOptions())
            }
        })
        with (binding.venueRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = venueListAdapter
        }
    }

    override fun getLayoutId() = R.layout.activity_main

    private fun handleState(state: MainState?) {
        when (state) {
            is MainState.LocationNotAvailableOnDevice -> {

            }
            is MainState.LocationPermissionGranted -> {
                viewModel.getLocation(this)
            }
            is MainState.ShowLocationPermission -> {
                showLocationPermission()
            }
            is MainState.CurrentLocation -> {
                Log.i("current location", "-> $state")
            }
            is MainState.ShowErrorNetwork -> {
                Toast.makeText(
                    this@MainActivity,
                    "Network Error",
                    Toast.LENGTH_LONG
                ).show()
            }
            is MainState.ShowLoading -> {
                binding.trackSwipeToRefresh.isRefreshing = true
            }
            is MainState.HideLoading -> {
                binding.trackSwipeToRefresh.isRefreshing = false
            }
            is MainState.ShowVenueList -> {
                venueListAdapter.updateDataSet(state.venues)
            }
            is MainState.ShowScanResult -> {
                Toast.makeText(
                    this@MainActivity,
                    "${state.result.result} : ${state.result.status}",
                    Toast.LENGTH_LONG
                ).show()
                Timer().schedule(timerTask {
                    barcodeLauncher.launch(ScanOptions())
                }, SCAN_DELAY)
            }
            else -> {
                //Do Nothing
            }
        }
    }

    private val retryLocationEnableRequest =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == Activity.RESULT_OK) checkLocationPermission()
        }

    private val permissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    checkLocationPermission()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    checkLocationPermission()
                } else -> {
                    // No location access granted.
                }
            }
        }

    private val barcodeLauncher = registerForActivityResult(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents == null) {
            Toast.makeText(this@MainActivity, "Cancelled", Toast.LENGTH_LONG).show()
        } else {
            viewModel.scanTicket(lastKnownVenueCode, result.contents)
        }
    }

    private fun checkLocationPermission() {
        viewModel.checkLocationEnabled(this, retryLocationEnableRequest)
    }

    private fun showLocationPermission() {
            permissionsLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }

    companion object {
        private const val SCAN_DELAY = 5000L
    }
}