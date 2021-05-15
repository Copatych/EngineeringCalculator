package calculator

import kotlin.Exception

class FunctionsDirector {
    // If nargs is null, then nargs can be anythyng
    private data class Function(
        val name: String, val nargs: Int?,
        val f: (v: Array<Double>) -> Double,
        val description: String? = null
    )

    private var functionsMap: MutableMap<String, Function> = mutableMapOf()

    fun registerFunction(
        name: String, nargs: Int?, f: (v: Array<Double>) -> Double,
        description: String? = null
    ) {
        val fModified: (v: Array<Double>) -> Double = {
            if (nargs != null && nargs != it.size) {
                // TODO my exceptions
                throw Exception("The function takes a different number of arguments (${nargs} arguments).")
            }
            f(it)
        }
        functionsMap[name] = Function(name, nargs, fModified, description)
    }

    fun calculate(functionName: String, v: Array<Double>): Double {
        val function = functionsMap[functionName]
        if (function != null) {
            return function.f(v)
        } else {
            // TODO my exceptions
            throw Exception("Function \"$functionName\" does not exist.")
        }
    }

    fun getFunctionsDetailed(): String {
        return StringBuilder(functionsMap.size * 50).apply {
            functionsMap.forEach {
                this.append(
                    "${it.key} - " +
                            (if (it.value.nargs != null) "${it.value.nargs} arguments"
                            else "variable number of arguments") +
                            (if (it.value.description != null) " - ${it.value.description}" else "") +
                            "\n"
                )
            }
        }.toString()
    }

    fun functionsNames(): List<String> {
        return functionsMap.keys.toList()
    }
}