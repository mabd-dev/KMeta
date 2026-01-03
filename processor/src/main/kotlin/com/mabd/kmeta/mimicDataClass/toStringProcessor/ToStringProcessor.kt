package com.mabd.kmeta.mimicDataClass.toStringProcessor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.validate
import com.mabd.kmeta.common.isDataClass
import com.mabd.kmeta.mimicDataClass.common.ExtensionFileGenerator
import com.mabd.kmeta.mimicDataClass.toStringProcessor.generators.ToNiceStringFunGenerator
import com.squareup.kotlinpoet.ksp.writeTo
import kotlin.jvm.java

/**
 * Generates a data-class-like `toNiceString` extension function for this class.
 *
 * Apply to a regular (non-data) class with a primary constructor. The generated extension will
 * generate toNiceString function, just like Kotlin's data class `toString`.
 *
 * Example:
 * ```
 * @Copy
 * class User(val name: String, val age: Int)
 * // Generates:
 * // fun User.toNiceString(): String = "User(name=$name, age=$age)
 * ```
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ToNiceString

private const val GENERATED_FILE_NAME = "ToNiceStringExtension"

class ToStringProcessor(
    private val env: SymbolProcessorEnvironment,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val classDeclarations = getToNiceStringAnnotationDeclarations(resolver)
        if (!classDeclarations.iterator().hasNext()) {
            return emptyList()
        }

        val privateInterfaces = classDeclarations.filter { it.modifiers.contains(Modifier.PRIVATE) }

        privateInterfaces.forEach { declaration ->
            val interfaceName = declaration.qualifiedName?.asString() ?: declaration.simpleName.asString()
            env.logger.error("$interfaceName class cannot be private")
        }

        val functionsInfo =
            classDeclarations
                .groupBy { declaration -> declaration.packageName.asString() }
                .mapNotNull { (packageName, declarations) ->
                    val functions =
                        declarations
                            .mapNotNull { declaration ->
                                ToNiceStringFunGenerator(declaration).generate(env.logger)
                            }.ifEmpty { return@mapNotNull null }
                    packageName to functions
                }.toMap()

        functionsInfo.map { (packageName, functions) ->
            val fileSpec = ExtensionFileGenerator(packageName, GENERATED_FILE_NAME, functions).generate()
            fileSpec.writeTo(env.codeGenerator, Dependencies(false))
        }

        return emptyList()
    }

    private fun getToNiceStringAnnotationDeclarations(resolver: Resolver): Sequence<KSClassDeclaration> =
        resolver
            .getSymbolsWithAnnotation(ToNiceString::class.java.name)
            .filterIsInstance<KSClassDeclaration>()
            .distinct()
            .filter { it.validate() }
            .filter { it.classKind == ClassKind.CLASS }
            .filter { !it.isDataClass() }
}
