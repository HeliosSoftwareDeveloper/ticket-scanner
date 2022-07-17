package com.heliossoftwaredeveloper.ticketscanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen https://developer.android.com/guide/topics/ui/splash-screen/migrate
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }
}