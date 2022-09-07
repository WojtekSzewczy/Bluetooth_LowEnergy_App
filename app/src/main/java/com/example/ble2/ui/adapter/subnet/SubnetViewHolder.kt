package com.example.ble2.ui.adapter.subnet

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.MainApplication
import com.example.ble2.databinding.SubnetLayoutBinding
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet

class SubnetViewHolder(val binding: SubnetLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
    lateinit var SubnetViewModel: SubnetsViewModel
    fun bind(subnet: Subnet) {
        binding.subnetName.text = subnet.name
        binding.removeSubnet.setOnClickListener {


        }
        binding.cardView.setOnClickListener {
            SubnetViewModel.setPostion(adapterPosition)
            Log.v("asdasd", adapterPosition.toString())
            MainApplication.subnet = subnet

        }
    }

    fun getViewModel(viewModel: SubnetsViewModel) {
        SubnetViewModel = viewModel

    }
}