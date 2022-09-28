package com.example.ble2.ui2.subnet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ble2.AppState
import com.example.ble2.MainApplication
import com.example.ble2.R
import com.example.ble2.adapters.SubnetAdapter
import com.example.ble2.databinding.FragmentSubnetsBinding


class SubnetsFragment : Fragment() {
    private lateinit var binding: FragmentSubnetsBinding
    val adapter = SubnetAdapter()
    val viewModel = SubnetsViewModel()
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_45
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_45
        )
    }

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
                binding.root.findNavController().navigateUp()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.subnetsList.adapter = adapter
        adapter.getViewModel(viewModel)
        submitNewList()
        observeViewModel()
        binding.addSubnetFab.setOnClickListener {
            setAnimation()
            openDialog()
        }
    }

    private fun setAnimation() {
        binding.addSubnetFab.startAnimation(rotateOpen)
    }

    private fun observeViewModel() {
        viewModel.currentPostion.observe(viewLifecycleOwner) {
            MainApplication.selectedPosition = it
            adapter.notifyDataSetChanged()
        }

        viewModel.currentName.observe(viewLifecycleOwner) {
            Log.v("currentName", it.toString())
            if (it != "") {
                binding.addSubnetFab.startAnimation(rotateClose)
                submitNewList()
            } else {
                binding.addSubnetFab.startAnimation(rotateClose)
            }
        }
        viewModel.rotate.observe(viewLifecycleOwner) {
            if (it) {
                binding.addSubnetFab.startAnimation(rotateClose)
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