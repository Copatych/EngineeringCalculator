package calculator

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.function.Executable

internal class TokenTest {

    @Test
    fun getAbbreviation() {
        assertAll(
            Executable { assertEquals(Token.Abbreviation.F, Token("sin").abbreviation) },
            Executable { assertEquals(Token.Abbreviation.F, Token("sin123").abbreviation) },
            Executable { assertEquals(Token.Abbreviation.N, Token("12.3").abbreviation) },
            Executable { assertEquals(Token.Abbreviation.N, Token("3.").abbreviation) },
            Executable { assertEquals(Token.Abbreviation.N, Token("-3.").abbreviation) },
            Executable { assertEquals(Token.Abbreviation.N, Token("-3.12321E15").abbreviation) },
            Executable { assertEquals(Token.Abbreviation.N, Token("-3.12321E-15").abbreviation) }
        )
    }
}