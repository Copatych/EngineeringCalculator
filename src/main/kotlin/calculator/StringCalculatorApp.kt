package calculator

class StringCalculatorApp(
    private val tokensPreprocessors: MutableList<TokenPreprocessor> = mutableListOf(),
    private val functionsDirector: FunctionsDirector = FunctionsDirectorInstance().funcDirector,
    private val operationsDirector: OperationsDirector = OperationsDirectorInstance().opDirector
) {
    private val calculatorEngine = CalculatorEngine(functionsDirector, operationsDirector)

    init {
        //TODO Serialisation of FunctionsAdder
        tokensPreprocessors.add(FunctionsAdder())
    }

    /* TODO Invent and implement string sequences for adding functions in FunctionsAdder
       and getting info about FunctionsAdder, functionsDirector, operationsDirector */
    fun process(expr: String): String {
        try {
            val lexer = Lexer(expr)
            // TODO My Exceptions or rewrite lexer for checking on correctness immediately
            if (!lexer.isCorrect()) throw Exception("Invalid sequence")
            var tokens: List<Token> = lexer.tokens
            for (tp in tokensPreprocessors) {
                tokens = tp.doProcessing(tokens)
            }
            // TODO My Exceptions
            return (calculatorEngine.calculate(tokens) ?: throw Exception("Returned null")).toString()
        } catch (e: Exception) {
            return e.toString()
        }
    }
}