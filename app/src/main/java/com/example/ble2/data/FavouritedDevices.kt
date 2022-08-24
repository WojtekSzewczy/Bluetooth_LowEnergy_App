package com.example.ble2.data

import com.example.ble2.MainApplication

object FavouritedDevices {
    val favourited = MainApplication.appContext.getSharedPreferences("Pref", 0)
    private val editor = favourited.edit()

    fun removeFromFavourite(device: MyScanResult) {
        editor.remove(device.scanResult.device.address)
        editor.commit()
    }

    fun addToFavourite(device: MyScanResult) {
        editor.putString(
            device.scanResult.device.address,
            device.scanResult.device.address
        )
        editor.commit()
    }

    fun isFavourite(device: MyScanResult) =
        favourited.getString(device.scanResult.device.address, null) != null
}