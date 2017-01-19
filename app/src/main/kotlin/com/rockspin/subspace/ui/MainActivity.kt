package com.rockspin.subspace.ui

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.rockspin.subspace.R
import com.rockspin.subspace.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        fragmentManager.beginTransaction()
            .replace(R.id.settingsFragment, SettingsFragment())
            .commit()
    }
}
