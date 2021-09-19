package com.soarex16.iexporer.emitter.python

import com.soarex16.iexporer.emitter.IInterfaceEmitter
import com.soarex16.iexporer.model.InterfaceDeclaration

/**
 * Простой генератор интерфейсов для питона, который просто формирует файла по шаблону
 */
class SimplePythonEmitter : IInterfaceEmitter {
    override fun emit(iface: InterfaceDeclaration): String {
        val headerTpl = "from abc import ABCMeta, abstractmethod\n\n" +
                "class ${iface.name}:\n" +
                "\t__metaclass__ = ABCMeta\n"

        val methods = iface.methods.map { methodDecl ->
            val paramsList = listOf("self") + methodDecl.arguments.map { param -> param.name }

            val paramsString = paramsList.joinToString(", ")

            "\t@classmethod\n\t@abstractmethod\n" +
                    "\tdef ${methodDecl.simpleName}($paramsString):\n" +
                    "\t\traise NotImplementedError"
        }

        val sb = StringBuilder(headerTpl)

        methods.forEach { method ->
            sb.append("\t\n")
            sb.appendLine(method)
        }

        return sb.toString()
    }
}