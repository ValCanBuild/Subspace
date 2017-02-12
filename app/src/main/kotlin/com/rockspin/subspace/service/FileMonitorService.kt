package com.rockspin.subspace.service

import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.preference.PreferenceManager
import com.rockspin.subspace.R
import com.rockspin.subspace.util.RxFileObserver
import rx.Subscription

/**
 * Created by valentin.hinov on 27/01/2017.
 */
class FileMonitorService: Service(), SharedPreferences.OnSharedPreferenceChangeListener {

    var rxFileObserver: RxFileObserver? = null

    var subscription: Subscription? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val rootFolderPathStr = preferences.getString(getString(
            R.string.key_folder_to_monitor), null)

        if (rootFolderPathStr != null) {
            rxFileObserver = RxFileObserver(rootFolderPathStr)
        }

        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (rxFileObserver != null) {
            startMonitoring()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this)
        subscription?.unsubscribe()
        subscription = null
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String?) {
        val folderKey = getString(R.string.key_folder_to_monitor)
        if (key == folderKey) {
            val newFolderToMonitorPath = sharedPreferences.getString(folderKey, null)
            rxFileObserver = RxFileObserver(newFolderToMonitorPath)
            startMonitoring()
        }
    }

    private fun startMonitoring() {
        subscription = rxFileObserver?.monitorEvents()
            ?.subscribe { fileEvent ->

            }
    }
}