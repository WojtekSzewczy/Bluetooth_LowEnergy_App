package com.example.ble2

import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ble2.data.BlinkyDevice
import com.example.ble2.data.ScannedDevice

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

    private val _isReady = MutableLiveData(ReadyState.UNDEFINED)
    val isReady: LiveData<ReadyState> = _isReady

    private var currentBlinkyDevice: BlinkyDevice? = null

    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())


    enum class ReadyState {
        UNDEFINED,
        READY,
        NOT_READY
    }

    fun startBleScan() {
        clearScanList()
        Log.v("scanner", "start")
        val SCAN_PERIOD: Long = 10000
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                scanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            scanning = true
            scanner.startScan(leScanCallback)
        } else {
            scanning = false
            scanner.stopScan(leScanCallback)
        }
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
                Log.v("uuid", result.scanRecord?.serviceUuids.toString() + result.device.address)
                if (!currentScannedDevices.contains(device.address) || result.scanRecord?.serviceUuids.toString() == "[00001827-0000-1000-8000-00805f9b34fb]") {
                    if (result.scanRecord?.serviceUuids.toString() == "[00001827-0000-1000-8000-00805f9b34fb]") {
                        device.type = ScannedDevice.deviceType.MESH_DEVICE
                        device.name = "BLUETOOTH MESH DEVICE"
                    } else if (result.device.name == "Blinky Example") {
                        device.type = ScannedDevice.deviceType.BLINKY_EXAMPLE
                    } else {
                        device.type = ScannedDevice.deviceType.OTHER
                    }
                    addDevice(device)
                }
            }
        }
    }

    private fun addDevice(device: ScannedDevice) {
        currentScannedDevices[device.address] = device
        updateScannedDevices()
    }
    fun clearData() {
        currentBlinkyDevice = null
        _isReady.value = ReadyState.UNDEFINED
    }

}