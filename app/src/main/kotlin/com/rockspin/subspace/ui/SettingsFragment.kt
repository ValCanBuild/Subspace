package com.rockspin.subspace.ui

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceFragment
import android.preference.PreferenceManager
import com.nononsenseapps.filepicker.FilePickerActivity
import com.rockspin.subspace.R


/**
 * Created by valentin.hinov on 19/01/2017.
 */

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    companion object {
        val DIR_PATH_CODE = 1
    }

    val sharedPreferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(activity)

    val folderToMonitorKey: String by lazy { getString(R.string.key_folder_to_monitor) }

    val folderToMonitor: String?
        get() = sharedPreferences.getString(folderToMonitorKey, null)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        addPreferencesFromResource(R.xml.preferences)

        val selectedFolder = folderToMonitor
        val folderPreference = findPreference(getString(R.string.key_folder_to_monitor))

        if (selectedFolder != null) {
            folderPreference.summary = selectedFolder
        }

        folderPreference.setOnPreferenceClickListener {
            openFilePicker()
            true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DIR_PATH_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data

            if (uri != null) {
                sharedPreferences.edit().putString(folderToMonitorKey, uri.toString()).apply()
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val pref = findPreference(key)

        // Same problem here
        if (key == folderToMonitorKey) {
            pref.summary = folderToMonitor
        }
    }

    private fun openFilePicker() {
        val intent = Intent(context, FilePickerActivity::class.java)

        // Set these depending on your use case. These are the defaults.
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
        intent.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true)
        intent.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_DIR)

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        intent.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().path)

        startActivityForResult(intent, DIR_PATH_CODE)
    }
}
