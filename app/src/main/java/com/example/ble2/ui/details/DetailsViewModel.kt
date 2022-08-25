package com.example.ble2.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ble2.Services

class DetailsViewModel : ViewModel() {

    //val buttonStateList = Services.buttonStateListLiveData
    val isReady = Services.isReady

    private val _areServicesVisible = MutableLiveData(true)
    val areServicesVisible: LiveData<Boolean> = _areServicesVisible

    fun switchButtonText() {
        val currentState = _areServicesVisible.value!!
        _areServicesVisible.postValue(!currentState)
    }
}