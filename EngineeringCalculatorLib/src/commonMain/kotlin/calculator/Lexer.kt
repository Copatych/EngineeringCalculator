package calculator

class Lexer(private val expression: String) {

    val tokens: List<Token> = run {
        val matchedTokens = Regex(Token.RegexStr.allTokens).findAll(expression)
        var t = matchedTokens.map { Token(it.value) }.toMutableList()
        t.toList()
    }

    // lazy evaluation
    private var isCorrectVar: Boolean? = null

    var erroredIndex: Int? = null
        private set


    fun isCorrect(): Boolean {
        val regexCheckCorrectness = """${Token.RegexStr.allTokens}|\s+"""
        val localIsCorrectVar = isCorrectVar
        if (localIsCorrectVar == null) {
            val matchedTokensAndSpaces = Regex(regexCheckCorrectness).findAll(expression)
            // [0..1, 2..3, 4..4, 6..6, 8..11] - ranges example.
            val ranges = matchedTokensAndSpaces.map { it.range }.toList()
            var unionRange: Set<Int> = setOf()
            ranges.map { unionRange = unionRange.union(it) }
            for ((i, j) in unionRange.withIndex()) {
                if (i != j) {
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