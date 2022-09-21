package com.example.ble2.ui2.home

import androidx.lifecycle.ViewModel
import com.example.ble2.Scanner

class HomeViewModel : ViewModel() {
    val devices = Scanner.scannedDevices
    val isScanning = Scanner.isScanning

    fun switchScanning() {
        val currentState = isScanning.value!!
        if (currentState) {
            Scanner.stopBleScan()
        } else {
            Scanner.startBleScan()
        }
        Scanner.postScanningValue(!currentState)
    }
}
