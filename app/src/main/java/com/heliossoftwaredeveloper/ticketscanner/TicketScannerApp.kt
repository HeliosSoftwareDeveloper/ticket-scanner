package com.heliossoftwaredeveloper.ticketscanner

import android.app.Application
import android.support.multidex.MultiDex
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TicketScannerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        MultiDex.install(this)
    }

    companion object {
        private var instance: TicketScannerApp? = null

        fun applicationContext() : TicketScannerApp {
            return instance as TicketScannerApp
        }
    }
}