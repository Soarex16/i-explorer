package com.soarex16.iexporer.parser.spoon.adapter

import com.soarex16.iexporer.model.IEMethodArgumentNode
import com.soarex16.iexporer.model.IEMethodDeclarationNode
import com.soarex16.iexporer.model.IETypeNode
import com.soarex16.iexporer.parser.IEAstTranslator
import spoon.reflect.declaration.CtMethod
import spoon.reflect.declaration.CtParameter
import spoon.reflect.reference.CtTypeReference
import spoon.reflect.visitor.CtScanner

class SpoonMethodTranslator : CtScanner(), IEAstTranslator<CtMethod<*>, IEMethodDeclarationNode> {
    private val currentMethodArgs = mutableListOf<IEMethodArgumentNode>()

    override fun translateToIEAst(root: CtMethod<*>): IEMethodDeclarationNode {
        root.parameters.forEach { visitCtParameter(it) }

        val genericParams = root.formalCtTypeParameters.map { it.simpleName }

        val modifiers: List<String> = root.modifiers.map { it.name.lowercase() }

        val returnType = createTypeNode(root.type)

        return IEMethodDeclarationNode(modifiers, returnType, root.simpleName, currentMethodArgs, genericParams)
    }

    override fun <T : Any?> visitCtParameter(ctParameter: CtParameter<T>?) {
        super.visitCtParameter(ctParameter)
        if (ctParameter == null)
            return

        val paramType = createTypeNode(ctParameter.type)

        val paramNode = IEMethodArgumentNode(ctParameter.simpleName, paramType)
        this.currentMethodArgs.add(paramNode)
    }

    // Тут мои нервы не выдержали мутирования глобального состояния
    private fun <T : Any?> createTypeNode(ctType: CtTypeReference<T>): IETypeNode {
        val genericParams: List<String> =
            if (ctType.isGenerics)
                listOf(ctType.simpleName)
            else
                ctType.actualTypeArguments.map { it.simpleName }

        return IETypeNode(ctType.qualifiedName, ctType.isArray, genericParams)
    }
}