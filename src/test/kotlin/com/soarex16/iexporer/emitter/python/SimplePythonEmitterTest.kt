package com.soarex16.iexporer.emitter.python

import com.soarex16.iexporer.emitter.EmitterMockData
import com.soarex16.iexporer.emitter.IInterfaceEmitter
import com.soarex16.iexporer.model.IECompilationUnitNode
import com.soarex16.iexporer.model.InterfaceDeclaration
import com.soarex16.iexporer.parser.spoon.SpoonParser
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class SimplePythonEmitterTest {
    private lateinit var ieAst: IECompilationUnitNode

    private lateinit var emitter: IInterfaceEmitter

    @BeforeEach
    fun init() {
        ieAst = EmitterMockData.createMockEast()
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