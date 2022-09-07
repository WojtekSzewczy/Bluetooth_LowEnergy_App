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
        val viewModel = SubnetsViewModel()
        adapter.getViewModel(viewModel)

        viewModel.currentPostion.observe(viewLifecycleOwner) {
            Log.v("click", it.toString())
            MainApplication.selectedPosition = it
            adapter.notifyDataSetChanged()
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
            if (network.canCreateSubnet()) {
                MainApplication.subnet =
                    network.createSubnet(binding.subnetName.text.toString())

                subnetsList = network.subnets.distinct()
                subnetsCount = subnetsList.size
                binding.subnetName.setText("")
                adapter.submitList(subnetsList)

            } else {
                Toast.makeText(requireContext(), "no powodzenia wariacie XDDDD", Toast.LENGTH_SHORT)
                    .show()
            }

        }

        binding.removeSubnet.setOnClickListener {


            if (subnetsCount - 1 < 0 || viewModel.currentPostion.value == -1) {
                Toast.makeText(requireContext(), "no powodzenia wariacie XDDDD", Toast.LENGTH_SHORT)
                    .show()
            } else {
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
                                    /*
                                        Toast.makeText(requireContext(),subnetsList[MainApplication.selectedPosition].name+ " failed to remove subnet",Toast.LENGTH_SHORT).show()
                                    */
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
                                    /*
                                        Toast.makeText(requireContext(),subnetsList[MainApplication.selectedPosition].name+ " failed to remove subnet",Toast.LENGTH_SHORT).show()
                                    */
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
                        }

                        override fun error(p0: Subnet?, p1: SubnetRemovalResult?, p2: ErrorType?) {
                            Log.v("removeSubnet", "error")
                            /*
                                    Toast.makeText(requireContext(),subnetsList[MainApplication.selectedPosition].name+ " failed to remove subnet",Toast.LENGTH_SHORT).show()
                                */
                        }
                    })
                }


            }

            /*Thread.slep(5000)

            if((subnetsCount-1)==viewModel.currentPostion.value){
                Log.v("ten if co nie pamietam po co go dałem", "tak to on")
                viewModel.setPostion(-1)
            }*/
        }


    }

    private fun replaceFragment() {

        val manager = (view?.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, Home())
        fragmentTransaction.commit()
    }


}