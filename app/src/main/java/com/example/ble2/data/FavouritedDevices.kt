package com.example.ble2.data

import com.example.ble2.MainApplication

object FavouritedDevices {
    val favourited = MainApplication.appContext.getSharedPreferences("Pref", 0)
    val editor = favourited.edit()
}