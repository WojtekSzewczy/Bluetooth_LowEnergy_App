package com.example.ble2

import com.siliconlab.bluetoothmesh.adk.BluetoothMesh
import com.siliconlab.bluetoothmesh.adk.configuration.BluetoothMeshConfiguration
import com.siliconlab.bluetoothmesh.adk.configuration.BluetoothMeshConfigurationLimits
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

object AppState {

    var currentSubnet: Subnet? = null
    const val networkName = "network"

    val bluetoothMesh by lazy {
        val limits = BluetoothMeshConfigurationLimits()
        val configuration = BluetoothMeshConfiguration(emptyList(), limits)
        BluetoothMesh.initialize(MainApplication.appContext, configuration)
        BluetoothMesh.getInstance()
    }

    val network by lazy {
        if (bluetoothMesh.networks.isEmpty()) {
            bluetoothMesh.createNetwork(networkName)
        } else {
            bluetoothMesh.networks.first()
        }
    }
}