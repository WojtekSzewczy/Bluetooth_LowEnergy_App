package com.example.ble2.data

import android.bluetooth.le.ScanResult

data class ScannedDevice(val result: ScanResult) {
    var type: DeviceType? = null
    var name = result.device.name
    val address = result.device.address

    enum class DeviceType {
        BLINKY_EXAMPLE,
        MESH_DEVICE,
        OTHER
    }
}
