package com.heliossoftwaredeveloper.ticketscanner.network.model

import com.google.gson.annotations.SerializedName

data class BarcodeParam(
    @SerializedName("barcode")
    val barcode: String
)