package com.example.ble2

import android.app.Application
import android.content.Context

class BleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
    }
}