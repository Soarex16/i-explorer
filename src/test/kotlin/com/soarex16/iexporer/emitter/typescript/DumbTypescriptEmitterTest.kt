package com.soarex16.iexporer.emitter.typescript

import com.soarex16.iexporer.emitter.EmitterMockData
import com.soarex16.iexporer.emitter.IInterfaceEmitter
import com.soarex16.iexporer.model.IECompilationUnitNode
import com.soarex16.iexporer.model.InterfaceDeclaration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DumbTypescriptEmitterTest {
    private lateinit var ieAst: IECompilationUnitNode

    private lateinit var emitter: IInterfaceEmitter

    @BeforeEach
    fun init() {
        ieAst = EmitterMockData.createMockEast()
        emitter = DumbTypescriptEmitter()
    }

    @Test
    fun emit() {
        val clazz = ieAst.declaredTypes.first()
        val methods = clazz.methods
        val iface = InterfaceDeclaration("IFace", ieAst, clazz, methods)

        val expected = """|export interface IFace {
                          |	fromArrayToCollection<T>(a: any[], b: any[], c: any): void;
                          |}""".trimMargin()

        val emittedCode = emitter.emit(iface)

        assertEquals(emittedCode, expected)
    }
}