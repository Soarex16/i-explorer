package com.soarex16.iexporer.parser

import com.soarex16.iexporer.model.IEAstNode

/**
 * Интерфейс для трансформеров в IEAst
 */
interface IEAstTranslator<in RootType> {
    fun translateToIEAst(root: RootType): IEAstNode
}