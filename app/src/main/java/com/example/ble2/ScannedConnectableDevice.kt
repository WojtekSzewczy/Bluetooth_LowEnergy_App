package com.example.ble2

import com.siliconlab.bluetoothmesh.adk.connectable_device.*
import java.util.*

class ScannedConnectableDevice : ConnectableDevice() {
    override fun disconnect() {
        TODO("Not yet implemented")
    }

    override fun getMTU(): Int {
        TODO("Not yet implemented")
    }

    override fun hasService(p0: UUID?): Boolean {
        TODO("Not yet implemented")
    }

    override fun getServiceData(p0: UUID?): ByteArray {
        TODO("Not yet implemented")
    }

    override fun subscribe(p0: UUID?, p1: UUID?, p2: ConnectableDeviceSubscriptionCallback?) {
        TODO("Not yet implemented")
    }

    override fun writeData(
        p0: UUID?,
        p1: UUID?,
        p2: ByteArray?,
        p3: ConnectableDeviceWriteCallback?
    ) {
        TODO("Not yet implemented")
    }

    override fun refreshGattServices(p0: RefreshGattServicesCallback) {
        TODO("Not yet implemented")
    }

    override fun getName(): String {
        TODO("Not yet implemented")
    }

    override fun refreshBluetoothDevice(p0: RefreshBluetoothDeviceCallback) {
        TODO("Not yet implemented")
    }

    override fun connect() {
        TODO("Not yet implemented")
    }

    override fun getAdvertisementData(): ByteArray {
        TODO("Not yet implemented")
    }
}