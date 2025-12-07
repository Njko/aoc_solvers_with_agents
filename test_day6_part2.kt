fun solveVerticalMathWorksheetPart2Debug(input: String): Long {
    val lines = input
        .replace("\r\n", "\n").replace("\r", "\n")
        .split('\n')
        .filter { it.isNotEmpty() }

    if (lines.isEmpty()) return 0

    // Find the width of the input (max line length)
    val width = lines.maxOf { it.length }

    // Transpose to get columns
    val columns = mutableListOf<MutableList<Char>>()
    for (col in 0 until width) {
        val column = mutableListOf<Char>()
        for (line in lines) {
            if (col < line.length) {
                column.add(line[col])
            }
        }
        columns.add(column)
    }

    println("Total columns: ${columns.size}")
    for ((i, col) in columns.withIndex()) {
        println("Column $i: ${col.joinToString("")}")
    }

    // Group consecutive non-space columns into problems
    val problems = mutableListOf<List<List<Char>>>()
    var currentProblem = mutableListOf<List<Char>>()

    for (column in columns) {
        val isAllSpaces = column.all { it == ' ' }
        if (isAllSpaces) {
            if (currentProblem.isNotEmpty()) {
                problems.add(currentProblem.toList())
                currentProblem = mutableListOf()
            }
        } else {
            currentProblem.add(column)
        }
    }
    if (currentProblem.isNotEmpty()) {
        problems.add(currentProblem.toList())
    }

    println("\nTotal problems: ${problems.size}")

    // Solve each problem
    var grandTotal = 0L

    for ((probIdx, problem) in problems.withIndex()) {
        if (problem.isEmpty()) continue

        println("\n=== Problem $probIdx (${problem.size} columns) ===")
        val numbers = mutableListOf<Long>()
        var operator: Char? = null

        // Process columns right-to-left
        for (colIdx in problem.indices.reversed()) {
            val column = problem[colIdx]

            print("Column $colIdx (RTL): [${column.joinToString("")}]")

            // Check if this column contains the operator (in last position)
            val lastChar = column.lastOrNull()
            if (lastChar == '*' || lastChar == '+') {
                operator = lastChar
                print(" -> operator='$operator'")
            }

            // Read column top-to-bottom to form number (top = most significant)
            val digitChars = mutableListOf<Char>()
            for (rowIdx in column.indices) {
                val ch = column[rowIdx]
                if (ch.isDigit()) {
                    digitChars.add(ch)
                }
            }

            if (digitChars.isNotEmpty()) {
                val number = digitChars.joinToString("").toLongOrNull() ?: 0L
                numbers.add(number)
                print(", number=$number")
            }
            println()
        }

        // Apply operation
        if (numbers.isNotEmpty() && operator != null) {
            println("Numbers: $numbers, Operator: $operator")
            val result = when (operator) {
                '+' -> numbers.sum()
                '*' -> numbers.reduce { acc, n -> acc * n }
                else -> 0L
            }
            println("Result: $result")
            grandTotal += result
        }
    }

    return grandTotal
}

fun main() {
    val example = """123 328  51 64
 45 64  387 23
  6 98  215 314
*   +   *   +  """

    println("Input:")
    println(example)
    println("\n" + "=".repeat(50))

    val result = solveVerticalMathWorksheetPart2Debug(example)
    println("\n" + "=".repeat(50))
    println("Final result: $result (expected: 3263827)")
}
