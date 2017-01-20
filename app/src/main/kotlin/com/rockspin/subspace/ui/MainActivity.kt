package com.rockspin.subspace.ui

import android.Manifest
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.rockspin.subspace.R
import com.rockspin.subspace.databinding.ActivityMainBinding
import com.rockspin.subspace.network.SubApi
import com.rockspin.subspace.util.SubFileHelper
import rx.Observable
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val subApi = SubApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        fragmentManager.beginTransaction()
            .replace(R.id.settingsFragment, SettingsFragment())
            .commit()

        binding.subCheckButton.setOnClickListener {
            checkForSubtitles()
        }
    }

    private fun checkForSubtitles() {

        if (!permissionCheck()) {
            return
        }

        // TODO: Check for connectivity

        // 1. Open the target folder we're monitoring
        // 2. Find the first file there that's one of the allowed file types : avi, mkv, mp4, mov
        // 3. Make sure there isn't a corresponding .srt file in that folder
        // 4. Try and download a subtitle for that file
        // 5. Report success / error

        val rootFolderPathStr = PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string
            .key_folder_to_monitor), null)

        if (rootFolderPathStr == null) {
            Toast.makeText(this, "Path to root folder not set", Toast.LENGTH_SHORT).show()
            return
        }

        val subFileHelper = SubFileHelper(rootFolderPathStr)
        if (!subFileHelper.hasSubCandidates) {
            Toast.makeText(this, "No available files found", Toast.LENGTH_SHORT).show()
            return
        }

        val subCandidates = subFileHelper.subCandidates
        Observable.from(subCandidates)
            .subscribeOn(Schedulers.io())
            .flatMap { movieFile ->
                Single.zip(subApi.downloadSubtitleForMovieFile(movieFile), Single.just(movieFile), { subtitle, movieFile ->
                    Pair(subtitle, movieFile)
                }).toObservable().onErrorResumeNext { Observable.empty() }
            }
            .map { pair -> subFileHelper.createSubtitleForFile(pair.second, pair.first) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ file ->
                Toast.makeText(this, "Successfully downloaded $file", Toast.LENGTH_LONG).show()
            }, { throwable ->
                val error = "Error while downloading subtitle: ${throwable.message}"
                Log.e(MainActivity::class.java.simpleName, error)
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            })
    }

    private fun permissionCheck(): Boolean {
        val grantedValue = PackageManager.PERMISSION_GRANTED
        val permissionsGranted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == grantedValue
                && ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == grantedValue

        if (!permissionsGranted) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
                .WRITE_EXTERNAL_STORAGE), 1)
        }

        return permissionsGranted
    }
}
