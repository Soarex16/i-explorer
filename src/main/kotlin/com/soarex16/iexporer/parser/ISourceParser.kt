package com.soarex16.iexporer.parser

import com.soarex16.iexporer.model.IEAstNode
import java.io.File
import java.io.InputStream

interface ISourceParser {
    fun parseString(string: String): IEAstNode
    fun parseInputStream(stream: InputStream): IEAstNode
    fun parseFile(file: File): IEAstNode
}