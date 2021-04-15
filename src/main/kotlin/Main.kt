import calculator.Lexer

fun main(args: Array<String>) {
//    printChristmasTree(15, 3, 2)
//    val string = "f f 2b25**2 + 3.14*(cos(3,14)-f(13; 12) )"
    val string = "3**2 / (sin(3.14) + 1,2)"
    val lexer = Lexer(string)
    println(string)
    println(lexer.tokens)
    println(lexer.erroredIndex)
    println(lexer.isCorrect())
    println(lexer.erroredIndex)
    println(lexer.tokensMap)
}