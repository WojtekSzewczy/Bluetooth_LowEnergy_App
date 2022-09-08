package com.example.ble2.ui.adapter.subnet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.ble2.AddSubnetDialog
import com.example.ble2.MainApplication
import com.example.ble2.R
import com.example.ble2.databinding.FragmentSubnetsBinding
import com.example.ble2.ui.home.Home
import com.siliconlab.bluetoothmesh.adk.BluetoothMesh
import com.siliconlab.bluetoothmesh.adk.ErrorType
import com.siliconlab.bluetoothmesh.adk.configuration_control.ConfigurationControl
import com.siliconlab.bluetoothmesh.adk.configuration_control.FactoryResetCallback
import com.siliconlab.bluetoothmesh.adk.data_model.node.Node
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.SubnetRemovalCallback
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.SubnetRemovalResult


class SubnetsFragment : Fragment() {


    private lateinit var binding: FragmentSubnetsBinding
    val viewModel = SubnetsViewModel()
    val adapter = SubnetsAdapter()

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
                replaceFragment()
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val network = BluetoothMesh.getInstance().getNetwork(MainApplication.network.uuid)
        var subnetsList = network.subnets.distinct()
        var subnetsCount = subnetsList.size

        adapter.getViewModel(viewModel)

        viewModel.currentPostion.observe(viewLifecycleOwner) {
            Log.v("click", it.toString())
            MainApplication.selectedPosition = it
            adapter.notifyDataSetChanged()
        }

        viewModel.currentName.observe(viewLifecycleOwner) {
            if (it != "") {
                if (network.canCreateSubnet()) {
                    MainApplication.subnet = network.createSubnet(it)
                    subnetsList = network.subnets.distinct()
                    subnetsCount = subnetsList.size
                    adapter.submitList(subnetsList)

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
        binding.subnetsList.adapter = adapter
        adapter.submitList(subnetsList)
        binding.testBtn.setOnClickListener {
            Log.v("subnets", network.subnets.size.toString())
            Log.v("subnets count", subnetsCount.toString())
            Log.v("nodes in current subnet count", MainApplication.subnet.nodes.size.toString())
            Log.v("selected postiotn", MainApplication.selectedPosition.toString())
        }


        binding.addSubnet.setOnClickListener {
            Log.v("subnets", network.subnets.size.toString())
            openDialog(viewModel)

        }

        binding.removeSubnet.setOnClickListener {


            if (subnetsCount - 1 < 0 || viewModel.currentPostion.value == -1) {
                Toast.makeText(requireContext(), "no powodzenia wariacie XDDDD", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Log.v("removesubnet", "else")
                subnetsList[MainApplication.selectedPosition].nodes.forEach {
                    Log.v("remove node ", "removin da node")
                    val configurationControl = ConfigurationControl(it)
                    configurationControl.factoryReset(object : FactoryResetCallback {
                        override fun success(p0: Node?) {
                            p0?.removeOnlyFromLocalStructure()
                            subnetsList[MainApplication.selectedPosition].removeSubnet(object :
                                SubnetRemovalCallback {
                                override fun success(p0: Subnet?) {
                                    subnetsList = network.subnets.distinct()
                                    subnetsCount = subnetsList.size
                                    adapter.submitList(subnetsList)
                                    viewModel.setPostion(-1)
                                }

                                override fun error(
                                    p0: Subnet?,
                                    p1: SubnetRemovalResult?,
                                    p2: ErrorType?
                                ) {
                                    Log.v("removeSubnet", "error")

                                }
                            })

                            Log.v("factory reset", "succes")
                        }

                        override fun error(p0: Node?, p1: ErrorType?) {
                            p0?.removeOnlyFromLocalStructure()
                            subnetsList[MainApplication.selectedPosition].removeSubnet(object :
                                SubnetRemovalCallback {
                                override fun success(p0: Subnet?) {
                                    subnetsList = network.subnets.distinct()
                                    subnetsCount = subnetsList.size
                                    adapter.submitList(subnetsList)
                                    viewModel.setPostion(-1)
                                }

                                override fun error(
                                    p0: Subnet?,
                                    p1: SubnetRemovalResult?,
                                    p2: ErrorType?
                                ) {
                                    Log.v("removeSubnet", "error")

                                }
                            })
                            Log.v("factory reset", "error")
                        }

                    })
                }
                if (viewModel.currentPostion.value != -1) {
                    subnetsList[MainApplication.selectedPosition].removeSubnet(object :
                        SubnetRemovalCallback {
                        override fun success(p0: Subnet?) {
                            subnetsList = network.subnets.distinct()
                            subnetsCount = subnetsList.size
                            adapter.submitList(subnetsList)
                            viewModel.setPostion(-1)

                        }

                        override fun error(p0: Subnet?, p1: SubnetRemovalResult?, p2: ErrorType?) {
                            Log.v("removeSubnet", "error")

                        }
                    })
                }


            }

            adapter.notifyDataSetChanged()
        }


    }

    private fun openDialog(viewModel: SubnetsViewModel) {
        val addSubnetDialog = AddSubnetDialog(viewModel)
        addSubnetDialog.show(parentFragmentManager, "tag")
        viewModel.setName(addSubnetDialog.subnetName)


    }

    private fun replaceFragment() {

        val manager = (view?.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, Home())
        fragmentTransaction.commit()
    }


}