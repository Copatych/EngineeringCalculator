package mymath

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

import kotlin.math.*

internal class MathKtTest {

    @Test
    fun gamma() {
        val epsilon = 1e-7
        assertEquals(1.0, gamma(1.0), epsilon)
        assertEquals(1.0, gamma(2.0), epsilon)
        assertEquals(2.0, gamma(3.0), epsilon)
        assertEquals(6.0, gamma(4.0), epsilon)
        assertEquals(24.0, gamma(5.0), epsilon)
        assertEquals(120.0, gamma(6.0), epsilon)
        assertEquals(sqrt(PI), gamma(0.5), epsilon)
        assertEquals(0.5 * sqrt(PI), gamma(1.5), epsilon)
        assertEquals(-2 * sqrt(PI), gamma(-0.5), epsilon)
        assertEquals(4.0 / 3.0 * sqrt(PI), gamma(-1.5), epsilon)
    }
}