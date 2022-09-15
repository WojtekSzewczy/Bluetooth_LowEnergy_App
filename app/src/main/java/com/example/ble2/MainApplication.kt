package com.example.ble2

import android.app.Application
import android.content.Context
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
        var subnet: Subnet? = null
        var selectedPosition = -1


    }


}