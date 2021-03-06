package com.soarex16.iexporer.parser.spoon

import com.soarex16.iexporer.model.IEAstNode
import com.soarex16.iexporer.model.ParserException
import com.soarex16.iexporer.parser.ISourceParser
import com.soarex16.iexporer.parser.spoon.adapter.SpoonAstTranslator
import org.apache.commons.io.FileUtils.copyInputStreamToFile
import spoon.Launcher
import spoon.compiler.SpoonResource
import spoon.reflect.declaration.CtCompilationUnit
import spoon.support.compiler.FileSystemFile
import java.io.File
import java.io.InputStream
import kotlin.io.path.createTempDirectory

class SpoonParser : ISourceParser {
    private val tempPath = createTempDirectory(prefix = "spoonTmp").also { it.toFile().deleteOnExit() }
    private val suffix = ".java"

    override fun parseString(string: String): IEAstNode {
        val tempFile = createTempFile()
        tempFile.writeText(string)

        return parseFile(tempFile)
    }

    override fun parseInputStream(stream: InputStream): IEAstNode {
        val tempFile = createTempFile()
        copyInputStreamToFile(stream, tempFile)

        return parseFile(tempFile)
    }

    override fun parseFile(file: File): IEAstNode {
        return parse(FileSystemFile(file), file.path)
    }

    private fun parse(input: SpoonResource, path: String): IEAstNode = try {
        val launcher = Launcher()
        launcher.addInputResource(input)
        launcher.buildModel()

        val parsedUnits = launcher.factory.CompilationUnit().map

        check(parsedUnits.isNotEmpty()) { "Specified file does not contain any type declaration" }

        val cu = launcher.factory.CompilationUnit().map.values.first()

        transformToIEAst(cu)
    } catch (e: Exception) {
        throw ParserException(path, e)
    }

    private fun createTempFile(): File = kotlin.io.path.createTempFile(suffix = suffix, directory = tempPath).toFile()

    private fun transformToIEAst(compilationUnit: CtCompilationUnit): IEAstNode {
        val translator = SpoonAstTranslator()

        return translator.translateToIEAst(compilationUnit)
    }
}