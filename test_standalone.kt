import java.io.File

fun solveExample(input: String): Long {
    val lines = input
        .replace("\r\n", "\n").replace("\r", "\n")
        .split('\n')
        .filter { it.isNotEmpty() }

    var totalJoltage = 0L

    for ((lineNum, line) in lines.withIndex()) {
        println("\n=== Line ${lineNum + 1}: $line ===")
        val digits = line.toList()
        val selected = mutableSetOf<Int>()
        val result = StringBuilder()

        for (resultPos in 0 until 12) {
            val needed = 12 - resultPos - 1
            var bestIdx = -1
            var bestDigit = '0'

            for (i in digits.indices) {
                if (i in selected) continue

                val unusedAfter = (i + 1 until digits.size).count { it !in selected }

                if (unusedAfter >= needed) {
                    if (digits[i] > bestDigit) {
                        bestDigit = digits[i]
                        bestIdx = i
                    }
                }
            }

            if (bestIdx >= 0) {
                selected.add(bestIdx)
                result.append(digits[bestIdx])
            }
        }

        println("Result: $result")
        val joltage = result.toString().toLongOrNull() ?: 0L
        println("Joltage: $joltage")
        totalJoltage += joltage
    }

    println("\n=== TOTAL: $totalJoltage ===")
    return totalJoltage
}

fun main() {
    val input = File("test_example.txt").readText()
    val result = solveExample(input)

    val expected = listOf(987654321111L, 811111111119L, 434234234278L, 888911112111L)
    println("\nExpected results:")
    expected.forEach { println(it) }
    println("Expected total: ${expected.sum()}")
    println("\nActual total: $result")
    println("Match: ${result == expected.sum()}")
}
