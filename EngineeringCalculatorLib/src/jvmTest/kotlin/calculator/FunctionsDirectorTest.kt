package calculator

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import kotlin.math.sin
import kotlin.math.PI

internal class FunctionsDirectorTest {

    @Test
    fun calculate() {
        val funcD = FunctionsDirector()
        funcD.registerFunction("add", null,
            {v: Array<Double> ->
                var res = 0.0
                v.forEach { res += it }
                res
            })
        funcD.registerFunction("sin", 1,
            {v: Array<Double> -> sin(v[0]) })
        funcD.registerFunction("pi", 0,
            { PI })
        assertEquals(0.0, funcD.calculate("add", arrayOf()), 1e-7)
        assertEquals(0.2, funcD.calculate("add", arrayOf(0.2)), 1e-7)
        assertEquals(0.2, funcD.calculate("add", arrayOf(0.0, 0.2)), 1e-7)
        assertEquals(3.0, funcD.calculate("add", arrayOf(1.0, 1.0, 1.0)), 1e-7)
        assertEquals(1.0, funcD.calculate("sin", arrayOf(PI / 2)), 1e-7)
        assertEquals(PI, funcD.calculate("pi", arrayOf()), 1e-7)
    }

    @Test
    fun getRegisteredFunctions() {
        val funcD = FunctionsDirector()
        assertEquals("", funcD.getFunctionsDetailed())
        funcD.registerFunction("sin", 1,
            {v: Array<Double> -> sin(v[0]) })
        assertEquals("sin - 1 arguments\n", funcD.getFunctionsDetailed())
        funcD.registerFunction("add", null,
            {v: Array<Double> ->
                var res = 0.0
                v.forEach { res += it }
                res
            },
            "Adding a variable number of arguments")
        assertEquals("sin - 1 arguments\n" +
                "add - variable number of arguments" +
                " - Adding a variable number of arguments\n",
            funcD.getFunctionsDetailed())
    }
}