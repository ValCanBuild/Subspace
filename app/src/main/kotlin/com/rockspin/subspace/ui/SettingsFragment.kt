package com.rockspin.subspace.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import android.widget.Toast
import com.rockspin.subspace.R

/**
 * Created by valentin.hinov on 19/01/2017.
 */

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    val folderToMonitorKey: String by lazy { getString(R.string.key_folder_to_monitor) }

    val folderToMonitor: String?
        get() = PreferenceManager.getDefaultSharedPreferences(activity).getString(folderToMonitorKey, null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        addPreferencesFromResource(R.xml.preferences)

        val selectedFolder = folderToMonitor
        val folderPreference = findPreference(getString(R.string.key_folder_to_monitor))

        if (selectedFolder != null) {
            folderPreference.summary = selectedFolder
        }

        folderPreference.setOnPreferenceClickListener {
            Toast.makeText(context, "Item clicked", Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val pref = findPreference(key)

        // Same problem here
        if (key == folderToMonitorKey) {
            pref.summary = folderToMonitor
        }
    }
}
