package com.virtual_market.planetshipmentapp.MyUils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.virtual_market.planetshipmentapp.Modal.ResponseTransporter
import com.virtual_market.planetshipmentapp.Modal.ResponseUserLogin
import net.gotev.uploadservice.BuildConfig
import net.gotev.uploadservice.UploadServiceConfig

class PlanetShippingApplication: Application() {

    private val ONESIGNAL_APP_ID = "636fee50-c816-4e25-acc0-7ec8e085787f"
    var CHANNEL = "UploadServiceDemoChannel"
    var responseUserLogin=ResponseUserLogin()
    var responseUserTransporter=ResponseTransporter()

    override fun onCreate() {
        super.onCreate()
//        createNotificationChannel()

//        // OneSignal Initialization
//        OneSignal.initWithContext(this)
//        OneSignal.setAppId(ONESIGNAL_APP_ID)
//
//
//        // Set your application namespace to avoid conflicts with other apps
//        // using this library
//
//        // Enable verbose OneSignal logging to debug issues if needed.
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        createNotificationChannel()

        UploadServiceConfig.initialize(
            this,
            CHANNEL,
            BuildConfig.DEBUG
        )

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL,
                "Upload Service Demo",
                NotificationManager.IMPORTANCE_LOW
            )
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel)
        }
    }

}