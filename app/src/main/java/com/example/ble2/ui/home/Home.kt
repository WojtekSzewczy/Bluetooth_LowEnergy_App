package com.example.ble2.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ble2.MainApplication
import com.example.ble2.R
import com.example.ble2.databinding.FragmentHomeBinding
import com.example.ble2.ui.adapter.DeviceAdapter
import com.siliconlab.bluetoothmesh.adk.BluetoothMesh
import com.siliconlab.bluetoothmesh.adk.ErrorType
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.SubnetRemovalCallback
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.SubnetRemovalResult

class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = DeviceAdapter()
        setupUI(adapter)
        observeViewModelState(adapter)
    }

    private fun setupUI(adapter: DeviceAdapter) {
        binding.devicesList.adapter = adapter
        binding.scanButton.setOnClickListener {
            viewModel.switchScanning()
        }
        binding.Test.setOnClickListener {


            (if (BluetoothMesh.getInstance().networks.first().subnets.size == 0) {
                Log.v("is empty", "wow such empty")
            } else {
                Log.v(
                    "not empty ale w sumie to chuj wi",
                    BluetoothMesh.getInstance().networks.first().subnets.toString()
                )
                Log.v(
                    "not empty",
                    BluetoothMesh.getInstance().networks.first().subnets.first().nodes.size.toString()
                )
            }
                    )
        }
        binding.sruton.setOnClickListener {
            Log.v("node size", MainApplication.subnet.nodes.size.toString())
            MainApplication.subnet.removeSubnet(object : SubnetRemovalCallback {
                override fun success(p0: Subnet?) {
                    Log.v("yeeet", "yeeeet")
                }

                override fun error(p0: Subnet?, p1: SubnetRemovalResult?, p2: ErrorType?) {
                    Log.v("ełoł", p1.toString())

                }

            })
        }
    }

    private fun observeViewModelState(adapter: DeviceAdapter) {
        viewModel.devices.observe(viewLifecycleOwner) { devices ->
            adapter.apply {
                submitList(devices)
            }
        }
        viewModel.isScanning.observe(viewLifecycleOwner) { currentState ->
            binding.scanButton.text =
                if (currentState) getString(R.string.stop) else getString(R.string.scan)
        }
    }
}
