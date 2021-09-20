package com.soarex16.iexporer.parser.spoon.adapter

import com.soarex16.iexporer.model.IECompilationUnitNode
import com.soarex16.iexporer.parser.IEAstTranslator
import spoon.reflect.declaration.CtClass
import spoon.reflect.declaration.CtCompilationUnit
import spoon.reflect.visitor.CtScanner

/**
 * Транслятор spoon CtNode в IEAst
 * NOTE: передал с визитора на "ручное" прохождение по причине того, что spoon в какой-то момент
 * начинает криво обходить дерево (т. е. не в глубину)
 */
class SpoonAstTranslator : CtScanner(), IEAstTranslator<CtCompilationUnit, IECompilationUnitNode> {
    override fun translateToIEAst(root: CtCompilationUnit): IECompilationUnitNode {
        val classTranslator = SpoonClassTranslator()

        val transformedClasses = root.declaredTypes.map { classTranslator.translateToIEAst(it as CtClass<*>) }

        return IECompilationUnitNode(root.file.path, root.declaredPackage.qualifiedName, transformedClasses)
    }
}