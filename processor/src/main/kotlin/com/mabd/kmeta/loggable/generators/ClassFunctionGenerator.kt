package com.mabd.kmeta.loggable.generators

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.mabd.kmeta.loggable.Loggable
import com.mabd.kmeta.loggable.doLog
import com.mabd.kmeta.loggable.toTypeVariable
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.toAnnotationSpec
import com.squareup.kotlinpoet.ksp.toKModifier
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver

internal class ClassFunctionGenerator(
    val declaration: KSFunctionDeclaration,
) {
    fun generate(args: Loggable): FunSpec =
        FunSpec
            .builder(declaration.simpleName.asString())
            .addAnnotations(declaration)
            .addModifiers(KModifier.OVERRIDE)
            .addModifiers(declaration)
            .addTypeVariableIfFound(declaration)
            .addKdocIfFound(declaration)
            .addParams(declaration)
            .addFunctionBody(declaration, args)
            .addReturnType(declaration)
            .build()

    private fun FunSpec.Builder.addAnnotations(func: KSFunctionDeclaration) =
        this.apply {
            addAnnotations(func.annotations.map { it.toAnnotationSpec() }.toList())
        }

    private fun FunSpec.Builder.addModifiers(func: KSFunctionDeclaration) =
        this.apply {
            addModifiers(func.modifiers.mapNotNull { it.toKModifier() })
        }

    private fun FunSpec.Builder.addTypeVariableIfFound(func: KSFunctionDeclaration) =
        this.apply {
            val typeVariables = func.typeParameters.map { it.toTypeVariable() }
            this.addTypeVariables(typeVariables)
        }

    private fun FunSpec.Builder.addKdocIfFound(func: KSFunctionDeclaration) =
        this.apply {
            func.docString?.let { this.addKdoc(it) }
        }

    private fun FunSpec.Builder.addParams(func: KSFunctionDeclaration) =
        this.apply {
            val params =
                func.parameters.map { param ->
                    val modifiers = if (param.isVararg) arrayOf(KModifier.VARARG) else arrayOf()

                    ParameterSpec(
                        name = param.name?.asString() ?: "_",
                        type = param.type.toTypeName(),
                        modifiers = modifiers,
                    )
                }
            addParameters(params)
        }

    private fun FunSpec.Builder.addReturnType(func: KSFunctionDeclaration) =
        this.apply {
            val typeParameterResolver = func.typeParameters.toTypeParameterResolver()
            func.returnType?.resolve()?.toTypeName(typeParameterResolver)?.let {
                this.returns(it)
            }
        }

    private fun FunSpec.Builder.addFunctionBody(
        func: KSFunctionDeclaration,
        args: com.mabd.kmeta.loggable.Loggable,
    ): FunSpec.Builder =
        this.apply {
            val functionName = func.simpleName.asString()
            val hasReturn = func.returnType?.toString() != "Unit"

            val paramsNames =
                func.parameters.joinToString(", ") { param ->
                    val varargStr = if (param.isVararg) "*" else ""
                    "${varargStr}${param.name?.getShortName()}"
                }
            val paramsPrint =
                func.parameters.joinToString(", ") { param ->
                    val name = param.name?.getShortName()
                    val varargStr = if (param.isVararg) ".toList()" else ""
                    "$name=\${${name}$varargStr}"
                }

            val typedVariableNames =
                func.typeParameters
                    .joinToString(", ") { it.name.asString() }
                    .let { if (it.isBlank()) "" else "<$it>" }

            this.addStatement("val result = ${_root_ide_package_.com.mabd.kmeta.loggable.DELEGATE_NAME}.${functionName}$typedVariableNames($paramsNames)")

            var returnStr = ""
            if (hasReturn) {
                returnStr = "->\$result"
            }

            if (func.annotations.doLog()) {
                this.addStatement("""println("${args.tag}: $functionName($paramsPrint)$returnStr")""")
            }

            if (hasReturn) {
                this.addStatement("return result")
            }
        }
}
