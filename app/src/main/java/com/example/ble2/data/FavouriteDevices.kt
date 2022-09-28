package com.example.ble2.data

import android.content.Context.MODE_PRIVATE
import com.example.ble2.MainApplication

object FavouriteDevices {

    private const val name = "favourite"
    private val favourite = MainApplication.appContext.getSharedPreferences(name, MODE_PRIVATE)
    private val editor = favourite.edit()

    fun contains(address: String) =
        favourite.getString(address, null) != null

    fun add(address: String) {
        editor.putString(
            address,
            address
        )
        editor.commit()
    }

    fun remove(address: String) {
        editor.remove(address)
        editor.commit()
    }
}