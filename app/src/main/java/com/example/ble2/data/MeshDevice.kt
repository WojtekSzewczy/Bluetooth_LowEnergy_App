package com.example.ble2.data

import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.ContentValues
import android.os.ParcelUuid
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ble2.MainApplication
import com.example.ble2.Services
import com.siliconlab.bluetoothmesh.adk.connectable_device.*
import java.lang.reflect.Method
import java.util.*

class MeshDevice(val result: ScanResult) : ConnectableDevice() {

    var bluetoothGatt: BluetoothGatt? = null
    private var mtuSize = 0
    val address: String = result.device.address


    private val _isReady = MutableLiveData(Services.ReadyState.UNDEFINED)
    val isReady: LiveData<Services.ReadyState> = _isReady

    private var refreshBluetoothDeviceCallback: RefreshBluetoothDeviceCallback? = null
    var refreshGattServicesCallback: RefreshGattServicesCallback? = null

    var scanResult = result

    private val scanner by lazy {
        val bluetoothManager = ContextCompat.getSystemService(
            MainApplication.appContext,
            BluetoothManager::class.java
        ) as BluetoothManager

        bluetoothManager.adapter.bluetoothLeScanner
    }


    val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothAdapter.STATE_CONNECTED -> {
                    bluetoothGatt = gatt
                    onConnected()
                    gatt.discoverServices()
                }
                BluetoothAdapter.STATE_DISCONNECTED -> {
                    onDisconnected()
                    gatt.close()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
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

        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            updateData(characteristic.service.uuid, characteristic.uuid, characteristic.value)
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

    enum class ReadyState {
        UNDEFINED,
        READY,
        NOT_READY
    }

    override fun onConnected() {
        super.onConnected()
    }

    override fun getAdvertisementData() = Objects.requireNonNull(scanResult.scanRecord)?.bytes

    override fun disconnect() {

        Log.v(ContentValues.TAG, "disconnect")
        bluetoothGatt.let {
            bluetoothGatt?.disconnect()
        }
    }

    fun readCharacteristic(characteristic: BluetoothGattCharacteristic) {
        Log.v("readCharacteristic", "fromCallback")
        bluetoothGatt?.readCharacteristic(characteristic)
    }

    override fun getMTU(): Int {
        return mtuSize
    }

    override fun hasService(service: UUID?): Boolean {
        Log.v(ContentValues.TAG, "hasService")

        if (bluetoothGatt?.services!!.isNotEmpty()) {
            return bluetoothGatt?.getService(service) != null
        } else {
            return scanResult.scanRecord?.serviceUuids?.contains(ParcelUuid(service))
                ?: return false
        }
    }

    override fun getServiceData(service: UUID?): ByteArray {
        Log.v(ContentValues.TAG, "getServiceData")
        Log.v("getServiceData UUID", service.toString())
        Log.v("getServiceData SCAN RESULT ", scanResult.toString())
        Log.v("getServiceData Scan record", scanResult.scanRecord.toString())
        Log.v("getServiceData service Data", scanResult.scanRecord?.serviceData.toString())

        return service?.let { scanResult.scanRecord?.serviceData?.get(ParcelUuid(it)) }!!
    }

    override fun subscribe(
        service: UUID?,
        characteristic: UUID?,
        connectableDeviceSubscriptionCallback: ConnectableDeviceSubscriptionCallback?
    ) {
        Log.v("subscribe", "subscribe")
        try {
            Log.v("try", service.toString())
            Log.v("try", characteristic.toString())
            val bluetoothGattCharacteristic =
                bluetoothGatt?.getService(service)!!.getCharacteristic(characteristic)
            if (!bluetoothGatt?.setCharacteristicNotification(
                    bluetoothGattCharacteristic,
                    true
                )!!
            ) {
                throw Exception("Enabling characteristic notification failed")
            }
            val bluetoothGattDescriptor = bluetoothGattCharacteristic.descriptors.takeIf {
                it.size == 1
            }?.first()
                ?: throw Exception("Descriptors size (${bluetoothGattCharacteristic.descriptors.size}) different than expected: 1")
            bluetoothGattDescriptor.apply {
                value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            }
            if (!bluetoothGatt?.writeDescriptor(bluetoothGattDescriptor)!!) {
                throw Exception("Writing to descriptor failed")
            }
            connectableDeviceSubscriptionCallback?.onSuccess(service, characteristic)
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "subscribe error: $e")
            connectableDeviceSubscriptionCallback?.onFail(service, characteristic)
        }
    }

    override fun writeData(
        service: UUID?,
        characteristic: UUID?,
        data: ByteArray?,
        connectableDeviceWriteCallback: ConnectableDeviceWriteCallback?
    ) {
        Log.v(ContentValues.TAG, "writeData")

        try {
            val bluetoothGattCharacteristic =
                bluetoothGatt?.getService(service)!!.getCharacteristic(characteristic)
            bluetoothGattCharacteristic.value = data
            bluetoothGattCharacteristic.writeType =
                BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
            if (!bluetoothGatt?.writeCharacteristic(bluetoothGattCharacteristic)!!) {
                throw Exception("Writing to characteristic failed")
            }
            connectableDeviceWriteCallback?.onWrite(service, characteristic)
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "writeData error: ${e.message}")
            connectableDeviceWriteCallback?.onFailed(service, characteristic)
        }
    }

    override fun refreshGattServices(refreshGattServicesCallback: RefreshGattServicesCallback) {
        Log.v(ContentValues.TAG, "refreshGattServices")

        refreshDeviceCache()
        bluetoothGatt?.discoverServices()
    }

    fun refreshDeviceCache() {
        Log.v(ContentValues.TAG, "refreshCache")

        try {
            val refreshMethod: Method? = bluetoothGatt?.javaClass?.getMethod("refresh")
            val result = refreshMethod?.invoke(bluetoothGatt, *arrayOfNulls(0)) as? Boolean
            Log.d(ContentValues.TAG, "refreshDeviceCache $result")
        } catch (localException: Exception) {
            Log.e(ContentValues.TAG, "An exception occured while refreshing device")
        }
    }

    override fun getName(): String? {
        Log.v(ContentValues.TAG, "getName")
        return scanResult.device.name
    }


    override fun refreshBluetoothDevice(callback: RefreshBluetoothDeviceCallback) {
        refreshBluetoothDeviceCallback = callback
        Log.v(ContentValues.TAG, "refreshBluetoothDevice")
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        val filters = ArrayList<ScanFilter>()
        val filter = ScanFilter.Builder().setDeviceAddress(address).build()
        filters.add(filter)
        val refreshScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result.let {
                    if (result?.device?.address == address) {
                        scanner.stopScan(this)
                        scanResult = result
                        refreshBluetoothDeviceCallback?.success()
                    }
                }
            }
        }

        scanner.startScan(null, settings, refreshScanCallback)
    }

    override fun connect() {
        Log.v(ContentValues.TAG, "connect")
        bluetoothGatt = result.device.connectGatt(

            MainApplication.appContext, false, bluetoothGattCallback,
            BluetoothDevice.TRANSPORT_LE
        )
    }
}