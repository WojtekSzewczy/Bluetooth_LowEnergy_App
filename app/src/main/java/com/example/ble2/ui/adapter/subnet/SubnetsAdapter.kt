package com.example.ble2.ui.adapter.subnet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.ble2.databinding.SubnetLayoutBinding
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class SubnetsAdapter :
    androidx.recyclerview.widget.ListAdapter<Subnet, SubnetViewHolder>(SubnetCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubnetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SubnetLayoutBinding.inflate(inflater, parent, false)
        return SubnetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubnetViewHolder, position: Int) {
        val subnet = getItem(position)
        holder.bind(subnet)
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
