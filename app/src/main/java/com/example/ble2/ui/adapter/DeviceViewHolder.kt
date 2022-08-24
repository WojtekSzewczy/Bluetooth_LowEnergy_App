package com.example.ble2.ui.adapter

import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.R
import com.example.ble2.Services
import com.example.ble2.data.FavouritedDevices
import com.example.ble2.data.MyScanResult
import com.example.ble2.databinding.CardLayoutBinding
import com.example.ble2.ui.details.DeviceDetails

class DeviceViewHolder(val binding: CardLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(result: MyScanResult) {
        val device = result.scanResult.device

        binding.cardView.setOnClickListener {
            val manager = (itemView.context as FragmentActivity).supportFragmentManager
            Services.stopBleScan()
            replaceFragment(DeviceDetails(result), manager)
            Log.d("everyOtherTime", "should replace fragment")
        }
        binding.favIcon.setOnClickListener {
            it as ImageView

            if (!FavouritedDevices.isFavourite(result)) {
                FavouritedDevices.addToFavourite(result)
                it.setImageResource(R.drawable.full_heart)
            } else {
                FavouritedDevices.removeFromFavourite(result)
                it.setImageResource(R.drawable.empty_heart)
            }
        }
        binding.deviceAddress.text = device.address
        binding.deviceName.text = device.name

        if (!FavouritedDevices.isFavourite(result)) {
            binding.favIcon.setImageResource(R.drawable.empty_heart)
        } else {
            binding.favIcon.setImageResource(R.drawable.full_heart)
        }

        if (device.name == "Blinky Example") {
            binding.cardView.setBackgroundColor(Color.parseColor("#fcba03"))
        } else {
            binding.cardView.setBackgroundColor(Color.parseColor("#933B3B"))

        }
    }

    private fun replaceFragment(fragment: Fragment, fragmentManager1: FragmentManager) {

        val fragmentManager = fragmentManager1
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}