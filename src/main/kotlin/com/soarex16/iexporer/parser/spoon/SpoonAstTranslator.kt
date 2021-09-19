package com.soarex16.iexporer.parser.spoon

import com.soarex16.iexporer.model.*
import com.soarex16.iexporer.parser.IEAstTranslator
import spoon.reflect.declaration.*
import spoon.reflect.reference.CtTypeReference
import spoon.reflect.visitor.CtScanner

/**
 * Транслятор SpoonAstTranslator в IEAst
 */
class SpoonAstTranslator : CtScanner(), IEAstTranslator<CtCompilationUnit> {
    private var declaredTypes: MutableList<IEClassDeclarationNode> = mutableListOf()

    /**
     * Мне не нравится, что визитор spoon-а (CtScanner) не позволяет ничего возвращать из методов,
     * это не очень удобно. Из-за этого приходится хранить состояние в классе,
     * что создает ряд проблем например, распараллелить будет трудно).
     */

    // Список методов текущего обходимого класса
    private var currentClassMethods: MutableList<IEMethodDeclarationNode> = mutableListOf()

    // Список аргументов текущего обходимого метода
    private var currentMethodArgs: MutableList<IEMethodArgumentNode> = mutableListOf()

    override fun translateToIEAst(root: CtCompilationUnit): IEAstNode {
        this.scan(root)
        return IECompilationUnitNode(root.file.path, root.declaredPackage.qualifiedName, declaredTypes)
    }

    override fun visitCtCompilationUnit(compilationUnit: CtCompilationUnit?) {
        super.visitCtCompilationUnit(compilationUnit)

        if (compilationUnit == null)
            return

        declaredTypes = mutableListOf()
        compilationUnit.declaredTypes.forEach { scan(it) }
    }

    override fun <T : Any?> visitCtClass(ctClass: CtClass<T>?) {
        super.visitCtClass(ctClass)
        if (ctClass == null)
            return

        val genericParams = ctClass.formalCtTypeParameters.map { it.simpleName }

        this.currentClassMethods = mutableListOf()
        ctClass.methods.forEach { visitCtMethod(it) }

        val fullName = ctClass.qualifiedName
        val simpleName = ctClass.simpleName

        val clazz = IEClassDeclarationNode(fullName, simpleName, this.currentClassMethods, genericParams)
        declaredTypes.add(clazz)
    }

    override fun <T : Any?> visitCtMethod(ctMethod: CtMethod<T>?) {
        super.visitCtMethod(ctMethod)
        if (ctMethod == null)
            return

        this.currentMethodArgs = mutableListOf()
        ctMethod.parameters.forEach { visitCtParameter(it) }

        val genericParams = ctMethod.formalCtTypeParameters.map { it.simpleName }

        val modifiers: List<String> = ctMethod.modifiers.map { it.name.lowercase() }

        val returnType = createTypeNode(ctMethod.type)

        val meth =
            IEMethodDeclarationNode(modifiers, returnType, ctMethod.simpleName, this.currentMethodArgs, genericParams)
        currentClassMethods.add(meth)
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
    private fun <T: Any?> createTypeNode(ctType: CtTypeReference<T>): IETypeNode {
        val genericParams: List<String> =
            if (ctType.isGenerics)
                listOf(ctType.simpleName)
            else
                ctType.actualTypeArguments.map { it.simpleName }

        return IETypeNode(ctType.qualifiedName, genericParams)
    }
}