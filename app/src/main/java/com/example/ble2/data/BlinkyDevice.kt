package com.example.ble2.data

import android.bluetooth.*
import android.bluetooth.le.ScanResult
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ble2.MainApplication
import com.example.ble2.ReadyState
import java.util.*

class BlinkyDevice(val result: ScanResult) {

    private val _diodeState = MutableLiveData(DiodeState.UNDEFINED)
    val diodeState: LiveData<DiodeState> = _diodeState

    private val _buttonState = MutableLiveData(ButtonState.UNDEFINED)
    val buttonState: LiveData<ButtonState> = _buttonState

    private var diodeCharacteristic: BluetoothGattCharacteristic? = null
    private var buttonCharacteristic: BluetoothGattCharacteristic? = null

    private var bluetoothGatt: BluetoothGatt? = null

    val address = result.device.address

    private val blinkyServiceIndex = 3
    private val diodeCharacteristicIndex = 0
    private val buttonCharacteristicIndex = 1

    private val signalOn = byteArrayOf(0x01)
    private val signalOff = byteArrayOf(0x00)

    private val descriptorUUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    enum class DiodeState {
        UNDEFINED,
        ON,
        OFF
    }

    enum class ButtonState {
        UNDEFINED,
        PRESSED,
        RELEASED
    }

    private val _isReady = MutableLiveData(ReadyState.UNDEFINED)
    val isReady: LiveData<ReadyState> = _isReady

    private val handler = Handler(Looper.getMainLooper())
    private val DELAY_PERIOD: Long = 200

    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothAdapter.STATE_CONNECTED -> {
                    bluetoothGatt = gatt
                    gatt.discoverServices()
                }
                BluetoothAdapter.STATE_DISCONNECTED -> {
                    _isReady.postValue(ReadyState.NOT_READY)
                    gatt.close()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            diodeCharacteristic =
                gatt.services[blinkyServiceIndex].characteristics[diodeCharacteristicIndex]
            buttonCharacteristic =
                gatt.services[blinkyServiceIndex].characteristics[buttonCharacteristicIndex]
            setNotification(gatt)
            handler.postDelayed({ readDiodeCharacteristic() }, DELAY_PERIOD)//TODO add queueing
            handler.postDelayed({ readButtonCharacteristic() }, DELAY_PERIOD * 2)
            handler.postDelayed({ _isReady.postValue(ReadyState.READY) }, DELAY_PERIOD * 3)
        }

        override fun onCharacteristicWrite(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            if (characteristic == diodeCharacteristic) {
                setDiodeState(characteristic.value)
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            when (characteristic) {
                diodeCharacteristic -> {
                    setDiodeState(characteristic.value)
                }
                buttonCharacteristic -> {
                    setButtonState(characteristic.value)
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic == buttonCharacteristic) {
                toggleButtonState()
            }
        }
    }

    private fun setDiodeState(state: ByteArray) {
        _diodeState.postValue(if (state.contentEquals(signalOn)) DiodeState.ON else DiodeState.OFF)
    }

    private fun setButtonState(state: ByteArray) {
        _buttonState.postValue(if (state.contentEquals(signalOn)) ButtonState.PRESSED else ButtonState.RELEASED)
    }

    fun toggleButtonState() {
        if (_buttonState.value == ButtonState.PRESSED) {
            _buttonState.postValue(ButtonState.RELEASED)
        } else if (_buttonState.value == ButtonState.RELEASED) {
            _buttonState.postValue(ButtonState.PRESSED)
        }
    }

    fun toggleDiodeState() {
        if (_diodeState.value == DiodeState.ON) {
            writeDiode(signalOff)
        } else if (_diodeState.value == DiodeState.OFF) {
            writeDiode(signalOn)
        }
    }

    fun getUUIDs(): String = buildString {
        bluetoothGatt?.services?.forEach {
            append("UUID: ")
            append(it.uuid)
            appendLine()
            it.characteristics.forEach {
                append("   CHARACTERISTIC: ")
                append(it.uuid)
                appendLine()
            }
            appendLine()
        }
    }

    private fun writeDiode(signalOn: ByteArray) {
        diodeCharacteristic?.value = signalOn
        bluetoothGatt?.writeCharacteristic(diodeCharacteristic)
    }

    private fun setNotification(gatt: BluetoothGatt) {
        gatt.setCharacteristicNotification(buttonCharacteristic, true)
        val descriptor =
            buttonCharacteristic?.getDescriptor(descriptorUUID)
        descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
        gatt.writeDescriptor(descriptor)
    }

    fun disconnect() {
        bluetoothGatt.let {
            bluetoothGatt?.disconnect()
        }
    }

    fun readButtonCharacteristic() {
        bluetoothGatt?.readCharacteristic(buttonCharacteristic)
    }

    fun readDiodeCharacteristic() {
        bluetoothGatt?.readCharacteristic(diodeCharacteristic)
    }

    fun connect() {
        bluetoothGatt = result.device.connectGatt(
            MainApplication.appContext, false, bluetoothGattCallback,
            BluetoothDevice.TRANSPORT_LE
        )
    }
}
