package com.example.ble2.view_holders

import android.util.Log
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.AppState
import com.example.ble2.databinding.SubnetManageLayoutBinding
import com.example.ble2.ui2.subnet.SubnetsFragmentDirections
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class SubnetManagementViewHolder(val binding: SubnetManageLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private lateinit var subnet: Subnet

    fun bind(subnetArgument: Subnet) {
        subnet = subnetArgument

        var nodesAdresses = ""

        binding.subnetName.text = subnet.name
        nodesAdresses = if (!subnet.nodes.isEmpty()) {
            Log.v("SubnetManagementViewHolder", "true")
            Log.v("SubnetManagementViewHolder", subnet.nodes.first().toString())
            getAddresses(subnet)
        } else {
            subnet.nodes.size.toString()
        }
        binding.nodesCount.text = nodesAdresses
        binding.cardView.setOnClickListener {
            val subnetIndex = AppState.network.subnets.toList().indexOf(subnet)
            val action =
                SubnetsFragmentDirections.actionSubnetsFragmentToSubnetDetails3(subnetIndex)
            binding.root.findNavController().navigate(action)
        }
    }

    private fun getAddresses(subnet: Subnet): String = buildString {
        subnet.nodes.forEach {
            append(
                if (it.name == null) {
                    it.removeOnlyFromLocalStructure()
                    subnet.nodes.size.toString()
                } else {
                    it.name + "\n"
                }
            )
        }
    }
}