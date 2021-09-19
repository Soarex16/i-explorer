package com.soarex16.iexporer.parser.spoon

import com.soarex16.iexporer.model.IECompilationUnitNode
import com.soarex16.iexporer.model.ParserException
import com.soarex16.iexporer.parser.ISourceParser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains

internal class SpoonParserTest {

    private lateinit var parser: ISourceParser;

    @BeforeEach
    fun init() {
        parser = SpoonParser()
    }

    @Test
    fun parseString_empty() {
        assertThrows<ParserException> {
            val ieAst = parser.parseString("")
        }
    }

    @Test
    fun parseString_staticMethodModifier() {
        val rawCode = """
                    |package org.sample;
                    |
                    |class SomeClass {
                    |    public static <T> void fromArrayToCollection(T[] a, Collection<T> c) {
                    |        for (T o : a) {
                    |            c.add(o); // Correct
                    |        }
                    |    }
                    |}""".trimMargin()

        val ast = parser.parseString(rawCode) as IECompilationUnitNode

        val parsedClass = ast.declaredTypes.first()
        val method = parsedClass.methods.first()

        assertContains(method.modifiers, "static")
    }
}