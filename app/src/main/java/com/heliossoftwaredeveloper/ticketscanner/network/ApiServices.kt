package com.heliossoftwaredeveloper.ticketscanner.network

import com.heliossoftwaredeveloper.ticketscanner.network.model.BarcodeParam
import com.heliossoftwaredeveloper.ticketscanner.network.model.GetVenuesResponse
import com.heliossoftwaredeveloper.ticketscanner.network.model.ScanBarcodeResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.*

interface ApiServices {

    @GET("venues")
    fun getVenues(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Single<GetVenuesResponse>

    @POST("venues/{venue_code}/pax/entry/scan")
    fun scanTicket(
        @Path("venue_code") venueCode: String,
        @Body barcode: BarcodeParam
    ): Single<ScanBarcodeResponse>
}
