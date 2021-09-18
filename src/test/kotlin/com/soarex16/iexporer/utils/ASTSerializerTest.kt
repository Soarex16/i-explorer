package com.soarex16.iexporer.utils

import com.soarex16.iexporer.model.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ASTSerializerTest {
    private val simpleClass_ast: IEAstNode = IECompilationUnitNode(
        "/some/path",
        "org.example",
        listOf(
            IEClassDeclarationNode(
                "org.example.SampleClass",
                "SampleClass",
                listOf(
                    IEMethodDeclarationNode(
                        listOf("public"),
                        IETypeNode("void"),
                        "someMethod"
                    )
                )
            )
        )
    )

    private val simpleClass_json = "[\"unit\",{\"path\":\"/some/path\",\"packageName\":\"org.example\",\"declaredTypes\":[{\"qualifiedName\":\"org.example.SampleClass\",\"simpleName\":\"SampleClass\",\"methods\":[{\"modifiers\":[\"public\"],\"returnType\":{\"qualifiedName\":\"void\"},\"simpleName\":\"someMethod\"}]}]}]"

    private val classWithNested_ast = IECompilationUnitNode(
        "/some/path",
        "org.example",
        listOf(
            IEClassDeclarationNode(
                "org.example.SomeClass\$NestedClass_SecondLevel\$NestedClass_ThirdLevel",
                "NestedClass_ThirdLevel",
                emptyList()
            ),
            IEClassDeclarationNode(
                "org.example.SomeClass\$NestedClass_SecondLevel",
                "NestedClass_SecondLevel",
                emptyList()
            ),
            IEClassDeclarationNode(
                "org.example.SomeClass",
                "SomeClass",
                emptyList()
            )
        )
    )

    private val classWithNested_json = "[\"unit\",{\"path\":\"/some/path\",\"packageName\":\"org.example\",\"declaredTypes\":[{\"qualifiedName\":\"org.example.SomeClass\$NestedClass_SecondLevel\$NestedClass_ThirdLevel\",\"simpleName\":\"NestedClass_ThirdLevel\",\"methods\":[]},{\"qualifiedName\":\"org.example.SomeClass\$NestedClass_SecondLevel\",\"simpleName\":\"NestedClass_SecondLevel\",\"methods\":[]},{\"qualifiedName\":\"org.example.SomeClass\",\"simpleName\":\"SomeClass\",\"methods\":[]}]}]"

    @Test
    fun toJson_simpleCase() {
        // Да, не очень хорошо сравнивать работу сериализатора влоб, потому что
        // он может переупорядочить поля и все такое, но я не хочу с этим париться
        val serializedAST = ASTSerializer.toJson(simpleClass_ast)
        assertEquals(serializedAST, simpleClass_json)
    }

    @Test
    fun parseFromJson_simpleCase() {
        val deserializedClass = ASTSerializer.parseFromJson(simpleClass_json)
        assertEquals(deserializedClass, simpleClass_ast)
    }

    @Test
    fun toJson_nestedClasses() {
        val serializedAST = ASTSerializer.toJson(classWithNested_ast)
        assertEquals(serializedAST, classWithNested_json)
    }
}