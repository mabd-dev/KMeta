package com.mabd.kmeta.loggable

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.ksp.toTypeName

internal const val DELEGATE_NAME = "delegate"

internal fun Sequence<KSAnnotation>.doLog(): Boolean =
    this
        .filter { it.shortName.asString() == _root_ide_package_.com.mabd.kmeta.loggable.NoLog::class.java.simpleName }
        .toList()
        .isEmpty()

internal fun KSTypeParameter.toTypeVariable(): TypeVariableName {
    val name = this.name.asString()
    val bounds = this.bounds.map { it.toTypeName() }.toList()
    return if (bounds.isEmpty()) {
        TypeVariableName(name)
    } else {
        TypeVariableName(name, bounds)
    }
}
