package calculator

import java.lang.Exception

data class Token(val value: String) {
    enum class Abbreviation {
        N, // Number
        F, // Function
        O,  // Operation
        S; // Spec_symbol
    }

    val abbreviation: Abbreviation = recognize(value)

    enum class RegexStr(val regexStr: String, val abbreviation: Abbreviation) {
        NUMBER("""(\d+([,\.]\d*(E-?\d\d?)?)?)""", Abbreviation.N),
        FUNCTION("""([a-zA-Z]\w*)""", Abbreviation.F),
        OPERATION("""([!${'$'}%^&*\-+=?<>\\|/]+)""", Abbreviation.O),
        SPEC_SYMB("""(\(|\)|;|\[|\])""", Abbreviation.S);
        companion object {
            val allTokens: String = run {
                var res = ""
                RegexStr.values().map { res += (it.regexStr + "|") }
                res.dropLast(1) // Last character is "|", and it is not needed
            }
        }
    }

    private fun recognize(s: String) : Abbreviation {
        /**
         * This function do recognition by first symbol.
         * It also take to account negative numbers.
         */
        val c = (if ((s[0] == '-') and (s.length != 1)) s[1] else s[0]).toString()
        for(t in RegexStr.values()) {
            if (Regex(t.regexStr).matches(c)) {
                return t.abbreviation
            }
        }
        throw Exception("Token class cannot recognize token \"${s}\"")
    }
}
