package com.heliossoftwaredeveloper.ticketscanner.repository

import com.heliossoftwaredeveloper.ticketscanner.model.ScanResult
import com.heliossoftwaredeveloper.ticketscanner.model.VenueResult
import com.heliossoftwaredeveloper.ticketscanner.network.source.RemoteSource
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

interface VenueRepository {
    fun getVenues(latitue: Double, longitute: Double): Single<List<VenueResult>>
    fun scanTicket(venueCode: String, barcode: String): Single<ScanResult>
}

class VenueRepositoryImpl @Inject constructor(
    private val remote: RemoteSource
) : VenueRepository {

    override fun getVenues(latitue: Double, longitute: Double): Single<List<VenueResult>> {
        return remote.getVenues(latitue, longitute)
    }

    override fun scanTicket(venueCode: String, barcode: String): Single<ScanResult> {
        return remote.scanTicket(venueCode, barcode)
    }
}