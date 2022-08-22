package com.example.ble2.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ble2.Services

class HomeViewModel : ViewModel() {
    val results = Services.resultsLiveData

    private val _isScanning = MutableLiveData(false)
    val isScanning: LiveData<Boolean> = _isScanning

    fun switchScanning() {
        val currentState = _isScanning.value!!
        if (currentState) {
            Services.stopBleScan()
        } else {
            Services.startBleScan()
        }
        _isScanning.postValue(!currentState)
    }
}
