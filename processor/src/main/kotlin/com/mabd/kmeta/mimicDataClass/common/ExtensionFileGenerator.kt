package com.mabd.kmeta.mimicDataClass.common

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.mabd.kmeta.common.createGeneratedAnnotation

class ExtensionFileGenerator(
    private val packageName: String,
    private val fileName: String,
    private val functions: List<FunSpec>,
) {
    fun generate(): FileSpec =
        FileSpec.Companion
            .builder(packageName, fileName)
            .addAnnotation(_root_ide_package_.com.mabd.kmeta.common.createGeneratedAnnotation())
            .apply { functions.forEach { this.addFunction(it) } }
            .build()
}
