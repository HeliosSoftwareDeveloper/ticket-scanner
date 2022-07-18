package com.heliossoftwaredeveloper.ticketscanner.network.model

import com.google.gson.annotations.SerializedName

data class GetVenuesResponse(
    @SerializedName("venues")
    val venues: List<Venue>
    )

data class Venue(
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("address")
    val address: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("postcode")
    val postcode: String
)