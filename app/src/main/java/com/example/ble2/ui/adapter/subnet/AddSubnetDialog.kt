package com.example.ble2.ui.adapter.subnet

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.ble2.databinding.LayoutDialogBinding

class AddSubnetDialog(val viewModel: SubnetsViewModel) : AppCompatDialogFragment() {
    private lateinit var bindig: LayoutDialogBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        bindig = LayoutDialogBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(requireActivity())


        builder.setView(bindig.root).setTitle("Select Subnet Name")
            .setPositiveButton("ok", DialogInterface.OnClickListener() { dialog, which ->
                viewModel.setName(bindig.subnetNameDialog.text.toString())
            })
        return builder.create()
    }


}