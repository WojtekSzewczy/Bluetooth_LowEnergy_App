package com.example.ble2.ui.details

import androidx.lifecycle.ViewModel
import com.example.ble2.Services

class DetailsViewModel : ViewModel() {
    val buttonStateList = Services.buttonStateListLiveData
    val isReady = Services.isReady
}