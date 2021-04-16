package calculator
import calculator.Lexer.TokenAbbreviation

class CalculatorEngine(val functionsDirector: FunctionsDirector,
                       val operationsDirector: OperationsDirector) {
    var ans: Double? = null
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
                        result = pairCurrent.second.toDouble()
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

                        }
                        ";" -> {
                            doNext = true
                        }
                        ")" -> {
                            // situation "F(N, N, N, .., N)"
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
                                        throw Exception("Wrong sequence")
                                    }
                                }
                            }
                            val pairWithFunc = i.getCurrentValue()
                            val funRes: Double = functionsDirector.calculate(pairWithFunc.second, v.toTypedArray())
                            pairCurrent = Pair(TokenAbbreviation.N, funRes.toString())
                            i.set(pairCurrent)
                            doNext = false
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