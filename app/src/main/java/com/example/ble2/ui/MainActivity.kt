package com.example.ble2.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.ble2.R
import com.example.ble2.databinding.ActivityMainBinding
import com.example.ble2.ui.home.Home
import com.siliconlab.bluetoothmesh.adk.BluetoothMesh
import com.siliconlab.bluetoothmesh.adk.configuration.BluetoothMeshConfiguration

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BluetoothMesh.initialize(this, BluetoothMeshConfiguration())
        var mesh = BluetoothMesh.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(Home())
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.apply {
            replace(R.id.frame_layout, fragment)
            commit()
        }
    }


}
