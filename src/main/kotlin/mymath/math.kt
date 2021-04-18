package mymath

import kotlin.math.*

fun gamma(x: Double) : Double {
    /*
     * Lanczos approximation
     * https://en.wikipedia.org/wiki/Lanczos_approximation
     */

    var xLocal = x
    val res: Double
    val p = arrayOf(676.5203681218851,
        -1259.1392167224028,
        771.32342877765313,
        -176.61502916214059,
        12.507343278686905,
        -0.13857109526572012,
        9.9843695780195716e-6,
        1.5056327351493116e-7)
    if (xLocal< 0.5) {
        res = PI / (sin(PI * xLocal) * gamma(1 - xLocal))
    } else {
        xLocal -= 1
        var t = 0.99999999999980993
        for ((i, pval) in p.withIndex()) {
            t += pval / (xLocal + i + 1)
        }
        val l = xLocal + p.size - 0.5
        res = sqrt(2 * PI) * l.pow(xLocal + 0.5) * exp(-l) * t
    }
    return res
}