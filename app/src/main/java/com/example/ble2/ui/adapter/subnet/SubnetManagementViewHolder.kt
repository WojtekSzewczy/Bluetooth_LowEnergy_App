package com.example.ble2.ui.adapter.subnet

import android.graphics.Color
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.MainApplication
import com.example.ble2.databinding.SubnetManageLayoutBinding
import com.example.ble2.ui.MainActivity
import com.example.ble2.ui.adapter.subnet.selectedSubnet.SubnetDetails
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class SubnetManagementViewHolder(val binding: SubnetManageLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val mainActivity = MainActivity.instance

    private lateinit var subnet: Subnet

    fun bind(subnetArgument: Subnet) {
        subnet = subnetArgument

        if (MainApplication.selectedPosition == adapterPosition) {
            Log.v("clicked", "white")
            binding.cardView.setBackgroundColor(Color.parseColor("#000000"))
        } else {
            Log.v("clicked", "black")
            binding.cardView.setBackgroundColor(Color.parseColor("#ffffff"))
        }
        var nodesAdresses = ""

        binding.subnetName.text = subnet.name
        if (!subnet.nodes.isEmpty()) {
            Log.v("SubnetManagementViewHolder", "true")
            Log.v("SubnetManagementViewHolder", subnet.nodes.first().toString())
            subnet.nodes.forEach {
                nodesAdresses += if (it.name == null) {
                    it.removeOnlyFromLocalStructure()
                    subnet.nodes.size.toString()
                } else {
                    it.name + "\n"
                }
            }
        } else {
            nodesAdresses = subnet.nodes.size.toString()
        }

        binding.nodesCount.text = nodesAdresses
        binding.cardView.setOnClickListener {
            mainActivity.replaceFragment(SubnetDetails(subnet))

        }
    }
}