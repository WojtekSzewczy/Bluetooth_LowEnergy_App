package com.example.ble2.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ble2.Scanner

class HomeViewModel : ViewModel() {
    val devices = Scanner.scannedDevices

    private val _isScanning = MutableLiveData(false)
    val isScanning: LiveData<Boolean> = _isScanning

    fun switchScanning() {
        val currentState = _isScanning.value!!
        if (currentState) {
            Scanner.stopBleScan()
        } else {
            Scanner.startBleScan()
        }
        _isScanning.postValue(!currentState)
    }
}
