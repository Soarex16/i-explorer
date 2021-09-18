package com.soarex16.iexporer.filters

import com.soarex16.iexporer.model.IEMethodDeclarationNode
import com.soarex16.iexporer.model.IMethodFilter
import com.soarex16.iexporer.model.MethodVisibility

class BlacklistNameMethodFilter(private val blacklist: Collection<String>): IMethodFilter {
    override fun matches(method: IEMethodDeclarationNode) = method.simpleName !in blacklist
}

class WhitelistNameMethodFilter(private val whitelist: Collection<String>): IMethodFilter {
    override fun matches(method: IEMethodDeclarationNode) = method.simpleName in whitelist
}

class MethodVisibilityFilter(private val visibilityModifiers: Collection<MethodVisibility>): IMethodFilter {
    // TODO: проверить package private
    override fun matches(method: IEMethodDeclarationNode) = method.modifiers.intersect(visibilityModifiers).any()
}