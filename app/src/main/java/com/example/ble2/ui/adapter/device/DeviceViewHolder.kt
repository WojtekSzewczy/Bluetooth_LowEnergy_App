package com.example.ble2.ui.adapter.device

import android.graphics.Color
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.R
import com.example.ble2.Scanner
import com.example.ble2.data.FavouriteDevices
import com.example.ble2.data.ScannedDevice
import com.example.ble2.databinding.ScannedDeviceLayoutBinding
import com.example.ble2.ui.deviceDetails.BlinkyDeviceDetails.BlinkyDeviceDetails
import com.example.ble2.ui.deviceDetails.MeshDeviceDetails.MeshDeviceFragment

class DeviceViewHolder(val binding: ScannedDeviceLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(device: ScannedDevice) {
        binding.cardView.setOnClickListener {
            val manager = (itemView.context as FragmentActivity).supportFragmentManager
            Scanner.stopBleScan()
            if (device.type == ScannedDevice.deviceType.BLINKY_EXAMPLE) {
                replaceFragment(BlinkyDeviceDetails(device), manager)
            } else if (device.type == ScannedDevice.deviceType.MESH_DEVICE) {
                replaceFragment(MeshDeviceFragment(device), manager)
            } else {
                Toast.makeText(binding.root.context, "wrong device selected", Toast.LENGTH_SHORT)
                    .show()
            }

        }
        binding.favIcon.setOnClickListener {
            it as ImageView

            if (!FavouriteDevices.contains(device.address)) {
                FavouriteDevices.add(device.address)
                it.setImageResource(R.drawable.full_heart)
            } else {
                FavouriteDevices.remove(device.address)
                it.setImageResource(R.drawable.empty_heart)
            }
        }
        binding.deviceAddress.text = device.address
        binding.deviceName.text = device.name

        if (!FavouriteDevices.contains(device.address)) {
            binding.favIcon.setImageResource(R.drawable.empty_heart)
        } else {
            binding.favIcon.setImageResource(R.drawable.full_heart)
        }

        when (device.type) {
            ScannedDevice.deviceType.BLINKY_EXAMPLE -> binding.cardView.setBackgroundColor(
                Color.parseColor(
                    "#5a42f5"
                )
            )
            ScannedDevice.deviceType.MESH_DEVICE -> binding.cardView.setBackgroundColor(
                Color.parseColor(
                    "#fcba03"
                )
            )
            ScannedDevice.deviceType.OTHER -> binding.cardView.setBackgroundColor(Color.parseColor("#933B3B"))
            else -> {}
        }
    }

    private fun replaceFragment(fragment: Fragment, fragmentManager1: FragmentManager) {

        val fragmentManager = fragmentManager1
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}