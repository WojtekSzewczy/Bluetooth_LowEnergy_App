package com.example.ble2.ui.deviceDetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import com.example.ble2.R
import com.example.ble2.Services
import com.example.ble2.data.BlinkyDevice
import com.example.ble2.data.ScannedDevice
import com.example.ble2.databinding.FragmentDeviceDetailsBinding
import com.example.ble2.ui.home.Home

class BlinkyDeviceDetails(scannedDevice: ScannedDevice) : Fragment() {

    lateinit var binding: FragmentDeviceDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

    private val blinkyDevice: BlinkyDevice =
        BlinkyDevice(scannedDevice.result, scannedDevice.type!!)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                blinkyDevice.disconnect()
            }
        })
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
        blinkyDevice.connect()
        observeViewModelState()
        observeBlinky()
        setupUI()
    }

    private fun observeViewModelState() {
        blinkyDevice.isReady.observe(viewLifecycleOwner) {
            when (it) {
                Services.ReadyState.READY -> {
                    Log.v("isReady", "is")
                    binding.progressBar.visibility = View.GONE
                    binding.uuids.text = getUUIDS()
                    Thread.sleep(200)
                    blinkyDevice.readCharacteristic(blinkyDevice.DiodeCharacteristic!!)
                    Thread.sleep(200)
                    blinkyDevice.readCharacteristic(blinkyDevice.ButtonCharacteristic!!)
                    makeVisible()
                }
                Services.ReadyState.NOT_READY -> {
                    Log.v("isReady", "is not")
                    Log.d("everyOtherTime", "will go back")
                    replaceFragment()
                }
                Services.ReadyState.UNDEFINED -> {
                    Log.v("is Ready", "undefined")
                }
            }
        }
        viewModel.areServicesVisible.observe(viewLifecycleOwner) { currenState ->
            binding.uuids.visibility = if (currenState) View.GONE else View.VISIBLE
            binding.buttonServices.text =
                if (currenState) getString(R.string.services) else getString(R.string.hide_services)
        }
    }

    private fun observeBlinky() {
        blinkyDevice.diodeState.observe(viewLifecycleOwner) {
            when (it) {
                BlinkyDevice.DiodeState.UNDEFINED -> {}
                BlinkyDevice.DiodeState.ON -> {
                    binding.DiodeControll.setImageResource(R.drawable.btn_state_clicked_foreground)
                    blinkyDevice.turnDidodeOn()

                }
                BlinkyDevice.DiodeState.OFF -> {
                    binding.DiodeControll.setImageResource(R.drawable.btn_state_unclicked_foreground)
                    blinkyDevice.turnDiodeOff()

                }
            }
        }
        blinkyDevice.buttonState.observe(viewLifecycleOwner) {
            when (it) {
                BlinkyDevice.ButtonState.UNDEFINED -> {}
                BlinkyDevice.ButtonState.CLICKED -> {
                    binding.ButtonState.setImageResource(R.drawable.btn_state_clicked_foreground)
                }
                BlinkyDevice.ButtonState.UNCLICKED -> {
                    binding.ButtonState.setImageResource(R.drawable.btn_state_unclicked_foreground)

                }
            }
        }
    }


    private fun setupUI() {
        binding.deviceAddress.text = blinkyDevice.result.device.address
        binding.DiodeControll.setOnClickListener {
            blinkyDevice.togleDiodeState()
        }
        binding.buttonBack.setOnClickListener {
            blinkyDevice.disconnect()
        }
        binding.buttonServices.setOnClickListener {

            viewModel.switchButtonText()

        }
    }


    private fun getUUIDS(): String {
        var servicesAndCharacteristics = ""
        blinkyDevice.bluetoothGatt?.services?.forEach {
            servicesAndCharacteristics += "UUID: " + it.uuid.toString() + "\n"
            it.characteristics.forEach {
                servicesAndCharacteristics += "   CHARACTERISTIC: " + it.uuid.toString() + "\n"
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


