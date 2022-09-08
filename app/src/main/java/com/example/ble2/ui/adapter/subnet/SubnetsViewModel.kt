package com.example.ble2.ui.adapter.subnet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SubnetsViewModel {

    private val _currentPostion = MutableLiveData(-1)
    val currentPostion: LiveData<Int> = _currentPostion

    private val _currentName = MutableLiveData("")
    val currentName: LiveData<String> = _currentName

    fun setName(name: String) {
        _currentName.value = name
    }

    fun setPostion(postion: Int) {
        Log.v("set position", postion.toString())
        _currentPostion.value = postion
    }
}