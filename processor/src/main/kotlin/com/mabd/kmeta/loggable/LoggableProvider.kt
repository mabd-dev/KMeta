package com.mabd.kmeta.loggable

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class LoggableProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        _root_ide_package_.com.mabd.kmeta.loggable.LoggableProcessor(environment)
}
