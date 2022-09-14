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


class SubnetsFragment : Fragment() {
    private lateinit var binding: FragmentSubnetsBinding
    val network = MainApplication.network
    var subnetsCount = MainApplication.subnetList.size
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
                replaceFragment()
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
            Log.v("subnets count", network.subnets.size.toString())

        }


        binding.addSubnet.setOnClickListener {
            Log.v("subnets", network.subnets.size.toString())
            openDialog()

        }

        /*   binding.removeSubnet.setOnClickListener {
               if (subnetsCount - 1 < 0 || viewModel.currentPostion.value == -1) {
                   Toast.makeText(requireContext(), "can't remove subnet", Toast.LENGTH_SHORT)
                       .show()
               } else {
                   MainApplication.subnetList[MainApplication.selectedPosition].nodes.forEach {
                       val configurationControl = ConfigurationControl(it)

                       configurationControl.factoryReset(object : FactoryResetCallback {
                           override fun success(p0: Node?) {
                               p0?.removeOnlyFromLocalStructure()
                               MainApplication.subnetList[MainApplication.selectedPosition].removeSubnet(object :
                                   SubnetRemovalCallback {

                                   override fun success(p0: Subnet?) {
                                       resetSubnet()
                                   }
                                   override fun error(
                                       p0: Subnet?,
                                       p1: SubnetRemovalResult?,
                                       p2: ErrorType?
                                   ) {

                                   }
                               })
                           }

                           override fun error(p0: Node?, p1: ErrorType?) {
                               p0?.removeOnlyFromLocalStructure()
                               MainApplication.subnetList[MainApplication.selectedPosition].removeSubnet(object :
                                   SubnetRemovalCallback {
                                   override fun success(p0: Subnet?) {
                                       resetSubnet()
                                   }

                                   override fun error(
                                       p0: Subnet?,
                                       p1: SubnetRemovalResult?,
                                       p2: ErrorType?
                                   ) {

                                   }
                               })
                           }

                       })
                   }
                   if (viewModel.currentPostion.value != -1) {
                       MainApplication.subnetList[MainApplication.selectedPosition].removeSubnet(object :
                           SubnetRemovalCallback {
                           override fun success(p0: Subnet?) {
                               resetSubnet()
                           }

                           override fun error(p0: Subnet?, p1: SubnetRemovalResult?, p2: ErrorType?) {
                           }
                       })
                   }


               }

               adapter.notifyDataSetChanged()
           }*/


    }

    private fun observeViewModel() {
        viewModel.currentPostion.observe(viewLifecycleOwner) {
            MainApplication.selectedPosition = it
            adapter.notifyDataSetChanged()
        }

        viewModel.currentName.observe(viewLifecycleOwner) {
            Log.v("currentName", it.toString())
            if (it != "") {
                if (network.canCreateSubnet()) {
                    MainApplication.subnet = network.createSubnet(it)
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
        Log.v("submitNewList", "submitNewList")
        MainApplication.subnetList = network.subnets.distinct()
        subnetsCount = MainApplication.subnetList.size
        adapter.submitList(MainApplication.subnetList)
    }

    private fun resetSubnet() {
        Log.v("resetSubnet", "resetSubnet")
        submitNewList()
        viewModel.setPostion(-1)
        MainApplication.subnet = null
    }

    private fun openDialog() {
        Log.v("dialog", "nooo")
        val addSubnetDialog = AddSubnetDialog(viewModel)
        addSubnetDialog.show(parentFragmentManager, "tag")


    }

    private fun replaceFragment() {

        val manager = (view?.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, Home())
        fragmentTransaction.commit()
    }


}