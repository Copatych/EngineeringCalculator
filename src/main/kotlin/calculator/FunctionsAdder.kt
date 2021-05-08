package calculator

import calculator.DelegatingMutableListIterator.Direction
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

class FunctionsAdder : TokenPreprocessor {
    @Serializable
    data class FunctionFullDescription(val name: String, val description: String, val comment: String = "")

    override fun doProcessing(s: List<Token>): List<Token> {
        val sInternal = s.toMutableList()
        for (f in functions) {
            var i = DelegatingMutableListIterator(sInternal.listIterator())
            while (i.hasNext()) {
                var curValue = i.next()
                if (curValue == Token(f.name)) {
                    val replacedSeq = if (!i.hasNext() || (i.hasNext() && i.get(1) != Token("("))) {
                        f.process(listOf())
                    } else processFuncWithArguments(i, f)
                    if (i.getCurrentValue() !== curValue) i.previous()
                    val addDirection = if (i.hasNext()) Direction.BACK else Direction.FORWARD
                    i.remove()
                    i.add(replacedSeq, addDirection)
                }
            }
        }
        return sInternal
    }

    private data class Function(val name: String, val description: List<Token>) {
        private val subTokensSeq: List<List<Token>> = run {
            val separators =
                description.mapIndexedNotNull { idx, t -> if (t == Token("[") || t == Token("]")) idx else null }
            var res = arrayListOf<List<Token>>()
            var fromSep = 0
            for (toSep in separators + listOf<Int>(description.size)) {
                res.add(description.subList(fromSep, toSep).filter { it != Token("[") && it != Token("]") })
                fromSep = toSep
            }
            res.removeIf { it.isEmpty() }
            res
        }

        fun process(args: List<List<Token>>): List<Token> {
            val res = mutableListOf<Token>()
            for (tokensSeq in subTokensSeq) {
                res += if (tokensSeq[0].abbreviation == Token.Abbreviation.N) {
                    insertArgs(tokensSeq, args)
                } else {
                    tokensSeq
                }
            }
            return res
        }

        private fun insertArgs(s: List<Token>, args: List<List<Token>>): List<Token> {
            val firstIndex: Int
            if (s.isEmpty() || s.size > 3 || s[0].abbreviation != Token.Abbreviation.N) {
                // TODO My Exception
                throw Exception("Internal error in FunctionsAdder.Function.insertArgs")
            } else {
                firstIndex = s[0].value.toInt()
            }
            if (s.size == 1) {
                return args[firstIndex]
            }
            if (s[1] != Token("-") || (s.size == 3 && s[2].abbreviation != Token.Abbreviation.N)) {
                // TODO My Exception
                throw Exception("Internal error in FunctionsAdder.Function.insertArgs")
            }
            val res = mutableListOf<Token>()
            val lastIndex = if (s.size == 2) (args.size - 1) else s[2].value.toInt()
            for (i in firstIndex..lastIndex) {
                res += args[i] + Token(";")
            }
            return res.dropLast(1)
        }
    }

    private val functions: ArrayList<Function> = arrayListOf()

    private val functionsFullDescription: ArrayList<FunctionFullDescription> = arrayListOf()

    fun registerFunction(name: String, description: String, comment: String = "") {
        /**
         * Examples:
         * registerFunction("renamed_sin", "sin", "just renaming of sin")
         * registerFunction("modified_sin", "sin([0] + pi)")
         * registerFunction("easy_sum", "[0] + [1]")
         * registerFunction("add", "sum([0-])")
         * registerFunction("F", "sum([0-4]) - [5]")
         * registerFunction("+", "-") // Bad idea
         */
        val nameSeparated = name.split(Regex("""\s+""")).filterNot { it.isNullOrBlank() }
        if (nameSeparated.size > 1 || nameSeparated.isEmpty()) {
            throw Exception("The name of registered function must be without spaces")
        }
        val nameBase = nameSeparated[0]
        if (functionsFullDescription.find { it.name == nameBase } != null) {
            // TODO Replace function or throw exception? Does the ordered set exist?
            // TODO My Exceptions
            throw Exception("Function $nameBase already exist.")
        }
        val lexer = Lexer(description)
        if (lexer.isCorrect()) {
            functions.add(Function(nameBase, lexer.tokens))
            functionsFullDescription.add(FunctionFullDescription(nameBase, description, comment))
        } else {
            // TODO My Exceptions
            throw Exception("Error in description for symbol '${description[lexer.erroredIndex!!]}' in position ${lexer.erroredIndex}")
        }
    }

    fun getFunctionsNames(): List<String> {
        return functions.map { f -> f.name }
    }

    fun getFunctionsFullDescription(): List<FunctionFullDescription> {
        try {
            return functions.map { f -> functionsFullDescription.find { it.name == f.name }!! }
        } catch (e: Exception) {
            // TODO My Exceptions
            throw Exception("Error in FunctionsAdder.getFunctionsWithPositionAndDescription")
        }
    }

    private fun processFuncWithArguments(i: DelegatingMutableListIterator<Token>, f: Function): List<Token> {
        try {
            i.next()
            i.remove() // remove open parenthesis
            val res = mutableListOf<List<Token>>()
            var curArg = mutableListOf<Token>()
            var pc = 1 // Parenthesis Counter
            while (pc > 0) {
                val curToken = i.getCurrentValue()
                i.remove()
                when (curToken) {
                    Token("(") -> pc++
                    Token(")") -> {
                        pc--
                        if (pc == 0) {
                            res.add(curArg)
                            break
                        }
                    }
                    Token(";") -> {
                        res.add(curArg)
                        curArg = mutableListOf<Token>()
                        continue
                    }
                }
                curArg.add(curToken)
            }
            return f.process(res)
        } catch (e: Exception) {
            // TODO My Exceptions. Check, if threw exception is mine
            throw Exception("Problem for arguments in ${f.name} function")
        }
    }

    fun serialize() : String = Json.encodeToString(getFunctionsFullDescription())

    companion object {
        fun deserialize(s: String): FunctionsAdder {
            if (s.isBlank()) return FunctionsAdder()
            val functionsFullDescription: List<FunctionFullDescription> = Json.decodeFromString(s)
            val funcAdder = FunctionsAdder()
            for (ffd in functionsFullDescription) {
                funcAdder.registerFunction(ffd.name, ffd.description, ffd.comment)
            }
            return funcAdder
        }
    }
}