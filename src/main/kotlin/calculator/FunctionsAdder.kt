package calculator
class FunctionsAdder : TokenPreprocessor {
    override fun doProcessing(s: List<Token>): List<Token> {
        val sInternal = s.toMutableList()
        for (f in functions) {
            var i = DelegatingMutableListIterator(sInternal.listIterator())
            while (i.hasNext()) {
                var curValue = i.next()
                if (curValue == Token(f.name)) {
                    if (!i.hasNext() || (i.hasNext() && i.get(1) != Token("("))) {
                        i.remove()
                        val replacedSeq = f.process(listOf())
                        for (t in replacedSeq) i.add(t)
                    }
                }
            }
        }
        return sInternal
    }
    private data class Function(val name: String, val description: List<Token>) {
        private val subTokensSeq: List<List<Token>> = run{
            val separators = description.mapIndexedNotNull {
                    idx, t -> if (t == Token("[") || t == Token("]")) idx else null }
            var res = arrayListOf<List<Token>>()
            var fromSep = 0
            for (toSep in separators + listOf<Int>(description.size)) {
                res.add(description.subList(fromSep, toSep).filter { it != Token("[") && it != Token("]") })
                fromSep = toSep
            }
            res
        }

        fun process(args: List<List<Token>>) : List<Token> {
            val res = mutableListOf<Token>()
            for (tokensSeq in subTokensSeq) {
                res += if (tokensSeq[0].abbreviation == Token.Abbreviation.N) {
                    insertArgs(tokensSeq, args)
                } else {
                    tokensSeq
                }
            }
            return res
        }

        private fun insertArgs(s: List<Token>, args: List<List<Token>>) : List<Token> {
            val firstIndex: Int
            if (s.isEmpty() || s.size > 3 || s[0].abbreviation != Token.Abbreviation.N) {
                // TODO My Exception
                throw Exception("Internal error in FunctionsAdder.Function.insertArgs")
            } else {
                firstIndex = s[0].value.toInt()
            }
            if (s.size == 1) {
                return args[firstIndex]
            }
            if (s[1] != Token("-") || (s.size == 3 && s[2].abbreviation != Token.Abbreviation.N)) {
                // TODO My Exception
                throw Exception("Internal error in FunctionsAdder.Function.insertArgs")
            }
            val res = mutableListOf<Token>()
            val lastIndex = if (s.size == 2) (args.size - 1) else s[2].value.toInt()
            for (i in firstIndex..lastIndex) {
                res += args[i] + Token(";")
            }
            return  res.dropLast(1)
        }
    }

    private val functions: ArrayList<Function> = arrayListOf()

    fun registerFunction(name: String, description: String) {
        /**
         * Examples:
         * registerFunction("renamed_sin", "sin") // just renaming
         * registerFunction("modified_sin", "sin([0] + pi)")
         * registerFunction("easy_sum", "[0] + [1]")
         * registerFunction("add", "sum([0-])")
         * registerFunction("F", "sum([0-4]) - [5]")
         * registerFunction("+", "-") // Bad idea
         */
        val lexer = Lexer(description)
        if (lexer.isCorrect()) {
            functions.add(Function(name, lexer.tokens))
        }
    }

    fun getFunctionsWithPosition() : List<Pair<Int, String>> {
        return functions.mapIndexed { index, f -> index to f.name }
    }
}