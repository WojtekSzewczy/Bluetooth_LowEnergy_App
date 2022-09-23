package com.example.ble2.ui2.subnet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class SubnetDetailsViewModel(private val subnet: Subnet) {

    private val _currentName = MutableLiveData(subnet.name)
    val currentName: LiveData<String> = _currentName


    fun setName(name: String) {
        subnet.name = name
        _currentName.value = name
    }
}