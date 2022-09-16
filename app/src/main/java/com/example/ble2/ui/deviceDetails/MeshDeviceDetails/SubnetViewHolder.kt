package com.example.ble2.ui.deviceDetails.MeshDeviceDetails

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.AppState
import com.example.ble2.MainApplication
import com.example.ble2.databinding.SubnetLayoutBinding
import com.example.ble2.ui.adapter.subnet.SubnetsViewModel
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class SubnetViewHolder(val binding: SubnetLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

    lateinit var SubnetViewModel: SubnetsViewModel
    fun bind(subnet: Subnet) {
        var nodesAdresses = ""
        binding.subnetName.text = subnet.name
        if (!subnet.nodes.isEmpty()) {
            subnet.nodes.forEach {
                if (it.name == null) {
                    nodesAdresses += ""
                } else {
                    nodesAdresses += it.name + "\n"
                }
            }
        } else {
            nodesAdresses = ""
        }
        binding.nodesCount.text = nodesAdresses

        if (MainApplication.selectedPosition == adapterPosition) {
            binding.subnetName.setTextColor(Color.parseColor("#FFFFFF"))
        } else {
            binding.subnetName.setTextColor(Color.parseColor("#000000"))
        }
        binding.cardView.setOnClickListener {
            SubnetViewModel.setPostion(adapterPosition)
            AppState.currentSubnet = subnet
        }
    }

    fun getViewModel(viewModel: SubnetsViewModel) {
        SubnetViewModel = viewModel

    }
}