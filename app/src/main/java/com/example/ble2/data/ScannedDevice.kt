package com.example.ble2.data

import android.bluetooth.le.ScanResult

data class ScannedDevice(val result: ScanResult) {
    var type: deviceType? = null
    var name = result.device.name
    val address = result.device.address

    enum class deviceType {
        BLINKY_EXAMPLE,
        MESH_DEVICE,
        OTHER
    }


}
