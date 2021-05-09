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
    fun doProcessingFuncWithArguments(): Collection<DynamicTest> {
        val funcAdder = FunctionsAdder()
        funcAdder.registerFunction("F", "sum([0-3]) - [4]")
        data class Tested(val expected: String, val actual: String)

        val t = arrayOf(
            Tested("sum(1;2;3;4) - 5", "F(1;2;3;4;5)"),
            Tested("1 + sum(1;2;3;4) - 5", "1 + F(1;2;3;4;5)"),
            Tested("sum(1;2;3;4) - 5 + 1", "F(1;2;3;4;5)+1"),
            Tested("1 + sum(1;2;3;4) - 5", "1 + F(1;2;3;4;5;6;7)"),
            Tested("sum(sin(pi);2;3;4) - 5", "F(sin(pi);2;3;4;5)"),
            Tested("1 + sum(sin(pi);2;3;4) - 5", "1 + F(sin(pi);2;3;4;5)"),
            Tested("1 + sum(1;sin(pi);3;4) - 5", "1 + F(1;sin(pi);3;4;5)"),
            Tested("1 + sin(sum(1;2;3;4) - 5)", "1 + sin(F(1;2;3;4;5))")
        )
        return t.map {
            dynamicTest(it.actual) {
                assertEquals(Lexer(it.expected).tokens, funcAdder.doProcessing(Lexer(it.actual).tokens))
            }
        }
    }

    @Test
    fun getFunctionsFullDescription() {
        val funcAdder = FunctionsAdder()
        assertEquals(arrayListOf<FunctionsAdder.FunctionFullDescription>(), funcAdder.getFunctionsFullDescription())
        funcAdder.registerFunction("F", "abc")
        assertEquals(
            arrayListOf(FunctionsAdder.FunctionFullDescription("F", "abc")),
            funcAdder.getFunctionsFullDescription()
        )
        funcAdder.registerFunction("F1", "abc12", "comment")
        assertEquals(
            arrayListOf(
                FunctionsAdder.FunctionFullDescription("F", "abc"),
                FunctionsAdder.FunctionFullDescription("F1", "abc12", "comment")
            ),
            funcAdder.getFunctionsFullDescription()
        )
    }

    @Test
    fun serialize() {
        val funcAdder = FunctionsAdder()
        assertEquals("[]", funcAdder.serialize())
        funcAdder.registerFunction("F", "abc")
        assertEquals("[{\"name\":\"F\",\"description\":\"abc\"}]", funcAdder.serialize())
        funcAdder.registerFunction("F1", "abc12", "comment")
        assertEquals(
            "[{\"name\":\"F\",\"description\":\"abc\"}," +
                    "{\"name\":\"F1\",\"description\":\"abc12\",\"comment\":\"comment\"}]",
            funcAdder.serialize()
        )
    }

    @Test
    fun deserialize() {
        val funcAdder = FunctionsAdder()
        assertEquals(
            funcAdder.getFunctionsFullDescription(),
            FunctionsAdder.deserialize("[]").getFunctionsFullDescription()
        )
        funcAdder.registerFunction("F", "abc")
        assertEquals(
            funcAdder.getFunctionsFullDescription(),
            FunctionsAdder.deserialize("[{\"name\":\"F\",\"description\":\"abc\"}]").getFunctionsFullDescription()
        )
        funcAdder.registerFunction("F1", "abc12", "comment")
        assertEquals(
            funcAdder.getFunctionsFullDescription(),
            FunctionsAdder.deserialize(
                "[{\"name\":\"F\",\"description\":\"abc\"}," +
                        "{\"name\":\"F1\",\"description\":\"abc12\",\"comment\":\"comment\"}]"
            ).getFunctionsFullDescription()
        )
    }

    @Test
    fun serializeAndDeserialize() {
        val funcAdder = FunctionsAdder()
        funcAdder.registerFunction("F", "abc")
        funcAdder.registerFunction("F1", "abc12", "comment")
        val s = funcAdder.serialize()
        val funcAdderDeserialized = FunctionsAdder.deserialize(s)
        assertEquals(funcAdder.getFunctionsFullDescription(), funcAdderDeserialized.getFunctionsFullDescription())
    }
}