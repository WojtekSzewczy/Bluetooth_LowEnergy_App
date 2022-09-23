package com.example.ble2.ui2.mesh_device

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.ble2.AppState
import com.example.ble2.MainApplication
import com.example.ble2.ReadyState
import com.example.ble2.data.MeshDevice
import com.example.ble2.databinding.FragmentMeshDeviceBinding
import com.example.ble2.ui.adapter.subnet.SubnetsAdapterForMesh
import com.example.ble2.ui2.subnet.SubnetsViewModel
import com.siliconlab.bluetoothmesh.adk.BluetoothMesh
import com.siliconlab.bluetoothmesh.adk.ErrorType
import com.siliconlab.bluetoothmesh.adk.configuration_control.ConfigurationControl
import com.siliconlab.bluetoothmesh.adk.configuration_control.FactoryResetCallback
import com.siliconlab.bluetoothmesh.adk.connectable_device.ConnectableDevice
import com.siliconlab.bluetoothmesh.adk.connectable_device.RefreshBluetoothDeviceCallback
import com.siliconlab.bluetoothmesh.adk.connectable_device.RefreshGattServicesCallback
import com.siliconlab.bluetoothmesh.adk.data_model.node.Node
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet
import com.siliconlab.bluetoothmesh.adk.provisioning.ProvisionerConfiguration
import com.siliconlab.bluetoothmesh.adk.provisioning.ProvisionerConnection
import com.siliconlab.bluetoothmesh.adk.provisioning.ProvisioningCallback


class MeshDeviceFragment : Fragment() {

    lateinit var binding: FragmentMeshDeviceBinding
    private val args: MeshDeviceFragmentArgs by navArgs()
    lateinit var meshDevice: MeshDevice
    private var node: Node? = null
    val adapter = SubnetsAdapterForMesh()
    val viewModel = SubnetsViewModel()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        meshDevice = MeshDevice(args.scanResult)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.v("MeshDeviceFragment", "on create")
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.root.findNavController().navigateUp()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeshDeviceBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meshDevice.connect()
        adapter.submitList(AppState.network.subnets.toList())
        adapter.getViewModel(viewModel)
        binding.subnetsList.adapter = adapter

        observeViewModelState()


        binding.provisionButton.setOnClickListener {
            val provisionerConfiguration = ProvisionerConfiguration()
            provisionerConfiguration.apply {
                isEnablingNodeIdentity = true
                isEnablingProxy = true
                isKeepingProxyConnection = true
                //isUsingOneGattConnection = true
                //isGettingDeviceCompositionData = true
            }

            val provisionerConnection = ProvisionerConnection(meshDevice, AppState.currentSubnet!!)
            provisionerConnection.provision(
                provisionerConfiguration,
                null,
                object : ProvisioningCallback {
                    override fun success(
                        device: ConnectableDevice,
                        subnet: Subnet,
                        provisionedNode: Node
                    ) {
                        node = provisionedNode;
                        node?.name = meshDevice.address
                        Log.v("Provisioning", "succes")
                        binding.provisionButton.visibility = View.GONE
                        binding.unprovisonButton.visibility = View.VISIBLE
                        Log.v("elements", node?.elements?.first()?.address.toString())
                    }

                    override fun error(
                        connectableDevice: ConnectableDevice,
                        subnet: Subnet,
                        errorType: ErrorType
                    ) {
                        Log.v("Provisioning", "error")
                    }
                })
            Log.v("Provisioning", "callback")
            Log.v(
                "network",
                BluetoothMesh.getInstance().networks.first().subnets.first().nodes.size.toString()
            )

        }

        binding.unprovisonButton.setOnClickListener {
            if (node == null) {
                Log.v("unprovision btn", "is null")
            }
            val configurationControl = ConfigurationControl(node)
            configurationControl.factoryReset(object : FactoryResetCallback {
                override fun success(p0: Node?) {
                    p0?.removeOnlyFromLocalStructure()
                    meshDevice.refreshGattServices(object : RefreshGattServicesCallback {
                        override fun onSuccess() {
                            Log.v("refresh Gatt Services", "Succes")
                        }

                        override fun onFail() {
                            Log.v("refresh Gatt Services", "Succes")
                        }

                    })
                    meshDevice.refreshDeviceCache()
                    meshDevice.refreshBluetoothDevice(object : RefreshBluetoothDeviceCallback {
                        override fun success() {
                            Log.v("refresh", "succes")
                        }

                        override fun failure() {
                            Log.v("refresh", "failure")
                        }

                    })
                    Log.v("factory reset", "succes")
                    meshDevice.disconnect()
                    binding.unprovisonButton.visibility = View.GONE
                    binding.connectButton.visibility = View.VISIBLE
                }

                override fun error(node: Node?, errorType: ErrorType?) {
                    Log.v("factory reset", errorType!!.message)
                }
            })
        }
        binding.connectButton.setOnClickListener {
            meshDevice.connect()
            Thread.sleep(500)
            binding.provisionButton.visibility = View.VISIBLE
            binding.connectButton.visibility = View.GONE
        }
    }

    private fun observeViewModelState() {
        viewModel.currentPostion.observe(viewLifecycleOwner) {
            MainApplication.selectedPosition = it
            adapter.notifyDataSetChanged()
        }
        meshDevice.isReady.observe(viewLifecycleOwner) {
            when (it) {
                ReadyState.READY -> {
                    Thread.sleep(200)
                    binding.progressBar.visibility = View.GONE
                    binding.provisionButton.visibility = View.VISIBLE
                }
                ReadyState.NOT_READY -> {
                    binding.root.findNavController().navigateUp()
                }
                ReadyState.UNDEFINED -> {
                }
                else -> {}
            }
        }
    }
}