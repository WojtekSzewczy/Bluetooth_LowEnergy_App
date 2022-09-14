package com.example.ble2.ui.adapter.subnet.selectedSubnet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.example.ble2.databinding.MeshDeviceInSubnetBinding
import com.siliconlab.bluetoothmesh.adk.data_model.node.Node
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class NodesAdapter(val subnet: Subnet) :
    androidx.recyclerview.widget.ListAdapter<Node, NodesViewHolder>(NodeCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NodesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MeshDeviceInSubnetBinding.inflate(inflater, parent, false)
        return NodesViewHolder(binding, this, subnet)
    }

    override fun onBindViewHolder(holder: NodesViewHolder, position: Int) {
        val node = getItem(position)
        holder.bind(node)

    }
}

class NodeCallback : DiffUtil.ItemCallback<Node>() {


    override fun areItemsTheSame(oldItem: Node, newItem: Node): Boolean {
        return oldItem.devKey == newItem.devKey
    }

    override fun areContentsTheSame(oldItem: Node, newItem: Node): Boolean {
        return oldItem.name == newItem.name
    }
}