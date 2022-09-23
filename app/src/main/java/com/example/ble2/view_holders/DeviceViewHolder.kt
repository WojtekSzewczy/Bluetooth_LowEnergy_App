package com.example.ble2.view_holders

import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.MainActivity
import com.example.ble2.R
import com.example.ble2.Scanner
import com.example.ble2.data.FavouriteDevices
import com.example.ble2.data.ScannedDevice
import com.example.ble2.databinding.ScannedDeviceLayoutBinding
import com.example.ble2.ui2.home.HomeFragmentDirections

class DeviceViewHolder(private val binding: ScannedDeviceLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(device: ScannedDevice) {
        onClick(device)
        addAndRemoveFromFavourite(device)
        setText(device)
        isDeviceFavourite(device)
        setColor(device)
    }

    private fun onClick(device: ScannedDevice) {
        if (device.type != ScannedDevice.DeviceType.OTHER) {
            binding.device.setOnClickListener {
                when (device.type) {
                    ScannedDevice.DeviceType.BLINKY_EXAMPLE -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToBlinkyDeviceDetails(device.result)
                        binding.root.findNavController().navigate(action)
                        clearHomeFragment()
                    }
                    ScannedDevice.DeviceType.MESH_DEVICE -> {
                        val action =
                            HomeFragmentDirections.actionHomeFragmentToMeshDeviceFragment(device.result)
                        binding.root.findNavController().navigate(action)
                        clearHomeFragment()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun clearHomeFragment() {
        Scanner.stopBleScan()
        Scanner.clearScanList()
    }

    private fun addAndRemoveFromFavourite(device: ScannedDevice) {
        binding.favIcon.setOnClickListener {
            it as ImageView

            if (!FavouriteDevices.contains(device.address)) {
                Log.v("DeviceViewHolder", "added")
                FavouriteDevices.add(device.address)
                it.setImageResource(R.drawable.other_full_heart_foreground)
            } else {
                Log.v("DeviceViewHolder", "removed")
                FavouriteDevices.remove(device.address)
                it.setImageResource(R.drawable.other_empty_heart_foreground)
            }
        }
    }

    private fun setText(device: ScannedDevice) {
        binding.deviceAddress.text = device.address
        binding.deviceName.text = device.name
    }

    private fun isDeviceFavourite(device: ScannedDevice) {
        if (!FavouriteDevices.contains(device.address)) {
            binding.favIcon.setImageResource(R.drawable.other_empty_heart_foreground)
        } else {
            binding.favIcon.setImageResource(R.drawable.other_full_heart_foreground)
        }
    }

    private fun setColor(device: ScannedDevice) {
        when (device.type) {
            ScannedDevice.DeviceType.BLINKY_EXAMPLE -> binding.device.setBackgroundColor(
                ContextCompat.getColor(MainActivity.instance, R.color.blinky_device_color)
            )

            ScannedDevice.DeviceType.MESH_DEVICE -> binding.device.setBackgroundColor(
                ContextCompat.getColor(
                    MainActivity.instance,
                    R.color.mesh_device_color
                )
            )

            ScannedDevice.DeviceType.OTHER -> binding.device.setBackgroundColor(
                ContextCompat.getColor(
                    MainActivity.instance,
                    R.color.other_device_color
                )
            )
            else -> {}
        }
    }
}