package com.heliossoftwaredeveloper.ticketscanner.model

data class ScanResult(
    val status: String,
    val action: String,
    val result: String,
    val concession: Int
)