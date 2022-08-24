package com.example.ble2.data

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult

data class MyScanResult(var scanResult: ScanResult) {
    var isFavourited = false
    var isAddedToFavourited = false
    var isConnected: Boolean = false
    var diodeFlag = false
    var diodeReadValue = ""
    var bluetoothGatt: BluetoothGatt? = null
    lateinit var characteristic: BluetoothGattCharacteristic
}