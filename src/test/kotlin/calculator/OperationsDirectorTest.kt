package calculator

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.lang.Exception
import kotlin.math.pow

internal class OperationsDirectorTest {

    private val opDirector = OperationsDirector()
    init {
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
            Arity.UNARY, 9,
            Associativity.LEFT)
    }

    @Test
    fun comparePriority() {
        assertEquals(opDirector.comparePriority(OperationKey("+", Arity.BINARY), OperationKey("+", Arity.BINARY)), 0)
        assertEquals(opDirector.comparePriority(OperationKey("-", Arity.BINARY), OperationKey("-", Arity.BINARY)), 0)
        assertEquals(opDirector.comparePriority(OperationKey("*", Arity.BINARY), OperationKey("*", Arity.BINARY)), 0)
        assertEquals(opDirector.comparePriority(OperationKey("/", Arity.BINARY), OperationKey("/", Arity.BINARY)), 0)
        assertEquals(opDirector.comparePriority(OperationKey("**", Arity.BINARY), OperationKey( "**",Arity.BINARY)), 0)
        assertEquals(opDirector.comparePriority(OperationKey("++", Arity.UNARY), OperationKey( "++", Arity.UNARY)), 0)
        assertEquals(opDirector.comparePriority(OperationKey("+", Arity.BINARY), OperationKey("-", Arity.BINARY)), 0)
        assertEquals(opDirector.comparePriority(OperationKey("*", Arity.BINARY), OperationKey("/", Arity.BINARY)), 0)
        assertEquals(opDirector.comparePriority(OperationKey("*", Arity.BINARY), OperationKey("-", Arity.BINARY)), 4)
        assertEquals(opDirector.comparePriority(OperationKey("-", Arity.BINARY), OperationKey("*", Arity.BINARY)), -4)
    }

    @Test
    fun getRegisteredOperations() {
        assertEquals(opDirector.getRegisteredOperations(), "+\t- BINARY - LEFT - priority: 1\n" +
                "-\t- BINARY - LEFT - priority: 1\n" +
                "*\t- BINARY - LEFT - priority: 5\n" +
                "/\t- BINARY - LEFT - priority: 5\n" +
                "**\t- BINARY - RIGHT - priority: 9\n" +
                "++\t- UNARY - LEFT - priority: 9\n")
    }

    @Test
    fun getPriorities() {
        assertEquals(opDirector.getPriorities(),
            mapOf(OperationKey("+", Arity.BINARY) to 1,
                OperationKey("-", Arity.BINARY) to 1,
                OperationKey("*", Arity.BINARY) to 5,
                OperationKey("/", Arity.BINARY) to 5,
                OperationKey("**", Arity.BINARY) to 9,
                OperationKey("++", Arity.UNARY) to 9))
    }

    @Test
    fun getAssociativity() {
        assertEquals(opDirector.getAssociativity(OperationKey("+", Arity.BINARY)), Associativity.LEFT)
        assertEquals(opDirector.getAssociativity(OperationKey("-", Arity.BINARY)), Associativity.LEFT)
        assertEquals(opDirector.getAssociativity(OperationKey("*", Arity.BINARY)), Associativity.LEFT)
        assertEquals(opDirector.getAssociativity(OperationKey("/", Arity.BINARY)), Associativity.LEFT)
        assertEquals(opDirector.getAssociativity(OperationKey("**", Arity.BINARY)), Associativity.RIGHT)
        val exceptionMessage = assertThrows(Exception::class.java, {opDirector.getAssociativity(OperationKey("^", Arity.BINARY))})
        assertEquals(exceptionMessage, Exception("This operation doesn't exist."))
    }

    @Test
    fun calculate() {
        assertEquals(opDirector.calculate(OperationKey("+", Arity.BINARY), arrayOf(2.0, 3.5)), 5.5)
        assertEquals(opDirector.calculate(OperationKey("-", Arity.BINARY), arrayOf(2.0, 3.5)), -1.5)
        assertEquals(opDirector.calculate(OperationKey("*", Arity.BINARY), arrayOf(2.0, 3.5)), 7.0)
        assertEquals(opDirector.calculate(OperationKey("/", Arity.BINARY), arrayOf(12.0, 3.0)), 4.0)
        assertEquals(opDirector.calculate(OperationKey("**", Arity.BINARY), arrayOf(2.0, 3.0)), 8.0)
        assertEquals(opDirector.calculate(OperationKey("++", Arity.UNARY), arrayOf(2.0)), 3.0)
        val exceptionMessage1 = assertThrows(Exception::class.java,
            {opDirector.calculate(OperationKey("**", Arity.BINARY), arrayOf(3.0, 4.0))})
        assertEquals(exceptionMessage1, Exception("Operation \"++\" must be unary."))
        val exceptionMessage2 = assertThrows(Exception::class.java,
            {opDirector.calculate(OperationKey("+", Arity.BINARY), arrayOf(3.0, 4.0, 15.9))})
        assertEquals(exceptionMessage1, Exception("Operation \"+\" must be binary."))
    }
}