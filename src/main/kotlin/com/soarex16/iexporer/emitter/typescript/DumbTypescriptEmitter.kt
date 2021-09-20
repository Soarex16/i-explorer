package com.soarex16.iexporer.emitter.typescript

import com.soarex16.iexporer.emitter.IInterfaceEmitter
import com.soarex16.iexporer.model.*

private val typeMapping = mapOf<String, String>(
    // special types
    "void" to "void",
    // numbers
    "byte" to "number",
    "short" to "number",
    "int" to "number",
    "long" to "number",
    "float" to "number",
    "double" to "number",
    "boolean" to "boolean",
    // strings
    "char" to "String",
    "java.lang.String" to "string"
)

// Преобразуем в any, когда не знаем во что
private const val DEFAULT_TARGET_TYPE = "any"

// Dumb because anything that is not primitive type it converts into Any type
class DumbTypescriptEmitter : IInterfaceEmitter {
    override fun emit(iface: InterfaceDeclaration): String {
        val classGenericParamsString = createClassGenericParamsString(iface.sourceClass)
        val interfaceDeclarationHeader = "export interface ${iface.name}${classGenericParamsString} {"
        val interfaceDeclarationFooter = "}"

        val methodDeclarationsString = iface.methods
            .joinToString("\n") { "\t" + createMethodDeclarationString(it) }

        return interfaceDeclarationHeader + "\n" + methodDeclarationsString + "\n" + interfaceDeclarationFooter
    }

    private fun createClassGenericParamsString(
        clazz: IEClassDeclarationNode
    ) = if (clazz.genericTypeParams == null || clazz.genericTypeParams.isEmpty()) "" else "<${clazz.genericTypeParams.joinToString(", ")}>"

    private fun createMethodDeclarationString(method: IEMethodDeclarationNode): String {
        val argumentsString = method.arguments.joinToString(", ") { createArgumentString(it) }
        val returnTypeString = convertType(method.returnType)
        val genericParamsDeclarationString = if (method.genericTypeParams == null || method.genericTypeParams.isEmpty())
            ""
        else
            "<${method.genericTypeParams.joinToString(", ")}>"

        return "${method.simpleName}${genericParamsDeclarationString}($argumentsString): $returnTypeString;"
    }

    private fun createArgumentString(arg: IEMethodArgumentNode): String = "${arg.name}: ${convertType(arg.type)}"

    private fun convertType(typeDecl: IETypeNode): String {
        val mappedType = typeMapping.getOrElse(typeDecl.qualifiedName) { DEFAULT_TARGET_TYPE }

        val actualType = when {
            typeDecl.isGenericParam() -> typeDecl.qualifiedName
            // try to substitute generic as Type<T1, T2>
            !typeDecl.isGenericParam() && !typeDecl.genericTypeParams.isNullOrEmpty() && mappedType != DEFAULT_TARGET_TYPE -> {
                "$typeDecl<${typeDecl.genericTypeParams.joinToString(", ")}>"
            }
            else -> mappedType
        }

        return if (typeDecl.isArray) "$actualType[]" else actualType
    }
}