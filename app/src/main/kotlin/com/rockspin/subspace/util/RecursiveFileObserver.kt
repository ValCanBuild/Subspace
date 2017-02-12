package com.rockspin.subspace.util

import android.os.FileObserver
import android.util.ArrayMap
import java.io.File
import java.util.*

/**
 * A FileObserver that observes all the files/folders within given directory
 * recursively. It automatically starts/stops monitoring new folders/files
 * created after starting the watch.
 * Based on: [this gist](https://gist.github.com/gitanuj/888ef7592be1d3f617f6#file-recursivefileobserver-java-L13)
 */
class RecursiveFileObserver(private val rootPath: String, private var eventMask: Int, private val eventListener: EventListener) :
    FileObserver(rootPath, eventMask) {

    interface EventListener {
        fun onEvent(event: Int, file: File)
    }

    private val observerMap = ArrayMap<String, FileObserver>()

    init {
        eventMask = eventMask.or(CREATE).or(DELETE_SELF)
    }

    constructor(path: String, eventListener: EventListener) : this(path, ALL_EVENTS, eventListener)

    override fun startWatching() {
        val stack = Stack<String>()
        stack.push(rootPath)

        // recursively watch all child directories
        while (!stack.empty()) {
            val parent = stack.pop()
            startWatching(parent)

            val pathFile = File(parent)
            pathFile.listFiles()?.forEach {
                if (watch(it)) {
                    stack.push(it.absolutePath)
                }
            }
        }
    }

    override fun stopWatching() {
        synchronized(observerMap) {
            observerMap.values.forEach { it.stopWatching() }
            observerMap.clear()
        }
    }

    override fun onEvent(event: Int, path: String?) {
        val file = if (path == null) File(rootPath) else File(rootPath, path)
        notify(event, file)
    }

    private fun startWatching(path: String) {
        synchronized(observerMap) {
            var observer = observerMap.remove(path)
            observer?.stopWatching()

            observer = SingleFileObserver(path, eventMask)
            observer.startWatching()
            observerMap[path] = observer
        }
    }

    private fun stopWatching(path: String) {
        synchronized(observerMap) {
            val observer = observerMap.remove(path)
            observer?.stopWatching()
        }
    }

    private fun watch(file: File): Boolean {
        return file.isDirectory && file.name != "." && file.name != ".."
    }

    private fun notify(event: Int, file: File) {
        eventListener.onEvent(event.and(ALL_EVENTS), file)
    }

    private inner class SingleFileObserver(val filePath: String, mask: Int) : FileObserver(filePath, mask) {

        override fun onEvent(event: Int, path: String?) {
            val file = if (path == null) File(filePath) else File(filePath, path)

            when (event.and(ALL_EVENTS)) {
                DELETE_SELF -> stopWatching(filePath)
                CREATE -> {
                    if (watch(file)) {
                        startWatching(file.absolutePath)
                    }
                }
            }

            notify(event, file)
        }

    }
}