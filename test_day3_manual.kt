import java.io.File

fun solveBatteryJoltagePart2Manual(input: String): Long {
    val lines = input
        .replace("\r\n", "\n").replace("\r", "\n")
        .split('\n')
        .filter { it.isNotEmpty() }

    var totalJoltage = 0L

    for (line in lines) {
        println("\n=== Line: $line ===")
        
        // For each result position, greedily select the leftmost largest digit
        // that still leaves enough digits for remaining positions
        val digits = line.toList()
        val selected = mutableListOf<Int>()  // indices of selected digits
        
        for (resultPos in 0 until 12) {
            val needed = 12 - resultPos - 1  // how many more we need after this
            var bestIdx = -1
            var bestDigit = '0'
            
            for (i in digits.indices) {
                if (i in selected) continue  // already used
                
                // Count how many unused digits are after this position
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
                println("Pos $resultPos: digit='$bestDigit' at index $bestIdx (unused after: ${(bestIdx + 1 until digits.size).count { it !in selected }})")
            }
        }
        
        val result = selected.sorted().map { digits[it] }.joinToString("")
        println("Selected indices: ${selected.sorted()}")
        println("Result: $result")
        
        val joltage = result.toLongOrNull() ?: 0L
        totalJoltage += joltage
    }

    return totalJoltage
}

fun main() {
    val example = """987654321111111
811111111111119
234234234234278
818181911112111"""
    
    val result = solveBatteryJoltagePart2Manual(example)
    println("\n\n=== TOTAL ===")
    println("Result: $result")
    println("Expected: 3121910778619")
}
