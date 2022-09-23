package com.example.ble2.ui2.blinky

import android.bluetooth.le.ScanResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ble2.data.BlinkyDevice

class DetailsViewModel : ViewModel() {
    private val _areServicesVisible = MutableLiveData(true)
    val areServicesVisible: LiveData<Boolean> = _areServicesVisible

    private lateinit var blinkyDevice: BlinkyDevice

    val isReady
        get() = blinkyDevice.isReady
    val diodeState
        get() = blinkyDevice.diodeState
    val buttonState
        get() = blinkyDevice.buttonState
    val address: String
        get() = blinkyDevice.address

    fun toggleDiodeState() {
        blinkyDevice.toggleDiodeState()
    }

    val uuiDs: String
        get() = blinkyDevice.getUUIDs()

    fun toggleButtonText() {
        val currentState = _areServicesVisible.value!!
        _areServicesVisible.postValue(!currentState)
    }

    override fun onCleared() {
        super.onCleared()
        blinkyDevice.disconnect()
    }

    fun init(result: ScanResult) {
        blinkyDevice = BlinkyDevice(result)
        blinkyDevice.connect()

    }
}