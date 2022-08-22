package com.example.ble2

object FavouritedDevices {
    val favourited = BleApplication.appContext.getSharedPreferences("Pref", 0)
    val editor = favourited.edit()

}