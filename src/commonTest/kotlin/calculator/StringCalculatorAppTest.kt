package calculator

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class StringCalculatorAppTest {
    var app = StringCalculatorApp()
    @BeforeTest
    fun initApp() {
        app = StringCalculatorApp()
    }

    @Test
    fun easyCalculations() {
        assertEquals(3.0, app.process("1+2").toDouble())
        assertEquals(0.0, app.process("sin(0)").toDouble())
        assertEquals(0.0, app.process("round(sin(pi))").toDouble())
        assertEquals(14.0, app.process("round(13 / sin(pi/2) + 1)").toDouble())
    }

    @Test
    fun registerFunction() {
        app.process("!!!>> register function myCos !!!>> if((([0] + pi / 2) % (2 * pi)) <= pi; 1; -1) * (1 - sin([0])^2)^0.5 " +
                "!!!>> Bad but correct formula of cosine\n")
        assertEquals(0.0, app.process("round(myCos(pi / 2))").toDouble())
    }
}
