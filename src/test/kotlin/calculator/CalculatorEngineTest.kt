package calculator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

internal class CalculatorEngineTest {
    private val funcDirector = FunctionsDirector()
    init {
        funcDirector.registerFunction("sum", null,
            { v: Array<Double> ->
                var res = 0.0
                v.forEach { res += it }
                res
            })
        funcDirector.registerFunction("sin", 1,
            { v: Array<Double> -> sin(v[0]) })
        funcDirector.registerFunction("pi", 0,
            { PI })
    }
    private val opDirector = OperationsDirector()
    init {
        opDirector.registerOperation("+", {v -> v[0] + v[1]},
            OperationsDirector.Arity.BINARY, 1,
            OperationsDirector.Associativity.LEFT)
        opDirector.registerOperation("-", {v -> v[0] - v[1]},
            OperationsDirector.Arity.BINARY, 1,
            OperationsDirector.Associativity.LEFT)
        opDirector.registerOperation("*", {v -> v[0] * v[1]},
            OperationsDirector.Arity.BINARY, 5,
            OperationsDirector.Associativity.LEFT)
        opDirector.registerOperation("/", {v -> v[0] / v[1]},
            OperationsDirector.Arity.BINARY, 5,
            OperationsDirector.Associativity.LEFT)
        opDirector.registerOperation("**", {v -> v[0].pow(v[1])},
            OperationsDirector.Arity.BINARY, 9,
            OperationsDirector.Associativity.RIGHT)
        opDirector.registerOperation("++", {v -> v[0] + 1},
            OperationsDirector.Arity.UNARY, 9,
            OperationsDirector.Associativity.LEFT)
    }
    data class TestedData(val expression: String, val result: Double?)
    private val tdArray: Array<TestedData> = arrayOf(
        TestedData("pi", PI),
        TestedData("sin(pi)", 0.0),
        TestedData("sum(sin(pi); 3; 5.5)", 8.5),
        TestedData("1+2", 3.0),
        TestedData("sin(pi / 2)", 1.0)
    )
    @Test
    fun calculate() {
        val calcEngine = CalculatorEngine(funcDirector, opDirector)
        for (td in tdArray) {
            val lexer = Lexer(td.expression)
            val calculatedResult = calcEngine.calculate(lexer.tokens, lexer.tokensMap)
            val errMessage = "${tdArray.indexOf(td) + 1} / ${tdArray.size}. Error in \"${td.expression}\"\n"
            if (calculatedResult != null && td.result != null) {
                Assertions.assertEquals(td.result, calculatedResult, 1e-7, errMessage)
            } else {
                Assertions.assertEquals(td.result, calculatedResult, errMessage)
            }
        }
    }

}