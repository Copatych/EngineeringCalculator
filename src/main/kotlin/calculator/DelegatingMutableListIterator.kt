package calculator

import kotlin.math.absoluteValue
import kotlin.math.sign

class DelegatingMutableListIterator<T> (private val it: MutableListIterator<T>) {
    enum class Direction {
        FORWARD, BACK
    }

    private var direction: Direction = Direction.FORWARD

    private fun changeDirectionValue() {
        direction = when(direction) {
            Direction.FORWARD -> Direction.BACK
            Direction.BACK -> Direction.FORWARD
        }
    }
    private fun changeDirection() {
        when(direction) {
            Direction.FORWARD -> it.previous()
            Direction.BACK -> it.next()
        }
        changeDirectionValue()
    }

    private fun <P> doFunToDirection(func: () -> P, dir: Direction) : P {
        if (direction == dir) {
            return func()
        } else {
            changeDirection()
            return func()
        }
    }

    fun hasNext(): Boolean {
        return doFunToDirection(it::hasNext, Direction.FORWARD)
    }

    fun next(): T {
        return doFunToDirection(it::next, Direction.FORWARD)
    }

    fun hasPrevious(): Boolean {
        return doFunToDirection(it::hasPrevious, Direction.BACK)
    }

    fun previous(): T {
        return doFunToDirection(it::previous, Direction.BACK)
    }

    fun getCurrentValue() : T {
        val res = when (direction) {
            Direction.FORWARD -> it.previous()
            Direction.BACK -> it.next()
        }
        changeDirectionValue()
        return res
    }

    fun remove() {
        direction = if (it.hasPrevious()) Direction.FORWARD else Direction.BACK
        it.remove()
    }

    fun add(element: T, direct: Direction) {
        if (direct != direction) {
            changeDirection()
        }
        add(element)
    }

    fun add(element: T) {
        if (!it.hasPrevious() && !it.hasNext()) { // if empty
            direction = Direction.FORWARD
        }
        it.add(element)
    }

    fun move(n: Int) : T? {
        if (n == 0) return getCurrentValue()
        var moveN = 0
        var res: T? = null
        for (i in 1..n.absoluteValue) {
            res = if(n > 0 && hasNext()) {
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

    fun get(shift: Int) : T? {
        val res = move(shift)
        if (res != null) move(-shift)
        return res
    }

    fun set(element: T) {
        getCurrentValue()
        it.set(element)
    }
}