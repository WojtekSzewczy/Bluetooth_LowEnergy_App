package com.example.ble2.ui2.subnet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ble2.AppState
import com.example.ble2.databinding.FragmentSubnetDetailsBinding
import com.example.ble2.ui.adapter.subnet.selectedSubnet.NodesAdapter
import com.siliconlab.bluetoothmesh.adk.ErrorType
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.SubnetRemovalCallback
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.SubnetRemovalResult


class SubnetDetails : Fragment() {

    private val args: SubnetDetailsArgs by navArgs()
    private lateinit var binding: FragmentSubnetDetailsBinding

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
        val adapter = NodesAdapter(subnet)
        binding.subnetName.text = subnet.name
        adapter.submitList(subnet.nodes.toList())
        binding.nodesList.adapter = adapter
        binding.removeSubnet.setOnClickListener {
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
    }
}