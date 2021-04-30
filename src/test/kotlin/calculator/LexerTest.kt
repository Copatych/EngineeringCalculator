package calculator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory


internal class LexerTest {
    data class TestedData(
        val string: String, val tokensValues: List<String>?,
        val tokensMap: List<Token.Abbreviation?>?, val isCorrect: Boolean,
        val erroredIndex: Int?, val comment: String? = null
    )

    private val tdArray: Array<TestedData> = arrayOf(
//        TestedData("-3",
//            listOf("-3"),
//            stringToListOfAbbrev("n"),
//            true, null,
//            "Negative number"),
        TestedData(
            "1+2",
            listOf("1", "+", "2"),
            stringToListOfAbbrev("non"),
            true, null,
            "Simple correct example"
        ),
        TestedData(
            "1 + 2",
            listOf("1", "+", "2"),
            stringToListOfAbbrev("non"),
            true, null,
            "Simple correct example"
        ),
        TestedData(
            "3**2 / (sin(3.14) + 1,2)",
            listOf("3", "**", "2", "/", "(", "sin", "(", "3.14", ")", "+", "1,2", ")"),
            stringToListOfAbbrev("nonosfsnsons"),
            true, null,
            "Complex correct example"
        ),
        TestedData(
            "3 № 1",
            null,
            null,
            false, 2,
            "Non-existent operation"
        ),
        TestedData(
            "3 sin123 + 1", listOf("3", "sin123", "+", "1"),
            stringToListOfAbbrev("nfon"),
            false, 2,
            "Function after number"
        ),
        TestedData(
            "sin 3 + 1",
            listOf("sin", "3", "+", "1"),
            stringToListOfAbbrev("fnon"),
            false, 4,
            "Number after function"
        )
    )

    private fun stringToListOfAbbrev(string: String): List<Token.Abbreviation> {
        return string.map {
            when (it) {
                'n' -> Token.Abbreviation.N
                'f' -> Token.Abbreviation.F
                'o' -> Token.Abbreviation.O
                's' -> Token.Abbreviation.S
                // TODO my exceptions for TokenAbbreviation
                else -> throw Exception(
                    "it is not possible to associate" +
                            " char \"$it\" with an TokenAbbreviation"
                )
            }
        }
    }

    @TestFactory
    fun checkWithExpected(): Collection<DynamicTest> {
        return tdArray.map {
            dynamicTest(it.comment) {
                val lexer = Lexer(it.string)
                val actualTokensValues = lexer.tokens.map { it.value }
                val actualTokensAbbrev = lexer.tokens.map { it.abbreviation }
                if (it.tokensValues != null) {
                    assertEquals(
                        it.tokensValues, actualTokensValues,
                        "${it}\nComment: ${it.comment}\nError in \"tokens\"\n"
                    )
                }
                if (it.tokensMap != null) {
                    assertEquals(
                        it.tokensMap, actualTokensAbbrev,
                        "${it}\nComment: ${it.comment}\nError in \"tokensMap\"\n"
                    )
                }
                assertEquals(
                    it.isCorrect, lexer.isCorrect(),
                    "${it}\nComment: ${it.comment}\nError in \"isCorrect\"\n"
                )
                assertEquals(
                    it.erroredIndex, lexer.erroredIndex,
                    "${it}\nComment: ${it.comment}\nError in \"erroredIndex\"\n"
                )
            }
        }
    }
}
