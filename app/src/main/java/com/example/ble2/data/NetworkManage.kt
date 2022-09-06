package com.example.ble2.data

import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

data class NetworkManage(val subnet: Subnet) {
    val name = subnet.name

}