package com.soarex16.iexporer.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.options.unique
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.enum
import com.github.ajalt.clikt.parameters.types.file
import com.soarex16.iexporer.emitter.IInterfaceEmitter
import com.soarex16.iexporer.emitter.java.SpoonJavaEmitter
import com.soarex16.iexporer.emitter.python.SimplePythonEmitter
import com.soarex16.iexporer.filters.BlacklistNameMethodFilter
import com.soarex16.iexporer.filters.MethodVisibilityFilter
import com.soarex16.iexporer.filters.WhitelistNameMethodFilter
import com.soarex16.iexporer.model.*
import com.soarex16.iexporer.parser.ISourceParser
import com.soarex16.iexporer.parser.spoon.SpoonParser
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Paths

class InterfaceExtractorCommand : CliktCommand(printHelpOnEmptyArgs = true, name = "i-exporer") {
    private val logger = LoggerFactory.getLogger(javaClass)!!

    private val inputFile by option("-i", "--input", help = "Path to input source file (.java)")
        .file(canBeDir = false, mustExist = true, mustBeReadable = true)
        .required()

    private val outputFile by option(
        "-o",
        "--output",
        help = "Path where you want to save the result. pathToFile+classname+\"Interface\"+extension by default"
    )
        .file(canBeDir = false, mustBeWritable = true)

    private val inputClass by option(
        "-c",
        "--class",
        help = """
            Qualified class name to process. 
            Root class by default.
            Must be in this format: ClassA.ClassB
        """.trimIndent()
    )

    private val target by option("-t", "--target", help = "Target platform for code emitting")
        .choice("python", "java")
        .required()

    private val visibilityModifiers by option("-s", "--scope", help = "Method filter by visibility")
        .enum<MethodVisibility>()
        .multiple()
        .unique()

    private val interfaceName by option(
        "-n",
        "--name",
        help = "Target interface name. Classname + \"Interface\" by default"
    )

    private val methodBlacklist by option("--exclude", help = "Method blacklist")
        .multiple()
        .unique()

    private val methodWhitelist by option("--include", help = "Method whitelist")
        .multiple()
        .unique()

    override fun run() {
        val codeEmitter = getEmitter(target)
        val parser: ISourceParser = SpoonParser()
        val rootUnit = parser.parseFile(inputFile) as IECompilationUnitNode

        check(rootUnit.declaredTypes.isNotEmpty()) {
            "Specified file does not contain any class declaration. Try another file"
        }

        val selectedClass = selectClass(rootUnit, inputClass);

        val filters = createMethodFilter()
        val filteredMethods = applyMethodFilters(selectedClass.methods, filters)

        val targetInterfaceName = this.interfaceName ?: getDefaultInterfaceName(selectedClass)
        val interfaceDeclaration = InterfaceDeclaration(targetInterfaceName, rootUnit, selectedClass, filteredMethods)

        val emittedInterface = codeEmitter.emit(interfaceDeclaration)

        val outputFile = this.outputFile ?: getDefaultOutputFile(target, selectedClass.simpleName)
        saveInterfaceToFile(outputFile, emittedInterface)
    }

    /**
     * Формирует список фильтров для отбора методов
     * Внимание: функция не является чистой
     */
    private fun createMethodFilter(): List<IMethodFilter> {
        val filters = mutableListOf<IMethodFilter>()

        if (visibilityModifiers.any()) {
            filters.add(MethodVisibilityFilter(visibilityModifiers))
        }

        if (methodBlacklist.isNotEmpty()) {
            filters.add(BlacklistNameMethodFilter(methodBlacklist))
        }

        if (methodWhitelist.isNotEmpty()) {
            filters.add(WhitelistNameMethodFilter(methodBlacklist))
        }

        return filters
    }

    /**
     * Отбирает методы по заданным критериям
     */
    private fun applyMethodFilters(
        methods: List<IEMethodDeclarationNode>,
        filters: List<IMethodFilter>
    ): List<IEMethodDeclarationNode> = methods.filter { meth -> filters.any { it.matches(meth) } }

    /**
     * Осуществляет поиск класса по заданному пути (если он задан)
     * Если путь к классу не указан (или такой путь не существует), то будет выбран класс по-умолчанию
     */
    private fun selectClass(unit: IECompilationUnitNode, pathToClass: String?): IEClassDeclarationNode {
        if (pathToClass == null)
            return getDefaultClass(unit)

        val qualifiedName = "${unit.packageName}.${pathToClass.split(".").joinToString("$")}}"
        val findResult = unit.declaredTypes.find { it.qualifiedName == qualifiedName }

        if (findResult != null)
            return findResult

        logger.warn("Specified path to class $pathToClass can't be reached. Ensure correctness of the format and try again")
        return getDefaultClass(unit)
    }

    private fun getDefaultClass(unit: IECompilationUnitNode): IEClassDeclarationNode {
        // TODO: ensure that last class is default, because now it's only heuristic
        val clazz = unit.declaredTypes.last()
        logger.warn("Selecting default class ${clazz.simpleName}")

        return clazz
    }

    private fun getDefaultInterfaceName(selectedClass: IEClassDeclarationNode) = "${selectedClass.simpleName}Interface"

    private fun getEmitter(targetPlatform: String): IInterfaceEmitter = when (targetPlatform) {
        "python" -> SimplePythonEmitter()
        // TODO: починить эмиттер в java
        "java" -> throw RuntimeException("${SpoonJavaEmitter::javaClass.name} currently not working")//SpoonJavaEmitter()
        else -> throw IllegalArgumentException("Specified target $targetPlatform is not supported")
    }

    private fun getTargetFileExtension(targetPlatform: String): String = when (targetPlatform) {
        "python" -> ".py"
        "java" -> ".java"
        else -> ""
    }

    private fun getDefaultOutputFile(targetPlatform: String, className: String): File {
        val fileName = "${className}Interface${getTargetFileExtension(targetPlatform)}"
        val fileUri = Paths.get("").resolve(fileName).toUri()

        return File(fileUri)
    }

    private fun saveInterfaceToFile(outputFile: File, emittedInterface: String) {
        outputFile.writeText(emittedInterface)
    }
}