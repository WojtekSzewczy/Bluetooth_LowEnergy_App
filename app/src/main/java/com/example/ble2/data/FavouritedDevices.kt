package com.example.ble2.data

import com.example.ble2.MainApplication

object FavouritedDevices {
    val favourited = MainApplication.appContext.getSharedPreferences("Pref", 0)
    private val editor = favourited.edit()

    fun removeFromFavourite(address: String) {
        editor.remove(address)
        editor.commit()
    }

    fun addToFavourite(address: String) {
        editor.putString(
            address,
            address
        )
        editor.commit()
    }

    fun isFavourite(address: String) =
        favourited.getString(address, null) != null
}