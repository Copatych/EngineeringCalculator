package calculator
import calculator.Lexer.TokenAbbreviation

class CalculatorEngine(val functionsDirector: FunctionsDirector,
                       val operationsDirector: OperationsDirector, private val ansOld: Double? = null) {

    var ans: Double? = ansOld
        private set

    // TODO
    fun calculate(tokens: List<String>, tokensMap: List<TokenAbbreviation>) : Double? {
        var result: Double? = null
        val store: MutableList<Pair<TokenAbbreviation, String>> = (tokensMap zip tokens).toMutableList()
        val i = DelegatingMutableListIterator(store.listIterator())
        var doNext = false
        var pairCurrent = i.next()
        while (true) {
            if (doNext) {
                pairCurrent = i.next()
            }
            when (pairCurrent.first) {
                TokenAbbreviation.F -> {
                    if (i.hasNext()) {
                        val pairNext = i.next()
                        if (pairNext.second == "(") {
                            doNext = true
                        } else {
                            // Function without arguments, as "PI"
                            i.previous()
                            val funRes: Double = functionsDirector.calculate(pairCurrent.second, arrayOf<Double>())
                            pairCurrent = Pair(TokenAbbreviation.N, funRes.toString())
                            i.set(pairCurrent)
                            doNext = false
                        }
                    } else {
                        // Function without arguments, as "PI"
                        val funRes: Double = functionsDirector.calculate(pairCurrent.second, arrayOf<Double>())
                        pairCurrent = Pair(TokenAbbreviation.N, funRes.toString())
                        i.set(pairCurrent)
                        doNext = false
                    }
                }
                TokenAbbreviation.N -> {
                    if (store.size == 1) {
                        // ".replace(',', '.')", because the calculator supports a comma as a decimal separator
                        result = pairCurrent.second.replace(',', '.').toDouble()
                        break
                    } else {
                        doNext = true
                    }
                }
                TokenAbbreviation.O -> {

                }
                TokenAbbreviation.S -> {
                    when (pairCurrent.second) {
                        "(" -> {
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
                        ";" -> {
                            doNext = true
                        }
                        ")" -> {
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
                                pairCurrent = Pair(TokenAbbreviation.N, funRes.toString())
                                i.set(pairCurrent)
                                doNext = false
                            } else {
                                i.add(Pair(TokenAbbreviation.N, v[0].toString()), DelegatingMutableListIterator.Direction.FORWARD)
                                doNext = true
                            }
                        }
                    }

                }
            }
        }
        if (result != null) {
            ans = result
        }
        return result
    }
}