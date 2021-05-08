package calculator

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

internal class StringCalculatorAppTest {
    var app = StringCalculatorApp()
    @BeforeEach
    fun initApp() {
        app = StringCalculatorApp()
    }

    @Test
    fun easyCalculations() {
        assertEquals("3.0", app.process("1+2"))
        assertEquals("0.0", app.process("sin(0)"))
    }

    @Test
    fun registerFunction() {
        app.process("!!!>> register function myCos !!!>> if((([0] + pi / 2) % (2 * pi)) < pi; 1; -1) * (1 - sin([0])^2)^0.5 " +
                "!!!>> Bad but correct formula of cosine\n")
//        assertEquals("0.0", app.process("round(myCos(pi / 2))"))
//        because negative zero to string is "-0.0"
        assertEquals("-0.0", app.process("round(myCos(pi / 2))"))
    }
}
