package com.example.ble2.data

import android.bluetooth.le.ScanResult

data class ScannedDevice(val result: ScanResult) {
    val name = result.device.name
    val address = result.device.address
}
