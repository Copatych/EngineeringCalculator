package calculator

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class FunctionsAdderTest {

    @Test
    fun doProcessingJustRenaming() {
        val funcAdder = FunctionsAdder()
        funcAdder.registerFunction("abc", "qwerty")
        assertEquals("sumqwerty", funcAdder.doProcessing("sumabc"))
        funcAdder.registerFunction("qwerty", "sum")
        assertEquals("sumsum", funcAdder.doProcessing("sumabc"))
    }
}