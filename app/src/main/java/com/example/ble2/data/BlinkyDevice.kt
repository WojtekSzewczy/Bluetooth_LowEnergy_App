package com.example.ble2.data

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues.TAG
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ble2.MainApplication
import com.example.ble2.Services
import com.siliconlab.bluetoothmesh.adk.connectable_device.RefreshBluetoothDeviceCallback
import com.siliconlab.bluetoothmesh.adk.connectable_device.RefreshGattServicesCallback
import java.util.*

class BlinkyDevice(val result: ScanResult, val deviceType: ScannedDevice.deviceType) {

    private val _diodeState = MutableLiveData(DiodeState.UNDEFINED)
    val diodeState: LiveData<DiodeState> = _diodeState

    private val _buttonState = MutableLiveData(ButtonState.UNDEFINED)

    val buttonState: LiveData<ButtonState> = _buttonState
    var DiodeCharacteristic: BluetoothGattCharacteristic? = null
    var ButtonCharacteristic: BluetoothGattCharacteristic? = null
    var scanResult = result
    var bluetoothGatt: BluetoothGatt? = null
    val address: String = result.device.address
    private val scanner by lazy {
        val bluetoothManager = ContextCompat.getSystemService(
            MainApplication.appContext,
            BluetoothManager::class.java
        ) as BluetoothManager

        bluetoothManager.adapter.bluetoothLeScanner
    }

    private var refreshBluetoothDeviceCallback: RefreshBluetoothDeviceCallback? = null
    var refreshGattServicesCallback: RefreshGattServicesCallback? = null
    private var mtuSize = 0

    enum class DiodeState {
        UNDEFINED,
        ON,
        OFF
    }

    enum class ButtonState {
        UNDEFINED,
        CLICKED,
        UNCLICKED
    }

    private val _isReady = MutableLiveData(Services.ReadyState.UNDEFINED)
    val isReady: LiveData<Services.ReadyState> = _isReady

    val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothAdapter.STATE_CONNECTED -> {
                    bluetoothGatt = gatt
                    gatt.discoverServices()
                }
                BluetoothAdapter.STATE_DISCONNECTED -> {
                    _isReady.postValue(Services.ReadyState.NOT_READY)
                    gatt.close()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if (deviceType == ScannedDevice.deviceType.BLINKY_EXAMPLE) {
                DiodeCharacteristic = gatt.services[3].characteristics[0]
                ButtonCharacteristic = gatt.services[3].characteristics[1]
                setNotification(gatt)
            }

            _isReady.postValue(Services.ReadyState.READY)

            if (status == BluetoothGatt.GATT_SUCCESS) {

                refreshGattServicesCallback?.onSuccess()
            } else {
                refreshGattServicesCallback?.onFail()
            }

        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            when (characteristic) {
                gatt.services[3].characteristics[0] -> {
                    setDiodeState(characteristic.value.contentToString())
                }
                gatt.services[3].characteristics[1] -> {
                    setButtonState(characteristic.value.contentToString())
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic == gatt.services[3].characteristics[1]) {
                togleButtonState()
            }

        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mtuSize = mtu
                gatt.discoverServices()
            }
        }
    }
    val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result);
            if (result.scanRecord == null ||
                result.scanRecord!!.serviceUuids == null ||
                result.scanRecord!!.serviceUuids.isEmpty()
            ) {
                if (result.device.address == address) {
                    scanner.stopScan(this)
                    scanResult = result


                    refreshBluetoothDeviceCallback?.success()
                }
            }
        }
    }

    fun setDiodeState(state: String) {
        if (state == "[1]") {
            _diodeState.postValue(DiodeState.ON)
        } else {
            _diodeState.postValue(DiodeState.OFF)
        }
    }

    fun setButtonState(state: String) {
        if (state == "[1]") {
            _buttonState.postValue(ButtonState.CLICKED)
        } else {
            _buttonState.postValue(ButtonState.UNCLICKED)
        }
    }

    fun togleButtonState() {
        if (_buttonState.value == ButtonState.CLICKED) {
            _buttonState.postValue(ButtonState.UNCLICKED)
        } else if (_buttonState.value == ButtonState.UNCLICKED) {
            _buttonState.postValue(ButtonState.CLICKED)
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
        writeDiode(signalOn)
    }

    fun turnDidodeOn() {
        val signalOn = byteArrayOf(0x01)
        writeDiode(signalOn)
    }

    fun writeDiode(signalOn: ByteArray) {
        DiodeCharacteristic?.value = signalOn
        bluetoothGatt?.writeCharacteristic(DiodeCharacteristic)
    }


    enum class ReadyState {
        UNDEFINED,
        READY,
        NOT_READY
    }

    private fun setNotification(gatt: BluetoothGatt) {
        gatt.setCharacteristicNotification(ButtonCharacteristic, true)
        val descriptor =
            ButtonCharacteristic?.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
        descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        gatt.writeDescriptor(descriptor)
    }


    fun disconnect() {
        Log.v(TAG, "disconnect")
        bluetoothGatt.let {
            bluetoothGatt?.disconnect()
        }
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        Log.v("readCharacteristic", "fromCallback")
        bluetoothGatt?.readCharacteristic(characteristic)

    }

    fun connect() {
        Log.v(TAG, "connect")
        bluetoothGatt = result.device.connectGatt(

            MainApplication.appContext, false, bluetoothGattCallback,
            BluetoothDevice.TRANSPORT_LE
        )
    }
}
