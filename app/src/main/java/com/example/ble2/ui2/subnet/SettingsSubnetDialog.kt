package com.example.ble2.ui2.subnet


import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.ble2.databinding.LayoutSettingsDialogBinding

class SettingsSubnetDialog(val viewModel: SubnetDetailsViewModel) : AppCompatDialogFragment() {
    private lateinit var binding: LayoutSettingsDialogBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = LayoutSettingsDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(requireActivity())
        binding.subnetNameDialog.setText(viewModel.currentName.value)
        builder.setView(binding.root).setTitle("Subnet Settings")
            .setPositiveButton("ok") { _, _ ->
                viewModel.setName(binding.subnetNameDialog.text.toString())
            }
        return builder.create()
    }


}