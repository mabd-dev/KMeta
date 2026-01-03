package com.mabd.kmeta.loggable.generators

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.mabd.kmeta.loggable.DELEGATE_NAME
import com.mabd.kmeta.loggable.doLog
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName

internal class ClassPropertyGenerator(
    val declaration: KSPropertyDeclaration,
) {
    fun generate(fileName: String): PropertySpec {
        val propertyName = declaration.simpleName.asString()
        return PropertySpec
            .builder(propertyName, declaration.type.toTypeName())
            .mutable(declaration.isMutable)
            .addAnnotations(declaration)
            .addModifiers(KModifier.OVERRIDE)
            .addModifiers(declaration)
            .addKdocIfFound(declaration)
            .getter(declaration, fileName)
            .setter(declaration, fileName)
            .build()
    }

    private fun PropertySpec.Builder.addKdocIfFound(prop: KSPropertyDeclaration) =
        this.apply {
            prop.docString?.let { this.addKdoc(it) }
        }

    private fun PropertySpec.Builder.addModifiers(prop: KSPropertyDeclaration) =
        this.apply {
            addModifiers(prop.modifiers.mapNotNull { it.toKModifier() })
        }

    private fun PropertySpec.Builder.addAnnotations(func: KSPropertyDeclaration) =
        this.apply {
            addAnnotations(func.annotations.map { it.toAnnotationSpec() }.toList())
        }

    private fun PropertySpec.Builder.getter(
        prop: KSPropertyDeclaration,
        fileName: String,
    ) = this.apply {
        val propName = prop.simpleName.asString()
        val func =
            FunSpec
                .getterBuilder()
                .addStatement("val result = ${DELEGATE_NAME}.${prop.simpleName.asString()}")

        if (prop.annotations.doLog()) {
            val str =
                """println("$fileName: get $propName=\$\{result\}")"""
                    .trimIndent()
                    .replace("\\", "")
            func.addStatement(str)
        }

        func.addStatement("return result")

        this.getter(func.build())
    }

    private fun PropertySpec.Builder.setter(
        prop: KSPropertyDeclaration,
        fileName: String,
    ) = this.apply {
        val propName = prop.simpleName.asString()
        val func =
            FunSpec
                .setterBuilder()
                .addParameter("value", prop.type.toTypeName())
                .addStatement("${DELEGATE_NAME}.${prop.simpleName.asString()} = value")

        if (prop.annotations.doLog()) {
            val str =
                """println("$fileName: set:$propName=\$\{value\}")"""
                    .trimIndent()
                    .replace("\\", "")
            func.addStatement(str)
        }

        this.setter(func.build())
    }
}
