import calculator.StringCalculatorApp

fun main(args: Array<String>) {
    val calculatorApp = StringCalculatorApp()
    println(StringCalculatorApp.helloInfo)
    while (true) {
        try {
            val expr = readLine() ?: continue
            println(calculatorApp.process(expr))
        } catch (e: Exception) {
            println(e.message)
        }
    }
}