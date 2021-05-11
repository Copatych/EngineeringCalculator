package calculator

import kotlin.math.absoluteValue
import kotlin.math.sign

class DelegatingMutableListIterator<T>(
    private val it: MutableListIterator<T>,
    private var direction: Direction = Direction.FORWARD
) {
    enum class Direction {
        FORWARD, BACK
    }

    private fun changeDirectionValue() {
        direction = when (direction) {
            Direction.FORWARD -> Direction.BACK
            Direction.BACK -> Direction.FORWARD
        }
    }

    private fun changeDirection() {
        when (direction) {
            Direction.FORWARD -> it.previous()
            Direction.BACK -> it.next()
        }
        changeDirectionValue()
    }

    private fun <P> doFunToDirection(func: () -> P, dir: Direction): P {
        if (direction == dir) {
            return func()
        } else {
            changeDirection()
            return func()
        }
    }

    fun hasNext(): Boolean {
        if (!it.hasNext()) return false
        return doFunToDirection(it::hasNext, Direction.FORWARD)
    }

    fun next(): T {
        return doFunToDirection(it::next, Direction.FORWARD)
    }

    fun hasPrevious(): Boolean {
        if (!it.hasPrevious()) return false
        return doFunToDirection(it::hasPrevious, Direction.BACK)
    }

    fun previous(): T {
        return doFunToDirection(it::previous, Direction.BACK)
    }

    fun getCurrentValue(): T {
        val res = when (direction) {
            Direction.FORWARD -> it.previous()
            Direction.BACK -> it.next()
        }
        changeDirectionValue()
        return res
    }

    fun remove() {
        getCurrentValue() // If remove more than 1 element in a row, there will be problems without it
        it.remove()
        if (it.hasNext()) {
            it.next()
            direction = Direction.FORWARD
        } else if (it.hasPrevious()) {
            it.previous()
            direction = Direction.BACK
        } else direction = Direction.FORWARD
    }

    fun add(element: T, direction: Direction) {
        if (!it.hasPrevious() && !it.hasNext()) { // if empty
            this.direction = Direction.FORWARD
        } else if (direction != this.direction) {
            changeDirection()
        }
        it.add(element)
        if (direction == Direction.BACK && it.hasPrevious()) it.previous()
    }

    fun add(element: T) {
        add(element, Direction.FORWARD)
    }

    fun add(l: List<T>, direction: Direction = Direction.FORWARD) {
        if (l.isEmpty()) return
        if (!it.hasNext() && !it.hasPrevious()) {
            add(l[0])
        } else add(l[0], direction)
        l.slice(1.until(l.size)).forEach { add(it) }
    }

    fun move(n: Int): T? {
        /**
         * Don't use it when just starting an iteration from the beginning
         */
        if (n == 0) return getCurrentValue()
        val ifZeroPosition = (it.nextIndex() == 0)
        if (ifZeroPosition && n < 0) return null
        var moveN = 0
        var res: T? = null
        for (i in 1..n.absoluteValue) {
            res = if (n > 0 && hasNext()) {
                moveN = i
                next()
            } else if (n < 0 && hasPrevious()) {
                moveN = i
                previous()
            } else {
                null
            }
        }
        if (res == null) move(n.sign * -1 * moveN) // undo move
        return res
    }

    fun get(shift: Int): T? {
        /**
         * Don't use it when just starting an iteration from the beginning
         */
        val res = move(shift)
        if (res != null) move(-shift)
        return res
    }

    fun set(element: T) {
        getCurrentValue()
        it.set(element)
    }
}