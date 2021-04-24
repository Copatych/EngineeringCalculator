package calculator

import kotlin.Exception

enum class Associativity{LEFT, RIGHT;}

enum class Arity{UNARY, BINARY;}

data class OperationKey(val name: String, val arity: Arity)

class OperationsDirector {
    private data class Operation(val name: String, val arity: Arity,
                                 val priority: Int, val associativity: Associativity,
                                 val calc: (v: Array<Double>) -> Double)

    private val operationsMap: MutableMap<OperationKey, Operation> = mutableMapOf()

    fun registerOperation(name: String, calc: (v: Array<Double>) -> Double,
                          arity: Arity, priority: Int,
                          associativity: Associativity = Associativity.LEFT) {
        val calcModified: (v: Array<Double>) -> Double = {
            if (arity == Arity.BINARY && it.size != 2) {
                // TODO my exceptions
                throw Exception("Operation $name must be binary.")
            } else if (arity == Arity.UNARY && it.size != 1) {
                // TODO my exceptions
                throw Exception("Operation \"$name\" must be unary.")
            }
            calc(it)
        }
        operationsMap[OperationKey(name, arity)] = Operation(name, arity, priority, associativity, calcModified)
    }

    private var lastGottenOpIndex: Int = 0
    private val lastGottenOp = Array<Operation?>(2) { null }

    private fun getOperation(opKey: OperationKey) : Operation? {
        val resOp = lastGottenOp.find { it?.name == opKey.name && it.arity == opKey.arity }
        if (resOp != null) {
            return resOp
        }
        val resOp1 = operationsMap[opKey] ?: return null
        lastGottenOp[lastGottenOpIndex] = resOp1
        lastGottenOpIndex = ++lastGottenOpIndex % 2
        return resOp1
    }

    fun comparePriority(opKey1: OperationKey, opKey2: OperationKey) : Int {
        val op1 = getOperation(opKey1)
        val op2 = getOperation(opKey2)
        if (op1 == null || op2 ==null) {
            // TODO My Exceptions
            throw Exception("Can't compare priorities for $op1 and $op2")
        }
        return op1.priority - op2.priority
    }

    fun getRegisteredOperations() : String {
        return StringBuilder(operationsMap.size * 50).apply {
            operationsMap.forEach {this.append("${it.key.name}\t- " +
                    "${it.value.arity} - " +
                    "${it.value.associativity} - " +
                    "priority: ${it.value.priority}\n")}
        }.toString()
    }

    fun getPriorities() : Map<OperationKey, Int> {
        return operationsMap.map{it.key to it.value?.priority}.toMap()
    }

    fun getAssociativity(opKey: OperationKey) : Associativity? {
        return getOperation(opKey)?.associativity
    }

    fun calculate(opKey: OperationKey, v: Array<Double>) : Double {
        val op = operationsMap[opKey]
        if(op != null) {
            return op.calc(v)
        } else {
            // TODO my exceptions
            throw Exception("Operation \"$opKey\" does not exist.")
        }
    }
}