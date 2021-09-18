package com.soarex16.iexporer.model

interface IMethodFilter {
    fun matches(method: IEMethodDeclarationNode): Boolean
}