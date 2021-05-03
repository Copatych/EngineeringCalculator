package calculator

class CalculatorEngine(
    val functionsDirector: FunctionsDirector,
    val operationsDirector: OperationsDirector, private val ansOld: Double? = null
) {

    var ans: Double? = ansOld
        private set

    fun calculate(tokens: List<Token>): Double? {
        val calcStore = CalculatorableStore(tokens.toMutableList())
        var curNumberOfIterations = 0
        var maxNumberOfIterations = tokens.size * 40
        while (calcStore.nextStep()) {
            when (calcStore.currentToken()) {
                Token.Abbreviation.F -> calcStore.processF()
                Token.Abbreviation.O -> calcStore.processO()
                Token.Abbreviation.N -> calcStore.processN()
                Token.Abbreviation.S -> calcStore.processS()
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
    private inner class CalculatorableStore(val store: MutableList<Token>) {
        val i = DelegatingMutableListIterator(store.listIterator())
        var doNext = false
        var tokenCurrent = i.next()
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
            tokenCurrent = if (doNext) i.next() else i.getCurrentValue()
            return true
        }

        fun currentToken(): Token.Abbreviation = tokenCurrent.abbreviation

        fun processF() {
            if (i.hasNext()) {
                val tokenNext = i.next()
                if (tokenNext.value == "(") { // do F, when get to the ")"
                    doNext = true
                    return
                } else { // Function without arguments, as "PI"
                    i.previous()
                }
            } // else Function without arguments, as "PI"
            val funRes: Double = functionsDirector.calculate(tokenCurrent.value, arrayOf<Double>())
            i.set(Token(funRes.toString()))
            doNext = false
        }

        fun processN() {
            if (store.size == 1) {
                // ".replace(',', '.')", because the calculator supports a comma as a decimal separator
                result = tokenCurrent.value.replace(',', '.').toDouble()
                isEnd = true
            } else {
                doNext = true
            }
        }

        fun processO() {
            val opKey = getOpKey()
            when (opKey.arity) {
                Arity.UNARY -> processOUnary(opKey)
                Arity.BINARY -> processOBinary(opKey)
            }
        }

        fun processS() {
            when (tokenCurrent.value) {
                "(" -> processOpeningParenthesis()
                ";" -> doNext = true
                ")" -> processClosingParenthesis()
            }
        }

        private fun getOpKey(): OperationKey {
            val op = i.getCurrentValue().value
            val neighbourFromNonImpactedSide =
                when (operationsDirector.getAssociativity(OperationKey(op, Arity.UNARY))) {
                    Associativity.LEFT -> i.get(1)
                    Associativity.RIGHT -> i.get(-1)
                    else -> return OperationKey(op, Arity.BINARY)
                }
            val isUnaryOp = neighbourFromNonImpactedSide.run {
                this == null ||
                        abbreviation == Token.Abbreviation.S ||
                        abbreviation == Token.Abbreviation.O
            }
            return if (isUnaryOp) OperationKey(op, Arity.UNARY)
            else OperationKey(op, Arity.BINARY)
        }

        private fun processOUnary(opKey: OperationKey) {
            val isLeftNeighbour = when (operationsDirector.getAssociativity(opKey)) {
                Associativity.RIGHT -> false
                Associativity.LEFT -> true
                null -> throw Exception("Can't process operation $opKey") // TODO My Exceptions
            }
            if (isFirstNeighbourReadyForOp(isLeftNeighbour)) {
                val secondNeighbourOpKey = checkSecondNeighbourForOp(isLeftNeighbour)
                if (secondNeighbourOpKey == null || operationsDirector.comparePriority(
                        opKey,
                        secondNeighbourOpKey
                    ) >= 0
                ) {
                    doUnaryOperation(opKey)
                } else {
                    doNext = true
                }
            } else {
                doNext = true
            }
        }

        private fun processOBinary(opKey: OperationKey) {
            if (isFirstNeighbourReadyForOp(isLeftNeighbour = true) && isFirstNeighbourReadyForOp(isLeftNeighbour = false)) {
                val leftOpKey = checkSecondNeighbourForOp(isLeftNeighbour = true)
                val rightOpKey = checkSecondNeighbourForOp(isLeftNeighbour = false)
                val priorityOverLeft = leftOpKey?.let { operationsDirector.comparePriority(opKey, it) }
                val priorityOverRight = rightOpKey?.let { operationsDirector.comparePriority(opKey, it) }
                if (priorityOverLeft == null && priorityOverRight == null) doBinaryOperation(opKey)
                else if ((priorityOverLeft == null || priorityOverLeft >= 0) && priorityOverRight != null) {
                    when {
                        priorityOverRight > 0 -> doBinaryOperation(opKey)
                        priorityOverRight < 0 -> doNext = true
                        priorityOverRight == 0 -> {
                            when (operationsDirector.getAssociativity(opKey)) {
                                Associativity.LEFT -> doBinaryOperation(opKey)
                                Associativity.RIGHT -> doNext = true
                            }
                        }
                    }
                } else if ((priorityOverRight == null || priorityOverRight > 0) && priorityOverLeft != null) {
                    when {
                        priorityOverLeft > 0 -> doBinaryOperation(opKey)
                        priorityOverLeft < 0 -> doNext = true
                        priorityOverLeft == 0 -> {
                            when (operationsDirector.getAssociativity(opKey)) {
                                Associativity.LEFT -> doNext = true
                                Associativity.RIGHT -> doBinaryOperation(opKey)
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

        private fun doBinaryOperation(opKey: OperationKey) {
            val v1 = i.previous().value.toDouble()
            i.remove()
            // TODO My Exceptions
            val v2 = if (i.getCurrentValue() !== tokenCurrent) {
                i.move(2)?.value?.toDouble()
                    ?: throw Exception("Internal error. \"Can't do operation \\\"${opKey}\\\"\"")
            } else i.next().value.toDouble()
            i.remove()
            if (i.getCurrentValue() !== tokenCurrent) i.previous()
            val res = operationsDirector.calculate(opKey, arrayOf(v1, v2))
            i.set(Token(res.toString()))
            doNext = false
        }

        private fun doUnaryOperation(opKey: OperationKey) {
            val moveTo = when (operationsDirector.getAssociativity(opKey)) {
                Associativity.RIGHT -> 1
                Associativity.LEFT -> -1
                else -> throw Exception("Can't do operation $opKey") // TODO My Exceptions
            }
            // TODO My Exceptions
            val impactedToken = i.move(moveTo)
                ?: throw Exception("Internal error. \"Can't do operation \\\"${opKey}\\\"\"")
            // We know, that impactedToken keep number, it was checked in fun isFirstNeighbourReadyForOp
            i.remove()
            if (tokenCurrent !== i.getCurrentValue()) i.move(-1)
            val res = operationsDirector.calculate(opKey, arrayOf(impactedToken.value.toDouble()))
            i.set(Token(res.toString()))
            doNext = false

        }

        private fun isFirstNeighbourReadyForOp(isLeftNeighbour: Boolean): Boolean {
            val firstNeighbour = (if (isLeftNeighbour) i.get(-1) else i.get(1))
                ?: // TODO My Exceptions
                throw Exception("Can't find variable for operation")
            if (firstNeighbour.abbreviation != Token.Abbreviation.N) return false
            return true
        }

        private fun checkSecondNeighbourForOp(isLeftNeighbour: Boolean): OperationKey? {
            val moveTo = if (isLeftNeighbour) -1 else 1
            val secondNeighbourToken = i.move(moveTo * 2) ?: return null
            if (secondNeighbourToken.abbreviation != Token.Abbreviation.O) {
                i.move(-moveTo * 2) // move back
                return null
            }
            val secondNeighbourOpKey = getOpKey()
            i.move(-moveTo * 2) // move back
            return secondNeighbourOpKey
        }

        private fun processOpeningParenthesis() {
            if (i.hasNext()) {
                val tokenNext = i.next()
                if (tokenNext.abbreviation == Token.Abbreviation.N) {
                    if (i.hasNext()) {
                        val tokenNextNext = i.next()
                        if (tokenNextNext.value == ")") {
                            // Situation "(N)"
                            val moveN = if (i.hasNext()) -2 else -1
                            i.remove()
                            i.move(moveN)
                            i.remove()
                            doNext = true
                        } else {
                            doNext = false
                        }
                    } else {
                        // TODO My Exceptions
                        throw Exception("Expression \"${tokenCurrent.value}${tokenNext.value}\" cannot be recognized")
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
                doNext = true
            } else {
                val tokenPrev = i.previous()
                if (tokenPrev.value == "(") { // situation "()"
                    i.remove()
                    i.remove()
                    doNext = false
                    return
                }
                val tokenPrevPrev = i.previous()
                if (tokenPrevPrev.value == "(") {
                    // situation (N). Processing in processOpeningParenthesis
                    doNext = false
                    return
                }
                i.move(2)
            }
        }

        private fun isClosingParenthesisFuncSituation(): Boolean {
            // check if situation "F(N, N, N, .., N)"
            var iters = 0
            var tokenCurrent: Token
            var res = false
            while (true) {
                if (i.hasPrevious()) {
                    iters++
                    tokenCurrent = i.previous()
                    if (tokenCurrent.value == "(") break
                    else if (tokenCurrent.value == ")") {
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
                tokenCurrent = i.previous()
                res = (tokenCurrent.abbreviation == Token.Abbreviation.F)
            } else res = false
            i.move(iters)
            return res
        }

        private fun processClosingParenthesisFuncSituation() {
            val v: MutableList<Double> = mutableListOf()
            i.remove()
            while (true) {
                var t = if (i.hasNext()) i.previous() else i.getCurrentValue()
                i.remove()
                if (t.abbreviation == Token.Abbreviation.N) {
                    v.add(0, t.value.toDouble())
                } else if (t.value == "(") {
                    break
                } else {
                    if (t.value != ";") {
                        // TODO My exceptions
                        throw Exception("Expression in parenthesis cannot be recognized")
                    }
                }
            }
            val tokenWithFunc = if (i.hasNext()) i.previous() else i.getCurrentValue()
            val funRes: Double = functionsDirector.calculate(tokenWithFunc.value, v.toTypedArray())
            i.set(Token(funRes.toString()))
            doNext = false

        }
    }
}