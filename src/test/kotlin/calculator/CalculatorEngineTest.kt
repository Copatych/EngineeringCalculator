package calculator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.Exception
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
import mymath.gamma

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
        opDirector.registerOperation("-", {v -> - v[0]},
            Arity.UNARY, 15,
            Associativity.RIGHT)
        opDirector.registerOperation("+", {v -> v[0] + v[1]},
            Arity.BINARY, 1,
            Associativity.LEFT)
        opDirector.registerOperation("-", {v -> v[0] - v[1]},
            Arity.BINARY, 1,
            Associativity.LEFT)
        opDirector.registerOperation("*", {v -> v[0] * v[1]},
            Arity.BINARY, 5,
            Associativity.LEFT)
        opDirector.registerOperation("/", {v -> v[0] / v[1]},
            Arity.BINARY, 5,
            Associativity.LEFT)
        opDirector.registerOperation("**", {v -> v[0].pow(v[1])},
            Arity.BINARY, 9,
            Associativity.RIGHT)
        opDirector.registerOperation("++", {v -> v[0] + 1},
            Arity.UNARY, 10,
            Associativity.LEFT)
        opDirector.registerOperation("!", {v -> gamma(v[0] + 1)},
            Arity.UNARY, 10,
            Associativity.LEFT)
        opDirector.registerOperation("!->", {v -> gamma(v[0] + 1)},
            Arity.UNARY, 10,
            Associativity.RIGHT)
    }
    data class TestedData(val expression: String, val result: Double?)
    private val tdArray: Array<TestedData> = arrayOf(
        TestedData("pi", PI),
        TestedData("sin(pi)", 0.0),
        TestedData("sum(sin(pi); 3; 5.5)", 8.5),
        TestedData("(3.4)", 3.4),
        TestedData("(3,4)", 3.4),
        TestedData("3.", 3.0),
        TestedData("3,", 3.0),
        TestedData("sin((pi))", 0.0),
        //--------------------------------------------------
        TestedData("1+2", 3.0),
        TestedData("sin(pi / 2)", 1.0),
        TestedData("1+2*3", 7.0),
        TestedData("1*2+3", 5.0),
        TestedData("1+2*3+4", 11.0),
        TestedData("1*2+3*4", 14.0),
        TestedData("(1+2*3)", 7.0),
        //--------------------------------------------------
        TestedData("2+2++", 5.0),
        TestedData("2++ ++ ++", 5.0),
        TestedData("((2++) ++) ++", 5.0),
        TestedData("((2++) ++) ++ + (2)", 7.0),
        TestedData("2++ +2", 5.0),
        TestedData("(2++) +2", 5.0),
        TestedData("2+(2++)", 5.0),
        TestedData("2**3**3", 2.0.pow(27)),
        TestedData("2**3", 2.0.pow(3)),
        TestedData("2**3++", 2.0.pow(4)),
        TestedData("1*34++ +2-7**2-8*2**3++", (35+2-49-8*16).toDouble()),
        //--------------------------------------------------
        TestedData("1!", 1.0),
        TestedData("5!", 120.0),
        TestedData("!->5", 120.0),
        TestedData("!->3!", 720.0),
        TestedData("(!->3)!", 720.0),
        TestedData("!->(3!)", 720.0),
        TestedData("2! + !->3!", 722.0),
        TestedData("1+2++ ! + !->3!", 727.0),
        //--------------------------------------------------
        TestedData("-3", -3.0),
        TestedData("(-3)", -3.0),
        TestedData("-3 + -2", -5.0),
        TestedData("(-3 + (-2))", -5.0)
    )
    @Test
    fun calculate() {
        val calcEngine = CalculatorEngine(funcDirector, opDirector)
        for (td in tdArray) {
            val errMessage = "${tdArray.indexOf(td) + 1} / ${tdArray.size}. Error in \"${td.expression}\"\n"
            try {
                val lexer = Lexer(td.expression)
                val calculatedResult = calcEngine.calculate(lexer.tokens)
                if (calculatedResult != null && td.result != null) {
                    Assertions.assertEquals(td.result, calculatedResult, 1e-7, errMessage)
                } else {
                    Assertions.assertEquals(td.result, calculatedResult, errMessage)
                }
            } catch (e: Exception) {
                print(errMessage)
                throw e
            }
        }
    }

}