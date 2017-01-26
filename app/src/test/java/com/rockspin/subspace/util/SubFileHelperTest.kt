package com.rockspin.subspace.util

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.runner.RunWith

/**
 * Created by valentin.hinov on 20/01/2017.
 */
class SubFileHelperSpekTest: Spek ({

    describe("a SubFileHelper") {
        val testFolder = javaClass.classLoader.getResource("Movies").file
        val subFileHelper = SubFileHelper(testFolder)


        it("returns the correct number of sub candidates") {
            val expectedNumber = 4
            assert(subFileHelper.subCandidates.size == expectedNumber)
        }
    }
})