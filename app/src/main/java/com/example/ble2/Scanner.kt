package com.example.ble2

import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ble2.data.ScannedDevice

object Scanner {

    val bluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(
            MainApplication.appContext,
            BluetoothManager::class.java
        ) as BluetoothManager

        bluetoothManager.adapter
    }

    val scanner = bluetoothAdapter.bluetoothLeScanner

    private val scanSettings =
        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

    private val currentScannedDevices = mutableMapOf<String, ScannedDevice>()
    private val _scannedDevices = MutableLiveData<List<ScannedDevice>>(emptyList())
    val scannedDevices: LiveData<List<ScannedDevice>> = _scannedDevices

    private val _isScanning = MutableLiveData(false)
    val isScanning: LiveData<Boolean> = _isScanning

    //private var scanning = false

    private val handler = Handler(Looper.getMainLooper())
    private val provisionService = ParcelUuid.fromString("00001827-0000-1000-8000-00805f9b34fb")

    private val runnableStoppingScanning = {
        _isScanning.value = false
        scanner.stopScan(leScanCallback)
    }

    fun startBleScan() {
        if (isScanning.value!!) {
            return
        }
        clearScanList()
        handler.postDelayed(runnableStoppingScanning, SCAN_PERIOD_IN_MS)
        _isScanning.value = true

        scanner.startScan(null, scanSettings, leScanCallback)
    }

    const val SCAN_PERIOD_IN_MS: Long = 10000

    fun clearScanList() {
        currentScannedDevices.clear()
        updateScannedDevices()
    }

    private fun updateScannedDevices() {
        _scannedDevices.value = currentScannedDevices.values.toList()
    }

    fun stopBleScan() {
        handler.removeCallbacks(runnableStoppingScanning)
        scanner.stopScan(leScanCallback)
        _isScanning.value = false
    }

    fun postScanningValue(value: Boolean) {
        _isScanning.postValue(value)
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            result?.let {
                val device = ScannedDevice(result)
                if (!currentScannedDevices.contains(device.address)) {
                    setDeviceType(result, device)
                } else {
                    correctDeviceType(device, result)
                }
            }
        }

        private fun setDeviceType(
            result: ScanResult,
            device: ScannedDevice
        ) {

            if (isUnprovisionedMeshDevice(result)) {
                device.type = ScannedDevice.DeviceType.MESH_DEVICE
                device.name = "BLUETOOTH MESH DEVICE"
            } else if (result.device.name == "Blinky Example") {
                device.type = ScannedDevice.DeviceType.BLINKY_EXAMPLE
            } else {
                device.type = ScannedDevice.DeviceType.OTHER
            }
            addDevice(device)
        }

        private fun correctDeviceType(
            device: ScannedDevice,
            result: ScanResult?
        ) {
            if (currentScannedDevices[device.address]?.type != ScannedDevice.DeviceType.MESH_DEVICE) {
                if (isUnprovisionedMeshDevice(result)) {
                    device.type = ScannedDevice.DeviceType.MESH_DEVICE
                    device.name = "BLUETOOTH MESH DEVICE"
                    addDevice(device)
                }
            }
        }
    }

    private fun isUnprovisionedMeshDevice(result: ScanResult?): Boolean {
        return result?.scanRecord?.serviceUuids?.contains(provisionService) == true
    }

    private fun addDevice(device: ScannedDevice) {
        currentScannedDevices[device.address] = device
        updateScannedDevices()
    }


}