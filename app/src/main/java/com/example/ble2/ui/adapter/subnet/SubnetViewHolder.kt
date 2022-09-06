package com.example.ble2.ui.adapter.subnet

import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.databinding.SubnetLayoutBinding
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class SubnetViewHolder(val binding: SubnetLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(subnet: Subnet) {
        binding.subnetName.text = subnet.name
        binding.removeSubnet.setOnClickListener {

        }
    }
}