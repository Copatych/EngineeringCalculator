package calculator

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class TokenTest {

    @Test
    fun getAbbreviation() {
        assertEquals(Token.Abbreviation.F, Token("sin").abbreviation)
        assertEquals(Token.Abbreviation.F, Token("sin123").abbreviation)
        assertEquals(Token.Abbreviation.N, Token("12.3").abbreviation)
        assertEquals(Token.Abbreviation.N, Token("3.").abbreviation)
        assertEquals(Token.Abbreviation.N, Token("-3.").abbreviation)
        assertEquals(Token.Abbreviation.N, Token("-3.12321E15").abbreviation)
        assertEquals(Token.Abbreviation.N, Token("-3.12321E-15").abbreviation)
    }
}