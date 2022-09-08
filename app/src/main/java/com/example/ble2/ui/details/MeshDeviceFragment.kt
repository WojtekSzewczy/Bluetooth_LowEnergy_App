package com.example.ble2.ui.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.ble2.MainApplication
import com.example.ble2.R
import com.example.ble2.Services
import com.example.ble2.data.MeshDevice
import com.example.ble2.data.ScannedDevice
import com.example.ble2.databinding.FragmentMeshDeviceBinding
import com.example.ble2.ui.home.Home
import com.siliconlab.bluetoothmesh.adk.BluetoothMesh
import com.siliconlab.bluetoothmesh.adk.ErrorType
import com.siliconlab.bluetoothmesh.adk.configuration_control.ConfigurationControl
import com.siliconlab.bluetoothmesh.adk.configuration_control.FactoryResetCallback
import com.siliconlab.bluetoothmesh.adk.connectable_device.ConnectableDevice
import com.siliconlab.bluetoothmesh.adk.connectable_device.RefreshBluetoothDeviceCallback
import com.siliconlab.bluetoothmesh.adk.connectable_device.RefreshGattServicesCallback
import com.siliconlab.bluetoothmesh.adk.data_model.node.Node
import com.siliconlab.bluetoothmesh.adk.data_model.subnet.Subnet
import com.siliconlab.bluetoothmesh.adk.node_control.NodeControl
import com.siliconlab.bluetoothmesh.adk.provisioning.NodeProperties
import com.siliconlab.bluetoothmesh.adk.provisioning.ProvisionerConfiguration
import com.siliconlab.bluetoothmesh.adk.provisioning.ProvisionerConnection
import com.siliconlab.bluetoothmesh.adk.provisioning.ProvisioningCallback


class MeshDeviceFragment(scannedDevice: ScannedDevice) : Fragment() {
    lateinit var binding: FragmentMeshDeviceBinding

    private val meshDevice = MeshDevice(scannedDevice.result)
    private var node: Node? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                replaceFragment()
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
        observeViewModelState()


        binding.provisionButton.setOnClickListener {
            val provisionerConfiguration = ProvisionerConfiguration()
            provisionerConfiguration.let {
                //it.isGettingDeviceCompositionData = true
                it.isEnablingNodeIdentity = true
                it.isEnablingProxy = true
                it.isKeepingProxyConnection = true

            }
            val nodeProperties = NodeProperties()
            nodeProperties.let {
                it.address = 5
                it.attentionTimer = 1
            }

            val provisionerConnection = ProvisionerConnection(meshDevice, MainApplication.subnet)
            provisionerConnection.provision(
                provisionerConfiguration,
                null,
                object : ProvisioningCallback {


                    override fun success(p0: ConnectableDevice?, p1: Subnet?, p2: Node?) {
                        node = p2;
                        node?.name = meshDevice.address
                        Log.v("Provisioning", "succes")
                        binding.provisionButton.visibility = View.GONE
                        binding.unprovisonButton.visibility = View.VISIBLE
                        Log.v("elements", node?.elements?.first()?.address.toString())
                    }

                    override fun error(p0: ConnectableDevice, p1: Subnet, p2: ErrorType) {
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
            val nodeControl = NodeControl(node)
            val currentSubnet = node!!.subnets.first()
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
                    binding.unprovisonButton.visibility = View.GONE
                    binding.connectButton.visibility = View.VISIBLE

                }

                override fun error(p0: Node?, p1: ErrorType?) {
                    Log.v("factory reset", p1!!.message)
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
        meshDevice.isReady.observe(viewLifecycleOwner) {
            when (it) {
                Services.ReadyState.READY -> {
                    Thread.sleep(200)
                    binding.progressBar.visibility = View.GONE
                    binding.provisionButton.visibility = View.VISIBLE
                }
                Services.ReadyState.NOT_READY -> {
                    replaceFragment()
                }
                Services.ReadyState.UNDEFINED -> {
                }
                else -> {}
            }
        }
    }

    private fun replaceFragment() {

        val manager = (view?.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, Home())
        fragmentTransaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Services.clearData()


    }
}