package com.mabd.kmeta.common

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun KSClassDeclaration.isDataClass(): Boolean = this.modifiers.contains(Modifier.DATA)

fun createGeneratedAnnotation(): AnnotationSpec {
    val generatorName = "org.mabd"
    val date = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val comment = "https://https://github.com/MABD-dev/Symbol-Processors-Playground"

    val generatedClass = ClassName("javax.annotation.processing", "Generated")

    return AnnotationSpec
        .builder(generatedClass)
        .addMember("value = [%S]", generatorName)
        .addMember("date = %S", date)
        .addMember("comments = %S", comment)
        .build()
}
