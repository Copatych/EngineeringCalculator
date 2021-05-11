package calculator

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin
import mymath.gamma
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory

internal class CalculatorEngineTest {
    private val funcDirector = FunctionsDirector()

    init {
        funcDirector.registerFunction("sum", null,
            { v -> v.reduce { acc, vi -> acc + vi } })
        funcDirector.registerFunction("sin", 1,
            { v -> sin(v[0]) })
        funcDirector.registerFunction("pi", 0,
            { PI })
    }

    private val opDirector = OperationsDirector()

    init {
        opDirector.registerOperation(
            "-", { v -> -v[0] },
            Arity.UNARY, 15,
            Associativity.RIGHT
        )
        opDirector.registerOperation(
            "+", { v -> v[0] + v[1] },
            Arity.BINARY, 1,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "-", { v -> v[0] - v[1] },
            Arity.BINARY, 1,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "*", { v -> v[0] * v[1] },
            Arity.BINARY, 5,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "/", { v -> v[0] / v[1] },
            Arity.BINARY, 5,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "**", { v -> v[0].pow(v[1]) },
            Arity.BINARY, 9,
            Associativity.RIGHT
        )
        opDirector.registerOperation(
            "++", { v -> v[0] + 1 },
            Arity.UNARY, 10,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "!", { v -> gamma(v[0] + 1) },
            Arity.UNARY, 10,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "!->", { v -> gamma(v[0] + 1) },
            Arity.UNARY, 10,
            Associativity.RIGHT
        )
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
        TestedData("1*34++ +2-7**2-8*2**3++", (35 + 2 - 49 - 8 * 16).toDouble()),
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
        TestedData("(-3 + (-2))", -5.0),
        //--------------------------------------------------
        //TestedData("3-2*2-5!+15", -106.0) // TODO It is bad Exception. Exception with "!+" should be shown
        TestedData("sin(13 / sin(pi/2) - 13)", 0.0)
    )

    @TestFactory
    fun calculate(): Collection<DynamicTest> {
        val calcEngine = CalculatorEngine(funcDirector, opDirector)
        return tdArray.map {
            dynamicTest(it.expression) {
                val errMessage = "${tdArray.indexOf(it) + 1} / ${tdArray.size}. Error in \"${it.expression}\"\n"
                try {
                    val lexer = Lexer(it.expression)
                    val calculatedResult = calcEngine.calculate(lexer.tokens)
                    if (calculatedResult != null && it.result != null) {
                        assertEquals(it.result, calculatedResult, 1e-7, errMessage)
                    } else {
                        assertEquals(it.result, calculatedResult, errMessage)
                    }
                } catch (e: Exception) {
                    print(errMessage)
                    throw e
                }
            }
        }
    }

}