package com.example.ble2.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.ble2.DeviceAdapter
import com.example.ble2.R
import com.example.ble2.databinding.FragmentHomeBinding

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
        val adapter2 = DeviceAdapter()
        adapter2.submitList(viewModel.results.value)
        setupUI(adapter2)
        observeViewModelState(adapter2)
    }

    private fun setupUI(adapter: DeviceAdapter) {
        binding.devicesList.adapter = adapter
        binding.scanButton.setOnClickListener {
            viewModel.switchScanning()
        }
    }

    private fun observeViewModelState(adapter: DeviceAdapter) {
        viewModel.results.observe(viewLifecycleOwner) { scanResults ->
            adapter.submitList(scanResults)
            Log.v("itemCont", adapter.itemCount.toString())
            adapter.notifyDataSetChanged()
        }
        viewModel.isScanning.observe(viewLifecycleOwner) { currentState ->
            binding.scanButton.text =
                if (currentState) getString(R.string.stop) else getString(R.string.scan)
        }
    }
}