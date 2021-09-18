package com.soarex16.iexporer.emitter.python

import com.soarex16.iexporer.emitter.IInterfaceEmitter
import com.soarex16.iexporer.model.IECompilationUnitNode
import com.soarex16.iexporer.model.InterfaceDeclaration
import com.soarex16.iexporer.parser.spoon.SpoonParser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SimplePythonEmitterTest {
    val rawCode = """
                    |package org.sample;
                    |
                    |class SomeClass {
                    |    public <T> void fromArrayToCollection(T[] a, Collection<T> c) {
                    |        for (T o : a) {
                    |            c.add(o); // Correct
                    |        }
                    |    }
                    |
                    |    class NestedClass_SecondLevel {
                    |        class NestedClass_ThirdLevel {
                    |            static <T> void fromArrayToCollection(T[] a, Collection<T> c) {
                    |                for (T o : a) {
                    |                    c.add(o); // Correct
                    |                }
                    |            }
                    |        }
                    |    }
                    |}""".trimMargin()

    lateinit var ieAst: IECompilationUnitNode

    lateinit var emitter: IInterfaceEmitter

    @BeforeEach
    fun init() {
        val parser = SpoonParser()
        ieAst = parser.parseString(rawCode) as IECompilationUnitNode
        emitter = SimplePythonEmitter()
    }

    @Test
    fun emit() {
        val clazz = ieAst.declaredTypes.first()
        val methods = clazz.methods
        val iface = InterfaceDeclaration("IFace", ieAst, clazz, methods)

        val expected = """
                          |from abc import ABCMeta, abstractmethod
                          |
                          |class IFace:
                          |	__metaclass__ = ABCMeta
                          |	
                          |	@abstractmethod
                          |	def fromArrayToCollection(self, a, c, a, c):
                          |		raise NotImplementedError
                          |""".trimMargin()

        val emittedCode = emitter.emit(iface)

        assertEquals(emittedCode, expected)
    }
}