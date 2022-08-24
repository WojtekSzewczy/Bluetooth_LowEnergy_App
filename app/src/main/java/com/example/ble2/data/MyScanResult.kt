package com.example.ble2.data

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult

data class MyScanResult(var scanResult: ScanResult) {
    var diodeFlag = false
    var diodeReadValue = ""
    var bluetoothGatt: BluetoothGatt? = null
    lateinit var characteristic: BluetoothGattCharacteristic
}