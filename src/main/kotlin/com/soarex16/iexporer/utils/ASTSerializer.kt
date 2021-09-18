package com.soarex16.iexporer.utils

import com.soarex16.iexporer.model.*
import com.soarex16.iexporer.parser.spoon.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.modules.SerializersModule

object ASTSerializer {
    private val astSerializerModule = SerializersModule {
        // Меня до жути раздражает, что нужно явно указывать реализации интерфейса, учитывая,
        // что интерфейс помечен sealed, т.е. все его реализации должны быть известны на этапе компиляции
        polymorphic(IEAstNode::class) {
            subclass(IEMethodArgumentNode::class)
            subclass(IEMethodDeclarationNode::class)
            subclass(IEClassDeclarationNode::class)
            subclass(IECompilationUnitNode::class)
        }
    }

    private val json = Json {
        serializersModule = astSerializerModule
        useArrayPolymorphism = true
    }

    fun toJson(subtree: IEAstNode): String = json.encodeToString(subtree)
    fun parseFromJson(jsonString: String): IEAstNode = json.decodeFromString(jsonString)
}