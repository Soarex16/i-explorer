package com.soarex16.iexporer.emitter

import com.soarex16.iexporer.model.InterfaceDeclaration

/**
 * TODO:
 *  что можно добавить
 *  namingConvention для преобразования имен методов и аргументов
 *  преобразование типов (для типизированных типов актуально)
 */
data class EmitParams(val name: String)

// TODO: подумать над тем, чтобы разделить эмиттинг параметров, имен типов и т.д.
interface IInterfaceEmitter {
    fun emit(iface: InterfaceDeclaration): String
}