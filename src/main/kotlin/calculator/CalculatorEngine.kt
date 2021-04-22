package calculator
import calculator.Lexer.TokenAbbreviation

class CalculatorEngine(val functionsDirector: FunctionsDirector,
                       val operationsDirector: OperationsDirector, private val ansOld: Double? = null) {

    var ans: Double? = ansOld
        private set

    fun calculate(tokens: List<String>, tokensMap: List<TokenAbbreviation>) : Double? {
        val store = CalculatorableStore((tokensMap zip tokens).toMutableList())
        var curNumberOfIterations = 0
        var maxNumberOfIterations = tokens.size * 40
        while (store.nextStep()) {
            when (store.currentToken()) {
                TokenAbbreviation.F -> store.processF()
                TokenAbbreviation.O -> store.processO()
                TokenAbbreviation.N -> store.processN()
                TokenAbbreviation.S -> store.processS()
            }
            curNumberOfIterations++
            if (curNumberOfIterations > maxNumberOfIterations) {
                // TODO My Exceptions
                throw Exception("Most likely the execution is looped")
            }
        }
        if (store.result != null) {
            ans = store.result
        }
        return store.result
    }

    // See CalculatorEngine.calculate
    private inner class CalculatorableStore(val store: MutableList<Pair<TokenAbbreviation, String>>){
        val i = DelegatingMutableListIterator(store.listIterator())
        var doNext = false
        var pairCurrent = i.next()
        private var isEnd = false

        var result: Double? = null

        fun nextStep() : Boolean {
            if (isEnd) {
                return false
            }
            if (doNext) {
                pairCurrent = i.next()
            } else {
                pairCurrent = i.getCurrentValue()
            }
            return true
        }

        fun currentToken() : TokenAbbreviation = pairCurrent.first

        fun processF() {
            if (i.hasNext()) {
                val pairNext = i.next()
                if (pairNext.second == "(") {
                    doNext = true
                } else {
                    // Function without arguments, as "PI"
                    i.previous()
                    val funRes: Double = functionsDirector.calculate(pairCurrent.second, arrayOf<Double>())
                    i.set(Pair(TokenAbbreviation.N, funRes.toString()))
                    doNext = false
                }
            } else {
                // Function without arguments, as "PI"
                val funRes: Double = functionsDirector.calculate(pairCurrent.second, arrayOf<Double>())
                i.set(Pair(TokenAbbreviation.N, funRes.toString()))
                doNext = false
            }
        }

        fun processN() {
            if (store.size == 1) {
                // ".replace(',', '.')", because the calculator supports a comma as a decimal separator
                result = pairCurrent.second.replace(',', '.').toDouble()
                isEnd = true
            } else {
                doNext = true
            }
        }

        fun processO() {

        }

        fun processS() {
            when (pairCurrent.second) {
                "(" -> processOpeningParenthesis()
                ";" -> doNext = true
                ")" -> processClosingParenthesis()
            }
        }

        private fun processOpeningParenthesis() {
            if (i.hasNext()) {
                val pairNext = i.next()
                if (pairNext.first == TokenAbbreviation.N) {
                    if (i.hasNext()) {
                        val pairNextNext = i.next()
                        if (pairNextNext.second == ")") {
                            // Situation "(N)"
                            i.remove()
                            i.previous()
                            i.remove()
                            pairCurrent = pairNext
                            doNext = false
                        } else {
                            // TODO My Exceptions
                            throw Exception("Expression \"${pairCurrent.second}${pairNext.second}${pairNextNext.second}\" cannot be recognized")
                        }
                    } else {
                        // TODO My Exceptions
                        throw Exception("Expression \"${pairCurrent.second}${pairNext.second}\" cannot be recognized")
                    }
                } else {
                    pairCurrent = pairNext
                    doNext = false
                }
            } else {
                // TODO My Exceptions
                throw Exception("Expression cannot be recognized")
            }
        }

        private fun processClosingParenthesis() {
            // situation "F(N, N, N, .., N)" or "F((N)...)"
            val v: MutableList<Double> = mutableListOf()
            i.remove()
            while (true) {
                var elem = i.getCurrentValue()
                i.remove()
                if (elem.first == TokenAbbreviation.N) {
                    v.add(0, elem.second.toDouble())
                } else if (elem.second == "(") {
                    break
                } else {
                    if (elem.second != ";") {
                        // TODO My exceptions
                        throw Exception("Expression cannot be recognized")
                    }
                }
            }
            val pairWithFunc = i.getCurrentValue()
            if (pairWithFunc.second != "(") {
                val funRes: Double = functionsDirector.calculate(pairWithFunc.second, v.toTypedArray())
                i.set(Pair(TokenAbbreviation.N, funRes.toString()))
                doNext = false
            } else {
                i.add(Pair(TokenAbbreviation.N, v[0].toString()), DelegatingMutableListIterator.Direction.FORWARD)
                doNext = true
            }
        }
    }
}