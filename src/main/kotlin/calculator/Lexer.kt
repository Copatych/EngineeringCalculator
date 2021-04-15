package calculator

class Lexer(private val string: String) {
    enum class TokenAbbreviation {
        N, // Number
        F, // Function
        O,  // Operation
        S; // Spec_symbol
        companion object {
            fun stringToListOfAbbrev(string: String) : List<TokenAbbreviation> {
                return string.map { when(it) {
                    'n' -> N
                    'f' -> F
                    'o' -> O
                    's' -> S
                    // TODO my exceptions for TokenAbbreviation
                    else -> throw Exception("it is not possible to associate" +
                            " char \"$it\" with an TokenAbbreviation")
                } }
            }
        }
    }
    enum class TokenRecognizer(val regexStr: String, val abbreviation: TokenAbbreviation) {
        NUMBER("""(\d+([,\.]\d+)?)""",                  TokenAbbreviation.N),
        FUNCTION("""([a-zA-Z]\w*)""",                   TokenAbbreviation.F),
        OPERATION("""([!${'$'}%^&*\-+=?<>\\|/]+)""",    TokenAbbreviation.O),
        SPEC_SYMB("""\(|\)|;""",                        TokenAbbreviation.S);

        companion object {
            val regexAllTokens: String = run {
                var res = ""
                TokenRecognizer.values().map { res += (it.regexStr + "|") }
                res.dropLast(1) // Last character is "|", and it is not needed
            }

            fun recognizeByFirstSymb(string: String) : TokenAbbreviation? {
                val c = string[0].toString()
                for(t in TokenRecognizer.values()) {
                    if (Regex(t.regexStr).matches(c)) {
                        return t.abbreviation
                    }
                }
                return null
            }

            fun createTokensMap(tokens: List<String>) : List<TokenAbbreviation> {
                // TODO my exceptions for TokenRecognizer.createTokensMap
                return tokens.map{ recognizeByFirstSymb(it) ?:
                            throw Exception("Can't recognise token")}
            }
        }
    }

    val regexAllTokens = TokenRecognizer.regexAllTokens
    val regexCheckCorrectness = """$regexAllTokens|\s+"""

    val tokens : List<String>
    init {
        val matchedTokens = Regex(regexAllTokens).findAll(string)
        tokens = matchedTokens.map { it.value }.toList()
    }

    val tokensMap: List<TokenAbbreviation> = TokenRecognizer.createTokensMap(tokens)

    // lazy evaluation
    private var isCorrectVar : Boolean? = null

    var erroredIndex: Int? = null
        private set

    fun isCorrect() : Boolean {
        val localIsCorrectVar = isCorrectVar
        if(localIsCorrectVar == null) {
            val matchedTokensAndSpaces = Regex(regexCheckCorrectness).findAll(string)
            // [0..1, 2..3, 4..4, 6..6, 8..11] - ranges example.
            val ranges = matchedTokensAndSpaces.map { it.range }.toList()
            var unionRange: Set<Int> = setOf()
            ranges.map { unionRange = unionRange.union(it) }
            for((i, j) in unionRange.withIndex()) {
                if(i != j) {
                    erroredIndex = i
                    isCorrectVar = false
                    return false
                }
            }
        } else {
            return localIsCorrectVar
        }
        isCorrectVar = true
        return true
    }

}