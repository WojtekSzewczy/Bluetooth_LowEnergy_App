package com.example.ble2

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanResult

class MyScanResult(var scanResult: ScanResult) {
    var isFavourited = false
    var isAddedToFavourited = false
    var isConnected: Boolean = false
    var imageViewVisibility = false
    var diodeFlag = false
    var buttonReadText = false
    var diodeReadValue = ""
    var bluetoothGatt: BluetoothGatt? = null
    lateinit var characteristic: BluetoothGattCharacteristic
}