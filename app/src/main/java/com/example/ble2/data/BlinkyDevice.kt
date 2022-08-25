package com.example.ble2.data

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ble2.Services

class BlinkyDevice(val device: BluetoothDevice) {

    private val _diodeState = MutableLiveData(DiodeState.UNDEFINED)
    val diodeState: LiveData<DiodeState> = _diodeState

    enum class DiodeState {
        UNDEFINED,
        ON,
        OFF
    }

    fun setDiodeState(state: String) {
        if (state == "[1]") {
            _diodeState.postValue(DiodeState.ON)
        } else {
            _diodeState.postValue(DiodeState.OFF)
        }
    }

    fun togleDiodeState() {
        if (_diodeState.value == DiodeState.ON) {
            _diodeState.value = DiodeState.OFF
        } else if (_diodeState.value == DiodeState.OFF) {
            _diodeState.value = DiodeState.ON
        }

    }

    fun turnDiodeOff() {
        val signalOn = byteArrayOf(0x00)
        Services.writeDiode(signalOn)
    }

    fun turnDidodeOn() {
        val signalOn = byteArrayOf(0x01)
        Services.writeDiode(signalOn)
    }

    var bluetoothGatt: BluetoothGatt? = null
    var characteristic: BluetoothGattCharacteristic? = null
}