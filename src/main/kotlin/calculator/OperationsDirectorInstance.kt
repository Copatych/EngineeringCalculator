package calculator

import mymath.*
import kotlin.math.*

class OperationsDirectorInstance {
    val opDirector = OperationsDirector()

    init {
        opDirector.registerOperation(
            "-", { v -> -v[0] },
            Arity.UNARY, 25,
            Associativity.RIGHT
        )
        opDirector.registerOperation(
            "+", { v -> v[0] + v[1] },
            Arity.BINARY, 1,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "-", { v -> v[0] - v[1] },
            Arity.BINARY, 1,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "*", { v -> v[0] * v[1] },
            Arity.BINARY, 5,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "/", { v -> v[0] / v[1] },
            Arity.BINARY, 5,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "%", { v -> v[0] % v[1] },
            Arity.BINARY, 5,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "^", { v -> v[0].pow(v[1]) },
            Arity.BINARY, 9,
            Associativity.RIGHT
        )
        opDirector.registerOperation(
            "!", { v -> gamma(v[0] + 1) },
            Arity.UNARY, 13,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "<", { v -> if(v[0] < v[1]) 1.0 else -1.0 },
            Arity.BINARY, -3,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            ">", { v -> if(v[0] > v[1]) 1.0 else -1.0 },
            Arity.BINARY, -3,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "==", { v -> if(v[0] == v[1]) 1.0 else -1.0 },
            Arity.BINARY, -3,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            "<=", { v -> if(v[0] <= v[1]) 1.0 else -1.0 },
            Arity.BINARY, -3,
            Associativity.LEFT
        )
        opDirector.registerOperation(
            ">=", { v -> if(v[0] >= v[1]) 1.0 else -1.0 },
            Arity.BINARY, -3,
            Associativity.LEFT
        )
    }

}