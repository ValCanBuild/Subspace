package com.rockspin.subspace.network

import okhttp3.ResponseBody
import retrofit2.http.*
import rx.Observable

/**
 * Created by valentin.hinov on 19/01/2017.
 */
interface SubtitleWebInterface {

    @Streaming
    @Headers("User-Agent: SubDB/1.0 (Subspace/1.0; https://github.com/ValCanBuild/Subspace")
    @GET("/?action=download")
    fun downloadSubtitleForHash(@Query("hash") hash: String, @Query("language") language: String = "en"): Observable<ResponseBody>
}