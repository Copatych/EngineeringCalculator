package calculator

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import calculator.DelegatingMutableListIterator.Direction

internal class DelegatingMutableListIteratorTest {

    private var l1 = mutableListOf<Int>(1, 2, 3, 4)
    private var l2 = mutableListOf<Int>(1, 2, 3, 4)
    private var i = DelegatingMutableListIterator(l1.listIterator())
    private var iReverse = DelegatingMutableListIterator(l2.listIterator(l2.size))

    @BeforeEach
    fun SetVars() {
        l1 = mutableListOf<Int>(1, 2, 3, 4)
        l2 = mutableListOf<Int>(1, 2, 3, 4)
        i = DelegatingMutableListIterator(l1.listIterator())
        iReverse = DelegatingMutableListIterator(l2.listIterator(l2.size), Direction.BACK)
    }

    @Test
    fun hasNext() {
        assertEquals(true, i.hasNext())
        assertEquals(false, iReverse.hasNext())
        iReverse.previous()
        assertEquals(false, iReverse.hasNext()) // Explanations similar as for hasPrevious for "i"
        iReverse.previous()
        assertEquals(true, iReverse.hasNext())
    }

    @Test
    operator fun next() {
        assertEquals(1, i.next())
        assertEquals(2, i.next())
    }

    @Test
    fun hasPrevious() {
        assertEquals(false, i.hasPrevious()) // The iterator is in the position "-1"
        i.next()
        assertEquals(false, i.hasPrevious()) // The iterator is in the position "0", so it hasn't previous
        assertEquals(true, iReverse.hasPrevious())
    }

    @Test
    fun previous() {
        assertEquals(4, iReverse.previous())
        assertEquals(3, iReverse.previous())
    }

    @Test
    fun getCurrentValue() {
        i.next() // The iterator starts in the position "-1"
        assertEquals(1, i.getCurrentValue())
        i.next()
        assertEquals(2, i.getCurrentValue())
        iReverse.previous() // The iterator starts in in the position out of size
        assertEquals(4, iReverse.getCurrentValue())
        iReverse.previous()
        assertEquals(3, iReverse.getCurrentValue())
    }

    @Test
    fun remove() {
        i.next() // Get first element
        i.remove()
        assertEquals(mutableListOf<Int>(2, 3, 4), l1)
        i.remove()
        assertEquals(mutableListOf<Int>(3, 4), l1)
        i.remove()
        assertEquals(mutableListOf<Int>(4), l1)

        iReverse.previous() // Get last element
        iReverse.remove()
        assertEquals(mutableListOf<Int>(1, 2, 3), l2)
        iReverse.remove()
        assertEquals(mutableListOf<Int>(1, 2), l2)

        val l3 = mutableListOf<Int>(1, 2, 3, 4)
        val i1 = DelegatingMutableListIterator(l3.listIterator())
        i1.move(2)
        i1.remove()
        assertEquals(mutableListOf<Int>(1, 3, 4), l3)
        i1.remove()
        assertEquals(mutableListOf<Int>(1, 4), l3)
    }

    @Test
    fun removeAndCheckGet() {
        i.next() // Get first element
        i.remove()
        assertEquals(mutableListOf<Int>(2, 3, 4), l1)
        assertEquals(2, i.getCurrentValue())
        assertEquals(3, i.next())
        i.remove()
        assertEquals(mutableListOf<Int>(2, 4), l1)
        assertEquals(4, i.getCurrentValue())
        i.remove()
        assertEquals(2, i.getCurrentValue())
        assertEquals(mutableListOf<Int>(2), l1)
        i.remove()
        assertEquals(mutableListOf<Int>(), l1)
    }

    @Test
    fun add() {
        i.add(0)
        assertEquals(mutableListOf<Int>(0, 1, 2, 3, 4), l1)
        i.add(-1)
        assertEquals(mutableListOf<Int>(0, -1, 1, 2, 3, 4), l1)
        i.next()
        i.add(5)
        assertEquals(mutableListOf<Int>(0, -1, 1, 5, 2, 3, 4), l1)
        i.remove()
        i.add(6)
        assertEquals(mutableListOf<Int>(0, -1, 1, 2, 6, 3, 4), l1)

        iReverse.previous()
        iReverse.add(5)
        assertEquals(mutableListOf<Int>(1, 2, 3, 4, 5), l2)
        iReverse.remove()
        iReverse.add(6)
        assertEquals(mutableListOf<Int>(1, 2, 3, 4, 6), l2)
    }

    @Test
    fun addList() {
        i.add(listOf(0, -1, -2))
        assertEquals(mutableListOf<Int>(0, -1, -2, 1, 2, 3, 4), l1)
        assertEquals(-2, i.getCurrentValue())
        i.add(listOf())
        assertEquals(mutableListOf<Int>(0, -1, -2, 1, 2, 3, 4), l1)
        i.add(listOf(-3))
        assertEquals(mutableListOf<Int>(0, -1, -2, -3, 1, 2, 3, 4), l1)
        i.add(listOf(-4, -5), Direction.BACK)
        assertEquals(mutableListOf<Int>(0, -1, -2, -4, -5, -3, 1, 2, 3, 4), l1)
    }

    @Test
    fun addListInEmpty() {
        i.next()
        repeat(l1.size) { i.remove() }

        i.add(listOf(0, -1, -2))
        assertEquals(mutableListOf<Int>(0, -1, -2), l1)
    }

    @Test
    fun addInBeginningAfterRemove() {
        i.next()
        i.remove()
        i.add(listOf(0, -1, -2), Direction.BACK)
        assertEquals(mutableListOf<Int>(0, -1, -2, 2, 3, 4), l1)
    }

    @Test
    fun addWithDirection() {
        i.add(0, Direction.FORWARD)
        assertEquals(0, i.getCurrentValue())
        assertEquals(mutableListOf<Int>(0, 1, 2, 3, 4), l1)
        i.add(-1, Direction.BACK)
        assertEquals(mutableListOf<Int>(-1, 0, 1, 2, 3, 4), l1)
        assertEquals(-1, i.getCurrentValue())
        i.move(3)
        i.add(5)
        assertEquals(mutableListOf<Int>(-1, 0, 1, 2, 5, 3, 4), l1)
        i.add(6)
        assertEquals(mutableListOf<Int>(-1, 0, 1, 2, 5, 6, 3, 4), l1)

        iReverse.add(5, Direction.BACK)
        assertEquals(mutableListOf<Int>(1, 2, 3, 4, 5), l2)
        iReverse.add(6, Direction.BACK)
        assertEquals(mutableListOf<Int>(1, 2, 3, 4, 6, 5), l2)
    }

    @Test
    fun move() {
        i.next()
        assertEquals(null, i.move(l1.size))
        assertEquals(3, i.move(2))
        assertEquals(3, i.move(0))
        assertEquals(2, i.move(-1))
        assertEquals(4, i.move(2))
        assertEquals(1, i.move(-3))
        assertEquals(null, i.move(-1))
        assertEquals(null, i.move(-30))
    }

    @Test
    fun get() {
        i.next()
        assertEquals(2, i.get(1))
        assertEquals(3, i.get(2))
        assertEquals(1, i.get(0))
        assertEquals(null, i.get(-1))
        i.move(2)
        assertEquals(3, i.get(0))
        assertEquals(2, i.get(-1))
        assertEquals(1, i.get(-2))
        assertEquals(4, i.get(1))
        assertEquals(null, i.get(2))

    }

    @Test
    fun set() {
        i.next()
        i.set(-1)
        assertEquals(-1, i.getCurrentValue())
    }
}