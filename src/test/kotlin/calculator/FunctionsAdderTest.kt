package calculator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class FunctionsAdderTest {

    @Test
    fun doProcessingJustRenaming() {
        val funcAdder = FunctionsAdder()
        assertEquals(Lexer("1 + 2 + 3").tokens, funcAdder.doProcessing(Lexer("1 + 2 + 3").tokens))
        funcAdder.registerFunction("abc", "qwerty")
        assertEquals(Lexer("sumabc").tokens, funcAdder.doProcessing(Lexer("sumabc").tokens))
        assertEquals(Lexer("sum+qwerty").tokens, funcAdder.doProcessing(Lexer("sum+abc").tokens))
        funcAdder.registerFunction("qwerty", "sum")
        assertEquals(Lexer("sum+sum").tokens, funcAdder.doProcessing(Lexer("sum+abc").tokens))
    }

    @TestFactory
    fun doProcessingFuncWithArguments() : Collection<DynamicTest> {
        val funcAdder = FunctionsAdder()
        funcAdder.registerFunction("F", "sum([0-3]) - [4]")
        data class Tested(val expected: String, val actual: String)
        val t = arrayOf(
            Tested("sum(1;2;3;4) - 5", "F(1;2;3;4;5)"),
            Tested("1 + sum(1;2;3;4) - 5", "1 + F(1;2;3;4;5)"),
            Tested("1 + sum(1;2;3;4) - 5", "1 + F(1;2;3;4;5;6;7)"),
            Tested("1 + sum(sin(pi);2;3;4) - 5", "1 + F(sin(pi);2;3;4;5)"),
            Tested("1 + sum(1;sin(pi);3;4) - 5", "1 + F(1;sin(pi);3;4;5)"),
            Tested("1 + sin(sum(1;2;3;4) - 5)", "1 + sin(F(1;2;3;4;5))"),
        )
        return t.map { dynamicTest(it.actual) { assertEquals(Lexer(it.expected).tokens, Lexer(it.actual).tokens)} }
    }
}