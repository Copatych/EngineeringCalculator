package calculator
import calculator.Lexer.TokenAbbreviation

class CalculatorEngine(val functionsDirector: FunctionsDirector,
                       val operationsDirector: OperationsDirector, private val ansOld: Double? = null) {

    var ans: Double? = ansOld
        private set

    fun calculate(tokens: List<String>, tokensMap: List<TokenAbbreviation>): Double? {
        val calcStore = CalculatorableStore((tokensMap zip tokens).toMutableList())
        var curNumberOfIterations = 0
        var maxNumberOfIterations = tokens.size * 40
        while (calcStore.nextStep()) {
            when (calcStore.currentToken()) {
                TokenAbbreviation.F -> calcStore.processF()
                TokenAbbreviation.O -> calcStore.processO()
                TokenAbbreviation.N -> calcStore.processN()
                TokenAbbreviation.S -> calcStore.processS()
            }
            curNumberOfIterations++
            if (curNumberOfIterations > maxNumberOfIterations) {
                // TODO My Exceptions
                throw Exception("Most likely the execution is looped")
            }
        }
        if (calcStore.result != null) {
            ans = calcStore.result
        }
        return calcStore.result
    }

    // See CalculatorEngine.calculate
    private inner class CalculatorableStore(val store: MutableList<Pair<TokenAbbreviation, String>>) {
        val i = DelegatingMutableListIterator(store.listIterator())
        var doNext = false
        var pairCurrent = i.next()
        private var isEnd = false

        var result: Double? = null

        fun nextStep(): Boolean {
            if (isEnd) {
                return false
            }
            if (doNext && !i.hasNext()) {
                // "i" reached the end
                i.move(-(store.size - 1))
                doNext = false
            }
            pairCurrent = if (doNext) i.next() else i.getCurrentValue()
            return true
        }

        fun currentToken(): TokenAbbreviation = pairCurrent.first

        fun processF() {
            if (i.hasNext()) {
                val pairNext = i.next()
                if (pairNext.second == "(") { // do F, when get to the ")"
                    doNext = true
                    return
                } else { // Function without arguments, as "PI"
                    i.previous()
                }
            } // else Function without arguments, as "PI"
            val funRes: Double = functionsDirector.calculate(pairCurrent.second, arrayOf<Double>())
            i.set(Pair(TokenAbbreviation.N, funRes.toString()))
            doNext = false
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
            val op = pairCurrent.second
            when (operationsDirector.getArity(op)) {
                Arity.UNARY -> processOUnary(op)
                Arity.BINARY -> processOBinary(op)
            }
        }

        private fun processOUnary(op: String) {
            val isLeftNeighbour = when (operationsDirector.getAssociativity(op)) {
                Associativity.RIGHT -> false
                Associativity.LEFT -> true
            }
            if (isFirstNeighbourReadyForOp(isLeftNeighbour)) {
                val neighbourOpPair = checkSecondNeighbourForOp(isLeftNeighbour)
                if (neighbourOpPair == null || operationsDirector.comparePriority(op, neighbourOpPair.second) >= 0) {
                    doUnaryOperation()
                } else {
                    doNext = true
                }
            } else {
                doNext = true
            }
        }

        private fun processOBinary(op: String) {
            if (isFirstNeighbourReadyForOp(isLeftNeighbour = true) && isFirstNeighbourReadyForOp(isLeftNeighbour = false)) {
                val leftOpPair = checkSecondNeighbourForOp(isLeftNeighbour = true)
                val rightOpPair = checkSecondNeighbourForOp(isLeftNeighbour = false)
                val priorityOverLeft = leftOpPair?.let { operationsDirector.comparePriority(op, it.second) }
                val priorityOverRight = rightOpPair?.let { operationsDirector.comparePriority(op, it.second) }
                if (priorityOverLeft == null && priorityOverRight == null) doBinaryOperation()
                else if ((priorityOverLeft == null || priorityOverLeft >= 0) && priorityOverRight != null) {
                    when {
                        priorityOverRight > 0 -> doBinaryOperation()
                        priorityOverRight < 0 -> doNext = true
                        priorityOverRight == 0 -> {
                            when (operationsDirector.getAssociativity(op)) {
                                Associativity.LEFT -> doBinaryOperation()
                                Associativity.RIGHT -> doNext = true
                            }
                        }
                    }
                } else if ((priorityOverRight == null || priorityOverRight > 0) && priorityOverLeft != null) {
                    when {
                        priorityOverLeft > 0 -> doBinaryOperation()
                        priorityOverLeft < 0 -> doNext = true
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

        private fun doBinaryOperation() {
            val op = pairCurrent.second
            val v1 = i.previous().second.toDouble()
            i.remove()
            // TODO My Exceptions
            val v2 = if (i.getCurrentValue() !== pairCurrent) {
                i.move(2)?.second?.toDouble() ?: throw Exception("Internal error. \"Can't do operation \\\"${op}\\\"\"")
            } else i.next().second.toDouble()
            i.remove()
            val res = operationsDirector.calculate(op, arrayOf(v1, v2))
            i.set(Pair(TokenAbbreviation.N, res.toString()))
            doNext = false
        }

        private fun doUnaryOperation() {
            val op = pairCurrent.second
            val moveTo = when (operationsDirector.getAssociativity(op)) {
                Associativity.RIGHT -> 1
                Associativity.LEFT -> -1
            }
            // TODO My Exceptions
            val impactedPair = i.move(moveTo)
                ?: throw Exception("Internal error. \"Can't do operation \\\"${op}\\\"\"")
            // We know, that impactedPair keep number, it was checked in fun isFirstNeighbourReadyForOp
            val ifNotTheFirst = i.hasPrevious()
            i.remove()
            if (ifNotTheFirst && moveTo == -1) i.move(1)
            val res = operationsDirector.calculate(op, arrayOf(impactedPair.second.toDouble()))
            i.set(Pair(TokenAbbreviation.N, res.toString()))
            doNext = false

        }

        private fun isFirstNeighbourReadyForOp(isLeftNeighbour: Boolean): Boolean {
            val moveTo = if (isLeftNeighbour) -1 else 1
            val firstNeighbour = i.move(moveTo)
                ?: // TODO My Exceptions
                throw Exception("Can't find variable for operation")
            i.move(-moveTo) // return back
            if (firstNeighbour.first != TokenAbbreviation.N) return false
            return true
        }

        private fun checkSecondNeighbourForOp(isLeftNeighbour: Boolean): Pair<TokenAbbreviation, String>? {
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
                            doNext = true
                        } else {
                            doNext = false
                        }
                    } else {
                        // TODO My Exceptions
                        throw Exception("Expression \"${pairCurrent.second}${pairNext.second}\" cannot be recognized")
                    }
                } else {
                    doNext = false
                }
            } else {
                // TODO My Exceptions
                throw Exception("Expression cannot be recognized")
            }
        }

        private fun processClosingParenthesis() {
            if (isClosingParenthesisFuncSituation()) {
                // situation "F(N, N, N, .., N)"
                processClosingParenthesisFuncSituation()
            } else {
                val pairPrev = i.previous()
                if (pairPrev.second == "(") { // situation "()"
                    i.remove()
                    i.remove()
                    doNext = false
                    return
                }
                val pairPrevPrev = i.previous()
                if (pairPrevPrev.second == "(") {
                    // situation (N). Processing in processOpeningParenthesis
                    doNext = false
                    return
                }
            }
            i.move(2)
        }

        private fun isClosingParenthesisFuncSituation(): Boolean {
            // check if situation "F(N, N, N, .., N)"
            var iters = 0
            var pairCurrent: Pair<TokenAbbreviation, String>
            var res = false
            while (true) {
                if (i.hasPrevious()) {
                    iters++
                    pairCurrent = i.previous()
                    if (pairCurrent.second == "(") break
                    else if (pairCurrent.second == ")") {
                        i.move(iters)
                        return false
                    }
                } else {
                    // TODO My Exceptions
                    throw Exception("Closing parenthesis without opening")
                }
            }
            if (i.hasPrevious()) {
                iters++
                pairCurrent = i.previous()
                res = (pairCurrent.first == TokenAbbreviation.F)
            } else res = false
            i.move(iters)
            return res
        }

        private fun processClosingParenthesisFuncSituation() {
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
            val funRes: Double = functionsDirector.calculate(pairWithFunc.second, v.toTypedArray())
            i.set(Pair(TokenAbbreviation.N, funRes.toString()))
            doNext = false

        }
    }
}