package com.example.ble2.ui.adapter.subnet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.example.ble2.AppState
import com.example.ble2.MainApplication
import com.example.ble2.databinding.FragmentSubnetsBinding
import com.example.ble2.ui.MainActivity
import com.example.ble2.ui.home.Home


class SubnetsFragment : Fragment() {
    private lateinit var binding: FragmentSubnetsBinding
    val adapter = SubnetAdapter()
    val viewModel = SubnetsViewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubnetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                MainActivity.instance.replaceFragment(Home())
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.subnetsList.adapter = adapter
        adapter.getViewModel(viewModel)
        submitNewList()

        observeViewModel()
        binding.testBtn.setOnClickListener {
            Log.v("subnets count", AppState.network.subnets.size.toString())

        }


        binding.addSubnet.setOnClickListener {
            Log.v("subnets", AppState.network.subnets.size.toString())
            openDialog()

        }
    }

    private fun observeViewModel() {
        viewModel.currentPostion.observe(viewLifecycleOwner) {
            MainApplication.selectedPosition = it
            adapter.notifyDataSetChanged()
        }

        viewModel.currentName.observe(viewLifecycleOwner) {
            Log.v("currentName", it.toString())
            if (it != "") {
                if (AppState.network.canCreateSubnet()) {
                    AppState.currentSubnet = AppState.network.createSubnet(it)
                    submitNewList()

                } else {
                    Toast.makeText(
                        requireContext(),
                        "can't create subnet",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }
        }
    }

    private fun submitNewList() {
        adapter.submitList(AppState.network.subnets.toList())
    }

    private fun openDialog() {
        Log.v("dialog", "nooo")
        val addSubnetDialog = AddSubnetDialog(viewModel)
        addSubnetDialog.show(parentFragmentManager, "tag")
    }
}