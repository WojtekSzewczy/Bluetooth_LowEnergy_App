package com.example.ble2

import android.app.Application
import android.content.Context
import com.siliconlab.bluetoothmesh.adk.BluetoothMesh
import com.siliconlab.bluetoothmesh.adk.configuration.BluetoothMeshConfiguration
import com.siliconlab.bluetoothmesh.adk.configuration.BluetoothMeshConfigurationLimits
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        val limits = BluetoothMeshConfigurationLimits()
        val configuration = BluetoothMeshConfiguration(emptyList(), limits)
        BluetoothMesh.initialize(this, configuration)
        BluetoothMesh.getInstance().networks.isEmpty()
        val network = BluetoothMesh.getInstance().createNetwork("other ")
        subnet = network.createSubnet("dfdg")
    }

    companion object {
        lateinit var appContext: Context
        lateinit var subnet: Subnet
    }


}