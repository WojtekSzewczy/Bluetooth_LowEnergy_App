package com.example.ble2

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.ble2.databinding.CardLayoutBinding


class RecyclerAdapter(
    private var initialResults: List<MyScanResult>?,
) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    lateinit var binding: CardLayoutBinding

    inner class ViewHolder(val binding: CardLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = CardLayoutBinding.inflate(layoutInflater, parent, false)


        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val device = initialResults?.get(position)
        val deviceDetails = DeviceDetails(device!!)
        val manager = (holder.itemView.context as FragmentActivity).supportFragmentManager

        holder.binding.deviceName.text = device?.scanResult?.device?.name
        holder.binding.deviceAddress.text = device?.scanResult?.device?.address
        holder.binding.cardView.setOnClickListener {

            Services.stopBleScan()
            replaceFragment(deviceDetails, manager)


        }
        device!!.isFavourited =
            FavouritedDevices.favourited.getString((device.scanResult.device.address), null) != null

        holder.binding.favUnclicked.setOnClickListener {
            device.isFavourited = !device.isFavourited
            Log.v("fav", "click")
            if (device.isFavourited) {
                FavouritedDevices.editor.putString(
                    device.scanResult.device.address,
                    device.scanResult.device.address
                )
                FavouritedDevices.editor.commit()
            } else {
                FavouritedDevices.editor.remove(device.scanResult.device.address)
                FavouritedDevices.editor.commit()
            }
            notifyItemChanged(position)
        }






        device.isAddedToFavourited =
            FavouritedDevices.favourited.getString((device.scanResult.device.address), null) != null

        if (device.scanResult.device.name == "Blinky Example") {
            holder.binding.cardView.setBackgroundColor(Color.parseColor("#fcba03"))
        } else {
            holder.binding.cardView.setBackgroundColor(Color.parseColor("#933B3B"))

        }



        if (device.imageViewVisibility) {
            device.imageViewVisibility = true


        } else {
            device.imageViewVisibility = false
        }

        if (!device.isAddedToFavourited) {
            holder.binding.favUnclicked.setImageResource(R.drawable.empty_heart)
        } else {
            holder.binding.favUnclicked.setImageResource(R.drawable.full_heart)
        }

    }


    private fun disconnectWithDevice(device: MyScanResult?) {
        var counter = 0
        device?.bluetoothGatt?.disconnect()
        while ((device?.isConnected!!)) {
            Thread.sleep(20)
            counter++
            if (counter == 100) {
                break
            }

        }
    }

    private fun toggleDiode(device: MyScanResult?) {
        val signalOn: ByteArray
        Log.v("toggle", device?.diodeReadValue.toString())
        if (device?.diodeReadValue == "[0]") {
            signalOn = byteArrayOf(0x01)
            device.diodeReadValue = "[1]"
        } else {
            device?.diodeReadValue = "[0]"
            signalOn = byteArrayOf(0x00)
        }
        device?.characteristic?.value = signalOn
        device?.bluetoothGatt?.writeCharacteristic(device.characteristic)
    }


    override fun getItemCount(): Int {

        return initialResults!!.size
    }

    fun readCharacteristic(device: MyScanResult?) {
        device?.bluetoothGatt?.readCharacteristic(device.characteristic)

    }

    fun setData(scanResult: List<MyScanResult>) {
        initialResults = scanResult
        notifyDataSetChanged()
    }

    fun setButtonState(buttonStateListIn: List<Boolean>) {
        Log.v("setbuttonState", "set")
        //buttonStateList = buttonStateListIn
        notifyDataSetChanged()

    }

    private fun replaceFragment(fragment: Fragment, fragmentManager1: FragmentManager) {

        val fragmentManager = fragmentManager1
        val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }

}