package com.soarex16.iexporer.emitter

import com.soarex16.iexporer.emitter.java.SpoonJavaEmitter
import com.soarex16.iexporer.model.*
import org.junit.jupiter.api.Test

internal class SpoonJavaEmitterTest {
    val sampleMethod = IEMethodDeclarationNode(
        listOf("public"),
        IETypeNode("void"),
        "someMethod"
    )

    val sampleClass = IEClassDeclarationNode(
        "org.example.SampleClass",
        "SampleClass",
        listOf(sampleMethod)
    )

    private val sampleUnit = IECompilationUnitNode(
        "/some/path",
        "org.example",
        listOf(sampleClass)
    )

    val sampleInterfaceDecl = InterfaceDeclaration("ISampleClass", sampleUnit, sampleClass, listOf(sampleMethod))

    @Test
    fun emit_oneClass() {
        val spoonEmitter = SpoonJavaEmitter()

        val emittedInterface = spoonEmitter.emit(sampleInterfaceDecl)
    }
}