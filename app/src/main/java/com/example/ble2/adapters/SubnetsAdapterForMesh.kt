package com.example.ble2.ui.adapter.subnet

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.ble2.MainApplication
import com.example.ble2.databinding.SubnetLayoutBinding
import com.example.ble2.ui2.subnet.SubnetsViewModel
import com.example.ble2.view_holders.SubnetViewHolderForMeshDevice
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class SubnetsAdapterForMesh :
    androidx.recyclerview.widget.ListAdapter<Subnet, SubnetViewHolderForMeshDevice>(SubnetCallback()) {
    lateinit var subnetViewModel: SubnetsViewModel
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubnetViewHolderForMeshDevice {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SubnetLayoutBinding.inflate(inflater, parent, false)
        return SubnetViewHolderForMeshDevice(binding)
    }

    override fun onBindViewHolder(
        holder: SubnetViewHolderForMeshDevice,
        position: Int
    ) {

        val subnet = getItem(position)
        holder.bind(subnet)
        holder.getViewModel(subnetViewModel)
        if (MainApplication.selectedPosition == position) {
            Log.v("clicked", "white")
            holder.itemView.setBackgroundColor(Color.parseColor("#000000"))
        } else {
            Log.v("clicked", "black")
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"))
        }
    }

    fun getViewModel(viewModel: SubnetsViewModel) {
        subnetViewModel = viewModel

    }
}

class SubnetCallback : DiffUtil.ItemCallback<Subnet>() {


    override fun areItemsTheSame(oldItem: Subnet, newItem: Subnet): Boolean {
        return oldItem.netKey == newItem.netKey
    }

    override fun areContentsTheSame(oldItem: Subnet, newItem: Subnet): Boolean {
        return oldItem.name == newItem.name
    }
}
