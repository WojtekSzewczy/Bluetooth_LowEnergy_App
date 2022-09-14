package com.example.ble2.ui.adapter.subnet

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.ble2.MainApplication
import com.example.ble2.databinding.SubnetManageLayoutBinding
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
