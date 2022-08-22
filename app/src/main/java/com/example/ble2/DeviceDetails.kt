package com.example.ble2

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.ble2.databinding.FragmentDeviceDetailsBinding
import com.example.ble2.ui.details.DetailsViewModel
import com.example.ble2.ui.home.Home

// TODO: Rename parameter arguments, choose names that match
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DeviceDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeviceDetails(val device: MyScanResult) : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentDeviceDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this).get(DetailsViewModel::class.java)
        Services.connect(device)
        // connectToDevice(device)

        viewModel.isReady.observe(viewLifecycleOwner) {
            when (it) {
                Services.ReadyState.READY -> {
                    Log.v("isReady", "is")
                    binding.progressBar.visibility = View.GONE
                    makeVisible()
                }
                Services.ReadyState.NOT_READY -> {
                    Log.v("isReady", "is not")
                    val home: Home = Home()
                    replaceFragment(home)
                }
                Services.ReadyState.UNDEFINED -> {
                    Log.v("is Ready", "undefined")
                }
                else -> {}
            }


        }



        binding.deviceAddress.text = device.scanResult.device.address
        binding.DiodeControll.setOnClickListener {
            diodeControll()
        }

        viewModel.buttonStateList.observe(viewLifecycleOwner) {
            if (!it) {
                binding.ButtonState.setImageResource(R.drawable.btn_state_clicked_foreground)
            } else {
                binding.ButtonState.setImageResource(R.drawable.btn_state_unclicked_foreground)
            }
        }
        binding.buttonBack.setOnClickListener {
            disconnectWithDevice(device)

            val home: Home = Home()
            replaceFragment(home)

        }

    }

    private fun makeVisible() {
        binding.DiodeControll.visibility = View.VISIBLE
        binding.deviceAddress.visibility = View.VISIBLE
        binding.ButtonState.visibility = View.VISIBLE
        binding.buttonBack.visibility = View.VISIBLE

    }

    private fun replaceFragment(fragment: Fragment) {
        val manager = (view?.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    fun diodeControll() {
        if (!device.diodeFlag) {
            readCharacteristic(device)
            while (device.characteristic?.value.contentToString() == "null") {
                Thread.sleep(10)
            }
            device.diodeFlag = !device.diodeFlag
        }
        toggleDiode(device)
    }

    fun readCharacteristic(device: MyScanResult?) { //jakoś przerzucic do serwisów
        device?.bluetoothGatt?.readCharacteristic(device.characteristic)

    }

    private fun toggleDiode(device: MyScanResult?) {
        val signalOn: ByteArray
        Log.v("toggle", device?.diodeReadValue.toString())
        if (device?.diodeReadValue == "[0]") {
            binding.DiodeControll.setImageResource(R.drawable.btn_state_clicked_foreground)
            signalOn = byteArrayOf(0x01)
            device.diodeReadValue = "[1]"
        } else {
            binding.DiodeControll.setImageResource(R.drawable.btn_state_unclicked_foreground)
            device?.diodeReadValue = "[0]"
            signalOn = byteArrayOf(0x00)
        }
        device?.characteristic?.value = signalOn
        device?.bluetoothGatt?.writeCharacteristic(device.characteristic)
    }

    private fun disconnectWithDevice(device: MyScanResult?) {
        var counter = 0
        Services.deinitIsReadyFlag()
        device?.bluetoothGatt?.disconnect()
        while ((device?.isConnected!!)) {
            Thread.sleep(20)
            counter++
            if (counter == 100) {
                break
            }

        }
    }

    private fun connectToDevice(device: MyScanResult?) {
        var counter = 0
        Services.deinitIsReadyFlag()
        device?.scanResult?.device?.connectGatt(binding.root.context, false, Services.mGattCallback)
        while (!device?.isConnected!!) {
            Thread.sleep(20)
            counter++
            if (counter == 100) {

                break
            }
        }
    }

}


