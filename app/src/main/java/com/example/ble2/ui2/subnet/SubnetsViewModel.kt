package com.example.ble2.ui2.subnet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ble2.AppState

class SubnetsViewModel {

    private val defaultPosition = -1
    private val _currentPostion = MutableLiveData(defaultPosition)
    val currentPostion: LiveData<Int> = _currentPostion

    private val _currentName = MutableLiveData("")
    val currentName: LiveData<String> = _currentName

    private val _rotate = MutableLiveData(false)
    val rotate: LiveData<Boolean> = _rotate

    fun setRotation(rotate: Boolean) {
        _rotate.value = rotate
    }

    fun setName(name: String) {
        Log.v("set Name", name)
        if (name != "") {
            if (AppState.network.canCreateSubnet()) {
                AppState.currentSubnet = AppState.network.createSubnet(name)
            } else {
                Log.v("SubnetsViewModel", "cant create subnet")
            }

        }
        _currentName.value = name
    }

    fun setPostion(postion: Int) {
        _currentPostion.value = postion
    }
}