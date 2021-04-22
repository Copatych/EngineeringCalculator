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
                if (!i.hasNext()) {
                    i.move(-(store.size - 1))
                }
                doNext = true
            }
        }

        fun processO() {
            val op = pairCurrent.second
            when(operationsDirector.getArity(op)) {
                Arity.UNARY -> {
                    val isLeftNeighbour = when(operationsDirector.getAssociativity(op)) {
                        Associativity.RIGHT -> false
                        Associativity.LEFT -> true
                    }
                    if (isFirstNeighbourReadyForOp(isLeftNeighbour)) {
                        val neighbourOpPair = checkSecondNeighbourForOp(isLeftNeighbour)
                        if (neighbourOpPair == null || operationsDirector.comparePriority(op, neighbourOpPair.second) > 0) {
                            doUnaryOperation()
                        } else {
                            doNext = true
                        }
                    } else {
                        doNext = true
                    }
                }
                Arity.BINARY -> {
                    if (isFirstNeighbourReadyForOp(isLeftNeighbour=true) && isFirstNeighbourReadyForOp(isLeftNeighbour = false)) {
                        val leftOpPair = checkSecondNeighbourForOp(isLeftNeighbour = true)
                        val rightOpPair = checkSecondNeighbourForOp(isLeftNeighbour = false)
                        val priorityOverLeft = leftOpPair?.let { operationsDirector.comparePriority(op, it.second) }
                        val priorityOverRight = rightOpPair?.let { operationsDirector.comparePriority(op, it.second) }
                        if (priorityOverLeft == null && priorityOverRight == null) doBinaryOperation()
                        else if((priorityOverLeft == null || priorityOverLeft >= 0) && priorityOverRight != null) {
                            when {
                                priorityOverRight > 0  -> doBinaryOperation()
                                priorityOverRight < 0  -> doNext = true
                                priorityOverRight == 0 -> {
                                    when (operationsDirector.getAssociativity(op)) {
                                        Associativity.LEFT -> doBinaryOperation()
                                        Associativity.RIGHT -> doNext = true
                                    }
                                }
                            }
                        } else if((priorityOverRight == null || priorityOverRight > 0) && priorityOverLeft != null) {
                            when {
                                priorityOverLeft > 0  -> doBinaryOperation()
                                priorityOverLeft < 0  -> doNext = true
                                priorityOverLeft == 0 -> {
                                    when (operationsDirector.getAssociativity(op)) {
                                        Associativity.LEFT -> doNext = true
                                        Associativity.RIGHT -> doBinaryOperation()
                                    }
                                }
                            }
                        } else {
                            doNext = true
                        }
                    } else {
                        doNext = true // move next
                    }
                }
            }
        }

        private fun doBinaryOperation() {
            val op = pairCurrent.second
            val v1 = i.previous().second.toDouble()
            i.remove()
            // TODO My Exceptions
            val v2 = if (i.hasPrevious()) {
                i.move(2)?.second?.toDouble() ?: throw Exception("Internal error. \"Can't do operation \\\"${op}\\\"\"")
            } else i.next().second.toDouble()
            i.remove()
            val res = operationsDirector.calculate(op, arrayOf(v1, v2))
            i.set(Pair(TokenAbbreviation.N, res.toString()))
            doNext = false
        }

        private fun doUnaryOperation() {
            val op = pairCurrent.second
            val moveTo = when(operationsDirector.getAssociativity(op)) {
                Associativity.RIGHT -> 1
                Associativity.LEFT -> -1
            }
            // TODO My Exceptions
            val impactedPair = i.move(moveTo) ?: throw Exception("Internal error. \"Can't do operation \\\"${op}\\\"\"")
            // We know, that impactedPair keep number, it was checked in fun isFirstNeighbourReadyForOp
            val ifNotTheFirst = i.hasPrevious()
            i.remove()
            if (ifNotTheFirst) i.move(1)
//            if (i.hasPrevious() && moveTo == -1) i.move(1)
            val res = operationsDirector.calculate(op, arrayOf(impactedPair.second.toDouble()))
            i.set(Pair(TokenAbbreviation.N, res.toString()))
            doNext = false

        }

        private fun isFirstNeighbourReadyForOp(isLeftNeighbour: Boolean) : Boolean {
            val moveTo = if (isLeftNeighbour) -1 else 1
            val firstNeighbour = i.move(moveTo)
            if (firstNeighbour == null) {
                // TODO My Exceptions
                throw Exception("Can't find variable for operation")
            }
            i.move(-moveTo) // return back
            if (firstNeighbour.first != TokenAbbreviation.N) return false
            return true
        }

        private fun checkSecondNeighbourForOp(isLeftNeighbour: Boolean) : Pair<TokenAbbreviation, String>? {
            val moveTo = if (isLeftNeighbour) -1 else 1
            val secondNeighbour = i.move(moveTo * 2) ?: return null
            i.move(-moveTo * 2) // return back
            if (secondNeighbour.first != TokenAbbreviation.O) return null
            return secondNeighbour
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
                            pairCurrent = pairNextNext
                            doNext = false
                            // TODO My Exceptions
//                            throw Exception("Expression \"${pairCurrent.second}${pairNext.second}${pairNextNext.second}\" cannot be recognized")
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
            // situation "F(N, N, N, .., N)" or "F((N)...)" or (...) or ...(...)
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
                        throw Exception("Expression in parenthesis cannot be recognized")
                    }
                }
            }
            val pairWithFunc = i.getCurrentValue()
            if (pairWithFunc.first == TokenAbbreviation.F) {
                val funRes: Double = functionsDirector.calculate(pairWithFunc.second, v.toTypedArray())
                i.set(Pair(TokenAbbreviation.N, funRes.toString()))
                doNext = false
            } else {
                val direction = if (pairWithFunc.second == "(" || !i.hasNext()) { // situation "F((N)...)"
                    DelegatingMutableListIterator.Direction.FORWARD
                } else { // situation (...) or ...(...)
                    DelegatingMutableListIterator.Direction.BACK
                }
                i.add(Pair(TokenAbbreviation.N, v[0].toString()), direction)
                doNext = false
            }
        }
    }
}