fun solveBatteryJoltagePart2(input: String): Long {
    val lines = input
        .replace("\r\n", "\n").replace("\r", "\n")
        .split('\n')
        .filter { it.isNotEmpty() }

    var totalJoltage = 0L

    for (line in lines) {
        println("Processing: $line")
        // To maximize, we want the 12 largest digits in order
        // Create list of (digit, originalIndex) pairs
        val digits = line.mapIndexed { idx, ch -> Pair(ch, idx) }

        // Sort by digit value descending, then by original index ascending
        val sorted = digits.sortedWith(compareByDescending<Pair<Char, Int>> { it.first }.thenBy { it.second })

        println("Sorted: $sorted")

        // Take the 12 largest, then sort by original index to maintain order
        val selected = sorted.take(12).sortedBy { it.second }

        println("Selected: $selected")

        val joltageStr = selected.map { it.first }.joinToString("")
        println("Joltage string: $joltageStr")
        val joltage = joltageStr.toLongOrNull() ?: 0L
        println("Joltage: $joltage")
        totalJoltage += joltage
    }

    return totalJoltage
}

fun main() {
    val example = """987654321111111
811111111111119
234234234234278
818181911112111"""

    val result = solveBatteryJoltagePart2(example)
    println("\nTotal: $result")
    println("Expected: 3121910778619")
    println("Match: ${result == 3121910778619L}")
}
