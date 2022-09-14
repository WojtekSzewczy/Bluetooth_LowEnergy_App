package com.example.ble2.ui.adapter.subnet.selectedSubnet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.ble2.R
import com.example.ble2.databinding.FragmentSubnetDetailsBinding
import com.example.ble2.ui.adapter.subnet.SubnetsFragment
import com.siliconlab.bluetoothmesh.adk.ErrorType
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.SubnetRemovalCallback
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.SubnetRemovalResult


class SubnetDetails(val subnet: Subnet) : Fragment() {

    private lateinit var binding: FragmentSubnetDetailsBinding
    private val adapter = NodesAdapter(subnet)

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
                replaceFragment()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.subnetName.text = subnet.name
        adapter.submitList(subnet.nodes.toList())
        binding.nodesList.adapter = adapter
        binding.removeSubnet.setOnClickListener {
            if (subnet.nodes.isEmpty()) {
                subnet.removeSubnet(object : SubnetRemovalCallback {
                    override fun success(p0: Subnet?) {
                        replaceFragment()
                    }

                    override fun error(p0: Subnet?, p1: SubnetRemovalResult?, p2: ErrorType?) {
                        Toast.makeText(requireContext(), "cant remove subnet", Toast.LENGTH_SHORT)
                            .show()
                    }

                })
            }
        }


    }

    private fun replaceFragment() {

        val manager = (view?.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, SubnetsFragment())
        fragmentTransaction.commit()
    }


}