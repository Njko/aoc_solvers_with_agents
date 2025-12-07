import java.io.File

fun solveBatteryJoltagePart2(input: String): Long {
    val lines = input
        .replace("\r\n", "\n").replace("\r", "\n")
        .split('\n')
        .filter { it.isNotEmpty() }

    var totalJoltage = 0L

    for (line in lines) {
        println("\n=== Processing: $line ===")
        // Greedy approach: for each position in result, pick the largest digit
        // that still leaves enough remaining digits
        val result = StringBuilder()
        val remaining = line.toMutableList()
        val needed = 12

        for (i in 0 until needed) {
            val minRemainingNeeded = needed - i - 1
            var bestIdx = -1
            var bestDigit = '0'

            for (j in remaining.indices) {
                val remainingAfter = remaining.size - j - 1
                if (remainingAfter >= minRemainingNeeded) {
                    if (bestIdx == -1 || remaining[j] > bestDigit) {
                        bestDigit = remaining[j]
                        bestIdx = j
                    }
                }
            }

            if (bestIdx != -1) {
                println("Position $i: selecting '${remaining[bestIdx]}' from index $bestIdx (remaining after: ${remaining.size - bestIdx - 1})")
                result.append(remaining[bestIdx])
                remaining.removeAt(bestIdx)
            }
        }

        val joltageStr = result.toString()
        println("Result: $joltageStr")
        val joltage = joltageStr.toLongOrNull() ?: 0L
        println("Joltage: $joltage")
        totalJoltage += joltage
    }

    return totalJoltage
}

fun main() {
    val example = File("test_day3_example.txt").readText()
    val result = solveBatteryJoltagePart2(example)
    println("\n\n=== FINAL ===")
    println("Total: $result")
    println("Expected: 3121910778619")
    println("Match: ${result == 3121910778619L}")
}
