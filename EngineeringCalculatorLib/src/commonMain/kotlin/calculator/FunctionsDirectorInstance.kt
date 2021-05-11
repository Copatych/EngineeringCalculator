package calculator

import kotlin.math.*
import mymath.*

class FunctionsDirectorInstance {
    val funcDirector = FunctionsDirector()
    init {
        funcDirector.registerFunction("sum", null,
            { v -> v.reduce { acc, vi -> acc + vi } })
        funcDirector.registerFunction("pi", 0,
            { PI })
        funcDirector.registerFunction("PI", 0,
            { PI })
        funcDirector.registerFunction("e", 0,
            { E })
        funcDirector.registerFunction("E", 0,
            { E })
        funcDirector.registerFunction("sin", 1,
            { v -> sin(v[0]) })
        funcDirector.registerFunction("cos", 1,
            { v -> cos(v[0]) })
        funcDirector.registerFunction("tan", 1,
            { v -> tan(v[0]) })
        funcDirector.registerFunction("asin", 1,
            { v -> asin(v[0]) })
        funcDirector.registerFunction("acos", 1,
            { v -> acos(v[0]) })
        funcDirector.registerFunction("atan", 1,
            { v -> atan(v[0]) })
        funcDirector.registerFunction("sinh", 1,
            { v -> sinh(v[0]) })
        funcDirector.registerFunction("cosh", 1,
            { v -> cosh(v[0]) })
        funcDirector.registerFunction("tanh", 1,
            { v -> tanh(v[0]) })
        funcDirector.registerFunction("asinh", 1,
            { v -> asinh(v[0]) })
        funcDirector.registerFunction("acosh", 1,
            { v -> acosh(v[0]) })
        funcDirector.registerFunction("atanh", 1,
            { v -> atanh(v[0]) })
        funcDirector.registerFunction("hypot", 2,
            { v -> hypot(v[0], v[1]) })
        funcDirector.registerFunction("exp", 1,
            { v -> exp(v[0]) })
        funcDirector.registerFunction("log", 2,
            { v -> log(v[0], v[1]) })
        funcDirector.registerFunction("ln", 1,
            { v -> ln(v[0]) })
        funcDirector.registerFunction("log10", 1,
            { v -> log10(v[0]) })
        funcDirector.registerFunction("log2", 1,
            { v -> log2(v[0]) })
        funcDirector.registerFunction("ceil", 1,
            { v -> ceil(v[0]) })
        funcDirector.registerFunction("floor", 1,
            { v -> floor(v[0]) })
        funcDirector.registerFunction("truncate", 1,
            { v -> truncate(v[0]) })
        funcDirector.registerFunction("round", 1,
            { v -> round(v[0]) })
        funcDirector.registerFunction("abs", 1,
            { v -> abs(v[0]) })
        funcDirector.registerFunction("sign", 1,
            { v -> sign(v[0]) })
        funcDirector.registerFunction("min", 2,
            { v -> min(v[0], v[1]) })
        funcDirector.registerFunction("max", 2,
            { v -> min(v[0], v[1]) })
        funcDirector.registerFunction("pow", 2,
            { v -> v[0].pow(v[1]) })
        funcDirector.registerFunction("withSign", 1,
            { v -> v[0].withSign(v[1]) })
        funcDirector.registerFunction("ulp", 1,
            { v -> v[0].ulp })
        funcDirector.registerFunction("nextUp", 1,
            { v -> v[0].nextUp() })
        funcDirector.registerFunction("nextDown", 1,
            { v -> v[0].nextDown() })
        funcDirector.registerFunction("nextTowards", 2,
            { v -> v[0].nextTowards(v[1]) })
        //---------------------------------------------------------------
        funcDirector.registerFunction("if", 3,
            { v -> if(v[0] >= 0) v[1] else v[2] })
        funcDirector.registerFunction("gamma", 1,
            { v -> gamma(v[0]) })
    }
}