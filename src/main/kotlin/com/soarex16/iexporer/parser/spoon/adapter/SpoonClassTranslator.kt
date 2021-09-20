package com.soarex16.iexporer.parser.spoon.adapter

import com.soarex16.iexporer.model.IEClassDeclarationNode
import com.soarex16.iexporer.parser.IEAstTranslator
import spoon.reflect.declaration.CtClass
import spoon.reflect.visitor.CtScanner

class SpoonClassTranslator : CtScanner(), IEAstTranslator<CtClass<*>, IEClassDeclarationNode> {
    override fun translateToIEAst(root: CtClass<*>): IEClassDeclarationNode {
        val genericParams = root.formalCtTypeParameters.map { it.simpleName }

        // Создаю каждый раз новый инстанс транслятора, пот
        val eastMethodNodes = root.methods.map { SpoonMethodTranslator().translateToIEAst(it) }

        val fullName = root.qualifiedName
        val simpleName = root.simpleName

        return IEClassDeclarationNode(fullName, simpleName, eastMethodNodes, genericParams)
    }
}