package calculator

interface TokenPreprocessor {
    fun doProcessing(s: List<Token>) : List<Token>
}