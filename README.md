# Engineering Calculator

This library is written from scratch in Kotlin and allows multi-platform use. The library itself is directly located in
EngineeringCalculatorLib. Simple applications based on this library are located in StringAppJS, StringAppJVM,
StringAppNative. Compiled files are located in release section on github.

StringAppJS [try](https://www.education-blog.ru/EngineeringCalculator/StringAppJS.html) without downloading.

## Examples of processed expressions

This examples you can repeat in StringApp (browser, JVM or native).

```1+1e-2```

* 1.01

```round(13 / sin(pi / 2) + 1.3)```

* 14

```if(round(sin(pi))==0; 1; -1)```

* 1

```!!!>> register function myCos !!!>> if((([0] + pi / 2) % (2 * pi)) < pi; 1; -1) * (1 - sin([0])^2)^0.5 !!!>> Bad but correct formula of cosine```

* The function is registered

```myCos(pi)```

* -1

```if(myCos(pi) == cos(pi), 1, -1)```

* Error for symbol ',' in position 23

```if(myCos(pi) == cos(pi); 1; -1)```

* 1

This factorial is calculated as gamma(n+1). For the implementation of gamma,
see [it](./EngineeringCalculatorLib/src/commonMain/kotlin/mymath/math.kt)

```7!```

* 5040.000000000011

```round(7!)```

* 5040

```round(6.5!)```

* 1871

```1 +- 2```

* Operation "OperationKey(name=+-, arity=BINARY)" does not exist.

# Understanding the Library

Let's start understanding the library with a simplified working example of a calculator that in a loop reads an
expression and print the result.

```kotlin
import calculator.*

fun main() {
    val calculatorEngine = CalculatorEngine(
        FunctionsDirectorInstance().funcDirector,
        OperationsDirectorInstance().opDirector
    )
    while (true) {
        val expr = readLine() ?: ""
        val lexer = Lexer(expr)
        if (lexer.isCorrect()) {
            var tokens: List<Token> = lexer.tokens
            val res: Double? = calculatorEngine.calculate(tokens)
            println(res)
        }
    }
}
```

CalculatorEngine constructor gets FunctionsDirector and OperationsDirector type parameters. It's a "system" functions
and operations for calculator. Function has a name, number of arguments and description. Operation has a name,
description, arity, priority, associativity.

CalculatorEngine.calculate operate with List of Tokens and return resulting value.
The Token is represented by a number, a function, an operation, and a special symbol.

You can generate List of Tokens directly or by using Lexer that take a string and returns a List of Tokens.

But without scalability, the calculator wouldn't be so cool. 
User can't modify the FunctionsDirector and OperationsDirector, 
therefore he can only impact to expression. 
Function adding is implemented as preprocessing of List&lt;Token&gt;. 
For more information, see [FunctionsAdder](./EngineeringCalculatorLib/src/commonMain/kotlin/calculator/FunctionsAdder.kt)
and method FunctionsAdder.registerFunction.

## CalculatorEngine

To understand the principles of evaluation, you should imagine a two-ordered mutable list
and iterator that can move forward and back.
Being in a certain position (by iterator), you can determine whether it is possible 
to simplify the expression by looking only at neighboring tokens.
As soon as the iterator reaches the end of the list, it starts moving from the beginning.
For example, if you are processing expression "1+2+3" and in position of first '+',
you see if leftToken and rightToken are numbers and compare priorities with neighbour operations, 
if all is well, then process "1+2" to "3" and are getting "3+3" and then "6".

More complex example:

1 | + | 2 | * | sin| ( | pi | ) | - | 3 | !
---|---|---|---|---|---|---|---|---|---|---
N  | O | N | O | F | S | F | S | O | N | O
   |   |   |  |  |  |  | i  |   |   |  |

->

1 | + | 2 | * | sin| ( | 3.14... | ) | - | 3 | !
---|---|---|---|---|---|---|---|---|---|---
N | O | N | O | F| S | N | S | O | N | O
|   |   |  |  |  |  |   |   |   |  | i

->

1 | + | 2 | * | sin| ( | 3.14... | ) | - | 6
---|---|---|---|---|---|---|---|---|---
N | O | N | O | F| S | N | S | O | N
|   |   |  |  | i |  |   |   |   |  | 

->

1 | + | 2 | * | 0 | - | 6
---|---|---|---|---|---|---
N | O | N | O | N | O | N
|   |   |  | i |  |  |   

->

1 | + | 0 | - | 6
---|---|---|---|---
N | O | N | O | N
|   |   |  | i |  |

->

1 | + | -6
---|---|---
N | O | N
|   | i  |  

->

|-5 |
|---|
| N | 
| i|

### Some ideas about priorities

- Parentheses and ";" are reserved characters;
- Brackets are the highest priority;
- "; "is used only in functions (together with brackets);
- Functions are used with brackets, the function is located to the left of the brackets. 
  If a function has no parentheses, then it is a function without arguments.

### Some ideas about tokens recognition

- Numbers: start with a digit or with a minus sign
- Functions: start with a letter, contain letters, numbers
- Operators: a sequence of one or more characters !$%^&*-+=?<>\|/
- Special symbols: single characters ();[]

See the specific implementation in the [Token.kt](./EngineeringCalculatorLib/src/commonMain/kotlin/calculator/Token.kt)