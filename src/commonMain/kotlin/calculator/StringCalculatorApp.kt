package calculator

class StringCalculatorApp(
    private val functionsAdder: FunctionsAdder = FunctionsAdder(),
    private val tokensPreprocessors: MutableList<TokenPreprocessor> = mutableListOf(),
    private val functionsDirector: FunctionsDirector = FunctionsDirectorInstance().funcDirector,
    private val operationsDirector: OperationsDirector = OperationsDirectorInstance().opDirector
) {
    private val calculatorEngine = CalculatorEngine(functionsDirector, operationsDirector)

    companion object {
        const val startOfControlSequence = "!!!>>"
        val helloInfo =
            "Hi. This is a powerful calculator, that can process expressions like \"round(13 / sin(pi / 2) + 1)\".\n" +
                    "If you want to see all the functions and operations or register your own function, use \"$startOfControlSequence " +
                    "control_sequence\", where \"control_sequence\" is one of these:\n" + ControlSeq.printControlSequences() +
                    "\nFor more information, see the documentation."
    }

    init {
        //TODO Serialisation of FunctionsAdder
        tokensPreprocessors.add(functionsAdder)
    }

    /* TODO Invent and implement string sequences for adding functions in FunctionsAdder
       and getting info about FunctionsAdder, functionsDirector, operationsDirector */
    fun process(expr: String): String {
        if (expr.startsWith(startOfControlSequence)) {
            return processControlSequence(expr.substring(startOfControlSequence.length))
        }
        val lexer = Lexer(expr)
        // TODO My Exceptions or rewrite lexer for checking on correctness immediately
        if (!lexer.isCorrect()) {
            // TODO My Exceptions
            throw Exception("Error for symbol '${expr[lexer.erroredIndex!!]}' in position ${lexer.erroredIndex}")
        }
        var tokens: List<Token> = lexer.tokens
        for (tp in tokensPreprocessors) {
            tokens = tp.doProcessing(tokens)
        }
        // TODO My Exceptions
        return (calculatorEngine.calculate(tokens) ?: throw Exception("Returned null")).toString()
    }

    fun main(print: (String)-> Unit, read: () -> String?) {
        print(helloInfo)
        while (true) {
            try {
                val expr = read() ?: continue
                print(process(expr))
            } catch (e: Exception) {
                print(e.message.toString())
            }
        }
    }

    enum class ControlSeq(val v: String) {
        SystemFunctions("system functions"),
        SystemOperations("system operations"),
        FunctionsNames("functions names"),
        FunctionsDescription("functions description"),
        RegisterFunction("register function") {
            override val printedInfo = v + " 'name' " +
                    ControlSeq.registerFunctionSep + " 'description' " + "[" + ControlSeq.registerFunctionSep +
                    " 'comment']"
        },
        UndefinedSeq("");

        protected open val printedInfo = v

        companion object {
            fun recognize(s: String): ControlSeq {
                for (cs in ControlSeq.values()) {
                    if (s.startsWith(cs.v)) return cs
                }
                return UndefinedSeq
            }

            const val registerFunctionSep = "!!!>>"

            fun printControlSequences(): String {
                return ControlSeq.values().fold("")
                { acc, controlSeq -> acc + "\n" + controlSeq.printedInfo }.substring(1)
            }
        }
    }

    private fun processControlSequence(s: String): String {
        // Change all space sequences to one space
        val sCorrected = s.trim().split(Regex("""\s+""")).joinToString(" ")
        return when (ControlSeq.recognize(sCorrected)) {
            ControlSeq.SystemFunctions -> functionsDirector.getRegisteredFunctions()
            ControlSeq.SystemOperations -> operationsDirector.getRegisteredOperations()
            ControlSeq.FunctionsNames -> functionsAdder.getFunctionsNames().joinToString("\n")
            ControlSeq.FunctionsDescription -> functionsAdder.getFunctionsFullDescription().joinToString("\n")
            ControlSeq.RegisterFunction -> {
                val regFuncParts = sCorrected.substring(ControlSeq.RegisterFunction.v.length).trim()
                    .splitToSequence(ControlSeq.registerFunctionSep).toList()
                val (name, description, comment) = when (regFuncParts.size) {
                    2 -> regFuncParts + ""
                    3 -> regFuncParts
                    else -> {
                        // TODO My Exceptions
                        throw Exception("register function error")
                    }
                }
                functionsAdder.registerFunction(name, description, comment)
                "The function is registered"
            }
            ControlSeq.UndefinedSeq -> {
                // TODO My Exceptions
                throw Exception("Undefined control sequence")
            }
        }
    }

}