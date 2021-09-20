package com.soarex16.iexporer.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Хочу независимое от парсера представление дерева
 */
sealed interface IEAstNode {
    // Тип вершины в дереве
    val nodeType: String

    // Список дочерних узлов. Если вершина терминальная, то список пустой
    @Serializable
    val children: List<IEAstNode>
        get() = emptyList()
}

// TODO: параметры дженериков-то какие-то могут быть уже подставлены
@Serializable
@SerialName("type")
data class IETypeNode(val qualifiedName: String, val isArray: Boolean = false, val genericTypeParams: List<String>? = null): IEAstNode {
    override val nodeType: String = javaClass.simpleName

    fun isGenericParam() = genericTypeParams?.size == 1 && qualifiedName == genericTypeParams.firstOrNull()
}

@Serializable
@SerialName("arg")
data class IEMethodArgumentNode(val name: String, val type: IETypeNode) : IEAstNode {
    override val nodeType: String = javaClass.simpleName

    override val children: List<IEAstNode>
        get() = listOf(type)
}

/**
 * Описание метода
 * Содержит имя, список аргументов, модификаторы и возвращаемый тип
 * TODO: thrown types (который нет в прекрасном котлине)
 */
@Serializable
@SerialName("method")
data class IEMethodDeclarationNode(
    val modifiers: List<String>,
    val returnType: IETypeNode,
    val simpleName: String,
    val arguments: List<IEMethodArgumentNode> = emptyList(),
    val genericTypeParams: List<String>? = null
) : IEAstNode {
    override val nodeType: String
        get() = javaClass.simpleName

    override val children: List<IEAstNode>
        get() = arguments

    fun isGeneric() = genericTypeParams != null
}

/**
 * Описание класса
 * В моей модели дерева все классы являются прямыми потомками CompilationUnitNode и содержат только определения методов
 */
@Serializable
@SerialName("class")
data class IEClassDeclarationNode(
    val qualifiedName: String,
    val simpleName: String,
    val methods: List<IEMethodDeclarationNode>,
    val genericTypeParams: List<String>? = null
) : IEAstNode {
    override val nodeType: String
        get() = javaClass.simpleName

    override val children: List<IEAstNode>
        get() = methods

    fun isGeneric() = genericTypeParams != null
}

/**
 * Упрощенное (до уровня, требуемого для ршения поставленно тестовой задачи) представление
 * единицы компиляции (java файл)
 * CompilationUnit содержит только список определяемых в нем классов в плоском виде (т. е. не учитывает вложенные классы)
 * Это, конечно, не лучшая идея, потому что могут возникнуть потом потенциальные проблемы со ссылками,
 * но чтобы не усложнять решил делать так
 */
@Serializable
@SerialName("unit")
data class IECompilationUnitNode(
    val path: String,
    val packageName: String,
    val declaredTypes: List<IEClassDeclarationNode>
) : IEAstNode {
    override val nodeType: String
        get() = javaClass.simpleName

    override val children: List<IEAstNode>
        get() = declaredTypes
}