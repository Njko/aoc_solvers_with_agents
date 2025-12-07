import java.io.File

fun solveVerticalMathWorksheetPart2(input: String): Long {
    val lines = input
        .replace("\r\n", "\n").replace("\r", "\n")
        .split('\n')
        .filter { it.isNotEmpty() }

    if (lines.isEmpty()) return 0

    val width = lines.maxOf { it.length }

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

    var grandTotal = 0L

    for (problem in problems) {
        if (problem.isEmpty()) continue

        val numbers = mutableListOf<Long>()
        var operator: Char? = null

        val reversedProblem = problem.reversed()
        for (column in reversedProblem) {
            val lastChar = column.lastOrNull()
            if (lastChar == '*' || lastChar == '+') {
                operator = lastChar
            }

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
            }
        }

        if (numbers.isNotEmpty() && operator != null) {
            val result = when (operator) {
                '+' -> numbers.sum()
                '*' -> numbers.reduce { acc, n -> acc * n }
                else -> 0L
            }
            grandTotal += result
        }
    }

    return grandTotal
}

fun main() {
    val input = File("day6_input.txt").readText()
    val result = solveVerticalMathWorksheetPart2(input)
    println("Result: $result")
}
