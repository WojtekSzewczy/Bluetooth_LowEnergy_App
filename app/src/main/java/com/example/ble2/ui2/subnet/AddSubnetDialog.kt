package com.example.ble2.ui2.subnet

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.ble2.databinding.LayoutDialogBinding

class AddSubnetDialog(val viewModel: SubnetsViewModel) : AppCompatDialogFragment() {
    private lateinit var binding: LayoutDialogBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = LayoutDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(requireActivity())


        builder.setView(binding.root).setTitle("Select Subnet Name")
            .setPositiveButton("ok") { _, _ ->
                viewModel.setName(binding.subnetNameDialog.text.toString())
            }
        return builder.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setRotation(true)
    }


}