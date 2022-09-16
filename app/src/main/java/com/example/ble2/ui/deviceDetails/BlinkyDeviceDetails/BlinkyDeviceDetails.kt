package com.example.ble2.ui.deviceDetails.BlinkyDeviceDetails

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
import com.example.ble2.ReadyState
import com.example.ble2.data.BlinkyDevice
import com.example.ble2.data.ScannedDevice
import com.example.ble2.databinding.FragmentDeviceDetailsBinding
import com.example.ble2.ui.home.Home

class BlinkyDeviceDetails(private val scannedDevice: ScannedDevice) : Fragment() {
    lateinit var binding: FragmentDeviceDetailsBinding
    private val viewModel: DetailsViewModel by viewModels()

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
        binding = FragmentDeviceDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.init(scannedDevice)
        observeViewModelState()
        observeBlinky()
        setupUI()
    }

    private fun observeViewModelState() {
        viewModel.isReady.observe(viewLifecycleOwner) {
            when (it) {
                ReadyState.READY -> {
                    Log.v("isReady", "is")
                    binding.progressBar.visibility = View.GONE
                    binding.uuids.text = viewModel.uuiDs
                    makeVisible()
                }
                ReadyState.NOT_READY -> {
                    Log.v("isReady", "is not")
                    replaceFragment()
                }
                ReadyState.UNDEFINED -> {
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
        viewModel.diodeState.observe(viewLifecycleOwner) {
            when (it) {
                BlinkyDevice.DiodeState.UNDEFINED -> {}
                BlinkyDevice.DiodeState.ON -> {
                    binding.DiodeControll.setImageResource(R.drawable.btn_state_clicked_foreground)
                }
                BlinkyDevice.DiodeState.OFF -> {
                    binding.DiodeControll.setImageResource(R.drawable.btn_state_unclicked_foreground)
                }
            }
        }
        viewModel.buttonState.observe(viewLifecycleOwner) {
            when (it) {
                BlinkyDevice.ButtonState.UNDEFINED -> {}
                BlinkyDevice.ButtonState.PRESSED -> {
                    binding.ButtonState.setImageResource(R.drawable.btn_state_clicked_foreground)
                }
                BlinkyDevice.ButtonState.RELEASED -> {
                    binding.ButtonState.setImageResource(R.drawable.btn_state_unclicked_foreground)

                }
            }
        }
    }

    private fun setupUI() {
        binding.deviceAddress.text = viewModel.address
        binding.DiodeControll.setOnClickListener {
            viewModel.toggleDiodeState()
        }
        binding.buttonServices.setOnClickListener {
            viewModel.switchButtonText()

        }
    }

    private fun makeVisible() {
        binding.DiodeControll.visibility = View.VISIBLE
        binding.deviceAddress.visibility = View.VISIBLE
        binding.ButtonState.visibility = View.VISIBLE
        binding.buttonServices.visibility = View.VISIBLE
    }

    private fun replaceFragment() {
        val manager = (view?.context as FragmentActivity).supportFragmentManager
        val fragmentTransaction: FragmentTransaction = manager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, Home())
        fragmentTransaction.commit()
    }
}


