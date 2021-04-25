package calculator
class FunctionsAdder : Preprocessor {
    override fun doProcessing(s: String): String {
        var sInternal = s
        for (f in functions) {
            var fWidth = f.name.length
            var i = 0
            while (true) {
                i = sInternal.indexOf(f.name, i)
                if (i == -1) break
                if ((i + fWidth) == sInternal.length || sInternal[i + fWidth] != '(') {
                    sInternal = sInternal.replaceRange(i, i + fWidth, f.process(listOf()))
                }
            }
        }
        return sInternal
    }
    private data class Function(val name: String, val description: String) {
        private val substrings: List<String> = description.split(Regex("""\[|\]"""))
            .filter { it.isNotEmpty() }

        fun process(args: List<String>) : String {
            var res = ""
            for (substring in substrings) {
                if (substring[0].isDigit()) {
                    res += insertArgs(substring, args)
                } else {
                    res += substring
                }
            }
            return res
        }

        private fun insertArgs(s: String, args: List<String>) : String {
            val argIndexes = s.split("..").map { it.toIntOrNull() }
            val firstIndex: Int
            if (argIndexes[0] == null || argIndexes.size > 2) {
                // TODO My Exception
                throw Exception("Internal error in FunctionsAdder.Function.insertArgs")
            } else {
                firstIndex = argIndexes[0]!!
            }
            if (argIndexes.size == 1) {
                return args[firstIndex]
            }
            var res = ""
            val lastIndex = if (argIndexes[1] == null) (args.size - 1) else argIndexes[1]!!
            for (i in firstIndex..lastIndex) {
                res += "${args[i]};"
            }
            return  res.dropLast(1)
        }
    }

    private val functions: ArrayList<Function> = arrayListOf()

    fun registerFunction(name: String, description: String) {
        /**
         * Examples:
         * registerFunction("renamed_sin", "sin") // just renaming
         * registerFunction("modified_sin", "sin([0] + pi)")
         * registerFunction("easy_sum", "[0] + [1]")
         * registerFunction("add", "sum([0..])")
         * registerFunction("F", "sum([0..4]) - [5]")
         * registerFunction("+", "-") // Bad idea
         */
        functions.add(Function(name, description))
    }

    fun getFunctionsWithPosition() : List<Pair<Int, String>> {
        return functions.mapIndexed { index, f -> index to f.name }
    }
}


