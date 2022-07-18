package com.heliossoftwaredeveloper.ticketscanner.network.source

import com.heliossoftwaredeveloper.ticketscanner.model.ScanResult
import com.heliossoftwaredeveloper.ticketscanner.model.VenueResult
import com.heliossoftwaredeveloper.ticketscanner.network.ApiServices
import com.heliossoftwaredeveloper.ticketscanner.network.model.BarcodeParam
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

interface RemoteSource {
    fun getVenues(latitude: Double, longitude: Double): Single<List<VenueResult>>
    fun scanTicket(venueCode: String, barcode: String): Single<ScanResult>
}

class RemoteSourceImpl @Inject constructor(
    private val apiServices: ApiServices
) : RemoteSource {

    override fun getVenues(latitude: Double, longitude: Double): Single<List<VenueResult>> {
        return apiServices.getVenues(latitude, longitude).map {
            it.venues.map { venue ->
                VenueResult(
                    code = venue.code,
                    name = venue.name,
                    address = venue.address,
                    state = venue.state,
                    postcode = venue.postcode
                )
            }
        }
    }

    override fun scanTicket(venueCode: String, barcode: String): Single<ScanResult> {
        return apiServices.scanTicket(venueCode, BarcodeParam(barcode)).map {
            ScanResult(
                status = it.status,
                action = it.action,
                result = it.result,
                concession = it.concession
            )
        }
    }
}