package com.mabd.kmeta.mimicDataClass.copyProcessor.generators

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.mabd.kmeta.common.createGeneratedAnnotation
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

class ClassCopyFunGenerator(
    private val declaration: KSClassDeclaration,
) {
    fun generate(logger: KSPLogger): FunSpec? {
        val constructorParameters =
            declaration
                .primaryConstructor
                ?.parameters
                ?: run {
                    logger.warn("${declaration.qualifiedName?.asString()} has no primary constructor")
                    return null
                }

        if (constructorParameters.isEmpty()) {
            logger.warn("${declaration.qualifiedName?.asString()} has no parameters in primary constructor")
            return null
        }

        val className = declaration.toClassName()
        val func =
            FunSpec
                .builder("copy")
                .receiver(className)
                .addAnnotation(createGeneratedAnnotation())

        val parameterSpecs = constructorParameters.createParameterSpecs()
        func.addParameters(parameterSpecs)

        val parametersNames =
            constructorParameters
                .mapNotNull { it.name?.asString() }
                .joinToString(", ")

        func.addStatement("return $className($parametersNames)")
        func.returns(declaration.toClassName())

        return func.build()
    }

    private fun List<KSValueParameter>.createParameterSpecs(): List<ParameterSpec> {
        return this.mapNotNull { param ->
            val name = param.name?.asString() ?: return@mapNotNull null
            val type = param.type.toTypeName()

            val builder = ParameterSpec.builder(name, type)

            if (param.isVar || param.isVal) {
                builder.defaultValue("this.$name")
            }

            builder.build()
        }
    }
}
