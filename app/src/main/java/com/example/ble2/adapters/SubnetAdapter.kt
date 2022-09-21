package com.example.ble2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ble2.databinding.SubnetManageLayoutBinding
import com.example.ble2.ui.adapter.subnet.SubnetCallback
import com.example.ble2.ui2.subnet.SubnetsViewModel
import com.example.ble2.view_holders.SubnetManagementViewHolder
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class SubnetAdapter :
    androidx.recyclerview.widget.ListAdapter<Subnet, SubnetManagementViewHolder>(SubnetCallback()) {
    lateinit var subnetViewModel: SubnetsViewModel
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubnetManagementViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SubnetManageLayoutBinding.inflate(inflater, parent, false)
        return SubnetManagementViewHolder(binding)
    }
    override fun onBindViewHolder(
        holder: SubnetManagementViewHolder,
        position: Int
    ) {
        val subnet = getItem(position)
        holder.bind(subnet)
    }
    fun getViewModel(viewModel: SubnetsViewModel) {
        subnetViewModel = viewModel
    }
}
