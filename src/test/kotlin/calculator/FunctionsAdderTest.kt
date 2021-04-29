package calculator

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class FunctionsAdderTest {

    @Test
    fun doProcessingJustRenaming() {
        val funcAdder = FunctionsAdder()
        funcAdder.registerFunction("abc", "qwerty")
        assertEquals(Lexer("sumabc").tokens, funcAdder.doProcessing(Lexer("sumabc").tokens))
        assertEquals(Lexer("sum+qwerty").tokens, funcAdder.doProcessing(Lexer("sum+abc").tokens))
        funcAdder.registerFunction("qwerty", "sum")
        assertEquals(Lexer("sum+sum").tokens, funcAdder.doProcessing(Lexer("sum+abc").tokens))
    }
}