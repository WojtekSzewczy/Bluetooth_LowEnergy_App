package com.example.ble2

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

object Services {

    private val addressResults = ArrayList<String>()
    private val bluetoothManager = getSystemService(
        BleApplication.appContext,
        BluetoothManager::class.java
    ) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }
    var deviceCounter = 0
    private val results = mutableListOf<MyScanResult>()
    private val _resultsLiveData = MutableLiveData<List<MyScanResult>>(emptyList())
    val resultsLiveData: LiveData<List<MyScanResult>> = _resultsLiveData

    private val _buttonStateListLiveData = MutableLiveData(false)
    val buttonStateListLiveData: LiveData<Boolean> = _buttonStateListLiveData

    private val _isReady = MutableLiveData(ReadyState.UNDEFINED)
    val isReady: LiveData<ReadyState> = _isReady


    enum class ReadyState {
        UNDEFINED,
        READY,
        NOT_READY
    }

    private val scanSettings =
        ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build()

    fun clearScanList() {
        deviceCounter = 0
        results.clear()
        addressResults.clear()
        updateResults()

    }

    fun startBleScan() {
        clearScanList()
        Log.v("scanner", "start")
        bleScanner.startScan(null, scanSettings, leScanCallback)
    }

    fun stopBleScan() {
        Log.v("scanner", "stop")
        bleScanner.stopScan(leScanCallback)
    }

    private val leScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Log.v("ScanCallback", "ScanCallback")
            if (!addressResults.contains(result!!.device.address.toString())) {
                result.let { addressResults.add(it.device.address.toString()) }
                val newResult = MyScanResult(result)
                addResult(newResult)
            }
        }
    }

    val mGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            val currentIndex = addressResults.indexOf(gatt.device.address)
            when (newState) {
                BluetoothAdapter.STATE_CONNECTED -> {
                    resultsLiveData.value?.get(currentIndex)?.isConnected = true
                    resultsLiveData.value?.get(currentIndex)?.bluetoothGatt = gatt
                    _buttonStateListLiveData.postValue(false)
                    Log.v("connection", "connected")

                    if (gatt.device.name == "Blinky Example") {
                        gatt.discoverServices()

                    } else {
                        gatt.disconnect()
                    }

                }
                BluetoothAdapter.STATE_DISCONNECTED -> {
                    Log.v("connection", "disconnected")
                    resultsLiveData.value?.get(currentIndex)?.isConnected = false
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
            val currentIndex = addressResults.indexOf(gatt.device.address)
            resultsLiveData.value?.get(currentIndex)?.characteristic =
                gatt.services[3].characteristics[0]
            _isReady.postValue(ReadyState.READY)

        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (characteristic == gatt.services[3].characteristics[0]) {
                val currentIndex = addressResults.indexOf(gatt.device.address)
                resultsLiveData.value?.get(currentIndex)?.diodeReadValue =
                    characteristic.value.contentToString()
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {

            if (characteristic == gatt.services[3].characteristics[1]) {

                Log.v("if", "asdads")

                if (!_buttonStateListLiveData.value!!) {
                    _buttonStateListLiveData.postValue(true)
                } else {
                    _buttonStateListLiveData.postValue(false)
                }
            }

        }
    }

    private fun updateResults() {
        _resultsLiveData.value = results
    }


    fun addResult(newResult: MyScanResult) {
        results.add(newResult)
        deviceCounter++
        _resultsLiveData.value = results
    }

    fun connect(device: MyScanResult) {
        device.scanResult.device.connectGatt(BleApplication.appContext, false, mGattCallback)
    }

    fun disconnectWithDevice(device: MyScanResult?) {
        var counter = 0
        device?.bluetoothGatt?.disconnect()
        while ((device?.isConnected!!)) {
            Thread.sleep(20)
            counter++
            if (counter == 100) {
                break
            }

        }
    }

    fun readCharacteristic(device: MyScanResult?) {
        device?.bluetoothGatt?.readCharacteristic(device.characteristic)

    }

    fun writeDiode(device: MyScanResult?, signalOn: ByteArray) {
        device?.characteristic?.value = signalOn
        device?.bluetoothGatt?.writeCharacteristic(device.characteristic)
    }

    fun clearData() {
        _isReady.value = ReadyState.UNDEFINED
    }

}