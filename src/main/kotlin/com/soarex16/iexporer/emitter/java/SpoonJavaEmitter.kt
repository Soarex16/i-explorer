package com.soarex16.iexporer.emitter.java

import com.soarex16.iexporer.emitter.IInterfaceEmitter
import com.soarex16.iexporer.model.IEMethodArgumentNode
import com.soarex16.iexporer.model.IETypeNode
import com.soarex16.iexporer.model.InterfaceDeclaration
import spoon.Launcher
import spoon.reflect.code.CtComment
import spoon.reflect.declaration.*
import spoon.reflect.reference.CtReference
import spoon.reflect.reference.CtTypeParameterReference
import spoon.reflect.reference.CtTypeReference

private val AUTOGEN_COMMENT = "This interface was generated automatically by i-explorer"

/**
 * Note: этот класс выглядит как очень криво пристроенный сбоку костыль,
 * но я уже переписываю пятый раз и я устал думать над дизайном иерархии,
 * чтобы обеспечить достаточный полиморфизм.
 * Поэтому данный класс работает только с обертками {@link SpoonAstNode}, что
 * оооочень сильно ломает Liskov Substitution Principle
 * Эту проблему можно решить, если заюзать паттерн Bridge
*/

class SpoonJavaEmitter : IInterfaceEmitter {
    private val launcher = Launcher()
    private val factory = launcher.factory

    init {
        launcher.environment.noClasspath = true
        launcher.environment.isAutoImports = true
    }

    override fun emit(iface: InterfaceDeclaration): String {
        val interfaceQualifiedName = "${iface.unit.packageName}.${iface.name}"

        val clazz = iface.sourceClass

        val emittedInterface = factory.createInterface(interfaceQualifiedName)

        addAutogenComment(emittedInterface)
        val classTypeParams = clazz.genericTypeParams

        iface.methods.forEach { meth ->
            val genericParams = meth.genericTypeParams

            val returnType = createSpoonTypeRef(meth.returnType)
            val params = meth.arguments.map { createSpoonParam(it) }

            val newGenericParams = getNewGenericParams(
                classTypeParams ?: emptyList(),
                emittedInterface,
                genericParams ?: emptyList()
            )

            factory.createTypeParameter()

            createGenericParams(newGenericParams).forEach {
                emittedInterface.addFormalCtTypeParameter<CtTypeParameter>(it)
            }

            val method = factory.Method().create(emittedInterface, emptySet(), returnType, meth.simpleName, params, emptySet())

            emittedInterface.addTypeMember<CtType<Any>>(method)
        }

        return emittedInterface.toStringWithImports()
    }

    private fun createGenericParams(params: List<String>) = params.map {
        val param = factory.createTypeParameter()
        param.setSimpleName<CtNamedElement>(it)
        param
    }

    private fun getNewGenericParams(
        classTypeParams: List<String>,
        iface: CtInterface<Any>,
        functionGenericParams: List<String>,
    ): List<String> {
        val ifaceParams = iface.formalCtTypeParameters

        return classTypeParams
            .filter {
                functionGenericParams.any { p -> p == it }
                        && ifaceParams.none { p -> p.simpleName == it }
            }
    }

    private fun createSpoonTypeRef(type: IETypeNode): CtTypeReference<*> {
        val typeRef = factory.createTypeReference<Any>()
        typeRef.setSimpleName<CtReference>(type.qualifiedName)
        return typeRef
    }

    private fun createSpoonParam(ieArg: IEMethodArgumentNode): CtParameter<*> {
        TODO()
    }

    private fun extractGenericParams(params: List<CtParameter<*>>): List<CtTypeReference<*>> = params
        .filter { it.type is CtTypeParameterReference }
        .map { it.type }

    private fun addAutogenComment(type: CtType<*>) {
        val comment = launcher.factory.createComment(AUTOGEN_COMMENT, CtComment.CommentType.JAVADOC)
        type.addComment<CtType<Any>>(comment)
    }
}
