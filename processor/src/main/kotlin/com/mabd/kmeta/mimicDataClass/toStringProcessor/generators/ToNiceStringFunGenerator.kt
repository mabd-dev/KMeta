package com.mabd.kmeta.mimicDataClass.toStringProcessor.generators

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.mabd.kmeta.common.createGeneratedAnnotation
import com.mabd.kmeta.mimicDataClass.toStringProcessor.ToNiceString
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.toClassName
import kotlin.jvm.java

internal class ToNiceStringFunGenerator(
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
            FunSpec.Companion
                .builder("toNiceString")
                .receiver(className)
                .addAnnotation(createGeneratedAnnotation())

        val parametersNames =
            constructorParameters
                .toNameValuePair(logger)
                .joinToString(", ") { (k, v) -> "$k=$v" }

        func.addStatement("return \"${className.simpleName}($parametersNames)\"")
        func.returns(String::class)

        return func.build()
    }

    private fun List<KSValueParameter>.toNameValuePair(logger: KSPLogger): List<Pair<String, String>> {
        return this.mapNotNull { param ->
            val name = param.name?.asString() ?: return@mapNotNull null

            val paramTypeAnnotations =
                param.type
                    .resolve()
                    .declaration.annotations

            val hasToNiceString =
                paramTypeAnnotations.distinct().firstOrNull {
                    val qualifiedName =
                        it.annotationType
                            .resolve()
                            .declaration.qualifiedName
                            ?.asString() ?: false
                    qualifiedName == ToNiceString::class.java.name
                } != null

            val value =
                if (hasToNiceString) {
                    "\${$name.toNiceString()}"
                } else {
                    "\$$name"
                }
            name to value
        }
    }
}
