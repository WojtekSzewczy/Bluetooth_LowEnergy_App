package com.example.ble2.ui.adapter.subnet

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.R
import com.example.ble2.databinding.SubnetManageLayoutBinding
import com.example.ble2.ui.adapter.subnet.selectedSubnet.SubnetDetails
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class SubnetManagementViewHolder(val binding: SubnetManageLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private lateinit var subnet: Subnet

    fun bind(subnetArgument: Subnet) {
        subnet = subnetArgument
        var nodesAdresses = ""

        binding.subnetName.text = subnet.name
        if (!subnet.nodes.isEmpty()) {
            Log.v("are ndoes empty", "true")
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
            val manager = (itemView.context as FragmentActivity).supportFragmentManager
            replaceFragment(SubnetDetails(subnet), manager)
        }
    }

    private fun replaceFragment(fragment: Fragment, fragmentManager1: FragmentManager) {
        val fragmentManager = fragmentManager1
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()

    }

}