package com.example.ble2

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.ble2.ui.adapter.subnet.SubnetsAdapterForMesh
import com.example.ble2.ui.adapter.subnet.SubnetsViewModel
import com.siliconlab.bluetoothmesh.adk.BluetoothMesh
import com.siliconlab.bluetoothmesh.adk.configuration.BluetoothMeshConfiguration
import com.siliconlab.bluetoothmesh.adk.configuration.BluetoothMeshConfigurationLimits
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        val limits = BluetoothMeshConfigurationLimits()
        val configuration = BluetoothMeshConfiguration(emptyList(), limits)
        BluetoothMesh.initialize(this, configuration)
        Log.v("kk", BluetoothMesh.getInstance().networks.size.toString())
        if (BluetoothMesh.getInstance().networks.isEmpty()) {
            network = BluetoothMesh.getInstance().createNetwork("netłork")
        } else {
            network = BluetoothMesh.getInstance().networks.first()
        }
        Log.v("networkOnCreate", network.subnets.size.toString())
        subnetList = network.subnets.distinct()
        adapter.getViewModel(viewModel)
        adapter.submitList(subnetList)


    }

    companion object {
        lateinit var appContext: Context
        lateinit var network: com.siliconlab.bluetoothmesh.adk.data_model.network.Network
        var subnet: Subnet? = null
        var subnetList: List<Subnet> = emptyList()
        var selectedPosition = -1;
        val adapter = SubnetsAdapterForMesh()
        val viewModel = SubnetsViewModel()

    }


}