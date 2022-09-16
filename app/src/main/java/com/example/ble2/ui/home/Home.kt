package com.example.ble2.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.ble2.R
import com.example.ble2.databinding.FragmentHomeBinding
import com.example.ble2.ui.adapter.DeviceAdapter
import com.example.ble2.ui.adapter.subnet.SubnetsFragment
import com.siliconlab.bluetoothmesh.adk.BluetoothMesh

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
            if (BluetoothMesh.getInstance().networks.first().subnets.size == 0) {
                Log.v("is empty", "wow such empty")
            } else {
                Log.v(
                    "not empty ale w sumie to chuj wi",
                    BluetoothMesh.getInstance().networks.first().subnets.toString()
                )
                Log.v(
                    "not empty",
                    BluetoothMesh.getInstance().networks.first().subnets.size.toString()
                )
            }
        }
        binding.subnetButton.setOnClickListener {
            replaceFragment()

        }
        binding.swipeToRefresh.setOnRefreshListener {
            binding.scanButton.performClick()
            binding.swipeToRefresh.isRefreshing = false
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

    private fun replaceFragment() {

        val manager = (view?.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, SubnetsFragment())
        fragmentTransaction.commit()
    }
}

