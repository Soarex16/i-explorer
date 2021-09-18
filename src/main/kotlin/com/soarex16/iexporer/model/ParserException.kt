package com.soarex16.iexporer.model

class ParserException(val compilationUnit: String, e: Exception) : Exception(e)