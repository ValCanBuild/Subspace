package com.rockspin.subspace.network

import com.rockspin.subspace.util.hexValue
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.Single
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.security.MessageDigest

class SubApi {

    private val subInterface: SubtitleWebInterface by lazy {
        Retrofit.Builder().apply {
            baseUrl("http://api.thesubdb.com")
            addCallAdapterFactory(RxJavaCallAdapterFactory.create())

        }.build().create(SubtitleWebInterface::class.java)
    }

    fun downloadSubtitleForMovieFile(file: File): Single<String> {
        return Single.defer {
            Single.just(calculateHash(file))
                .flatMap { subInterface.downloadSubtitleForHash(it).toSingle() }
                .map { responseBody ->
                    val readerIn = BufferedReader(InputStreamReader(responseBody.byteStream()))
                    val sb = StringBuilder()

                    readerIn.lines().forEach {
                        sb.append(it)
                        sb.appendln()
                    }

                    readerIn.close()

                    sb.toString()
                }
        }

    }

    private fun calculateHash(file: File): String {

        val fs = FileInputStream(file)
        val first64Bytes = ByteArray(65536)
        fs.read(first64Bytes, 0, 65536)

        val last64Bytes = ByteArray(65536)
        fs.skip(file.length() - 131072)
        fs.read(last64Bytes)

        val combined = first64Bytes + last64Bytes

        val digest = MessageDigest.getInstance("MD5")
        val hashValue = digest.digest(combined).hexValue

        return hashValue
    }
}