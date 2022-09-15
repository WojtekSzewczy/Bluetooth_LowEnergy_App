package com.example.ble2.ui.adapter.subnet.selectedSubnet

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.MainApplication
import com.example.ble2.databinding.MeshDeviceInSubnetBinding
import com.siliconlab.bluetoothmesh.adk.ErrorType
import com.siliconlab.bluetoothmesh.adk.configuration_control.ConfigurationControl
import com.siliconlab.bluetoothmesh.adk.configuration_control.FactoryResetCallback
import com.siliconlab.bluetoothmesh.adk.data_model.node.Node
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class NodesViewHolder(
    val binding: MeshDeviceInSubnetBinding,
    val adapter: NodesAdapter,
    val subnet: Subnet
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(node: Node) {

        binding.removeSubnet.setOnClickListener {
            binding.progressBarRemoving.visibility = View.VISIBLE
            val configurationControl = ConfigurationControl(node)
            configurationControl.factoryReset(object : FactoryResetCallback {
                override fun success(p0: Node?) {
                    removeNode(p0)
                    Toast.makeText(
                        MainApplication.appContext,
                        "restart node succeded ",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                override fun error(p0: Node?, p1: ErrorType?) {
                    removeNode(p0)
                    Toast.makeText(
                        MainApplication.appContext,
                        "failed to restart node",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
        binding.nodeName.text = node.name.toString()
    }

    private fun removeNode(p0: Node?) {
        p0?.removeOnlyFromLocalStructure()
        adapter.submitList(subnet.nodes.toList())
        adapter.notifyDataSetChanged()
        binding.progressBarRemoving.visibility = View.GONE

    }


}