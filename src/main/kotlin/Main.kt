import calculator.StringCalculatorApp

fun main(args: Array<String>) {
    val calculatorApp = StringCalculatorApp()
    while (true) {
        val expr = readLine()?: continue
        println(calculatorApp.process(expr))
    }
}