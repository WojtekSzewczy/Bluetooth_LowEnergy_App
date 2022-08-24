package com.example.ble2

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.databinding.CardLayoutBinding
import com.example.ble2.ui.details.DeviceDetails

class DeviceAdapter : ListAdapter<MyScanResult, DeviceAdapter.DeviceViewHolder>(DeviceCallback()) {

    inner class DeviceViewHolder(val binding: CardLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CardLayoutBinding.inflate(inflater, parent, false)
        return DeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {

        val manager = (holder.itemView.context as FragmentActivity).supportFragmentManager
        val deviceDetails = DeviceDetails(getItem(position))
        holder.binding.cardView.setOnClickListener {
            Services.stopBleScan()
            replaceFragment(deviceDetails, manager)
            Log.d("everyOtherTime", "should replace fragment")
        }
        setupUI(holder, position, manager)
        manageFavourites(holder, position)

    }

    private fun setupUI(holder: DeviceViewHolder, position: Int, manager: FragmentManager) {

        val device = getItem(position).scanResult.device

        holder.binding.deviceAddress.text = device.address
        holder.binding.deviceName.text = device.name

        if (device.name == "Blinky Example") {
            holder.binding.cardView.setBackgroundColor(Color.parseColor("#fcba03"))
        } else {
            holder.binding.cardView.setBackgroundColor(Color.parseColor("#933B3B"))

        }
    }

    private fun manageFavourites(
        holder: DeviceViewHolder,
        position: Int
    ) {
        val device = getItem(position)
        device.isFavourited =
            FavouritedDevices.favourited.getString((device.scanResult.device.address), null) != null
        holder.binding.favIcon.setOnClickListener {
            device.isFavourited = !device.isFavourited
            if (device.isFavourited) {
                addToFavourite(device)
            } else {
                removeFromFavourite(device)
            }
            notifyItemChanged(position)
        }
        device.isAddedToFavourited =
            FavouritedDevices.favourited.getString((device.scanResult.device.address), null) != null

        if (!device.isAddedToFavourited) {
            holder.binding.favIcon.setImageResource(R.drawable.empty_heart)
        } else {
            holder.binding.favIcon.setImageResource(R.drawable.full_heart)
        }
    }

    private fun removeFromFavourite(device: MyScanResult) {
        FavouritedDevices.editor.remove(device.scanResult.device.address)
        FavouritedDevices.editor.commit()
    }

    private fun addToFavourite(device: MyScanResult) {
        FavouritedDevices.editor.putString(
            device.scanResult.device.address,
            device.scanResult.device.address
        )
        FavouritedDevices.editor.commit()
    }
}

private fun replaceFragment(fragment: Fragment, fragmentManager1: FragmentManager) {

    val fragmentManager = fragmentManager1
    val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.frame_layout, fragment)
    fragmentTransaction.commit()
}

class DeviceCallback : DiffUtil.ItemCallback<MyScanResult>() {
    override fun areItemsTheSame(oldItem: MyScanResult, newItem: MyScanResult): Boolean {
        return oldItem.scanResult.device.address == newItem.scanResult.device.address
    }

    override fun areContentsTheSame(oldItem: MyScanResult, newItem: MyScanResult): Boolean {
        return oldItem == newItem
    }
}