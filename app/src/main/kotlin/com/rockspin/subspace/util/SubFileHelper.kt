package com.rockspin.subspace.util

import java.io.File
import java.io.FileWriter

/**
 * Created by valentin.hinov on 20/01/2017.
 */
class SubFileHelper(private val rootFolderPath: String) {

    private val allowedExtensions = arrayOf("avi", "mkv", "mp4", "mov")

    val hasSubCandidates: Boolean
    get() = subCandidates.isNotEmpty()

    /**
     * An array of files in the rootFolderPath
     * which are candidates for subtitle downloading
     */
    val subCandidates: Array<File> by lazy {
        val rootFolder = File(rootFolderPath)
        findSubCandidatesInFolder(rootFolder)
    }

    fun createSubtitleForFile(movieFile: File, subtitle: String): File {
        val srtFile = File("${movieFile.parent}/${movieFile.nameWithoutExtension}.srt")
        val fileWriter = FileWriter(srtFile)
        fileWriter.write(subtitle)
        fileWriter.flush()
        fileWriter.close()

        return srtFile
    }

    private fun findSubCandidatesInFolder(folder: File): Array<File> {
        var candidates = emptyArray<File>()

        // find all sub candidates in any directories in this folder
        folder.listFiles { it -> it.isDirectory }
            .forEach { candidates += findSubCandidatesInFolder(it) }

        candidates += folder.listFiles { it -> allowedExtensions.contains(it.extension) }
            .filter { it ->
                // make sure a subtitle for this file does not exist
                val subFile = File("${it.parent}/${it.nameWithoutExtension}.srt")
                !subFile.exists()
            }

        return candidates
    }
}