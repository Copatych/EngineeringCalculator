package calculator

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*


internal class LexerTest {
    data class TestedData(val string: String, val tokensValues: List<String>?,
                          val tokensMap: List<Token.Abbreviation?>?, val isCorrect: Boolean,
                          val erroredIndex: Int?, val comment: String? = null)
    private val tdArray: Array<TestedData> = arrayOf(
//        TestedData("-3",
//            listOf("-3"),
//            stringToListOfAbbrev("n"),
//            true, null,
//            "Negative number"),
        TestedData("1+2",
            listOf("1", "+", "2"),
            stringToListOfAbbrev("non"),
            true, null,
            "Simple correct example"),
        TestedData("1 + 2",
            listOf("1", "+", "2"),
            stringToListOfAbbrev("non"),
            true, null,
            "Simple correct example"),
        TestedData("3**2 / (sin(3.14) + 1,2)",
            listOf("3", "**", "2", "/", "(", "sin", "(", "3.14", ")", "+", "1,2", ")"),
            stringToListOfAbbrev("nonosfsnsons"),
            true, null,
            "Complex correct example"),
        TestedData("3 № 1",
            null,
            null,
            false, 2,
            "Non-existent operation"),
        TestedData("3 sin123 + 1", listOf("3", "sin123", "+", "1"),
            stringToListOfAbbrev("nfon"),
            false, 2,
            "Function after number"),
        TestedData("sin 3 + 1",
            listOf("sin", "3", "+", "1"),
            stringToListOfAbbrev("fnon"),
            false, 4,
            "Number after function")
    )

    fun stringToListOfAbbrev(string: String) : List<Token.Abbreviation> {
        return string.map { when(it) {
            'n' -> Token.Abbreviation.N
            'f' -> Token.Abbreviation.F
            'o' -> Token.Abbreviation.O
            's' -> Token.Abbreviation.S
            // TODO my exceptions for TokenAbbreviation
            else -> throw Exception("it is not possible to associate" +
                    " char \"$it\" with an TokenAbbreviation")
        } }
    }

    @Test
    fun checkWithExpected() {
        for((i, tdElem) in tdArray.withIndex()) {
            val lexer = Lexer(tdElem.string)
            val actualTokensValues = lexer.tokens.map { it.value }
            val actualTokensAbbrev = lexer.tokens.map { it.abbreviation }
            if(tdElem.tokensValues != null) {
                assertEquals(tdElem.tokensValues, actualTokensValues,
                    "$i / ${tdArray.size}, ${tdElem.toString()}\n" +
                            "Comment: ${tdElem.comment}\nError in \"tokens\"\n")
            }
            if(tdElem.tokensMap != null) {
                assertEquals(tdElem.tokensMap, actualTokensAbbrev,
                    "$i / ${tdArray.size}, ${tdElem.toString()}\n" +
                            "Comment: ${tdElem.comment}\nError in \"tokensMap\"\n")
            }
            assertEquals(tdElem.isCorrect, lexer.isCorrect(),
                "$i / ${tdArray.size}, ${tdElem.toString()}\n" +
                        "Comment: ${tdElem.comment}\nError in \"isCorrect\"\n")
            assertEquals(tdElem.erroredIndex, lexer.erroredIndex,
                "$i / ${tdArray.size}, ${tdElem.toString()}\n" +
                        "Comment: ${tdElem.comment}\nError in \"erroredIndex\"\n")
        }
    }
}
