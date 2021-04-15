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
        val i = store.listIterator()
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
                            i.previous() // return current element
                            i.previous() // return previous element
                            val funRes: Double = functionsDirector.calculate(pairCurrent.second, arrayOf<Double>())
                            pairCurrent = Pair(TokenAbbreviation.N, funRes.toString())
                            i.set(pairCurrent)
                            doNext = false
                        }
                    } else {
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

                }
            }
        }
        if (result != null) {
            ans = result
        }
        return result
    }
}