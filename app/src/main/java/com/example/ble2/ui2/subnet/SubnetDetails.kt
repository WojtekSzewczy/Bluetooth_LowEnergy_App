package com.example.ble2.ui2.subnet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ble2.AppState
import com.example.ble2.R
import com.example.ble2.databinding.FragmentSubnetDetailsBinding
import com.example.ble2.ui.adapter.subnet.selectedSubnet.NodesAdapter
import com.siliconlab.bluetoothmesh.adk.ErrorType
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.SubnetRemovalCallback
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.SubnetRemovalResult


class SubnetDetails : Fragment() {

    private lateinit var viewModel: SubnetDetailsViewModel
    private val args: SubnetDetailsArgs by navArgs()
    private lateinit var binding: FragmentSubnetDetailsBinding
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open_anim
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close_anim
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom_anim
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom_anim
        )
    }
    private var clicked = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubnetDetailsBinding.inflate(inflater, container, false)
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

        val subnetIndex = args.subnet
        val subnet = AppState.network.subnets.toList()[subnetIndex]
        viewModel = SubnetDetailsViewModel(subnet)
        observeViewModel()
        binding.floatingActionButtonMore.setOnClickListener {
            onMoreButtonClicked()
        }

        val adapter = NodesAdapter(subnet)
        binding.subnetName.text = subnet.name
        adapter.submitList(subnet.nodes.toList())
        binding.nodesList.adapter = adapter
        binding.floatingActionButtonSettings.setOnClickListener {
            openDialog()
        }
        binding.floatingActionButtonRemove.setOnClickListener {
            removeSubnet(subnet)
        }

    }

    private fun removeSubnet(subnet: Subnet) {
        if (subnet.nodes.isEmpty()) {
            subnet.removeSubnet(object : SubnetRemovalCallback {
                override fun success(p0: Subnet?) {
                    binding.root.findNavController().navigateUp()
                }

                override fun error(p0: Subnet?, p1: SubnetRemovalResult?, p2: ErrorType?) {
                    Toast.makeText(requireContext(), "cant remove subnet", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    private fun onMoreButtonClicked() {
        setVisibility(clicked)
        setAnimation(clicked)
        clicked = !clicked
    }

    private fun setAnimation(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonSettings.startAnimation(fromBottom)
            binding.floatingActionButtonRemove.startAnimation(fromBottom)
            binding.floatingActionButtonMore.startAnimation(rotateOpen)
        } else {
            binding.floatingActionButtonSettings.startAnimation(toBottom)
            binding.floatingActionButtonRemove.startAnimation(toBottom)
            binding.floatingActionButtonMore.startAnimation(rotateClose)
        }
    }

    private fun setVisibility(clicked: Boolean) {
        if (!clicked) {
            binding.floatingActionButtonRemove.visibility = View.VISIBLE
            binding.floatingActionButtonSettings.visibility = View.VISIBLE
        } else {
            binding.floatingActionButtonRemove.visibility = View.GONE
            binding.floatingActionButtonSettings.visibility = View.GONE
        }

    }

    private fun observeViewModel() {
        viewModel.currentName.observe(viewLifecycleOwner) {
            binding.subnetName.text = it
        }
    }

    private fun openDialog() {
        Log.v("dialog", "nooo")
        val addSubnetDialog = SettingsSubnetDialog(viewModel)
        addSubnetDialog.show(parentFragmentManager, "tag")
    }
}