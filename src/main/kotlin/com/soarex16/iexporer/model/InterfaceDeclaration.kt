package com.soarex16.iexporer.model

data class InterfaceDeclaration(val name: String, val unit: IECompilationUnitNode, val sourceClass: IEClassDeclarationNode, val methods: List<IEMethodDeclarationNode>)