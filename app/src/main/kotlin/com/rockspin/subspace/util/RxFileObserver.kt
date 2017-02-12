package com.rockspin.subspace.util

import android.os.FileObserver
import rx.Observable
import rx.lang.kotlin.PublishSubject
import java.io.File

/**
 * Created by valentin.hinov on 27/01/2017.
 */
class RxFileObserver(rootPath: String) {

    data class FileEvent(val event: Int, val file: File)

    private val fileObserver: RecursiveFileObserver
    private val publishSubject = PublishSubject<FileEvent>()

    companion object {
        val DEFAULT_FLAGS = FileObserver.CLOSE_NOWRITE
            .or(FileObserver.CLOSE_WRITE)
            .or(FileObserver.CREATE)
            .or(FileObserver.MODIFY)
            .or(FileObserver.MOVED_TO)
            .or(FileObserver.OPEN)
    }

    init {
        fileObserver = RecursiveFileObserver(rootPath, DEFAULT_FLAGS, object : RecursiveFileObserver.EventListener {
            override fun onEvent(event: Int, file: File) {
                publishSubject.onNext(FileEvent(event, file))
            }
        })
    }

    fun monitorEvents() : Observable<FileEvent> {
        return publishSubject
    }
}