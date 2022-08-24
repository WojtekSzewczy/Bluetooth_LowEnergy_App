package com.example.ble2.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.ble2.data.MyScanResult
import com.example.ble2.databinding.CardLayoutBinding

class DeviceAdapter : ListAdapter<MyScanResult, DeviceViewHolder>(DeviceCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardLayoutBinding.inflate(inflater, parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = getItem(position)
        holder.bind(device)
    }
}

class DeviceCallback : DiffUtil.ItemCallback<MyScanResult>() {
    override fun areItemsTheSame(oldItem: MyScanResult, newItem: MyScanResult): Boolean {
        return oldItem.scanResult.device.address == newItem.scanResult.device.address
    }

    override fun areContentsTheSame(oldItem: MyScanResult, newItem: MyScanResult): Boolean {
        return oldItem == newItem
    }
}