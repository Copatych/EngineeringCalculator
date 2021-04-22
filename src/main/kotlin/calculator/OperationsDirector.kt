package calculator

import java.lang.Exception

enum class Associativity{LEFT, RIGHT;}

enum class Arity{UNARY, BINARY;}

class OperationsDirector {
    private data class Operation(val name: String, val arity: Arity,
                                 val priority: Int, val associativity: Associativity,
                                 val calc: (v: Array<Double>) -> Double)

    private val operationsMap: MutableMap<String, Operation> = mutableMapOf()

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
        operationsMap[name] = Operation(name, arity, priority, associativity, calcModified)
    }

    private var lastGottenOpIndex: Int = 0
    private val lastGottenOp = Array<Operation?>(2, { null })
    
    private fun getOperation(name: String) : Operation {
        val resOp = lastGottenOp.find { it?.name == name }
        if (resOp != null) {
            return resOp
        }
        val resOp1 = operationsMap[name]
        if (resOp1 == null) {
            // TODO my exceptions
            throw Exception("This operation doesn't exist.")
        }
        lastGottenOp[lastGottenOpIndex] = resOp1
        lastGottenOpIndex = ++lastGottenOpIndex % 2
        return resOp1
    }

    fun comparePriority(op1Name: String, op2Name: String) : Int {
        val op1 = getOperation(op1Name)
        val op2 = getOperation(op2Name)
        return op1.priority - op2.priority
    }

    fun getRegisteredOperations() : String {
        return StringBuilder(operationsMap.size * 50).apply {
            operationsMap.forEach {this.append("${it.key}\t- " +
                    "${it.value.arity} - " +
                    "${it.value.associativity} - " +
                    "priority: ${it.value.priority}\n")}
        }.toString()
    }

    fun getPriorities() : Map<String, Int> {
        return operationsMap.map{it.key to it.value?.priority}.toMap()
    }

    fun getAssociativity(op: String) : Associativity {
        return getOperation(op)?.associativity
    }

    fun getArity(op: String) : Arity {
        return getOperation(op)?.arity
    }

    fun calculate(opName: String, v: Array<Double>) : Double {
        val op = operationsMap[opName]
        if(op != null) {
            return op.calc(v)
        } else {
            // TODO my exceptions
            throw Exception("Operation \"$opName\" does not exist.")
        }
    }
}