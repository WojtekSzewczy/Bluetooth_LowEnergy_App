package com.example.ble2.ui2.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ble2.MainActivity
import com.example.ble2.R
import com.example.ble2.Scanner
import com.example.ble2.databinding.FragmentHomeBinding
import com.example.ble2.ui.adapter.DeviceAdapter
import com.example.ble2.ui2.subnet.SubnetsFragment

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val adapter = DeviceAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModelState()
    }

    private fun setupUI() {
        binding.devicesList.adapter = adapter
        binding.scanButton.setOnClickListener {
            viewModel.switchScanning()
        }
        binding.subnetButton.setOnClickListener {
            Scanner.stopBleScan()
            Scanner.clearScanList()
            (activity as MainActivity?)!!.replaceFragment(SubnetsFragment())
        }
        binding.swipeToRefresh.setOnRefreshListener {
            binding.scanButton.performClick()
            binding.swipeToRefresh.isRefreshing = false
        }
    }

    private fun observeViewModelState() {
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

