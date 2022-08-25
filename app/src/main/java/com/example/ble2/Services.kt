package com.example.ble2

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ble2.data.BlinkyDevice
import com.example.ble2.data.ScannedDevice
import java.util.*

object Services {

    private val scanner by lazy {
        val bluetoothManager = getSystemService(
            MainApplication.appContext,
            BluetoothManager::class.java
        ) as BluetoothManager

        bluetoothManager.adapter.bluetoothLeScanner
    }
    private val scanSettings =
        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

    private val currentScannedDevices = mutableMapOf<String, ScannedDevice>()
    private val _scannedDevices = MutableLiveData<List<ScannedDevice>>(emptyList())
    val scannedDevices: LiveData<List<ScannedDevice>> = _scannedDevices

    private val _buttonStateListLiveData = MutableLiveData(false)
    val buttonStateListLiveData: LiveData<Boolean> = _buttonStateListLiveData

    private val _isReady = MutableLiveData(ReadyState.UNDEFINED)
    val isReady: LiveData<ReadyState> = _isReady

    private var currentBlinkyDevice: BlinkyDevice? = null

    enum class ReadyState {
        UNDEFINED,
        READY,
        NOT_READY
    }

    fun startBleScan() {
        clearScanList()
        Log.v("scanner", "start")
        scanner.startScan(null, scanSettings, leScanCallback)
    }

    private fun clearScanList() {
        currentScannedDevices.clear()
        updateScannedDevices()
    }

    private fun updateScannedDevices() {
        _scannedDevices.value = currentScannedDevices.values.toList()
    }

    fun stopBleScan() {
        Log.v("scanner", "stop")
        scanner.stopScan(leScanCallback)
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.let {
                val device = ScannedDevice(result)
                if (!currentScannedDevices.contains(device.address)) {
                    addDevice(device)
                }
            }
        }
    }

    private fun addDevice(device: ScannedDevice) {
        currentScannedDevices[device.address] = device
        updateScannedDevices()
    }

    val mGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothAdapter.STATE_CONNECTED -> {
                    currentBlinkyDevice?.bluetoothGatt = gatt
                    _buttonStateListLiveData.postValue(false)
                    Log.d("connection", "connected")
                    if (gatt.device.name == "Blinky Example") {
                        gatt.discoverServices()
                    } else {
                        gatt.disconnect()
                    }
                }
                BluetoothAdapter.STATE_DISCONNECTED -> {
                    Log.d("connection", "disconnected")
                    _isReady.postValue(ReadyState.NOT_READY)
                    gatt.close()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val characteristic2 = gatt.services[3].characteristics[1]
            Log.v("before", "setNotification")
            gatt.setCharacteristicNotification(characteristic2, true)
            val descriptor =
                characteristic2.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
            Log.v("after", "setNotification")
            currentBlinkyDevice?.characteristic = gatt.services[3].characteristics[0]
            _isReady.postValue(ReadyState.READY)

        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            Log.v("onCharacteristicRead", characteristic.value.contentToString())
            if (characteristic == gatt.services[3].characteristics[0]) {
                currentBlinkyDevice?.setDiodeState(characteristic.value.contentToString())
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {

            if (characteristic == gatt.services[3].characteristics[1]) {
                if (!_buttonStateListLiveData.value!!) {
                    _buttonStateListLiveData.postValue(true)
                } else {
                    _buttonStateListLiveData.postValue(false)
                }
            }

        }
    }

    fun connect(device: BlinkyDevice) {
        currentBlinkyDevice = device
        currentBlinkyDevice!!.device.connectGatt(MainApplication.appContext, false, mGattCallback)
    }

    fun disconnectWithDevice() {
        currentBlinkyDevice?.bluetoothGatt?.disconnect()
    }

    fun readCharacteristic() {
        Log.v("readCharacteristic", "fromCallback")

        currentBlinkyDevice?.bluetoothGatt?.readCharacteristic(currentBlinkyDevice!!.characteristic)

    }

    fun writeDiode(signalOn: ByteArray) {
        currentBlinkyDevice?.characteristic?.value = signalOn
        currentBlinkyDevice?.bluetoothGatt?.writeCharacteristic(currentBlinkyDevice!!.characteristic)
    }

    fun clearData() {
        currentBlinkyDevice = null
        _isReady.value = ReadyState.UNDEFINED
    }

}