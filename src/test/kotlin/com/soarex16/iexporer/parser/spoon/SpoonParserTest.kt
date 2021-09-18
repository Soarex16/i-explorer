package com.soarex16.iexporer.parser.spoon

import com.soarex16.iexporer.model.ParserException
import com.soarex16.iexporer.parser.ISourceParser
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows

internal class SpoonParserTest {

    private lateinit var parser: ISourceParser;

    @BeforeEach
    fun init() {
        parser = SpoonParser()
    }

    @Test
    fun parseString_empty() {
        assertThrows<ParserException> {
            val ieAst = parser.parseString("")
        }
    }
}