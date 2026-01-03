package com.mabd.kmeta.loggable.generators

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.mabd.kmeta.loggable.DELEGATE_NAME
import com.mabd.kmeta.loggable.toTypeVariable
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

internal class LoggerImplClassGenerator(
    val declaration: KSClassDeclaration,
) {
    fun generate(): FileSpec {
        val packageName = declaration.packageName.asString()
        val interfaceName = declaration.simpleName.asString()
        val fileName = "${interfaceName}LoggerImpl"

        val args = declaration.createAnnotationArgs()
        val typeParameters = declaration.typeParameters.map { it.toTypeVariable() }

        val loggerClassName = ClassName(packageName, fileName)
        val interfaceClassName =
            ClassName(packageName, interfaceName)
                .let {
                    if (typeParameters.isEmpty()) {
                        it
                    } else {
                        it.parameterizedBy(typeParameters)
                    }
                }

        val delegateProp =
            PropertySpec
                .builder(DELEGATE_NAME, interfaceClassName)
                .initializer(DELEGATE_NAME)
                .addModifiers(KModifier.PRIVATE)
                .build()

        val constructor =
            FunSpec
                .constructorBuilder()
                .addParameter(DELEGATE_NAME, interfaceClassName)
                .build()

        val loggerClass =
            TypeSpec
                .classBuilder(loggerClassName)
                .primaryConstructor(constructor)
                .addProperty(delegateProp)
                .addSuperinterface(interfaceClassName)
                .addTypeVariables(declaration.typeParameters.map { it.toTypeVariable() })

        declaration.docString?.let { loggerClass.addKdoc(it) }

        val properties =
            declaration
                .getDeclaredProperties()
                .map { ClassPropertyGenerator(it).generate(fileName) }
                .toList()
        loggerClass.addProperties(properties)

        val functions =
            declaration
                .getDeclaredFunctions()
                .map { ClassFunctionGenerator(it).generate(args) }
                .toList()
        loggerClass.addFunctions(functions)

        return FileSpec
            .builder(loggerClassName)
            .addType(loggerClass.build())
            .build()
    }

    private fun KSClassDeclaration.createAnnotationArgs(): com.mabd.kmeta.loggable.Loggable {
        val interfaceName = this.simpleName.asString()
        val fileName = "${interfaceName}LoggerImpl"

        var tag: String = fileName

        val loggableAnnotation =
            this.annotations.firstOrNull {
                it.shortName.asString() == com.mabd.kmeta.loggable.Loggable::class.java.name
            }

        loggableAnnotation?.arguments?.forEach { arg ->
            when (arg.name?.asString()?.lowercase()) {
                "tag" -> tag = arg.value as? String ?: tag
            }
        }

        return _root_ide_package_.com.mabd.kmeta.loggable.Loggable(
            tag = tag,
        )
    }
}
