package com.example.ble2.ui.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.ble2.R
import com.example.ble2.Services
import com.example.ble2.data.MyScanResult
import com.example.ble2.data.ScannedDevice
import com.example.ble2.databinding.FragmentDeviceDetailsBinding
import com.example.ble2.ui.home.Home

class DeviceDetails(scannedDevice: ScannedDevice) : Fragment() {

    lateinit var binding: FragmentDeviceDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

    private val device: MyScanResult = MyScanResult(scannedDevice.result)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDeviceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("everyOtherTime", "hello world")

        Services.connect(device)
        observeViewModelState()
        setupUI()
    }

    private fun setupUI() {
        binding.deviceAddress.text = device.scanResult.device.address
        binding.DiodeControll.setOnClickListener {
            diodeControll()
        }
        binding.buttonBack.setOnClickListener {
            Services.disconnectWithDevice(device)
        }
        binding.buttonServices.setOnClickListener {

            viewModel.switchButtonText()

        }
    }

    private fun observeViewModelState() {
        viewModel.isReady.observe(viewLifecycleOwner) {
            when (it) {
                Services.ReadyState.READY -> {
                    Log.v("isReady", "is")
                    binding.progressBar.visibility = View.GONE
                    binding.uuids.text = getUUIDS()

                    makeVisible()
                }
                Services.ReadyState.NOT_READY -> {
                    Log.v("isReady", "is not")
                    Log.d("everyOtherTime", "will go back")
                    val home: Home = Home()
                    replaceFragment(home)
                }
                Services.ReadyState.UNDEFINED -> {
                    Log.v("is Ready", "undefined")
                }
                else -> {}
            }


        }
        viewModel.buttonStateList.observe(viewLifecycleOwner) {
            binding.ButtonState.setImageResource(if (!it) R.drawable.btn_state_clicked_foreground else R.drawable.btn_state_unclicked_foreground)

        }
        viewModel.areServicesVisible.observe(viewLifecycleOwner) { currenState ->
            binding.uuids.visibility = if (currenState) View.GONE else View.VISIBLE
            binding.buttonServices.text =
                if (currenState) getString(R.string.services) else getString(R.string.hide_services)
        }
    }

    private fun getUUIDS(): String {
        var servicesAndCharacteristics = ""
        device.bluetoothGatt?.services?.forEach {
            servicesAndCharacteristics += "UUID: "
            servicesAndCharacteristics += it.uuid.toString()
            servicesAndCharacteristics += "\n"
            it.characteristics.forEach {
                servicesAndCharacteristics += "   CHARACTERISTIC: "
                servicesAndCharacteristics += it.uuid.toString()
                servicesAndCharacteristics += "\n"
//                Log.v("string", servicesAndCharacteristics)
            }
            servicesAndCharacteristics += "\n"

        }
        return servicesAndCharacteristics
    }

    private fun makeVisible() {
        binding.DiodeControll.visibility = View.VISIBLE
        binding.deviceAddress.visibility = View.VISIBLE
        binding.ButtonState.visibility = View.VISIBLE
        binding.buttonBack.visibility = View.VISIBLE
        binding.buttonServices.visibility = View.VISIBLE
    }

    private fun replaceFragment(fragment: Fragment) {
        val manager = (view?.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

    fun diodeControll() {
        if (!device.diodeFlag) {
            Services.readCharacteristic(device)
            while (device.characteristic?.value.contentToString() == "null") {
                Thread.sleep(10)
            }
            device.diodeFlag = !device.diodeFlag
        }
        toggleDiode(device)
    }

    fun toggleDiode(device: MyScanResult?) {
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
        Services.writeDiode(device, signalOn)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Services.clearData()
    }

}


