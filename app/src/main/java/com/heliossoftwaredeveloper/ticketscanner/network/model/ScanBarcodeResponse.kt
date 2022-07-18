package com.heliossoftwaredeveloper.ticketscanner.network.model

import com.google.gson.annotations.SerializedName

data class ScanBarcodeResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("action")
    val action: String,

    @SerializedName("result")
    val result: String,

    @SerializedName("concession")
    val concession: Int
)