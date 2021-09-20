package com.soarex16.iexporer.emitter

import com.soarex16.iexporer.model.IECompilationUnitNode
import com.soarex16.iexporer.parser.spoon.SpoonParser

object EmitterMockData {
    private val rawCode = """
                    |package org.sample;
                    |
                    |class SomeClass {
                    |    public <T> void fromArrayToCollection(T[] a, int[] b, Collection<T> c) {
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

    fun createMockEast() = SpoonParser().parseString(rawCode) as IECompilationUnitNode
}