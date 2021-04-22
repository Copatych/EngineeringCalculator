package calculator

import kotlin.math.absoluteValue
import kotlin.math.sign

class DelegatingMutableListIterator<T> (private val it: MutableListIterator<T>) : MutableListIterator<T> by it {
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

    override fun hasNext(): Boolean {
        return doFunToDirection(it::hasNext, Direction.FORWARD)
    }

    override fun next(): T {
        return doFunToDirection(it::next, Direction.FORWARD)
    }

    override fun hasPrevious(): Boolean {
        return doFunToDirection(it::hasPrevious, Direction.BACK)
    }

    override fun previous(): T {
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

    override fun remove() {
        direction = if (it.hasPrevious()) Direction.FORWARD else Direction.BACK
        it.remove()
    }

    fun add(element: T, direct: Direction) {
        if (direct != direction) {
            changeDirection()
        }
        add(element)
    }

    override fun add(element: T) {
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

    override fun set(element: T) {
        getCurrentValue()
        it.set(element)
    }
}