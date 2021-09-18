package com.soarex16.iexporer.model

class ParserException(val compilationUnit: String, val innerException: Exception) : Exception(innerException)