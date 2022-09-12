package com.example.ble2.ui.adapter.subnet

import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.MainApplication
import com.example.ble2.databinding.SubnetLayoutBinding
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
        binding.removeSubnet.setOnClickListener {


        }
        binding.cardView.setOnClickListener {
            SubnetViewModel.setPostion(adapterPosition)
            MainApplication.subnet = subnet

        }
    }


    fun getViewModel(viewModel: SubnetsViewModel) {
        SubnetViewModel = viewModel

    }
}